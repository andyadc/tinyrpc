package io.tinyrpc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static io.tinyrpc.constant.RpcConstants.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface RpcReference {

	/**
	 * 版本号
	 */
	String version() default RPC_COMMON_DEFAULT_VERSION;

	/**
	 * 注册中心类型, 目前的类型包含：zookeeper、nacos、etcd、consul
	 */
	String registryType() default REGISTRY_CENTER_ZOOKEEPER;

	/**
	 * 注册地址
	 */
	String registryAddress() default RPC_REFERENCE_DEFAULT_REGISTRYADDRESS;

	/**
	 * 负载均衡类型，默认基于ZK的一致性Hash
	 */
	String loadBalanceType() default RPC_REFERENCE_DEFAULT_LOADBALANCETYPE;

	/**
	 * 序列化类型，目前的类型包含：protostuff、kryo、json、jdk、hessian2、fst
	 */
	String serializationType() default RPC_REFERENCE_DEFAULT_SERIALIZATIONTYPE;

	/**
	 * 超时时间，默认5s
	 */
	long timeout() default RPC_REFERENCE_DEFAULT_TIMEOUT;

	/**
	 * 是否异步执行
	 */
	boolean async() default false;

	/**
	 * 是否单向调用
	 */
	boolean oneway() default false;

	/**
	 * 代理的类型，jdk：jdk代理， javassist: javassist代理, cglib: cglib代理
	 */
	String proxy() default RPC_REFERENCE_DEFAULT_PROXY;

	/**
	 * 服务分组，默认为空
	 */
	String group() default "";

	/**
	 * 心跳间隔时间，默认30秒
	 */
	int heartbeatInterval() default RPC_COMMON_DEFAULT_HEARTBEATINTERVAL;

	/**
	 * 扫描空闲连接间隔时间，默认60秒
	 */
	int scanNotActiveChannelInterval() default RPC_COMMON_DEFAULT_SCANNOTACTIVECHANNELINTERVAL;

	/**
	 * 重试间隔时间
	 */
	int retryInterval() default RPC_REFERENCE_DEFAULT_RETRYINTERVAL;

	/**
	 * 重试间隔时间
	 */
	int retryTimes() default RPC_REFERENCE_DEFAULT_RETRYTIMES;

	/**
	 * 是否开启结果缓存
	 */
	boolean enableResultCache() default false;

	/**
	 * 缓存结果的时长，单位是毫秒
	 */
	int resultCacheExpire() default RPC_SCAN_RESULT_CACHE_EXPIRE;

	/**
	 * 是否开启直连服务
	 */
	boolean enableDirectServer() default false;

	/**
	 * 直连服务的地址
	 */
	String directServerUrl() default "";

	/**
	 * 默认并发线程池核心线程数
	 */
	int corePoolSize() default DEFAULT_CORE_POOL_SIZE;

	/**
	 * 默认并发线程池最大线程数
	 */
	int maximumPoolSize() default DEFAULT_MAXI_NUM_POOL_SIZE;

	/**
	 * 流控分析类型
	 */
	String flowType() default FLOW_POST_PROCESSOR_PRINT;
}
