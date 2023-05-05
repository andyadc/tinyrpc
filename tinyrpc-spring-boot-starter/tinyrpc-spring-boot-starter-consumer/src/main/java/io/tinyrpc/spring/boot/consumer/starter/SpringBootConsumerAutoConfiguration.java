package io.tinyrpc.spring.boot.consumer.starter;

import io.tinyrpc.consumer.RpcClient;
import io.tinyrpc.spring.boot.consumer.config.SpringBootConsumerConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
	public RpcClient rpcClient(final SpringBootConsumerConfig springBootConsumerConfig) {
		return new RpcClient(
			springBootConsumerConfig.getRegistryAddress(),
			springBootConsumerConfig.getRegistryType(),
			springBootConsumerConfig.getLoadBalanceType(),
			springBootConsumerConfig.getProxy(),
			springBootConsumerConfig.getVersion(),
			springBootConsumerConfig.getGroup(),
			springBootConsumerConfig.getSerializationType(),
			springBootConsumerConfig.getTimeout(),
			springBootConsumerConfig.getAsync(),
			springBootConsumerConfig.getOneway(),
			springBootConsumerConfig.getHeartbeatInterval(),
			springBootConsumerConfig.getScanNotActiveChannelInterval(),
			springBootConsumerConfig.getRetryInterval(),
			springBootConsumerConfig.getRetryTimes()
		);
	}
}
