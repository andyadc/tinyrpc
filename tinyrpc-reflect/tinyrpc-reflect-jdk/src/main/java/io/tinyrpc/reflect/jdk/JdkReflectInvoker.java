package io.tinyrpc.reflect.jdk;

import io.tinyrpc.reflect.api.ReflectInvoker;
import io.tinyrpc.spi.annotation.SPIClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

/**
 * JDK反射调用方法的类
 */
@SPIClass
public class JdkReflectInvoker implements ReflectInvoker {

	private static final Logger logger = LoggerFactory.getLogger(JdkReflectInvoker.class);

	@Override
	public Object invokeMethod(Object serviceBean, Class<?> serviceClass, String methodName, Class<?>[] parameterTypes, Object[] parameters) throws Throwable {
		if (logger.isDebugEnabled()) {
			logger.debug("-- jdk reflect --");
		}

		Method method = serviceClass.getMethod(methodName, parameterTypes);
		method.setAccessible(true);
		return method.invoke(serviceBean, parameters);
	}
}
