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

	public ProxyConfig() {
	}

	public ProxyConfig(Class<T> clazz, String serviceVersion, String serviceGroup, String serializationType,
					   long timeout, RegistryService registryService, Consumer consumer,
					   boolean async, boolean oneway,
					   boolean enableResultCache, int resultCacheExpire) {
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
}
