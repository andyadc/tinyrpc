package io.tinyrpc.consumer.common.context;

import io.tinyrpc.proxy.api.future.RPCFuture;

/**
 * 保存RPC上下文
 */
public class RpcContext {

	/**
	 * RpcContext实例
	 */
	private static final RpcContext AGENT = new RpcContext();
	/**
	 * 存放RPCFuture的InheritableThreadLocal
	 */
	private static final InheritableThreadLocal<RPCFuture> RPC_FUTURE_INHERITABLE_THREAD_LOCAL = new InheritableThreadLocal<>();

	private RpcContext() {
	}

	/**
	 * 获取上下文
	 *
	 * @return RPC服务的上下文信息
	 */
	public static RpcContext getContext() {
		return AGENT;
	}

	/**
	 * 获取RPCFuture
	 */
	public RPCFuture getRPCFuture() {
		return RPC_FUTURE_INHERITABLE_THREAD_LOCAL.get();
	}

	/**
	 * 将RPCFuture保存到线程的上下文
	 *
	 * @param rpcFuture
	 */
	public void setRPCFuture(RPCFuture rpcFuture) {
		RPC_FUTURE_INHERITABLE_THREAD_LOCAL.set(rpcFuture);
	}

	/**
	 * 移除RPCFuture
	 */
	public void removeRPCFuture() {
		RPC_FUTURE_INHERITABLE_THREAD_LOCAL.remove();
	}
}
