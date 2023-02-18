package io.tinyrpc.consumer;

import io.tinyrpc.consumer.common.RpcConsumer;
import io.tinyrpc.proxy.api.async.IAsyncObjectProxy;
import io.tinyrpc.proxy.api.object.ObjectProxy;
import io.tinyrpc.proxy.jdk.JdkProxyFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 服务消费客户端
 */
public class RpcClient {

	private static final Logger logger = LoggerFactory.getLogger(RpcClient.class);

	/**
	 * 服务版本
	 */
	private final String serviceVersion;
	/**
	 * 服务分组
	 */
	private final String serviceGroup;
	/**
	 * 序列化类型
	 */
	private final String serializationType;
	/**
	 * 超时时间
	 */
	private final long timeout;

	/**
	 * 是否异步调用
	 */
	private final boolean async;

	/**
	 * 是否单向调用
	 */
	private final boolean oneway;

	public RpcClient(String serviceVersion, String serviceGroup, String serializationType, long timeout, boolean async, boolean oneway) {
		this.serviceVersion = serviceVersion;
		this.timeout = timeout;
		this.serviceGroup = serviceGroup;
		this.serializationType = serializationType;
		this.async = async;
		this.oneway = oneway;
	}

	public <T> T create(Class<T> interfaceClass) {
		JdkProxyFactory<T> jdkProxyFactory = new JdkProxyFactory<>(serviceVersion, serviceGroup, serializationType, timeout, RpcConsumer.getInstance(), async, oneway);
		return jdkProxyFactory.getProxy(interfaceClass);
	}

	public <T> IAsyncObjectProxy createAsync(Class<T> interfaceClass) {
		return new ObjectProxy<>(interfaceClass, serviceVersion, serviceGroup, serializationType, timeout, RpcConsumer.getInstance(), async, oneway);
	}

	public void shutdown() {
		RpcConsumer.getInstance().close();
	}
}
