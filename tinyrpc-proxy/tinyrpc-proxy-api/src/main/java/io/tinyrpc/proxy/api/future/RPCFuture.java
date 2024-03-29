package io.tinyrpc.proxy.api.future;

import io.tinyrpc.common.exception.RpcException;
import io.tinyrpc.common.threadpool.ConcurrentThreadPool;
import io.tinyrpc.protocol.RpcProtocol;
import io.tinyrpc.protocol.enumeration.RpcStatus;
import io.tinyrpc.protocol.header.RpcHeader;
import io.tinyrpc.protocol.request.RpcRequest;
import io.tinyrpc.protocol.response.RpcResponse;
import io.tinyrpc.proxy.api.callback.AsyncRPCCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.ReentrantLock;

/**
 * RPC框架获取异步结果的自定义Future
 */
public class RPCFuture extends CompletableFuture<Object> {

	private static final Logger logger = LoggerFactory.getLogger(RPCFuture.class);

	private final Sync sync;
	private final ReentrantLock lock = new ReentrantLock();

	private final long startTime;
	private final long responseTimeThreshold = 5000;

	private final RpcProtocol<RpcRequest> requestRpcProtocol;
	private final List<AsyncRPCCallback> pendingCallbacks = new ArrayList<>();
	private final ConcurrentThreadPool concurrentThreadPool;
	private RpcProtocol<RpcResponse> responseRpcProtocol;

	public RPCFuture(RpcProtocol<RpcRequest> requestRpcProtocol, ConcurrentThreadPool concurrentThreadPool) {
		this.startTime = System.currentTimeMillis();
		this.sync = new Sync();
		this.requestRpcProtocol = requestRpcProtocol;
		this.concurrentThreadPool = concurrentThreadPool;
	}

	@Override
	public boolean isDone() {
		return sync.isDone();
	}

	@Override
	public Object get() throws InterruptedException, ExecutionException {
		sync.acquire(-1);
//		if (this.responseRpcProtocol != null) {
//			return this.responseRpcProtocol.getBody().getResult();
//		} else {
//			return null;
//		}
		return this.getResult(responseRpcProtocol);
	}

	@Override
	public Object get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
		boolean success = sync.tryAcquireNanos(-1, unit.toNanos(timeout));
		logger.info("### {} ###", success);
		if (success) {
//			if (this.responseRpcProtocol != null) {
//				return this.responseRpcProtocol.getBody().getResult();
//			} else {
//				return null;
//			}
			return this.getResult(this.responseRpcProtocol);
		} else {
			throw new RpcException("Timeout exception. Request id: " + this.requestRpcProtocol.getHeader().getRequestId()
				+ ". Request class name: " + this.requestRpcProtocol.getBody().getClassName()
				+ ". Request method: " + this.requestRpcProtocol.getBody().getMethodName());
		}
	}

	/**
	 * 获取最终结果
	 */
	private Object getResult(RpcProtocol<RpcResponse> rpcResponseProtocol) {
		if (rpcResponseProtocol == null) {
			return null;
		}
		RpcHeader header = rpcResponseProtocol.getHeader();
		// 服务提供者抛出了异常
		RpcResponse rpcResponse = rpcResponseProtocol.getBody();
		if ((byte) RpcStatus.FAIL.getCode() == header.getStatus()) {
			throw new RpcException("rpc provider throws exception", new Throwable(rpcResponse.getError()));
		}
		return rpcResponse.getResult();
	}

	@Override
	public boolean isCancelled() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {
		throw new UnsupportedOperationException();
	}

	public void done(RpcProtocol<RpcResponse> responseRpcProtocol) {
		this.responseRpcProtocol = responseRpcProtocol;
		sync.release(1);

		invokeCallbacks();
		// Threshold
		long responseTime = System.currentTimeMillis() - startTime;
		if (responseTime > this.responseTimeThreshold) {
			logger.warn("Service response time is too slow. Request id = " + responseRpcProtocol.getHeader().getRequestId() + ". Response Time = " + responseTime + "ms");
		}
	}

	private void invokeCallbacks() {
		lock.lock();
		try {
			for (final AsyncRPCCallback pendingCallback : pendingCallbacks) {
				runCallback(pendingCallback);
			}
		} finally {
			lock.unlock();
		}
	}

	public RPCFuture addCallback(AsyncRPCCallback callback) {
		lock.lock();
		try {
			if (isDone()) {
				runCallback(callback);
			} else {
				this.pendingCallbacks.add(callback);
			}
		} finally {
			lock.unlock();
		}
		return this;
	}

	private void runCallback(final AsyncRPCCallback callback) {
		final RpcResponse res = this.responseRpcProtocol.getBody();
		concurrentThreadPool.submit(() -> {
			if (!res.isError()) {
				callback.onSuccess(res.getResult());
			} else {
				callback.onException(new RpcException("Response error", new Throwable(res.getError())));
			}
		});
	}

	static class Sync extends AbstractQueuedSynchronizer {

		private static final long serialVersionUID = -6743110410564138528L;

		//future status
		private final int done = 1;
		private final int pending = 0;

		protected boolean tryAcquire(int acquires) {
			return getState() == done;
		}

		protected boolean tryRelease(int releases) {
			if (getState() == pending) {
				return compareAndSetState(pending, done);
			}
			return false;
		}

		public boolean isDone() {
			getState();
			return getState() == done;
		}
	}
}
