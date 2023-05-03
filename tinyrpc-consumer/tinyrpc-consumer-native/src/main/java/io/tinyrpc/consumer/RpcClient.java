package io.tinyrpc.consumer;

import io.tinyrpc.common.exception.RegistryException;
import io.tinyrpc.consumer.common.RpcConsumer;
import io.tinyrpc.proxy.api.ProxyFactory;
import io.tinyrpc.proxy.api.async.IAsyncObjectProxy;
import io.tinyrpc.proxy.api.config.ProxyConfig;
import io.tinyrpc.proxy.api.object.ObjectProxy;
import io.tinyrpc.registry.api.RegistryService;
import io.tinyrpc.registry.api.config.RegistryConfig;
import io.tinyrpc.spi.loader.ExtensionLoader;
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

	/**
	 * 注册服务
	 */
	private final RegistryService registryService;

	/**
	 * 代理
	 */
	private final String proxy;

	//心跳间隔时间，默认30秒
	private final int heartbeatInterval;

	//扫描空闲连接时间，默认60秒
	private final int scanNotActiveChannelInterval;

	public RpcClient(String registryAddress, String registryType, String registryLoadBalanceType,
					 String proxy, String serviceVersion, String serviceGroup, String serializationType, long timeout,
					 boolean async, boolean oneway,
					 int heartbeatInterval, int scanNotActiveChannelInterval) {
		this.serviceVersion = serviceVersion;
		this.timeout = timeout;
		this.serviceGroup = serviceGroup;
		this.serializationType = serializationType;
		this.async = async;
		this.oneway = oneway;
		this.proxy = proxy;
		this.heartbeatInterval = heartbeatInterval;
		this.scanNotActiveChannelInterval = scanNotActiveChannelInterval;
		this.registryService = this.getRegistryService(registryAddress, registryType, registryLoadBalanceType);
	}

	private RegistryService getRegistryService(String registryAddress, String registryType, String registryLoadBalanceType) {
		if (registryType == null) {
			throw new IllegalArgumentException("registry type is null");
		}
		RegistryService registryService = ExtensionLoader.getExtension(RegistryService.class, registryType);
		try {
			registryService.init(new RegistryConfig(registryAddress, registryType, registryLoadBalanceType));
		} catch (Exception e) {
			logger.error("RpcClient init registry service error", e);
			throw new RegistryException(e.getMessage(), e);
		}
		return registryService;
	}

	public <T> T create(Class<T> interfaceClass) {
		ProxyFactory proxyFactory = ExtensionLoader.getExtension(ProxyFactory.class, proxy);
		proxyFactory.init(new ProxyConfig<>(interfaceClass, serviceVersion, serviceGroup, serializationType, timeout,
			registryService, RpcConsumer.getInstance(heartbeatInterval, scanNotActiveChannelInterval),
			async, oneway));
		return proxyFactory.getProxy(interfaceClass);
	}

	public <T> IAsyncObjectProxy createAsync(Class<T> interfaceClass) {
		return new ObjectProxy<>(interfaceClass, serviceVersion, serviceGroup, serializationType, timeout,
			registryService, RpcConsumer.getInstance(heartbeatInterval, scanNotActiveChannelInterval),
			async, oneway);
	}

	public void shutdown() {
		RpcConsumer.getInstance(heartbeatInterval, scanNotActiveChannelInterval).close();
	}
}
