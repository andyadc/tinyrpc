package io.tinyrpc.proxy.api;

import io.tinyrpc.proxy.api.config.ProxyConfig;
import io.tinyrpc.proxy.api.object.ObjectProxy;

public abstract class BaseProxyFactory<T> implements ProxyFactory {

	protected ObjectProxy<T> objectProxy;

	@Override
	public <T> void init(ProxyConfig<T> proxyConfig) {

	}
}
