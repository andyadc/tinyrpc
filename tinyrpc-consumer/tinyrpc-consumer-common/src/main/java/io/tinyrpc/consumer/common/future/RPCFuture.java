package io.tinyrpc.consumer.common.future;

import io.tinyrpc.protocol.RpcProtocol;
import io.tinyrpc.protocol.request.RpcRequest;
import io.tinyrpc.protocol.response.RpcResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;

/**
 * RPC框架获取异步结果的自定义Future
 */
public class RPCFuture extends CompletableFuture<Object> {

	private static final Logger logger = LoggerFactory.getLogger(RPCFuture.class);

	private final Sync sync;
	private final RpcProtocol<RpcRequest> requestRpcProtocol;
	private final long startTime;
	private final long responseTimeThreshold = 5000;
	private RpcProtocol<RpcResponse> responseRpcProtocol;

	public RPCFuture(RpcProtocol<RpcRequest> requestRpcProtocol) {
		this.sync = new Sync();
		this.requestRpcProtocol = requestRpcProtocol;
		this.startTime = System.currentTimeMillis();
	}

	@Override
	public boolean isDone() {
		return sync.isDone();
	}

	@Override
	public Object get() throws InterruptedException, ExecutionException {
		sync.acquire(-1);
		if (this.responseRpcProtocol != null) {
			return this.responseRpcProtocol.getBody().getResult();
		} else {
			return null;
		}
	}

	@Override
	public Object get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
		boolean success = sync.tryAcquireNanos(-1, unit.toNanos(timeout));
		if (success) {
			if (this.responseRpcProtocol != null) {
				return this.responseRpcProtocol.getBody().getResult();
			} else {
				return null;
			}
		} else {
			throw new RuntimeException("Timeout exception. Request id: " + this.requestRpcProtocol.getHeader().getRequestId()
				+ ". Request class name: " + this.requestRpcProtocol.getBody().getClassName()
				+ ". Request method: " + this.requestRpcProtocol.getBody().getMethodName());
		}
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
		// Threshold
		long responseTime = System.currentTimeMillis() - startTime;
		if (responseTime > this.responseTimeThreshold) {
			logger.warn("Service response time is too slow. Request id = " + responseRpcProtocol.getHeader().getRequestId() + ". Response Time = " + responseTime + "ms");
		}
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
