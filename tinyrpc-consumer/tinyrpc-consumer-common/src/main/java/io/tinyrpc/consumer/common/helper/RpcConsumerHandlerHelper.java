package io.tinyrpc.consumer.common.helper;

import io.tinyrpc.consumer.common.handler.RpcConsumerHandler;
import io.tinyrpc.protocol.meta.ServiceMeta;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RpcConsumerHandlerHelper {

	private static final Map<String, RpcConsumerHandler> rpcConsumerHandlerMap;

	static {
		rpcConsumerHandlerMap = new ConcurrentHashMap<>();
	}

	public static void put(ServiceMeta key, RpcConsumerHandler value) {
		rpcConsumerHandlerMap.put(getKey(key), value);
	}

	public static RpcConsumerHandler get(ServiceMeta meta) {
		return rpcConsumerHandlerMap.get(getKey(meta));
	}

	public static void closeRpcClientHandler() {
		Collection<RpcConsumerHandler> rpcClientHandlers = rpcConsumerHandlerMap.values();
		if (rpcClientHandlers.size() > 0) {
			rpcClientHandlers.forEach(RpcConsumerHandler::close);
		}
		rpcClientHandlers.clear();
	}

	private static String getKey(ServiceMeta meta) {
		return meta.getServiceAddr().concat("_").concat(String.valueOf(meta.getServicePort()));
	}
}
