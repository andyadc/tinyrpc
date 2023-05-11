package io.tinyrpc.reflect.asm;

import io.tinyrpc.reflect.api.ReflectInvoker;
import io.tinyrpc.reflect.asm.proxy.ReflectProxy;
import io.tinyrpc.spi.annotation.SPIClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * ASM反射机制
 */
@SPIClass
public class AsmReflectInvoker implements ReflectInvoker {

	private static final Logger logger = LoggerFactory.getLogger(AsmReflectInvoker.class);

	private final ThreadLocal<Boolean> exceptionThreadLocal = new ThreadLocal<>();

	@Override
	public Object invokeMethod(Object serviceBean, Class<?> serviceClass, String methodName, Class<?>[] parameterTypes, Object[] parameters) throws Throwable {
		if (logger.isDebugEnabled()) {
			logger.debug("-- asm reflect --");
		}
		exceptionThreadLocal.set(Boolean.FALSE);
		Object result;
		try {
			Constructor<?> constructor = serviceClass.getConstructor();
			Object[] constructorParam = new Object[]{};
			Object instance = ReflectProxy.newProxyInstance(AsmReflectInvoker.class.getClassLoader(),
				getInvocationHandler(serviceBean),
				serviceClass,
				constructor,
				constructorParam);
			Method method = serviceClass.getMethod(methodName, parameterTypes);
			method.setAccessible(true);
			result = method.invoke(instance, parameters);
			if (exceptionThreadLocal.get()) {
				throw new RuntimeException("rpc provider throws exception...");
			}
		} finally {
			exceptionThreadLocal.remove();
		}
		return result;
	}

	private InvocationHandler getInvocationHandler(Object obj) {
		return (proxy, method, args) -> {
			if (logger.isDebugEnabled()) {
				logger.info("--- proxy invoke method ---");
			}
			method.setAccessible(true);
			try {
				return method.invoke(obj, args);
			} catch (Exception e) {
				exceptionThreadLocal.set(Boolean.TRUE);
			}
			return null;
		};
	}
}
