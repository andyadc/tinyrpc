package io.tinyrpc.proxy.jdk;

import io.tinyrpc.exception.ProxyException;
import io.tinyrpc.proxy.ProxyFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

public class JdkProxyFactory implements ProxyFactory {

    @SuppressWarnings({"unchecked"})
    @Override
    public <T> T getProxy(Class<T> clz, InvocationHandler invoker, ClassLoader classLoader) throws ProxyException {
        try {
            return (T) Proxy.newProxyInstance(classLoader, new Class[]{clz}, invoker);
        } catch (IllegalArgumentException e) {
            throw new ProxyException(e.getMessage(), e);
        }
    }
}
