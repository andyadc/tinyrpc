package io.tinyrpc.spring.boot.consumer.starter;

import io.tinyrpc.common.utils.StringUtil;
import io.tinyrpc.constant.RpcConstants;
import io.tinyrpc.consumer.RpcClient;
import io.tinyrpc.consumer.spring.RpcReferenceBean;
import io.tinyrpc.consumer.spring.context.RpcConsumerSpringContext;
import io.tinyrpc.spring.boot.consumer.config.SpringBootConsumerConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * RPC 服务提供者的自动配置类
 */
@Configuration
@EnableConfigurationProperties
public class SpringBootConsumerAutoConfiguration {

	@Bean
	@ConfigurationProperties(prefix = "tinyrpc.consumer")
	public SpringBootConsumerConfig springBootConsumerConfig() {
		return new SpringBootConsumerConfig();
	}

	@Bean
	public List<RpcClient> rpcClient(final SpringBootConsumerConfig springBootConsumerConfig) {
		return parseRpcClient(springBootConsumerConfig);
	}

	private List<RpcClient> parseRpcClient(final SpringBootConsumerConfig springBootConsumerConfig) {
		List<RpcClient> rpcClientList = new ArrayList<>();
		ApplicationContext context = RpcConsumerSpringContext.getInstance().getContext();
		Map<String, RpcReferenceBean> rpcReferenceBeanMap = context.getBeansOfType(RpcReferenceBean.class);
		Collection<RpcReferenceBean> rpcReferenceBeans = rpcReferenceBeanMap.values();
		for (RpcReferenceBean rpcReferenceBean : rpcReferenceBeans) {
			this.getRpcReferenceBean(rpcReferenceBean, springBootConsumerConfig);
			rpcReferenceBean.init();
			rpcClientList.add(rpcReferenceBean.getRpcClient());
		}
		return rpcClientList;
	}

