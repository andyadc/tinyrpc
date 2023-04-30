package io.tinyrpc.proxy.jdk;

import io.tinyrpc.proxy.api.BaseProxyFactory;
import io.tinyrpc.proxy.api.ProxyFactory;

import java.lang.reflect.Proxy;

/**
 * JDK动态代理
 *
 * @param <T>
 */
public class JdkProxyFactory<T> extends BaseProxyFactory<T> implements ProxyFactory {

	@Override
	public <T> T getProxy(Class<T> clazz) {
		return (T) Proxy.newProxyInstance(
			clazz.getClassLoader(),
			new Class<?>[]{clazz},
			objectProxy
		);
	}
}
