package io.tinyrpc.loadbalancer.hash.weight;

import io.tinyrpc.loadbalancer.api.ServiceLoadBalancer;
import io.tinyrpc.spi.annotation.SPIClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 基于加权Hash算法负载均衡策略
 */
@SPIClass
public class HashWeightServiceLoadBalancer<T> implements ServiceLoadBalancer<T> {

	private static final Logger logger = LoggerFactory.getLogger(HashWeightServiceLoadBalancer.class);

	@Override
	public T select(List<T> servers, int hashCode, String sourceIp) {
		logger.info("--- Hash weight LoadBalancer ---");
		if (servers == null || servers.isEmpty()) {
			return null;
		}

		hashCode = Math.abs(hashCode);
		int count = hashCode % servers.size();
		if (count <= 0) {
			count = servers.size();
		}
		int index = hashCode % count;
		return servers.get(index);
	}
}
