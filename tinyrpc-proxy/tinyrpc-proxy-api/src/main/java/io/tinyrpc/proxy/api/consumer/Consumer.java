package io.tinyrpc.proxy.api.consumer;

import io.tinyrpc.protocol.RpcProtocol;
import io.tinyrpc.protocol.request.RpcRequest;
import io.tinyrpc.proxy.api.future.RPCFuture;

/**
 * 服务消费者
 */
public interface Consumer {

	/**
	 * 消费者发送 request 请求
	 */
	RPCFuture sendRequest(RpcProtocol<RpcRequest> protocol) throws Exception;
}
