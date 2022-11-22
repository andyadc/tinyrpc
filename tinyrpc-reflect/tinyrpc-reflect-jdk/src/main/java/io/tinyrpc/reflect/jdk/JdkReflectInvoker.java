package io.tinyrpc.reflect.jdk;

import io.tinyrpc.reflect.api.ReflectInvoker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JdkReflectInvoker implements ReflectInvoker {

	private static final Logger logger = LoggerFactory.getLogger(JdkReflectInvoker.class);

	@Override
	public Object invokeMethod(Object serviceBean, Class<?> serviceClass, String methodName, Class<?>[] parameterTypes, Object[] parameters) throws Throwable {
		return null;
	}
}
