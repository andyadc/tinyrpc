package io.tinyrpc.proxy.javassist;

import io.tinyrpc.proxy.api.BaseProxyFactory;
import io.tinyrpc.proxy.api.ProxyFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JavassistProxyFactory<T> extends BaseProxyFactory<T> implements ProxyFactory {

	private static final Logger logger = LoggerFactory.getLogger(JavassistProxyFactory.class);

	@Override
	public <T> T getProxy(Class<T> clazz) {
		return null;
	}
}
