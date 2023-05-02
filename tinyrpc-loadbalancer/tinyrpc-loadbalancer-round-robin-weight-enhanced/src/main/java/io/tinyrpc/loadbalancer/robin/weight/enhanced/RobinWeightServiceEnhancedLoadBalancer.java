package io.tinyrpc.loadbalancer.robin.weight.enhanced;

import io.tinyrpc.loadbalancer.base.BaseEnhancedServiceLoadBalancer;
import io.tinyrpc.protocol.meta.ServiceMeta;
import io.tinyrpc.spi.annotation.SPIClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 增强型加权轮询负载均衡
 */
@SPIClass
public class RobinWeightServiceEnhancedLoadBalancer extends BaseEnhancedServiceLoadBalancer {

	private static final Logger logger = LoggerFactory.getLogger(RobinWeightServiceEnhancedLoadBalancer.class);

	private final AtomicInteger atomicInteger = new AtomicInteger(0);

	@Override
	public ServiceMeta select(List<ServiceMeta> servers, int hashCode, String sourceIp) {
		logger.info("--- Enhanced Round-robin weight LoadBalancer ---");
		if (servers == null || servers.isEmpty()) {
			return null;
		}

		servers = this.getWeightServiceMetaList(servers);
		if (servers == null || servers.isEmpty()) {
			return null;
		}
		int index = atomicInteger.incrementAndGet();
		if (index >= Integer.MAX_VALUE - 10000) {
			atomicInteger.set(0);
		}
		return servers.get(index % servers.size());
	}
}
