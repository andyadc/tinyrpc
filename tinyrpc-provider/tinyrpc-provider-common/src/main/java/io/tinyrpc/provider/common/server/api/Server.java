package io.tinyrpc.provider.common.server.api;

/**
 * 启动RPC服务的接口
 */
public interface Server {

	/**
	 * 启动Netty服务
	 */
	void startNettyServer();
}
