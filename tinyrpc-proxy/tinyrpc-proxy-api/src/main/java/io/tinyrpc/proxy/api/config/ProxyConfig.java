package io.tinyrpc.proxy.api.config;

import io.tinyrpc.proxy.api.consumer.Consumer;
import io.tinyrpc.registry.api.RegistryService;

import java.io.Serializable;

/**
 * 代理配置类
 *
 * @param <T>
 */
public class ProxyConfig<T> implements Serializable {

	private static final long serialVersionUID = 7374626033168328842L;

	/**
	 * 接口的Class实例
	 */
	private Class<T> clazz;
	/**
	 * 服务版本号
	 */
	private String serviceVersion;
	/**
	 * 服务分组
	 */
	private String serviceGroup;
	/**
	 * 超时时间
	 */
	private long timeout;

	/**
	 * 服务注册接口
	 */
	private RegistryService registryService;

	/**
	 * 消费者接口
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
	 * 缓存结果的时长，单位是毫秒
	 */
	private int resultCacheExpire;

	/**
	 * 反射类型
	 */
	private String reflectType;

	/**
	 * 容错class名称
	 */
	private String fallbackClassName;

	/**
	 * 容错class
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
	/**
	 * 异常监控类型
	 */
	private String exceptionPostProcessorType;

	public ProxyConfig() {
	}

	public ProxyConfig(Class<T> clazz, String serviceVersion, String serviceGroup, String serializationType,
					   long timeout, RegistryService registryService, Consumer consumer,
					   boolean async, boolean oneway,
					   boolean enableResultCache, int resultCacheExpire,
					   String reflectType, String fallbackClassName, Class<?> fallbackClass,
					   boolean enableRateLimiter, String rateLimiterType, int permits, int milliSeconds,
					   String rateLimiterFailStrategy,
					   boolean enableCircuitBreaker, String circuitBreakerType, double totalFailure, int circuitBreakerMilliSeconds,
					   String exceptionPostProcessorType) {
		this.clazz = clazz;
		this.serviceVersion = serviceVersion;
		this.serviceGroup = serviceGroup;
		this.timeout = timeout;
		this.consumer = consumer;
		this.serializationType = serializationType;
		this.async = async;
		this.oneway = oneway;
		this.registryService = registryService;
		this.enableResultCache = enableResultCache;
		this.resultCacheExpire = resultCacheExpire;
		this.reflectType = reflectType;
		this.fallbackClassName = fallbackClassName;
		this.fallbackClass = fallbackClass;
		this.enableRateLimiter = enableRateLimiter;
		this.rateLimiterType = rateLimiterType;
		this.permits = permits;
		this.milliSeconds = milliSeconds;
		this.rateLimiterFailStrategy = rateLimiterFailStrategy;
		this.enableCircuitBreaker = enableCircuitBreaker;
		this.circuitBreakerType = circuitBreakerType;
		this.totalFailure = totalFailure;
		this.circuitBreakerMilliSeconds = circuitBreakerMilliSeconds;
		this.exceptionPostProcessorType = exceptionPostProcessorType;
	}

	public RegistryService getRegistryService() {
		return registryService;
	}

	public void setRegistryService(RegistryService registryService) {
		this.registryService = registryService;
	}

	public Class<T> getClazz() {
		return clazz;
	}

	public void setClazz(Class<T> clazz) {
		this.clazz = clazz;
	}

	public String getServiceVersion() {
		return serviceVersion;
	}

	public void setServiceVersion(String serviceVersion) {
		this.serviceVersion = serviceVersion;
	}

	public String getServiceGroup() {
		return serviceGroup;
	}

	public void setServiceGroup(String serviceGroup) {
		this.serviceGroup = serviceGroup;
	}

	public long getTimeout() {
		return timeout;
	}

	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}

	public Consumer getConsumer() {
		return consumer;
	}

	public void setConsumer(Consumer consumer) {
		this.consumer = consumer;
	}

	public String getSerializationType() {
		return serializationType;
	}

	public void setSerializationType(String serializationType) {
		this.serializationType = serializationType;
	}

	public boolean isAsync() {
		return async;
	}

	public void setAsync(boolean async) {
		this.async = async;
	}

	public boolean isOneway() {
		return oneway;
	}

	public void setOneway(boolean oneway) {
		this.oneway = oneway;
	}

	public boolean isEnableResultCache() {
		return enableResultCache;
	}

	public void setEnableResultCache(boolean enableResultCache) {
		this.enableResultCache = enableResultCache;
	}

	public int getResultCacheExpire() {
		return resultCacheExpire;
	}

	public void setResultCacheExpire(int resultCacheExpire) {
		this.resultCacheExpire = resultCacheExpire;
	}

	public String getReflectType() {
		return reflectType;
	}

	public void setReflectType(String reflectType) {
		this.reflectType = reflectType;
	}

	public String getFallbackClassName() {
		return fallbackClassName;
	}

	public void setFallbackClassName(String fallbackClassName) {
		this.fallbackClassName = fallbackClassName;
	}

	public Class<?> getFallbackClass() {
		return fallbackClass;
	}

	public void setFallbackClass(Class<?> fallbackClass) {
		this.fallbackClass = fallbackClass;
	}

	public boolean isEnableRateLimiter() {
		return enableRateLimiter;
	}

	public void setEnableRateLimiter(boolean enableRateLimiter) {
		this.enableRateLimiter = enableRateLimiter;
	}

	public String getRateLimiterType() {
		return rateLimiterType;
	}

	public void setRateLimiterType(String rateLimiterType) {
		this.rateLimiterType = rateLimiterType;
	}

	public int getPermits() {
		return permits;
	}

	public void setPermits(int permits) {
		this.permits = permits;
	}

	public int getMilliSeconds() {
		return milliSeconds;
	}

	public void setMilliSeconds(int milliSeconds) {
		this.milliSeconds = milliSeconds;
	}

	public String getRateLimiterFailStrategy() {
		return rateLimiterFailStrategy;
	}

	public void setRateLimiterFailStrategy(String rateLimiterFailStrategy) {
		this.rateLimiterFailStrategy = rateLimiterFailStrategy;
	}

	public boolean isEnableCircuitBreaker() {
		return enableCircuitBreaker;
	}

	public void setEnableCircuitBreaker(boolean enableCircuitBreaker) {
		this.enableCircuitBreaker = enableCircuitBreaker;
	}

	public String getCircuitBreakerType() {
		return circuitBreakerType;
	}

	public void setCircuitBreakerType(String circuitBreakerType) {
		this.circuitBreakerType = circuitBreakerType;
	}

	public double getTotalFailure() {
		return totalFailure;
	}

	public void setTotalFailure(double totalFailure) {
		this.totalFailure = totalFailure;
	}

	public int getCircuitBreakerMilliSeconds() {
		return circuitBreakerMilliSeconds;
	}

	public void setCircuitBreakerMilliSeconds(int circuitBreakerMilliSeconds) {
		this.circuitBreakerMilliSeconds = circuitBreakerMilliSeconds;
	}

	public String getExceptionPostProcessorType() {
		return exceptionPostProcessorType;
	}

	public void setExceptionPostProcessorType(String exceptionPostProcessorType) {
		this.exceptionPostProcessorType = exceptionPostProcessorType;
	}
}
