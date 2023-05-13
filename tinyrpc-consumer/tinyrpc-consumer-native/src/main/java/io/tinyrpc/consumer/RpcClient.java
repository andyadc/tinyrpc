package io.tinyrpc.consumer;

import io.tinyrpc.common.exception.RegistryException;
import io.tinyrpc.common.threadpool.ConcurrentThreadPool;
import io.tinyrpc.consumer.common.RpcConsumer;
import io.tinyrpc.proxy.api.ProxyFactory;
import io.tinyrpc.proxy.api.async.IAsyncObjectProxy;
import io.tinyrpc.proxy.api.config.ProxyConfig;
import io.tinyrpc.proxy.api.object.ObjectProxy;
import io.tinyrpc.registry.api.RegistryService;
import io.tinyrpc.registry.api.config.RegistryConfig;
import io.tinyrpc.spi.loader.ExtensionLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 服务消费客户端
 */
public class RpcClient {

	private static final Logger logger = LoggerFactory.getLogger(RpcClient.class);

	/**
	 * 服务版本
	 */
	private final String serviceVersion;

	/**
	 * 服务分组
	 */
	private final String serviceGroup;

	/**
	 * 序列化类型
	 */
	private final String serializationType;

	/**
	 * 超时时间
	 */
	private final long timeout;

	/**
	 * 是否异步调用
	 */
	private final boolean async;

	/**
	 * 是否单向调用
	 */
	private final boolean oneway;

	/**
	 * 注册服务
	 */
	private final RegistryService registryService;

	/**
	 * 代理
	 */
	private final String proxy;

	//心跳间隔时间，默认30秒
	private final int heartbeatInterval;
	//扫描空闲连接时间，默认60秒
	private final int scanNotActiveChannelInterval;
	/**
	 * 流控分析类型
	 */
	private final String flowType;
	//重试间隔时间
	private int retryInterval = 1000;
	//重试次数
	private int retryTimes = 3;
	// 是否开启结果缓存
	private boolean enableResultCache;
	// 缓存结果的时长，单位是毫秒
	private int resultCacheExpire;
	// 是否开启直连服务
	private boolean enableDirectServer;
	// 直连服务的地址
	private String directServerUrl;
	/**
	 * 并发线程池
	 */
	private ConcurrentThreadPool concurrentThreadPool;
	/**
	 * 是否开启数据缓冲
	 */
	private boolean enableBuffer;
	/**
	 * 缓冲区大小
	 */
	private int bufferSize;

	/**
	 * 反射类型
	 */
	private String reflectType;
	/**
	 * 容错类Class名称
	 */
	private String fallbackClassName;
	/**
	 * 容错类
	 */
	private Class<?> fallbackClass;
	/**
	 * 是否开启限流
	 */
	private boolean enableRateLimiter;
	/**
	 * 限流类型
	 */
	private String rateLimiterType;
	/**
	 * 在milliSeconds毫秒内最多能够通过的请求个数
	 */
	private int permits;
	/**
	 * 毫秒数
	 */
	private int milliSeconds;

	/**
	 * 当限流失败时的处理策略
	 */
	private String rateLimiterFailStrategy;

	//是否开启熔断策略
	private boolean enableCircuitBreaker;
	//熔断规则标识
	private String circuitBreakerType;
	//在fusingMilliSeconds毫秒内触发熔断操作的上限值
	private double totalFailure;
	//熔断的毫秒时长
	private int circuitBreakerMilliSeconds;

