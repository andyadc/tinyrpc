package io.tinyrpc.spring.boot.provider.starter;

import io.tinyrpc.provider.spring.RpcSpringServer;
import io.tinyrpc.spring.boot.provider.config.SpringBootProviderConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringBootProviderAutoConfiguration {

	@Bean
	@ConfigurationProperties(prefix = "tinyrpc.provider")
	public SpringBootProviderConfig springBootProviderConfig() {
		return new SpringBootProviderConfig();
	}

	@Bean
	public RpcSpringServer rpcSpringServer(final SpringBootProviderConfig springBootProviderConfig) {
		return new RpcSpringServer(
			springBootProviderConfig.getServerAddress(),
			springBootProviderConfig.getRegistryAddress(),
			springBootProviderConfig.getRegistryType(),
			springBootProviderConfig.getRegistryLoadBalanceType(),
			springBootProviderConfig.getReflectType(),
			springBootProviderConfig.getHeartbeatInterval(),
			springBootProviderConfig.getScanNotActiveChannelInterval(),
			springBootProviderConfig.isEnableResultCache(),
			springBootProviderConfig.getResultCacheExpire(),
			springBootProviderConfig.getCorePoolSize(),
			springBootProviderConfig.getMaximumPoolSize(),
			springBootProviderConfig.getFlowType(),
			springBootProviderConfig.getMaxConnections(),
			springBootProviderConfig.getDisuseStrategyType(),
			springBootProviderConfig.isEnableBuffer(),
			springBootProviderConfig.getBufferSize(),
			springBootProviderConfig.isEnableRateLimiter(),
			springBootProviderConfig.getRateLimiterType(),
			springBootProviderConfig.getPermits(),
			springBootProviderConfig.getMilliSeconds()
		);
	}
}
