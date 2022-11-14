package io.tinyrpc.proxy.asm;

import io.tinyrpc.proxy.api.BaseProxyFactory;
import io.tinyrpc.proxy.api.ProxyFactory;

public class AsmProxyFactory<T> extends BaseProxyFactory<T> implements ProxyFactory {

	@Override
	public <T> T getProxy(Class<T> clazz) {
		return null;
	}
}
