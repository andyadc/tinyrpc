package io.tinyrpc.proxy.api.object;

import io.tinyrpc.proxy.api.async.AsyncObjectProxy;
import io.tinyrpc.proxy.api.future.RPCFuture;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class ObjectProxy<T> implements AsyncObjectProxy, InvocationHandler {

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		return null;
	}

	@Override
	public RPCFuture call(String funcName, Object... args) {
		return null;
	}
}
