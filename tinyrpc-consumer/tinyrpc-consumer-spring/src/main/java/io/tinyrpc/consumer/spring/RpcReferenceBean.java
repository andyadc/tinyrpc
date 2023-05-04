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

	public void init() throws Exception {
		RpcClient rpcClient = new RpcClient(registryAddress, registryType, loadBalanceType, proxy, version, group, serializationType, timeout, async, oneway, heartbeatInterval, scanNotActiveChannelInterval, retryInterval, retryTimes);
		this.object = rpcClient.create(interfaceClass);
	}

	public void setInterfaceClass(Class<?> interfaceClass) {
		this.interfaceClass = interfaceClass;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public void setRegistryType(String registryType) {
		this.registryType = registryType;
	}

	public void setLoadBalanceType(String loadBalanceType) {
		this.loadBalanceType = loadBalanceType;
	}

	public void setSerializationType(String serializationType) {
		this.serializationType = serializationType;
	}

	public void setRegistryAddress(String registryAddress) {
		this.registryAddress = registryAddress;
	}

	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public void setAsync(boolean async) {
		this.async = async;
	}

	public void setOneway(boolean oneway) {
		this.oneway = oneway;
	}

	public void setProxy(String proxy) {
		this.proxy = proxy;
	}

	public void setScanNotActiveChannelInterval(int scanNotActiveChannelInterval) {
		this.scanNotActiveChannelInterval = scanNotActiveChannelInterval;
	}

	public void setHeartbeatInterval(int heartbeatInterval) {
		this.heartbeatInterval = heartbeatInterval;
	}

	public void setRetryInterval(int retryInterval) {
		this.retryInterval = retryInterval;
	}

	public void setRetryTimes(int retryTimes) {
		this.retryTimes = retryTimes;
	}
}
