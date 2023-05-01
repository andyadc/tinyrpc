package io.tinyrpc.proxy.asm;

import io.tinyrpc.proxy.api.BaseProxyFactory;
import io.tinyrpc.proxy.api.ProxyFactory;
import io.tinyrpc.proxy.asm.proxy.ASMProxy;
import io.tinyrpc.spi.annotation.SPIClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ASM动态代理
 */
@SPIClass
public class AsmProxyFactory<T> extends BaseProxyFactory<T> implements ProxyFactory {

	private static final Logger logger = LoggerFactory.getLogger(AsmProxyFactory.class);

	@SuppressWarnings({"unchecked"})
	@Override
	public <T> T getProxy(Class<T> clazz) {
		if (logger.isDebugEnabled()) {
			logger.debug("-- asm proxy --");
		}

		try {
			return (T) ASMProxy.newProxyInstance(this.getClass().getClassLoader(), new Class[]{clazz}, objectProxy);
		} catch (Exception e) {
			logger.error("asm proxy error", e);
		}
		return null;
	}
}
