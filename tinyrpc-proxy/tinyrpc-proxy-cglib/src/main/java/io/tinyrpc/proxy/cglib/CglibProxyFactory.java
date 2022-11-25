package io.tinyrpc.proxy.cglib;

import io.tinyrpc.proxy.api.BaseProxyFactory;
import io.tinyrpc.proxy.api.ProxyFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CglibProxyFactory<T> extends BaseProxyFactory<T> implements ProxyFactory {

	private static final Logger logger = LoggerFactory.getLogger(CglibProxyFactory.class);

	@Override
	public <T> T getProxy(Class<T> clazz) {
		return null;
	}
}