	public RpcClient(String registryAddress, String registryType, String registryLoadBalanceType,
					 String proxy, String serviceVersion, String serviceGroup, String serializationType, long timeout,
					 boolean async, boolean oneway,
					 int heartbeatInterval, int scanNotActiveChannelInterval,
					 int retryInterval, int retryTimes,
					 boolean enableResultCache, int resultCacheExpire,
					 boolean enableDirectServer, String directServerUrl,
					 int corePoolSize, int maximumPoolSize, String flowType,
					 boolean enableBuffer, int bufferSize,
					 String reflectType, String fallbackClassName,
					 boolean enableRateLimiter, String rateLimiterType, int permits, int milliSeconds,
					 String rateLimiterFailStrategy,
					 boolean enableCircuitBreaker, String circuitBreakerType, double totalFailure, int circuitBreakerMilliSeconds) {
		this.serviceVersion = serviceVersion;
		this.timeout = timeout;
		this.serviceGroup = serviceGroup;
		this.serializationType = serializationType;
		this.async = async;
		this.oneway = oneway;
		this.proxy = proxy;
		this.heartbeatInterval = heartbeatInterval;
		this.scanNotActiveChannelInterval = scanNotActiveChannelInterval;
		this.retryInterval = retryInterval;
		this.retryTimes = retryTimes;
		this.registryService = this.getRegistryService(registryAddress, registryType, registryLoadBalanceType);
		this.enableResultCache = enableResultCache;
		this.resultCacheExpire = resultCacheExpire;
		this.enableDirectServer = enableDirectServer;
		this.directServerUrl = directServerUrl;
		this.concurrentThreadPool = ConcurrentThreadPool.getInstance(corePoolSize, maximumPoolSize);
		this.flowType = flowType;
		this.enableBuffer = enableBuffer;
		this.bufferSize = bufferSize;
		this.reflectType = reflectType;
		this.fallbackClassName = fallbackClassName;
		this.enableRateLimiter = enableRateLimiter;
		this.rateLimiterType = rateLimiterType;
		this.permits = permits;
		this.milliSeconds = milliSeconds;
		this.rateLimiterFailStrategy = rateLimiterFailStrategy;
		this.enableCircuitBreaker = enableCircuitBreaker;
		this.circuitBreakerType = circuitBreakerType;
		this.totalFailure = totalFailure;
		this.circuitBreakerMilliSeconds = circuitBreakerMilliSeconds;
	}

	public void setFallbackClass(Class<?> fallbackClass) {
		this.fallbackClass = fallbackClass;
	}

	private RegistryService getRegistryService(String registryAddress, String registryType, String registryLoadBalanceType) {
		if (registryType == null) {
			throw new IllegalArgumentException("registry type is null");
		}
		RegistryService registryService = ExtensionLoader.getExtension(RegistryService.class, registryType);
		try {
			registryService.init(new RegistryConfig(registryAddress, registryType, registryLoadBalanceType));
		} catch (Exception e) {
			logger.error("RpcClient init registry service error", e);
			throw new RegistryException(e.getMessage(), e);
		}
		return registryService;
	}

	public <T> T create(Class<T> interfaceClass) {
		ProxyFactory proxyFactory = ExtensionLoader.getExtension(ProxyFactory.class, proxy);
		proxyFactory.init(new ProxyConfig<>(interfaceClass, serviceVersion, serviceGroup, serializationType, timeout,
			registryService,
			RpcConsumer.getInstance()
				.setHeartbeatInterval(heartbeatInterval)
				.setScanNotActiveChannelInterval(scanNotActiveChannelInterval)
				.setRetryInterval(retryInterval)
				.setRetryTimes(retryTimes)
				.setEnableDirectServer(enableDirectServer)
				.setDirectServerUrl(directServerUrl)
				.setConcurrentThreadPool(concurrentThreadPool)
				.setFlowPostProcessor(flowType)
				.setEnableBuffer(enableBuffer)
				.setBufferSize(bufferSize)
				.buildNettyGroup()
				.buildConnection(registryService),
			async, oneway, enableResultCache, resultCacheExpire,
			reflectType, fallbackClassName, fallbackClass,
			enableRateLimiter, rateLimiterType, permits, milliSeconds,
			rateLimiterFailStrategy,
			enableCircuitBreaker, circuitBreakerType, totalFailure, circuitBreakerMilliSeconds));
		return proxyFactory.getProxy(interfaceClass);
	}

	public <T> IAsyncObjectProxy createAsync(Class<T> interfaceClass) {
		return new ObjectProxy<>(interfaceClass, serviceVersion, serviceGroup, serializationType, timeout,
			registryService,
			RpcConsumer.getInstance()
				.setHeartbeatInterval(heartbeatInterval)
				.setScanNotActiveChannelInterval(scanNotActiveChannelInterval)
				.setRetryInterval(retryInterval)
				.setRetryTimes(retryTimes)
				.setEnableDirectServer(enableDirectServer)
				.setDirectServerUrl(directServerUrl)
				.setConcurrentThreadPool(concurrentThreadPool)
				.setFlowPostProcessor(flowType)
				.setEnableBuffer(enableBuffer)
				.setBufferSize(bufferSize)
				.buildNettyGroup()
				.buildConnection(registryService),
			async, oneway, enableResultCache, resultCacheExpire,
			reflectType, fallbackClassName, fallbackClass,
			enableRateLimiter, rateLimiterType, permits, milliSeconds,
			rateLimiterFailStrategy,
			enableCircuitBreaker, circuitBreakerType, totalFailure, circuitBreakerMilliSeconds);
	}

	public void shutdown() {
		RpcConsumer.getInstance().close();
	}
}
