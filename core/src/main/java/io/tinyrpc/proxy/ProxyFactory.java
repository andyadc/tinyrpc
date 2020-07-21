package io.tinyrpc.proxy;

import io.tinyrpc.exception.ProxyException;
import io.tinyrpc.util.ClassLoaderUtil;

import java.lang.reflect.InvocationHandler;

public interface ProxyFactory {

    /**
     * Gets proxy.
     *
     * @param <T>     the type parameter
     * @param clz     the clz
     * @param invoker the invoker
     * @return the proxy
     * @throws ProxyException
     */
    default <T> T getProxy(final Class<T> clz, final InvocationHandler invoker) throws ProxyException {
        return getProxy(clz, invoker, ClassLoaderUtil.getCurrentClassLoader());
    }

    /**
     * Gets proxy.
     *
     * @param <T>         the type parameter
     * @param clz         the clz
     * @param invoker     the invoker
     * @param classLoader the class loader
     * @return the proxy
     * @throws ProxyException
     */
    <T> T getProxy(Class<T> clz, InvocationHandler invoker, ClassLoader classLoader) throws ProxyException;
}
