package io.tinyrpc.reflect.cglib;

import io.tinyrpc.reflect.api.ReflectInvoker;
import net.sf.cglib.reflect.FastClass;
import net.sf.cglib.reflect.FastMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CglibReflectInvoker implements ReflectInvoker {

	private static final Logger logger = LoggerFactory.getLogger(CglibReflectInvoker.class);

	@Override
	public Object invokeMethod(Object serviceBean, Class<?> serviceClass, String methodName, Class<?>[] parameterTypes, Object[] parameters) throws Throwable {
		logger.info("Use cglib reflect type invoke method...");

		FastClass serviceFastClass = FastClass.create(serviceClass);
		FastMethod fastMethod = serviceFastClass.getMethod(methodName, parameterTypes);
		return fastMethod.invoke(serviceBean, parameters);
	}
}
