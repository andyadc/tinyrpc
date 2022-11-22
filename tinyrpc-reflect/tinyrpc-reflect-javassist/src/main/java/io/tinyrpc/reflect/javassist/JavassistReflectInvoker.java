package io.tinyrpc.reflect.javassist;

import io.tinyrpc.reflect.api.ReflectInvoker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JavassistReflectInvoker implements ReflectInvoker {

	private static final Logger logger = LoggerFactory.getLogger(JavassistReflectInvoker.class);

	@Override
	public Object invokeMethod(Object serviceBean, Class<?> serviceClass, String methodName, Class<?>[] parameterTypes, Object[] parameters) throws Throwable {
		return null;
	}
}
