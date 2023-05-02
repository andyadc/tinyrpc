package io.tinyrpc.loadbalancer.enhanced.random.weight;

import io.tinyrpc.loadbalancer.base.BaseEnhancedServiceLoadBalancer;
import io.tinyrpc.protocol.meta.ServiceMeta;
import io.tinyrpc.spi.annotation.SPIClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.SecureRandom;
import java.util.List;

/**
 * 增强型加权随机
 */
@SPIClass
public class RandomWeightServiceEnhancedLoadBalancer extends BaseEnhancedServiceLoadBalancer {

	private static final Logger logger = LoggerFactory.getLogger(RandomWeightServiceEnhancedLoadBalancer.class);

	@Override
	public ServiceMeta select(List<ServiceMeta> servers, int hashCode, String sourceIp) {
		logger.info("--- Enhanced Random-Weight LoadBalancer ---");
		if (servers == null || servers.isEmpty()) {
			return null;
		}

		servers = this.getWeightServiceMetaList(servers);
		if (servers == null || servers.isEmpty()) {
			return null;
		}
		SecureRandom random = new SecureRandom();
		int index = random.nextInt(servers.size());
		return servers.get(index);
	}
}
