package io.tinyrpc.reflect.javassist;

import io.tinyrpc.reflect.api.ReflectInvoker;
import javassist.util.proxy.ProxyFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

public class JavassistReflectInvoker implements ReflectInvoker {

	private static final Logger logger = LoggerFactory.getLogger(JavassistReflectInvoker.class);

	@Override
	public Object invokeMethod(Object serviceBean, Class<?> serviceClass, String methodName, Class<?>[] parameterTypes, Object[] parameters) throws Throwable {
		logger.info("use javassist reflect type invoke method...");

		ProxyFactory proxyFactory = new ProxyFactory();
		proxyFactory.setSuperclass(serviceClass);
		Class<?> childClass = proxyFactory.createClass();
		Method method = childClass.getMethod(methodName, parameterTypes);
		method.setAccessible(true);
		return method.invoke(childClass.newInstance(), parameters);
	}
}
