package io.tinyrpc.proxy.api;

import io.tinyrpc.proxy.api.config.ProxyConfig;
import io.tinyrpc.proxy.api.object.ObjectProxy;

/**
 * 基础代理工厂类
 *
 * @param <T>
 */
public abstract class BaseProxyFactory<T> implements ProxyFactory {

	protected ObjectProxy<T> objectProxy;

	@Override
	public <T> void init(ProxyConfig<T> proxyConfig) {
		this.objectProxy = new ObjectProxy(proxyConfig.getClazz(),
			proxyConfig.getServiceVersion(),
			proxyConfig.getServiceGroup(),
			proxyConfig.getSerializationType(),
			proxyConfig.getTimeout(),
			proxyConfig.getConsumer(),
			proxyConfig.getAsync(),
			proxyConfig.getOneway()
		);
	}
}
