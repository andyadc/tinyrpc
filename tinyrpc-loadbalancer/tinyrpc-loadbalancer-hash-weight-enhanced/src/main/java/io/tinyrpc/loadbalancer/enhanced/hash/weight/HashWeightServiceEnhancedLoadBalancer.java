package io.tinyrpc.loadbalancer.enhanced.hash.weight;

import io.tinyrpc.loadbalancer.base.BaseEnhancedServiceLoadBalancer;
import io.tinyrpc.protocol.meta.ServiceMeta;
import io.tinyrpc.spi.annotation.SPIClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 增强型加权Hash算法负载均衡
 */
@SPIClass
public class HashWeightServiceEnhancedLoadBalancer extends BaseEnhancedServiceLoadBalancer {

	private static final Logger logger = LoggerFactory.getLogger(HashWeightServiceEnhancedLoadBalancer.class);

	@Override
	public ServiceMeta select(List<ServiceMeta> servers, int hashCode, String sourceIp) {
		logger.info("--- Enhanced Hash weight LoadBalancer ---");
		if (servers == null || servers.isEmpty()) {
			return null;
		}

		servers = this.getWeightServiceMetaList(servers);
		if (servers == null || servers.isEmpty()) {
			return null;
		}
		int index = Math.abs(hashCode) % servers.size();
		return servers.get(index);
	}
}
