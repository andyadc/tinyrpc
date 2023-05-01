package io.tinyrpc.proxy.cglib;

import io.tinyrpc.proxy.api.BaseProxyFactory;
import io.tinyrpc.proxy.api.ProxyFactory;
import io.tinyrpc.spi.annotation.SPIClass;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.InvocationHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

/**
 * Cglib动态代理
 */
@SPIClass
public class CglibProxyFactory<T> extends BaseProxyFactory<T> implements ProxyFactory {

	private static final Logger logger = LoggerFactory.getLogger(CglibProxyFactory.class);

	private final Enhancer enhancer = new Enhancer();

	@Override
	public <T> T getProxy(Class<T> clazz) {
		if (logger.isDebugEnabled()) {
			logger.debug("-- cglib proxy --");
		}

		enhancer.setInterfaces(new Class[]{clazz});
		enhancer.setCallback(new InvocationHandler() {
			@Override
			public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
				return objectProxy.invoke(o, method, objects);
			}
		});
		return (T) enhancer.create();
	}
}
