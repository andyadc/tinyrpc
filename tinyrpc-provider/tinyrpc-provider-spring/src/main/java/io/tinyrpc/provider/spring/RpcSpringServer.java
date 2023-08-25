package io.tinyrpc.provider.spring;

import io.tinyrpc.annotation.RpcService;
import io.tinyrpc.common.helper.RpcServiceHelper;
import io.tinyrpc.common.threadpool.AsyncStartProviderThreadPool;
import io.tinyrpc.constant.RpcConstants;
import io.tinyrpc.protocol.meta.ServiceMeta;
import io.tinyrpc.provider.common.server.base.BaseServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Map;

/**
 * 基于Spring启动RPC服务
 */
public class RpcSpringServer extends BaseServer implements ApplicationContextAware, InitializingBean {

	private static final Logger logger = LoggerFactory.getLogger(RpcSpringServer.class);

	public RpcSpringServer(String serverAddress, String registryAddress,
						   String registryType, String registryLoadBalanceType,
						   String reflectType,
						   int heartbeatInterval, int scanNotActiveChannelInterval,
						   boolean enableResultCache, int resultCacheExpire,
						   int corePoolSize, int maximumPoolSize, String flowType,
						   int maxConnections, String disuseStrategyType,
						   boolean enableBuffer, int bufferSize,
						   boolean enableRateLimiter, String rateLimiterType, int permits, int milliSeconds,
						   String rateLimiterFailStrategy,
						   boolean enableCircuitBreaker, String circuitBreakerType, double totalFailure, int circuitBreakerMilliSeconds,
						   String exceptionPostProcessorType) {
		super(
			serverAddress, registryAddress, registryType, registryLoadBalanceType,
			reflectType,
			heartbeatInterval, scanNotActiveChannelInterval,
			enableResultCache, resultCacheExpire,
			corePoolSize, maximumPoolSize, flowType,
			maxConnections, disuseStrategyType, enableBuffer, bufferSize,
			enableRateLimiter, rateLimiterType, permits, milliSeconds,
			rateLimiterFailStrategy,
			enableCircuitBreaker, circuitBreakerType, totalFailure, circuitBreakerMilliSeconds,
			exceptionPostProcessorType
		);
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		Map<String, Object> serviceBeanMap = applicationContext.getBeansWithAnnotation(RpcService.class);
		if (serviceBeanMap.size() > 0) {
			for (Object serviceBean : serviceBeanMap.values()) {
				RpcService rpcService = serviceBean.getClass().getAnnotation(RpcService.class);
				ServiceMeta serviceMeta = new ServiceMeta(RpcServiceHelper.getServiceName(rpcService), rpcService.version(), rpcService.group(), host, port, getWeight(rpcService.weight()));
				handlerMap.put(RpcServiceHelper.buildServiceKey(serviceMeta.getServiceName(), serviceMeta.getServiceVersion(), serviceMeta.getServiceGroup()), serviceBean);

				try {
					registryService.register(serviceMeta);
				} catch (Exception e) {
					logger.error("spring rpc init error", e);
				}
			}
		} else {
			logger.info("Not found Bean annotated for [RpcService]");
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		logger.info("Starting RPC server...");
//		this.startNettyServer();
		// SpringBoot整合服务提供者时启动服务卡住Spring线程
		AsyncStartProviderThreadPool.submit(this::startNettyServer);
	}

	private int getWeight(int weight) {
		if (weight < RpcConstants.SERVICE_WEIGHT_MIN) {
			weight = RpcConstants.SERVICE_WEIGHT_MIN;
		}
		if (weight > RpcConstants.SERVICE_WEIGHT_MAX) {
			weight = RpcConstants.SERVICE_WEIGHT_MAX;
		}
		return weight;
	}
}
