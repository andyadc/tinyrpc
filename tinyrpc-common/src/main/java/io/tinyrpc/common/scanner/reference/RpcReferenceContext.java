package io.tinyrpc.common.scanner.reference;

import io.tinyrpc.annotation.RpcReference;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 存在{@link RpcReference}注解字段代理实例的上下文
 */
public class RpcReferenceContext {

	private static final Map<String, Object> instance;

	static {
		instance = new ConcurrentHashMap<>();
	}

	public static void put(String key, Object value) {
		instance.put(key, value);
	}

	public static Object get(String key) {
		return instance.get(key);
	}

	public static Object remove(String key) {
		return instance.remove(key);
	}
}
