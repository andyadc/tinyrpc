package io.tinyrpc.proxy.jdk;

import io.tinyrpc.proxy.api.consumer.Consumer;
import io.tinyrpc.proxy.api.object.ObjectProxy;

import java.lang.reflect.Proxy;

/**
 * JDK动态代理
 *
 * @param <T>
 */
public class JdkProxyFactory<T> {

	/**
	 * 服务版本号
	 */
	private final String serviceVersion;
	/**
	 * 服务分组
	 */
	private final String serviceGroup;
	/**
	 * 服务消费者
	 */
	private final Consumer consumer;
	/**
	 * 序列化类型
	 */
	private final String serializationType;
	/**
	 * 是否异步调用
	 */
	private final boolean async;
	/**
	 * 是否单向调用
	 */
	private final boolean oneway;
	/**
	 * 超时时间，默认15s
	 */
	private long timeout = 15000;

	public JdkProxyFactory(String serviceVersion, String serviceGroup, String serializationType, long timeout, Consumer consumer, boolean async, boolean oneway) {
		this.serviceVersion = serviceVersion;
		this.timeout = timeout;
		this.serviceGroup = serviceGroup;
		this.consumer = consumer;
		this.serializationType = serializationType;
		this.async = async;
		this.oneway = oneway;
	}

	public <T> T getProxy(Class<T> clazz) {
		return (T) Proxy.newProxyInstance(
			clazz.getClassLoader(),
			new Class<?>[]{clazz},
			new ObjectProxy<>(clazz, serviceVersion, serviceGroup, serializationType, timeout, consumer, async, oneway)
		);
	}
}
