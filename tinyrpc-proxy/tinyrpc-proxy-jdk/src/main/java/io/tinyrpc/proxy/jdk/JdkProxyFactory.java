package io.tinyrpc.proxy.jdk;

import io.tinyrpc.proxy.api.BaseProxyFactory;
import io.tinyrpc.proxy.api.ProxyFactory;
import io.tinyrpc.spi.annotation.SPIClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Proxy;

/**
 * JDK动态代理
 *
 * @param <T>
 */
@SPIClass
public class JdkProxyFactory<T> extends BaseProxyFactory<T> implements ProxyFactory {

	private static final Logger logger = LoggerFactory.getLogger(JdkProxyFactory.class);

	@SuppressWarnings({"unchecked"})
	@Override
	public <T> T getProxy(Class<T> clazz) {
		if (logger.isDebugEnabled()) {
			logger.debug("-- jdk proxy --");
		}
		return (T) Proxy.newProxyInstance(
			clazz.getClassLoader(),
			new Class<?>[]{clazz},
			objectProxy
		);
	}
}
