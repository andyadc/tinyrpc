package io.tinyrpc.provider.common.handler;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;
import io.tinyrpc.buffer.cache.BufferCacheManager;
import io.tinyrpc.buffer.object.BufferObject;
import io.tinyrpc.cache.result.CacheResultKey;
import io.tinyrpc.cache.result.CacheResultManager;
import io.tinyrpc.circuitbreaker.api.CircuitBreakerInvoker;
import io.tinyrpc.common.exception.RpcException;
import io.tinyrpc.common.helper.RpcServiceHelper;
import io.tinyrpc.common.threadpool.BufferCacheThreadPool;
import io.tinyrpc.common.threadpool.ConcurrentThreadPool;
import io.tinyrpc.common.utils.JsonUtil;
import io.tinyrpc.common.utils.StringUtil;
import io.tinyrpc.connection.manager.ConnectionManager;
import io.tinyrpc.constant.RpcConstants;
import io.tinyrpc.protocol.RpcProtocol;
import io.tinyrpc.protocol.enumeration.RpcStatus;
import io.tinyrpc.protocol.enumeration.RpcType;
import io.tinyrpc.protocol.header.RpcHeader;
import io.tinyrpc.protocol.request.RpcRequest;
import io.tinyrpc.protocol.response.RpcResponse;
import io.tinyrpc.provider.common.cache.ProviderChannelCache;
import io.tinyrpc.ratelimiter.api.RateLimiterInvoker;
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

	/**
	 * 连接管理器
	 */
	private final ConnectionManager connectionManager;

	/**
	 * 是否开启缓冲区
	 */
	private final boolean enableBuffer;

	/**
	 * 缓冲区管理器
	 */
	private BufferCacheManager<BufferObject<RpcRequest>> bufferCacheManager;

	/**
	 * 是否开启限流
	 */
	private boolean enableRateLimiter;

	/**
	 * 限流SPI接口
	 */
	private RateLimiterInvoker rateLimiterInvoker;

	/**
	 * 当限流失败时的处理策略
	 */
	private String rateLimiterFailStrategy;

	/**
	 * 是否开启熔断
	 */
	private boolean enableCircuitBreaker;

	/**
	 * 熔断SPI接口
	 */
	private CircuitBreakerInvoker circuitBreakerInvoker;

	public RpcProviderHandler(String reflectType, boolean enableResultCache, int resultCacheExpire,
							  int corePoolSize, int maximumPoolSize,
							  int maxConnections, String disuseStrategyType,
							  boolean enableBuffer, int bufferSize,
							  boolean enableRateLimiter, String rateLimiterType, int permits, int milliSeconds,
							  String rateLimiterFailStrategy,
							  boolean enableCircuitBreaker, String circuitBreakerType, double totalFailure, int circuitBreakerMilliSeconds,
							  Map<String, Object> handlerMap) {
		this.handlerMap = handlerMap;
		this.reflectInvoker = ExtensionLoader.getExtension(ReflectInvoker.class, reflectType);
		this.enableResultCache = enableResultCache;
		if (resultCacheExpire <= 0) {
			resultCacheExpire = RpcConstants.RPC_SCAN_RESULT_CACHE_EXPIRE;
		}
		this.cacheResultManager = CacheResultManager.getInstance(resultCacheExpire, enableResultCache);
		this.concurrentThreadPool = ConcurrentThreadPool.getInstance(corePoolSize, maximumPoolSize);
		this.connectionManager = ConnectionManager.getInstance(maxConnections, disuseStrategyType);

		this.enableBuffer = enableBuffer;
		if (enableBuffer) {
			logger.info("--- enable buffer ---");
			this.bufferCacheManager = BufferCacheManager.getInstance(bufferSize);
			BufferCacheThreadPool.submit(this::consumerBufferCache);
		}
		this.enableRateLimiter = enableRateLimiter;
		this.initRateLimiter(rateLimiterType, permits, milliSeconds);
		if (StringUtil.isEmpty(rateLimiterFailStrategy)) {
			rateLimiterFailStrategy = RpcConstants.RATE_LIMILTER_FAIL_STRATEGY_DIRECT;
		}
		this.rateLimiterFailStrategy = rateLimiterFailStrategy;
		this.enableCircuitBreaker = enableCircuitBreaker;
		this.initCircuitBreaker(circuitBreakerType, totalFailure, circuitBreakerMilliSeconds);
	}

	/**
	 * 初始化熔断SPI接口
	 */
	private void initCircuitBreaker(String circuitBreakerType, double totalFailure, int circuitBreakerMilliSeconds) {
		if (enableCircuitBreaker) {
			circuitBreakerType = StringUtil.isEmpty(circuitBreakerType) ? RpcConstants.DEFAULT_CIRCUIT_BREAKER_INVOKER : circuitBreakerType;
			this.circuitBreakerInvoker = ExtensionLoader.getExtension(CircuitBreakerInvoker.class, circuitBreakerType);
			this.circuitBreakerInvoker.init(totalFailure, circuitBreakerMilliSeconds);
		}
	}

	/**
	 * 初始化限流器
	 */
	private void initRateLimiter(String rateLimiterType, int permits, int milliSeconds) {
		if (enableRateLimiter) {
			rateLimiterType = StringUtil.isEmpty(rateLimiterType) ? RpcConstants.DEFAULT_RATELIMITER_INVOKER : rateLimiterType;
			this.rateLimiterInvoker = ExtensionLoader.getExtension(RateLimiterInvoker.class, rateLimiterType);
			this.rateLimiterInvoker.init(permits, milliSeconds);
		}
	}

	/**
	 * 消费缓冲区的数据
	 */
	private void consumerBufferCache() {
		// 不断消息缓冲区的数据
		while (true) {
			BufferObject<RpcRequest> bufferObject = this.bufferCacheManager.take();
			if (bufferObject != null) {
				ChannelHandlerContext ctx = bufferObject.getCtx();
				RpcProtocol<RpcRequest> protocol = bufferObject.getProtocol();
				RpcHeader header = protocol.getHeader();
				RpcProtocol<RpcResponse> responseRpcProtocol = handleRequestMessageWithCache(protocol, header);
				this.writeAndFlush(header.getRequestId(), ctx, responseRpcProtocol);
			}
		}
	}

	/**
	 * 缓冲数据
	 */
	private void bufferRequest(ChannelHandlerContext ctx, RpcProtocol<RpcRequest> protocol) {
		RpcHeader header = protocol.getHeader();
		//接收到服务消费者发送的心跳消息
		if (header.getMsgType() == (byte) RpcType.HEARTBEAT_FROM_CONSUMER.getType()) {
			RpcProtocol<RpcResponse> responseRpcProtocol = handleHeartbeatMessage(protocol, header);
			this.writeAndFlush(protocol.getHeader().getRequestId(), ctx, responseRpcProtocol);
		} else if (header.getMsgType() == (byte) RpcType.HEARTBEAT_TO_PROVIDER.getType()) {  //接收到服务消费者响应的心跳消息
			handleHeartbeatMessageToProvider(protocol, ctx.channel());
		} else if (header.getMsgType() == (byte) RpcType.REQUEST.getType()) { //请求消息
			this.bufferCacheManager.put(new BufferObject<>(ctx, protocol));
		}
	}

	/**
	 * 提交请求
	 */
	private void submitRequest(ChannelHandlerContext ctx, RpcProtocol<RpcRequest> protocol) {
		RpcProtocol<RpcResponse> responseRpcProtocol = handleMessage(protocol, ctx.channel());
		writeAndFlush(protocol.getHeader().getRequestId(), ctx, responseRpcProtocol);
	}

	/**
	 * 向服务消费者写回数据
	 */
	private void writeAndFlush(long requestId, ChannelHandlerContext ctx, RpcProtocol<RpcResponse> responseRpcProtocol) {
		ctx.writeAndFlush(responseRpcProtocol).addListener(new ChannelFutureListener() {
			@Override
			public void operationComplete(ChannelFuture channelFuture) throws Exception {
				logger.debug("Send response for request " + requestId);
			}
		});
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, RpcProtocol<RpcRequest> protocol) {

		// 异步 ServerThreadPool.submit(() -> {})
		concurrentThreadPool.submit(() -> {
			connectionManager.update(ctx.channel());
			if (enableBuffer) {
				bufferRequest(ctx, protocol);
			} else {
				submitRequest(ctx, protocol);
			}
		});
	}

	/**
	 * 处理消息
	 */
	private RpcProtocol<RpcResponse> handleMessage(RpcProtocol<RpcRequest> protocol, Channel channel) {
		logger.info("Provider received request ===>>> {}", JsonUtil.toJSONString(protocol));
		RpcProtocol<RpcResponse> responseRpcProtocol = null;
		RpcHeader header = protocol.getHeader();

		if (header.getMsgType() == (byte) RpcType.REQUEST.getType()) { // 请求消息
//			responseRpcProtocol = handleRequestMessage(protocol, header);
			responseRpcProtocol = handleRequestMessageWithCacheAndRateLimiter(protocol, header);
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

		if (logger.isDebugEnabled()) {
			logger.debug("serviceClassName: {}, methodName: {}", serviceClass.getName(), methodName);
		}

		return this.reflectInvoker.invokeMethod(serviceBean, serviceClass, methodName, parameterTypes, parameters);
	}

	/**
	 * 结合服务熔断请求方法
	 */
	private RpcProtocol<RpcResponse> handlerRequestMessageWithCircuitBreaker(RpcProtocol<RpcRequest> protocol, RpcHeader header) {
		if (enableCircuitBreaker) {
			return handleCircuitBreakerRequestMessage(protocol, header);
		} else {
			return handleRequestMessage(protocol, header);
		}
	}

	/**
	 * 开启熔断策略时调用的方法
	 */
	private RpcProtocol<RpcResponse> handleCircuitBreakerRequestMessage(RpcProtocol<RpcRequest> protocol, RpcHeader header) {
		//如果触发了熔断的规则，则直接返回降级处理数据
		if (circuitBreakerInvoker.invokeCircuitBreakerStrategy()) {
			return handleFallbackMessage(protocol);
		}
		//请求计数加1
		circuitBreakerInvoker.incrementCount();

		//调用handlerRequestMessage()方法获取数据
		RpcProtocol<RpcResponse> responseRpcProtocol = handleRequestMessage(protocol, header);
		//如果是调用失败，则失败次数加1
		if (responseRpcProtocol.getHeader().getStatus() == (byte) RpcStatus.FAIL.getCode()) {
			circuitBreakerInvoker.incrementFailureCount();
		}
		return responseRpcProtocol;
	}

	/**
	 * 带有限流模式提交请求信息
	 */
	private RpcProtocol<RpcResponse> handleRequestMessageWithCacheAndRateLimiter(RpcProtocol<RpcRequest> protocol, RpcHeader header) {
		RpcProtocol<RpcResponse> responseRpcProtocol = null;
		long start = System.currentTimeMillis();
		if (enableRateLimiter) {
			if (rateLimiterInvoker.tryAcquire()) {
				logger.info(">>> pass RequestId: {}", header.getRequestId());
				try {
					responseRpcProtocol = this.handleRequestMessageWithCache(protocol, header);
				} finally {
					rateLimiterInvoker.release();
				}
			} else {
				logger.info(">>> reject RequestId: {}", header.getRequestId());
				responseRpcProtocol = invokeFailRateLimiterMethod(protocol, header);
			}
		} else {
			responseRpcProtocol = this.handleRequestMessageWithCache(protocol, header);
		}
		return responseRpcProtocol;
	}

	/**
	 * 执行限流失败时的处理逻辑
	 */
	private RpcProtocol<RpcResponse> invokeFailRateLimiterMethod(RpcProtocol<RpcRequest> protocol, RpcHeader header) {
		logger.info("execute {} fail rate limiter strategy...", rateLimiterFailStrategy);
		switch (rateLimiterFailStrategy) {
			case RpcConstants.RATE_LIMILTER_FAIL_STRATEGY_EXCEPTION:
			case RpcConstants.RATE_LIMILTER_FAIL_STRATEGY_FALLBACK:
				return this.handleFallbackMessage(protocol);
			case RpcConstants.RATE_LIMILTER_FAIL_STRATEGY_DIRECT:
				return this.handleRequestMessageWithCache(protocol, header);
		}
		return this.handleRequestMessageWithCache(protocol, header);
	}

	/**
	 * 处理降级（容错）消息
	 * 当rateLimiterFailStrategy的值为抛出异常和降级处理时，执行的逻辑，就是将消息头中的状态设置为失败的状态，并且将消息类型设置为响应类型的消息
	 */
	private RpcProtocol<RpcResponse> handleFallbackMessage(RpcProtocol<RpcRequest> protocol) {
		RpcProtocol<RpcResponse> responseRpcProtocol = new RpcProtocol<>();
		RpcHeader header = protocol.getHeader();
		header.setStatus((byte) RpcStatus.FAIL.getCode());
		header.setMsgType((byte) RpcType.RESPONSE.getType());
		responseRpcProtocol.setHeader(header);

		RpcResponse response = new RpcResponse();
		response.setError("provider execute ratelimiter fallback strategy...");
		responseRpcProtocol.setBody(response);

		return responseRpcProtocol;
	}

	/**
	 * 结合缓存处理结果
	 */
	private RpcProtocol<RpcResponse> handleRequestMessageWithCache(RpcProtocol<RpcRequest> protocol, RpcHeader header) {
		header.setMsgType((byte) RpcType.RESPONSE.getType());
		if (enableResultCache) {
			return handleRequestMessageCache(protocol, header);
		}
		return handleCircuitBreakerRequestMessage(protocol, header);
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
			responseRpcProtocol = handleCircuitBreakerRequestMessage(protocol, header);
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
				connectionManager.remove(ctx.channel());
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

		Channel channel = ctx.channel();
		ProviderChannelCache.add(channel);
		connectionManager.add(channel);
		if (logger.isDebugEnabled()) {
			logger.debug("channelActive: {}", channel.id().asLongText());
		}
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		super.channelInactive(ctx);

		Channel channel = ctx.channel();
		ProviderChannelCache.remove(channel);
		connectionManager.remove(channel);
		if (logger.isDebugEnabled()) {
			logger.debug("channelInactive: {}", channel.id().asLongText());
		}
	}

	@Override
	public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
		super.channelUnregistered(ctx);

		Channel channel = ctx.channel();
		ProviderChannelCache.remove(channel);
		connectionManager.remove(channel);
		if (logger.isDebugEnabled()) {
			logger.debug("channelUnregistered: {}", channel.id().asLongText());
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		logger.error("Server caught exception.", cause);

		Channel channel = ctx.channel();
		ProviderChannelCache.remove(channel);
		connectionManager.remove(channel);
		if (logger.isDebugEnabled()) {
			logger.debug("exceptionCaught: {}", channel.id().asLongText());
		}

		ctx.close();
	}
}
