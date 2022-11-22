package io.tinyrpc.reflect.jdk;

import io.tinyrpc.reflect.api.ReflectInvoker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

public class JdkReflectInvoker implements ReflectInvoker {

	private static final Logger logger = LoggerFactory.getLogger(JdkReflectInvoker.class);

	@Override
	public Object invokeMethod(Object serviceBean, Class<?> serviceClass, String methodName, Class<?>[] parameterTypes, Object[] parameters) throws Throwable {
		logger.info("Use jdk reflect type invoke method...");

		Method method = serviceClass.getMethod(methodName, parameterTypes);
		method.setAccessible(true);
		return method.invoke(serviceBean, parameters);
	}
}
