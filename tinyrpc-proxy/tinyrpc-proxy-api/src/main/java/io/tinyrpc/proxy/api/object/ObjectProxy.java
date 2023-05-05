package io.tinyrpc.proxy.api.object;

import io.tinyrpc.cache.result.CacheResultKey;
import io.tinyrpc.cache.result.CacheResultManager;
import io.tinyrpc.common.constants.RpcConstants;
import io.tinyrpc.protocol.RpcProtocol;
import io.tinyrpc.protocol.enumeration.RpcType;
import io.tinyrpc.protocol.header.RpcHeaderFactory;
import io.tinyrpc.protocol.request.RpcRequest;
import io.tinyrpc.proxy.api.async.IAsyncObjectProxy;
import io.tinyrpc.proxy.api.consumer.Consumer;
import io.tinyrpc.proxy.api.future.RPCFuture;
import io.tinyrpc.registry.api.RegistryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

public class ObjectProxy<T> implements IAsyncObjectProxy, InvocationHandler {

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
	 * 注册服务
	 */
	private RegistryService registryService;

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

	/**
	 * 是否开启结果缓存
	 */
	private boolean enableResultCache;

	/**
	 * 结果缓存管理器
	 */
	private CacheResultManager<Object> cacheResultManager;

	public ObjectProxy(Class<T> clazz) {
		this.clazz = clazz;
	}

	public ObjectProxy(Class<T> clazz, String serviceVersion, String serviceGroup, String serializationType,
					   long timeout, RegistryService registryService, Consumer consumer,
					   boolean async, boolean oneway,
					   boolean enableResultCache, int resultCacheExpire) {
		this.clazz = clazz;
		this.serviceVersion = serviceVersion;
		this.timeout = timeout;
		this.serviceGroup = serviceGroup;
		this.consumer = consumer;
		this.serializationType = serializationType;
		this.async = async;
		this.oneway = oneway;
		this.registryService = registryService;
		this.enableResultCache = enableResultCache;
		if (resultCacheExpire <= 0) {
			resultCacheExpire = RpcConstants.RPC_SCAN_RESULT_CACHE_EXPIRE;
		}
		this.cacheResultManager = CacheResultManager.getInstance(resultCacheExpire, enableResultCache);
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

		if (enableResultCache) {
			return invokeSendRequestMethodCache(method, args);
		}
		return invokeSendRequestMethod(method, args);
	}

	private Object invokeSendRequestMethodCache(Method method, Object[] args) throws Exception {
		//开启缓存，则处理缓存
		CacheResultKey cacheResultKey = new CacheResultKey(method.getDeclaringClass().getName(), method.getName(), method.getParameterTypes(), args, serviceVersion, serviceGroup);
		Object obj = this.cacheResultManager.get(cacheResultKey);
		if (obj == null) {
			logger.info("--- cache is null ---");
			obj = invokeSendRequestMethod(method, args);
			if (obj != null) {
				cacheResultKey.setCacheTimeStamp(System.currentTimeMillis());
				this.cacheResultManager.put(cacheResultKey, obj);
			}
		} else {
			logger.info("--- from cache ---");
		}
		return obj;
	}

	/**
	 * 真正发送请求调用远程方法
	 */
	private Object invokeSendRequestMethod(Method method, Object[] args) throws Exception {
		RpcProtocol<RpcRequest> requestRpcProtocol = new RpcProtocol<>();
		requestRpcProtocol.setHeader(RpcHeaderFactory.getRequestHeader(serializationType, RpcType.REQUEST.getType()));

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

		if (logger.isDebugEnabled()) {
			logger.debug("className: {}, methodName: {}", method.getDeclaringClass().getName(), method.getName());
		}

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

		RPCFuture rpcFuture = this.consumer.sendRequest(requestRpcProtocol, registryService);
		return rpcFuture == null ? null
			: timeout > 0 ? rpcFuture.get(timeout, TimeUnit.MILLISECONDS)
			: rpcFuture.get();
	}

	@Override
	public RPCFuture call(String funcName, Object... args) {
		RpcProtocol<RpcRequest> request = createRequest(this.clazz.getName(), funcName, args);
		RPCFuture rpcFuture = null;
		try {
			rpcFuture = this.consumer.sendRequest(request, registryService);
		} catch (Exception e) {
			logger.error("async call throws exception", e);
		}
		return rpcFuture;
	}

	private RpcProtocol<RpcRequest> createRequest(String className, String methodName, Object[] args) {
		RpcProtocol<RpcRequest> requestRpcProtocol = new RpcProtocol<>();
		requestRpcProtocol.setHeader(RpcHeaderFactory.getRequestHeader(serializationType, RpcType.REQUEST.getType()));

		RpcRequest request = new RpcRequest();
		request.setClassName(className);
		request.setMethodName(methodName);
		request.setParameters(args);
		request.setVersion(this.serviceVersion);
		request.setGroup(this.serviceGroup);

		Class<?>[] parameterTypes = new Class[args.length];
		// Get the right class type
		for (int i = 0; i < args.length; i++) {
			parameterTypes[i] = getClassType(args[i]);
		}
		request.setParameterTypes(parameterTypes);
		requestRpcProtocol.setBody(request);

		if (logger.isDebugEnabled()) {
			logger.debug("className: {}, methodName: {}", className, methodName);
		}

		for (Class<?> parameterType : parameterTypes) {
			logger.debug(parameterType.getName());
		}

		for (Object arg : args) {
			logger.debug(arg.toString());
		}

		return requestRpcProtocol;
	}

	private Class<?> getClassType(Object obj) {
		Class<?> classType = obj.getClass();
		String typeName = classType.getName();
		switch (typeName) {
			case "java.lang.Integer":
				return Integer.TYPE;
			case "java.lang.Long":
				return Long.TYPE;
			case "java.lang.Float":
				return Float.TYPE;
			case "java.lang.Double":
				return Double.TYPE;
			case "java.lang.Character":
				return Character.TYPE;
			case "java.lang.Boolean":
				return Boolean.TYPE;
			case "java.lang.Short":
				return Short.TYPE;
			case "java.lang.Byte":
				return Byte.TYPE;
		}
		return classType;
	}
}
