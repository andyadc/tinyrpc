package io.tinyrpc.reflect.bytebuddy;

import io.tinyrpc.reflect.api.ReflectInvoker;
import io.tinyrpc.spi.annotation.SPIClass;
import net.bytebuddy.ByteBuddy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

/**
 * ByteBuddy反射机制
 */
@SPIClass
public class ByteBuddyReflectInvoker implements ReflectInvoker {

	private static final Logger logger = LoggerFactory.getLogger(ByteBuddyReflectInvoker.class);

	@Override
	public Object invokeMethod(Object serviceBean, Class<?> serviceClass, String methodName, Class<?>[] parameterTypes, Object[] parameters) throws Throwable {
		if (logger.isDebugEnabled()) {
			logger.debug("-- bytebuddy reflect --");
		}

		Class<?> childClass = new ByteBuddy().subclass(serviceClass)
			.make()
			.load(ByteBuddyReflectInvoker.class.getClassLoader())
			.getLoaded();
		Object instance = childClass.getDeclaredConstructor().newInstance();
		Method method = childClass.getMethod(methodName, parameterTypes);
		method.setAccessible(true);
		return method.invoke(instance, parameters);
	}
}
