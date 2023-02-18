package io.tinyrpc.proxy.api.object;

import io.tinyrpc.protocol.RpcProtocol;
import io.tinyrpc.protocol.header.RpcHeaderFactory;
import io.tinyrpc.protocol.request.RpcRequest;
import io.tinyrpc.proxy.api.consumer.Consumer;
import io.tinyrpc.proxy.api.future.RPCFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

public class ObjectProxy<T> implements InvocationHandler {

	private static final Logger logger = LoggerFactory.getLogger(ObjectProxy.class);

	/**
	 * 接口的Class对象
	 */
	private final Class<T> clazz;

	/**
	 * 服务版本号
	 */
	private String serviceVersion;
	/**
	 * 服务分组
	 */
	private String serviceGroup;
	/**
	 * 超时时间，默认15s
	 */
	private long timeout = 15000;

	/**
	 * 服务消费者
	 */
	private Consumer consumer;
	/**
	 * 序列化类型
	 */
	private String serializationType;

	/**
	 * 是否异步调用
	 */
	private boolean async;

	/**
	 * 是否单向调用
	 */
	private boolean oneway;

	public ObjectProxy(Class<T> clazz) {
		this.clazz = clazz;
	}

	public ObjectProxy(Class<T> clazz, String serviceVersion, String serviceGroup, String serializationType, long timeout, Consumer consumer, boolean async, boolean oneway) {
		this.clazz = clazz;
		this.serviceVersion = serviceVersion;
		this.timeout = timeout;
		this.serviceGroup = serviceGroup;
		this.consumer = consumer;
		this.serializationType = serializationType;
		this.async = async;
		this.oneway = oneway;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		if (Object.class == method.getDeclaringClass()) {
			String name = method.getName();
			switch (name) {
				case "equals":
					return proxy == args[0];
				case "hashCode":
					return System.identityHashCode(proxy);
				case "toString":
					return proxy.getClass().getName()
						+ "@"
						+ Integer.toHexString(System.identityHashCode(proxy))
						+ ", with InvocationHandler " + this;
				default:
					throw new IllegalStateException(String.valueOf(method));
			}
		}

		RpcProtocol<RpcRequest> requestRpcProtocol = new RpcProtocol<>();
		requestRpcProtocol.setHeader(RpcHeaderFactory.getRequestHeader(serializationType));

		RpcRequest request = new RpcRequest();
		request.setVersion(this.serviceVersion);
		request.setClassName(method.getDeclaringClass().getName());
		request.setMethodName(method.getName());
		request.setParameterTypes(method.getParameterTypes());
		request.setGroup(this.serviceGroup);
		request.setParameters(args);
		request.setAsync(async);
		request.setOneway(oneway);
		requestRpcProtocol.setBody(request);

		// Debug
		logger.debug(method.getDeclaringClass().getName());
		logger.debug(method.getName());

		if (method.getParameterTypes().length > 0) {
			for (int i = 0; i < method.getParameterTypes().length; ++i) {
				logger.debug(method.getParameterTypes()[i].getName());
			}
		}

		if (args != null && args.length > 0) {
			for (Object arg : args) {
				logger.debug(arg.toString());
			}
		}

		RPCFuture rpcFuture = this.consumer.sendRequest(requestRpcProtocol);
		return rpcFuture == null ? null
			: timeout > 0 ? rpcFuture.get(timeout, TimeUnit.MILLISECONDS)
			: rpcFuture.get();
	}
}
