package io.tinyrpc.loadbalancer.context;

import io.tinyrpc.protocol.meta.ServiceMeta;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 连接数上下文
 */
public class ConnectionsContext {

	private static final Map<String, Integer> connectionsMap = new ConcurrentHashMap<>();

	public static void add(ServiceMeta serviceMeta) {
		String key = generateKey(serviceMeta);
		Integer count = connectionsMap.get(key);
		if (count == null) {
			count = 0;
		}
		count++;
		connectionsMap.put(key, count);
	}

	public static Integer getValue(ServiceMeta serviceMeta) {
		String key = generateKey(serviceMeta);
		return connectionsMap.get(key);
	}

	private static String generateKey(ServiceMeta serviceMeta) {
		return serviceMeta.getServiceAddr()
			.concat(":")
			.concat(String.valueOf(serviceMeta.getServicePort()));
	}
}
