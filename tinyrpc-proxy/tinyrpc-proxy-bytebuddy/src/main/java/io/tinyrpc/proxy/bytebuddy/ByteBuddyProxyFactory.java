package io.tinyrpc.proxy.bytebuddy;

import io.tinyrpc.proxy.api.BaseProxyFactory;
import io.tinyrpc.proxy.api.ProxyFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ByteBuddyProxyFactory<T> extends BaseProxyFactory<T> implements ProxyFactory {

	private static final Logger logger = LoggerFactory.getLogger(ByteBuddyProxyFactory.class);

	@Override
	public <T> T getProxy(Class<T> clazz) {
		logger.info("基于 ByteBuddy 动态代理...");

		return null;
	}
}
