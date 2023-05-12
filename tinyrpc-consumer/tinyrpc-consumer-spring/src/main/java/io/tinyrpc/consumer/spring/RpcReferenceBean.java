package io.tinyrpc.consumer.spring;

import io.tinyrpc.consumer.RpcClient;
import org.springframework.beans.factory.FactoryBean;

/**
 * RpcReferenceBean
 * <p>
 * 覆写了FactoryBean接口的getObject()方法和getObjectType()方法，
 * 在getObject()方法中，直接返回了在init()方法中通过RpcClient对象创建的代理对象，在getObjectType()方法中返回了代理对象的Class类型。
 * </p>
 */
public class RpcReferenceBean implements FactoryBean<Object> {

	/**
	 * 接口类型
	 */
	private Class<?> interfaceClass;
	/**
	 * 版本号
	 */
	private String version;
	/**
	 * 注册中心类型：zookeeper/nacos/apoll/etcd/eureka等
	 */
	private String registryType;

	/**
	 * 负载均衡类型：zkconsistenthash
	 */
	private String loadBalanceType;

	/**
	 * 序列化类型：fst/kryo/protostuff/jdk/hessian2/json
	 */
	private String serializationType;

	/**
	 * 注册中心地址
	 */
	private String registryAddress;
	/**
	 * 超时时间
	 */
	private long timeout;

	/**
	 * 服务分组
	 */
	private String group;
	/**
	 * 是否异步
	 */
	private boolean async;

	/**
	 * 是否单向调用
	 */
	private boolean oneway;
	/**
	 * 代理方式
	 */
	private String proxy;
	/**
	 * 生成的代理对象
	 */
	private Object object;

	/**
	 * 扫描空闲连接时间，默认60秒
	 */
	private int scanNotActiveChannelInterval;

	/**
	 * 心跳检测时间
	 */
	private int heartbeatInterval;

	//重试间隔时间
	private int retryInterval = 1000;

	//重试次数
	private int retryTimes = 3;

	// 是否开启结果缓存
	private boolean enableResultCache;

	// 缓存结果的时长，单位是毫秒
	private int resultCacheExpire;

	/**
	 * 是否开启直连服务
	 */
	private boolean enableDirectServer;

	/**
	 * 直连服务的地址
	 */
	private String directServerUrl;

	/**
	 * 并发线程池核心线程数
	 */
	private int corePoolSize;

	/**
	 * 并发线程池最大线程数
	 */
	private int maximumPoolSize;

	/**
	 * 流控分析类型
	 */
	private String flowType;

	/**
	 * 是否开启缓冲区
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

	private RpcClient rpcClient;

	@Override
	public Object getObject() throws Exception {
		return object;
	}

	@Override
	public Class<?> getObjectType() {
		return interfaceClass;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	public void init() {
		rpcClient = new RpcClient(registryAddress, registryType, loadBalanceType,
			proxy, version, group, serializationType, timeout, async, oneway,
			heartbeatInterval, scanNotActiveChannelInterval,
			retryInterval, retryTimes,
			enableResultCache, resultCacheExpire,
			enableDirectServer, directServerUrl,
			corePoolSize, maximumPoolSize, flowType,
			enableBuffer, bufferSize, reflectType, fallbackClassName,
			enableRateLimiter, rateLimiterType, permits, milliSeconds);
		this.rpcClient.setFallbackClass(fallbackClass);
		this.object = rpcClient.create(interfaceClass);
	}

	public RpcClient getRpcClient() {
		return rpcClient;
	}

	public void setInterfaceClass(Class<?> interfaceClass) {
		this.interfaceClass = interfaceClass;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getRegistryType() {
		return registryType;
	}

	public void setRegistryType(String registryType) {
		this.registryType = registryType;
	}

	public String getLoadBalanceType() {
		return loadBalanceType;
	}

	public void setLoadBalanceType(String loadBalanceType) {
		this.loadBalanceType = loadBalanceType;
	}

	public String getSerializationType() {
		return serializationType;
	}

	public void setSerializationType(String serializationType) {
		this.serializationType = serializationType;
	}

	public String getRegistryAddress() {
		return registryAddress;
	}

	public void setRegistryAddress(String registryAddress) {
		this.registryAddress = registryAddress;
	}

	public long getTimeout() {
		return timeout;
	}

	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
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

	public String getProxy() {
		return proxy;
	}

	public void setProxy(String proxy) {
		this.proxy = proxy;
	}

	public int getScanNotActiveChannelInterval() {
		return scanNotActiveChannelInterval;
	}

	public void setScanNotActiveChannelInterval(int scanNotActiveChannelInterval) {
		this.scanNotActiveChannelInterval = scanNotActiveChannelInterval;
	}

	public int getHeartbeatInterval() {
		return heartbeatInterval;
	}

	public void setHeartbeatInterval(int heartbeatInterval) {
		this.heartbeatInterval = heartbeatInterval;
	}

	public int getRetryInterval() {
		return retryInterval;
	}

	public void setRetryInterval(int retryInterval) {
		this.retryInterval = retryInterval;
	}

	public int getRetryTimes() {
		return retryTimes;
	}

	public void setRetryTimes(int retryTimes) {
		this.retryTimes = retryTimes;
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

	public boolean isEnableDirectServer() {
		return enableDirectServer;
	}

	public void setEnableDirectServer(boolean enableDirectServer) {
		this.enableDirectServer = enableDirectServer;
	}

	public String getDirectServerUrl() {
		return directServerUrl;
	}

	public void setDirectServerUrl(String directServerUrl) {
		this.directServerUrl = directServerUrl;
	}

	public int getCorePoolSize() {
		return corePoolSize;
	}

	public void setCorePoolSize(int corePoolSize) {
		this.corePoolSize = corePoolSize;
	}

	public int getMaximumPoolSize() {
		return maximumPoolSize;
	}

	public void setMaximumPoolSize(int maximumPoolSize) {
		this.maximumPoolSize = maximumPoolSize;
	}

	public String getFlowType() {
		return flowType;
	}

	public void setFlowType(String flowType) {
		this.flowType = flowType;
	}

	public boolean isEnableBuffer() {
		return enableBuffer;
	}

	public void setEnableBuffer(boolean enableBuffer) {
		this.enableBuffer = enableBuffer;
	}

	public int getBufferSize() {
		return bufferSize;
	}

	public void setBufferSize(int bufferSize) {
		this.bufferSize = bufferSize;
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
}
