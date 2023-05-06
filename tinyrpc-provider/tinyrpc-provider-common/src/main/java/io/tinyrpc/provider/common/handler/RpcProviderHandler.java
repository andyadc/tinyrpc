package io.tinyrpc.provider.common.handler;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;
import io.tinyrpc.cache.result.CacheResultKey;
import io.tinyrpc.cache.result.CacheResultManager;
import io.tinyrpc.constant.RpcConstants;
import io.tinyrpc.common.exception.RpcException;
import io.tinyrpc.common.helper.RpcServiceHelper;
import io.tinyrpc.common.threadpool.ConcurrentThreadPool;
import io.tinyrpc.common.utils.JsonUtils;
import io.tinyrpc.protocol.RpcProtocol;
import io.tinyrpc.protocol.enumeration.RpcStatus;
import io.tinyrpc.protocol.enumeration.RpcType;
import io.tinyrpc.protocol.header.RpcHeader;
import io.tinyrpc.protocol.request.RpcRequest;
import io.tinyrpc.protocol.response.RpcResponse;
import io.tinyrpc.provider.common.cache.ProviderChannelCache;
import io.tinyrpc.reflect.api.ReflectInvoker;
import io.tinyrpc.spi.loader.ExtensionLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * RPC服务提供者的 Handler处理类
 */
public class RpcProviderHandler extends SimpleChannelInboundHandler<RpcProtocol<RpcRequest>> {

	private final Logger logger = LoggerFactory.getLogger(RpcProviderHandler.class);

	/**
	 * 存储服务提供者中被@RpcService注解标注的类的对象
	 * key为：serviceName#serviceVersion#group
	 * value为：@RpcService注解标注的类的对象
	 */
	private final Map<String, Object> handlerMap;

	/**
	 * 反射调用真实方法的SPI接口
	 */
	private final ReflectInvoker reflectInvoker;

	/**
	 * 是否启用结果缓存
	 */
	private final boolean enableResultCache;

	/**
	 * 结果缓存管理器
	 */
	private final CacheResultManager<RpcProtocol<RpcResponse>> cacheResultManager;

	/**
	 * 线程池
	 */
	private final ConcurrentThreadPool concurrentThreadPool;

	public RpcProviderHandler(String reflectType, boolean enableResultCache, int resultCacheExpire, int corePoolSize, int maximumPoolSize, Map<String, Object> handlerMap) {
		this.handlerMap = handlerMap;
		this.reflectInvoker = ExtensionLoader.getExtension(ReflectInvoker.class, reflectType);
		this.enableResultCache = enableResultCache;
		if (resultCacheExpire <= 0) {
			resultCacheExpire = RpcConstants.RPC_SCAN_RESULT_CACHE_EXPIRE;
		}
		this.cacheResultManager = CacheResultManager.getInstance(resultCacheExpire, enableResultCache);
		this.concurrentThreadPool = ConcurrentThreadPool.getInstance(corePoolSize, maximumPoolSize);
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, RpcProtocol<RpcRequest> protocol) {

		// 异步 ServerThreadPool.submit(() -> {})
		concurrentThreadPool.submit(() -> {
			RpcProtocol<RpcResponse> responseRpcProtocol = handleMessage(protocol, ctx.channel());

			ctx.writeAndFlush(responseRpcProtocol).addListener(new ChannelFutureListener() {
				@Override
				public void operationComplete(ChannelFuture channelFuture) {
					logger.info("Sent response for request " + protocol.getHeader().getRequestId());
				}
			});
		});
	}

	/**
	 * 处理消息
	 */
	private RpcProtocol<RpcResponse> handleMessage(RpcProtocol<RpcRequest> protocol, Channel channel) {
		logger.info("Provider received request ===>>> {}", JsonUtils.toJSONString(protocol));
		RpcProtocol<RpcResponse> responseRpcProtocol = null;
		RpcHeader header = protocol.getHeader();

		if (header.getMsgType() == (byte) RpcType.REQUEST.getType()) { // 请求消息
//			responseRpcProtocol = handleRequestMessage(protocol, header);
			responseRpcProtocol = handleRequestMessageWithCache(protocol, header);
		} else if (header.getMsgType() == (byte) RpcType.HEARTBEAT_FROM_CONSUMER.getType()) { // 接收到服务消费者发送的心跳消息
			responseRpcProtocol = handleHeartbeatMessage(protocol, header);
		} else if (header.getMsgType() == (byte) RpcType.HEARTBEAT_TO_PROVIDER.getType()) { // 接收到服务消费者响应的心跳消息
			handleHeartbeatMessageToProvider(protocol, channel);
		}

		return responseRpcProtocol;
	}

