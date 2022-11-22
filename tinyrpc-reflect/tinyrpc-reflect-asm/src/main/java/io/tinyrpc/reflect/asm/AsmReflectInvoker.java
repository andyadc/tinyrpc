package io.tinyrpc.reflect.asm;

import io.tinyrpc.reflect.api.ReflectInvoker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AsmReflectInvoker implements ReflectInvoker {

	private static final Logger logger = LoggerFactory.getLogger(AsmReflectInvoker.class);

	@Override
	public Object invokeMethod(Object serviceBean, Class<?> serviceClass, String methodName, Class<?>[] parameterTypes, Object[] parameters) throws Throwable {

		return null;
	}
}
