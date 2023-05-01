package io.tinyrpc.proxy.bytebuddy;

import io.tinyrpc.proxy.api.BaseProxyFactory;
import io.tinyrpc.proxy.api.ProxyFactory;
import io.tinyrpc.spi.annotation.SPIClass;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.implementation.InvocationHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ByteBuddy动态代理
 */
@SPIClass
public class ByteBuddyProxyFactory<T> extends BaseProxyFactory<T> implements ProxyFactory {

	private static final Logger logger = LoggerFactory.getLogger(ByteBuddyProxyFactory.class);

	@SuppressWarnings({"unchecked"})
	@Override
	public <T> T getProxy(Class<T> clazz) {
		if (logger.isDebugEnabled()) {
			logger.debug("-- bytebuddy proxy --");
		}

		try {
			return (T) new ByteBuddy().subclass(Object.class)
				.implement(clazz)
				.intercept(InvocationHandlerAdapter.of(objectProxy))
				.make()
				.load(ByteBuddyProxyFactory.class.getClassLoader())
				.getLoaded()
				.getDeclaredConstructor()
				.newInstance();
		} catch (Exception e) {
			logger.error("bytebuddy proxy error", e);
		}
		return null;
	}
}