	/**
	 * 处理请求消息
	 */
	private RpcProtocol<RpcResponse> handleRequestMessage(RpcProtocol<RpcRequest> protocol, RpcHeader header) {
		header.setMsgType((byte) RpcType.RESPONSE.getType());
		RpcRequest request = protocol.getBody();

		RpcProtocol<RpcResponse> responseRpcProtocol = new RpcProtocol<>();
		RpcResponse response = new RpcResponse();
		try {
			Object result = handle(request);
			response.setResult(result);
			response.setAsync(request.getAsync());
			response.setOneway(request.getOneway());
			header.setStatus((byte) RpcStatus.SUCCESS.getCode());
		} catch (Throwable t) {
			response.setError(t.toString());
			header.setStatus((byte) RpcStatus.FAIL.getCode());
			logger.error("RPC Server handle request error", t);
		}

		responseRpcProtocol.setHeader(header);
		responseRpcProtocol.setBody(response);
		return responseRpcProtocol;
	}

	/**
	 * 处理服务消费者响应的心跳消息
	 */
	private void handleHeartbeatMessageToProvider(RpcProtocol<RpcRequest> protocol, Channel channel) {
		logger.info("receive service consumer heartbeat message, the consumer is: {}, the heartbeat message is: {}",
			channel.remoteAddress(), protocol.getBody().getParameters()[0]);
	}

	/**
	 * 处理心跳消息
	 */
	private RpcProtocol<RpcResponse> handleHeartbeatMessage(RpcProtocol<RpcRequest> protocol, RpcHeader header) {
		header.setMsgType((byte) RpcType.HEARTBEAT_TO_CONSUMER.getType());
		RpcRequest request = protocol.getBody();
		RpcProtocol<RpcResponse> responseRpcProtocol = new RpcProtocol<>();
		RpcResponse response = new RpcResponse();
		response.setResult(RpcConstants.HEARTBEAT_PONG);
		response.setAsync(request.getAsync());
		response.setOneway(request.getOneway());
		header.setStatus((byte) RpcStatus.SUCCESS.getCode());

		responseRpcProtocol.setHeader(header);
		responseRpcProtocol.setBody(response);
		return responseRpcProtocol;
	}

	private Object handle(RpcRequest request) throws Throwable {
		String serviceKey = RpcServiceHelper.buildServiceKey(request.getClassName(), request.getVersion(), request.getGroup());
		Object serviceBean = handlerMap.get(serviceKey);
		if (serviceBean == null) {
			throw new RpcException(String.format("Service not exist: %s:%s", request.getClassName(), request.getMethodName()));
		}

		Class<?> serviceClass = serviceBean.getClass();
		String methodName = request.getMethodName();
		Class<?>[] parameterTypes = request.getParameterTypes();
		Object[] parameters = request.getParameters();

		logger.info(serviceClass.getName());
		logger.info(methodName);

		if (parameterTypes != null && parameterTypes.length > 0) {
			for (Class<?> parameterType : parameterTypes) {
				logger.info(parameterType.getName());
			}
		}

		if (parameters != null && parameters.length > 0) {
			for (Object parameter : parameters) {
				logger.info(parameter.toString());
			}
		}

		return this.reflectInvoker.invokeMethod(serviceBean, serviceClass, methodName, parameterTypes, parameters);
	}

	/**
	 * 结合缓存处理结果
	 */
	private RpcProtocol<RpcResponse> handleRequestMessageWithCache(RpcProtocol<RpcRequest> protocol, RpcHeader header) {
		header.setMsgType((byte) RpcType.RESPONSE.getType());
		if (enableResultCache) {
			return handleRequestMessageCache(protocol, header);
		}
		return handleRequestMessage(protocol, header);
	}

	/**
	 * 处理缓存
	 */
	private RpcProtocol<RpcResponse> handleRequestMessageCache(RpcProtocol<RpcRequest> protocol, RpcHeader header) {
		RpcRequest request = protocol.getBody();
		CacheResultKey cacheKey = new CacheResultKey(request.getClassName(), request.getMethodName(), request.getParameterTypes(), request.getParameters(), request.getVersion(), request.getGroup());
		RpcProtocol<RpcResponse> responseRpcProtocol = cacheResultManager.get(cacheKey);
		if (responseRpcProtocol == null) {
			logger.info("--- cache is null ---");
			responseRpcProtocol = handleRequestMessage(protocol, header);
			// 设置保存的时间
			cacheKey.setCacheTimeStamp(System.currentTimeMillis());
			cacheResultManager.put(cacheKey, responseRpcProtocol);
		} else {
			logger.info("--- from cache ---");
		}
		return responseRpcProtocol;
	}

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		// 如果是IdleStateEvent事件
		if (evt instanceof IdleStateEvent) {
			Channel channel = ctx.channel();
			try {
				logger.info("IdleStateEvent triggered, close channel " + channel.remoteAddress());
				channel.close();
			} finally {
				channel.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
			}
		}
		super.userEventTriggered(ctx, evt);
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		super.channelActive(ctx);

		ProviderChannelCache.add(ctx.channel());
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		super.channelInactive(ctx);

		ProviderChannelCache.remove(ctx.channel());
	}

	@Override
	public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
		super.channelUnregistered(ctx);

		ProviderChannelCache.remove(ctx.channel());
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		logger.error("Server caught exception.", cause);
		ProviderChannelCache.remove(ctx.channel());
		ctx.close();
	}
}
