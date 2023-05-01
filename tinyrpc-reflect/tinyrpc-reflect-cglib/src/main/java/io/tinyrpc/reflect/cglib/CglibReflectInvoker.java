package io.tinyrpc.reflect.cglib;

import io.tinyrpc.reflect.api.ReflectInvoker;
import io.tinyrpc.spi.annotation.SPIClass;
import net.sf.cglib.reflect.FastClass;
import net.sf.cglib.reflect.FastMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Cglib 反射调用方法的类
 */
@SPIClass
public class CglibReflectInvoker implements ReflectInvoker {

	private static final Logger logger = LoggerFactory.getLogger(CglibReflectInvoker.class);

	@Override
	public Object invokeMethod(Object serviceBean, Class<?> serviceClass, String methodName, Class<?>[] parameterTypes, Object[] parameters) throws Throwable {
		if (logger.isDebugEnabled()) {
			logger.debug("-- cglib reflect --");
		}

		FastClass serviceFastClass = FastClass.create(serviceClass);
		FastMethod fastMethod = serviceFastClass.getMethod(methodName, parameterTypes);
		return fastMethod.invoke(serviceBean, parameters);
	}
}