	/**
	 * 首先从Spring IOC容器中获取RpcReferenceBean，
	 * 如果存在RpcReferenceBean，部分RpcReferenceBean的字段为空，则使用springBootConsumerConfig字段进行填充
	 * 如果不存在RpcReferenceBean，则使用springBootConsumerConfig构建RpcReferenceBean
	 */
	private RpcReferenceBean getRpcReferenceBean(final RpcReferenceBean referenceBean, final SpringBootConsumerConfig springBootConsumerConfig) {
		if (StringUtil.isEmpty(referenceBean.getGroup())
			|| (RpcConstants.RPC_COMMON_DEFAULT_GROUP.equals(referenceBean.getGroup()) && !StringUtil.isEmpty(springBootConsumerConfig.getGroup()))) {
			referenceBean.setGroup(springBootConsumerConfig.getGroup());
		}
		if (StringUtil.isEmpty(referenceBean.getVersion())
			|| (RpcConstants.RPC_COMMON_DEFAULT_VERSION.equals(referenceBean.getVersion()) && !StringUtil.isEmpty(springBootConsumerConfig.getVersion()))) {
			referenceBean.setVersion(springBootConsumerConfig.getVersion());
		}
		if (StringUtil.isEmpty(referenceBean.getRegistryType())
			|| (RpcConstants.RPC_REFERENCE_DEFAULT_REGISTRYTYPE.equals(referenceBean.getRegistryType()) && !StringUtil.isEmpty(springBootConsumerConfig.getRegistryType()))) {
			referenceBean.setRegistryType(springBootConsumerConfig.getRegistryType());
		}
		if (StringUtil.isEmpty(referenceBean.getLoadBalanceType())
			|| (RpcConstants.RPC_REFERENCE_DEFAULT_LOADBALANCETYPE.equals(referenceBean.getLoadBalanceType()) && !StringUtil.isEmpty(springBootConsumerConfig.getLoadBalanceType()))) {
			referenceBean.setLoadBalanceType(springBootConsumerConfig.getLoadBalanceType());
		}
		if (StringUtil.isEmpty(referenceBean.getSerializationType())
			|| (RpcConstants.RPC_REFERENCE_DEFAULT_SERIALIZATIONTYPE.equals(referenceBean.getSerializationType()) && !StringUtil.isEmpty(springBootConsumerConfig.getSerializationType()))) {
			referenceBean.setSerializationType(springBootConsumerConfig.getSerializationType());
		}
		if (StringUtil.isEmpty(referenceBean.getRegistryAddress())
			|| (RpcConstants.RPC_REFERENCE_DEFAULT_REGISTRYADDRESS.equals(referenceBean.getRegistryAddress()) && !StringUtil.isEmpty(springBootConsumerConfig.getRegistryAddress()))) {
			referenceBean.setRegistryAddress(springBootConsumerConfig.getRegistryAddress());
		}
		if (referenceBean.getTimeout() <= 0
			|| (RpcConstants.RPC_REFERENCE_DEFAULT_TIMEOUT == referenceBean.getTimeout() && springBootConsumerConfig.getTimeout() > 0)) {
			referenceBean.setTimeout(springBootConsumerConfig.getTimeout());
		}
		if (!referenceBean.isAsync()) {
			referenceBean.setAsync(springBootConsumerConfig().isAsync());
		}
		if (!referenceBean.isOneway()) {
			referenceBean.setOneway(springBootConsumerConfig().isOneway());
		}
		if (StringUtil.isEmpty(referenceBean.getProxy())
			|| (RpcConstants.RPC_REFERENCE_DEFAULT_PROXY.equals(referenceBean.getProxy()) && !StringUtil.isEmpty(springBootConsumerConfig.getProxy()))) {
			referenceBean.setProxy(springBootConsumerConfig.getProxy());
		}
		if (referenceBean.getHeartbeatInterval() <= 0
			|| (RpcConstants.RPC_COMMON_DEFAULT_HEARTBEATINTERVAL == referenceBean.getHeartbeatInterval() && springBootConsumerConfig.getHeartbeatInterval() > 0)) {
			referenceBean.setHeartbeatInterval(springBootConsumerConfig.getHeartbeatInterval());
		}
		if (referenceBean.getRetryInterval() <= 0
			|| (RpcConstants.RPC_REFERENCE_DEFAULT_RETRYINTERVAL == referenceBean.getRetryInterval() && springBootConsumerConfig.getRetryInterval() > 0)) {
			referenceBean.setRetryInterval(springBootConsumerConfig.getRetryInterval());
		}
		if (referenceBean.getRetryTimes() <= 0
			|| (RpcConstants.RPC_REFERENCE_DEFAULT_RETRYTIMES == referenceBean.getRetryTimes() && springBootConsumerConfig.getRetryTimes() > 0)) {
			referenceBean.setRetryTimes(springBootConsumerConfig.getRetryTimes());
		}
		if (referenceBean.getScanNotActiveChannelInterval() <= 0
			|| (RpcConstants.RPC_COMMON_DEFAULT_SCANNOTACTIVECHANNELINTERVAL == referenceBean.getScanNotActiveChannelInterval() && springBootConsumerConfig.getScanNotActiveChannelInterval() > 0)) {
			referenceBean.setScanNotActiveChannelInterval(springBootConsumerConfig().getScanNotActiveChannelInterval());
		}
		if (!referenceBean.isEnableResultCache()) {
			referenceBean.setEnableResultCache(springBootConsumerConfig.isEnableResultCache());
		}
		if (referenceBean.getResultCacheExpire() <= 0
			|| (RpcConstants.RPC_SCAN_RESULT_CACHE_EXPIRE == referenceBean.getResultCacheExpire() && springBootConsumerConfig.getResultCacheExpire() > 0)) {
			referenceBean.setResultCacheExpire(springBootConsumerConfig.getResultCacheExpire());
		}
		if (!referenceBean.isEnableDirectServer()) {
			referenceBean.setEnableDirectServer(springBootConsumerConfig.isEnableDirectServer());
		}
		if (StringUtil.isEmpty(referenceBean.getDirectServerUrl())
			|| (RpcConstants.RPC_COMMON_DEFAULT_DIRECT_SERVER.equals(referenceBean.getDirectServerUrl()) && !StringUtil.isEmpty(springBootConsumerConfig.getDirectServerUrl()))) {
			referenceBean.setDirectServerUrl(springBootConsumerConfig.getDirectServerUrl());
		}
		if (referenceBean.getCorePoolSize() <= 0
			|| (RpcConstants.DEFAULT_CORE_POOL_SIZE == referenceBean.getCorePoolSize() && springBootConsumerConfig.getCorePoolSize() > 0)) {
			referenceBean.setCorePoolSize(springBootConsumerConfig.getCorePoolSize());
		}
		if (referenceBean.getMaximumPoolSize() <= 0
			|| (RpcConstants.DEFAULT_MAXI_NUM_POOL_SIZE == referenceBean.getMaximumPoolSize() && springBootConsumerConfig.getMaximumPoolSize() > 0)) {
			referenceBean.setMaximumPoolSize(springBootConsumerConfig.getMaximumPoolSize());
		}
		if (StringUtil.isEmpty(referenceBean.getFlowType())
			|| (RpcConstants.FLOW_POST_PROCESSOR_PRINT.equals(referenceBean.getFlowType()) && !StringUtil.isEmpty(springBootConsumerConfig.getFlowType()))) {
			referenceBean.setFlowType(springBootConsumerConfig.getFlowType());
		}
		if (!referenceBean.isEnableBuffer()) {
			referenceBean.setEnableBuffer(springBootConsumerConfig.isEnableBuffer());
		}
		if (referenceBean.getBufferSize() <= 0
			|| (RpcConstants.DEFAULT_BUFFER_SIZE == referenceBean.getBufferSize() && springBootConsumerConfig.getBufferSize() > 0)) {
			referenceBean.setBufferSize(springBootConsumerConfig.getBufferSize());
		}

		if (StringUtil.isEmpty(referenceBean.getReflectType())
			|| (RpcConstants.DEFAULT_REFLECT_TYPE.equals(referenceBean.getReflectType()) && !StringUtil.isEmpty(springBootConsumerConfig.getReflectType()))) {
			referenceBean.setReflectType(springBootConsumerConfig.getReflectType());
		}
		if (StringUtil.isEmpty(referenceBean.getFallbackClassName())
			|| (RpcConstants.DEFAULT_FALLBACK_CLASS_NAME.equals(referenceBean.getFallbackClassName()) && !StringUtil.isEmpty(springBootConsumerConfig.getFallbackClassName()))) {
			referenceBean.setFallbackClassName(springBootConsumerConfig.getFallbackClassName());
		}

		if (!referenceBean.isEnableRateLimiter()) {
			referenceBean.setEnableRateLimiter(springBootConsumerConfig.isEnableRateLimiter());
		}

		if (StringUtil.isEmpty(referenceBean.getRateLimiterType())
			|| (RpcConstants.DEFAULT_RATELIMITER_INVOKER.equals(referenceBean.getRateLimiterType()) && !StringUtil.isEmpty(springBootConsumerConfig.getRateLimiterType()))) {
			referenceBean.setRateLimiterType(springBootConsumerConfig.getRateLimiterType());
		}

		if (referenceBean.getPermits() <= 0
			|| (RpcConstants.DEFAULT_RATELIMITER_PERMITS == referenceBean.getPermits() && springBootConsumerConfig.getPermits() > 0)) {
			referenceBean.setPermits(springBootConsumerConfig.getPermits());
		}

		if (referenceBean.getMilliSeconds() <= 0
			|| (RpcConstants.DEFAULT_RATELIMITER_MILLI_SECONDS == referenceBean.getMilliSeconds() && springBootConsumerConfig.getMilliSeconds() > 0)) {
			referenceBean.setMilliSeconds(springBootConsumerConfig.getMilliSeconds());
		}

		if (StringUtil.isEmpty(referenceBean.getRateLimiterFailStrategy())
			|| (RpcConstants.RATE_LIMILTER_FAIL_STRATEGY_DIRECT.equals(referenceBean.getRateLimiterFailStrategy()) && !StringUtil.isEmpty(springBootConsumerConfig.getRateLimiterFailStrategy()))) {
			referenceBean.setRateLimiterFailStrategy(springBootConsumerConfig.getRateLimiterFailStrategy());
		}

		if (!referenceBean.isEnableCircuitBreaker()) {
			referenceBean.setEnableCircuitBreaker(springBootConsumerConfig.isEnableCircuitBreaker());
		}

		if (StringUtil.isEmpty(referenceBean.getCircuitBreakerType())
			|| (RpcConstants.DEFAULT_CIRCUIT_BREAKER_INVOKER.equals(referenceBean.getCircuitBreakerType()) && !StringUtil.isEmpty(springBootConsumerConfig.getCircuitBreakerType()))) {
			referenceBean.setCircuitBreakerType(springBootConsumerConfig.getCircuitBreakerType());
		}

		if (referenceBean.getTotalFailure() <= 0
			|| (RpcConstants.DEFAULT_CIRCUIT_BREAKER_TOTAL_FAILURE == referenceBean.getTotalFailure() && springBootConsumerConfig.getTotalFailure() > 0)) {
			referenceBean.setTotalFailure(springBootConsumerConfig.getTotalFailure());
		}

		if (referenceBean.getCircuitBreakerMilliSeconds() <= 0
			|| (RpcConstants.DEFAULT_CIRCUIT_BREAKER_MILLI_SECONDS == referenceBean.getCircuitBreakerMilliSeconds() && springBootConsumerConfig.getCircuitBreakerMilliSeconds() > 0)) {
			referenceBean.setCircuitBreakerMilliSeconds(springBootConsumerConfig.getCircuitBreakerMilliSeconds());
		}

		if (StringUtil.isEmpty(referenceBean.getExceptionPostProcessorType())
			|| (RpcConstants.EXCEPTION_POST_PROCESSOR_PRINT.equals(referenceBean.getExceptionPostProcessorType()) && !StringUtil.isEmpty(springBootConsumerConfig.getExceptionPostProcessorType()))) {
			referenceBean.setExceptionPostProcessorType(springBootConsumerConfig.getExceptionPostProcessorType());
		}

		return referenceBean;
	}
}
