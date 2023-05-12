package io.tinyrpc.demo.spring.annotation.provider.config;

import io.tinyrpc.provider.spring.RpcSpringServer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ComponentScan(value = {"io.tinyrpc.demo"})
@PropertySource(value = {"classpath:rpc.properties"})
public class SpringAnnotationProviderConfig {

	@Value("${registry.address}")
	private String registryAddress;

	@Value("${registry.type}")
	private String registryType;

	@Value("${registry.loadbalance.type}")
	private String registryLoadbalanceType;

	@Value("${server.address}")
	private String serverAddress;

	@Value("${reflect.type}")
	private String reflectType;

	@Value("${server.heartbeatInterval}")
	private int heartbeatInterval;

	@Value("${server.scanNotActiveChannelInterval}")
	private int scanNotActiveChannelInterval;

	@Value("${server.enableResultCache}")
	private boolean enableResultCache;

	@Value("${server.resultCacheExpire}")
	private int resultCacheExpire;

	@Value("${server.corePoolSize}")
	private int corePoolSize;

	@Value("${server.maximumPoolSize}")
	private int maximumPoolSize;

	@Value("${server.flowType}")
	private String flowType;

	@Value("${server.maxConnections}")
	private int maxConnections;

	@Value("${server.disuseStrategyType}")
	private String disuseStrategyType;

	@Value("${server.enableBuffer}")
	private boolean enableBuffer;

	@Value("${server.bufferSize}")
	private int bufferSize;

	@Value("${server.enableRateLimiter}")
	private boolean enableRateLimiter;

	@Value("${server.rateLimiterType}")
	private String rateLimiterType;

	@Value("${server.permits}")
	private int permits;

	@Value("${server.milliSeconds}")
	private int milliSeconds;

	@Bean
	public RpcSpringServer rpcSpringServer() {
		return new RpcSpringServer(serverAddress, registryAddress, registryType, registryLoadbalanceType,
			reflectType, heartbeatInterval, scanNotActiveChannelInterval,
			enableResultCache, resultCacheExpire, corePoolSize, maximumPoolSize, flowType,
			maxConnections, disuseStrategyType, enableBuffer, bufferSize,
			enableRateLimiter, rateLimiterType, permits, milliSeconds);
	}
}
