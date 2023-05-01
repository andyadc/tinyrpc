package io.tinyrpc.proxy.javassist;

import io.tinyrpc.proxy.api.BaseProxyFactory;
import io.tinyrpc.proxy.api.ProxyFactory;
import io.tinyrpc.spi.annotation.SPIClass;
import javassist.util.proxy.MethodHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

/**
 * Javassist动态代理
 */
@SPIClass
public class JavassistProxyFactory<T> extends BaseProxyFactory<T> implements ProxyFactory {

	private static final Logger logger = LoggerFactory.getLogger(JavassistProxyFactory.class);

	private final javassist.util.proxy.ProxyFactory proxyFactory = new javassist.util.proxy.ProxyFactory();

	@Override
	public <T> T getProxy(Class<T> clazz) {
		if (logger.isDebugEnabled()) {
			logger.debug("-- javassist proxy --");
		}

		//设置代理类的父类
		proxyFactory.setInterfaces(new Class[]{clazz});
		proxyFactory.setHandler(new MethodHandler() {
			@Override
			public Object invoke(Object self, Method thisMethod, Method proceed,
								 Object[] args) throws Throwable {
				return objectProxy.invoke(self, thisMethod, args);
			}
		});
		try {
			// 通过字节码技术动态创建子类实例
			return (T) proxyFactory.createClass().newInstance();
		} catch (Exception e) {
			logger.error("javassist proxy error", e);
		}
		return null;
	}
}
