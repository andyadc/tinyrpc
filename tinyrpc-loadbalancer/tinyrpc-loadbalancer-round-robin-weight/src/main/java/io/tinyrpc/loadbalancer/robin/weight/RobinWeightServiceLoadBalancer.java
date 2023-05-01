package io.tinyrpc.loadbalancer.robin.weight;

import io.tinyrpc.loadbalancer.api.ServiceLoadBalancer;
import io.tinyrpc.spi.annotation.SPIClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 加权轮询负载均衡
 *
 * @param <T>
 */
@SPIClass
public class RobinWeightServiceLoadBalancer<T> implements ServiceLoadBalancer<T> {

	private static final Logger logger = LoggerFactory.getLogger(RobinWeightServiceLoadBalancer.class);

	private final AtomicInteger atomicInteger = new AtomicInteger(0);

	@Override
	public T select(List<T> servers, int hashCode, String sourceIp) {
		logger.info("--- Round-robin weight LoadBalancer ---");
		if (servers == null || servers.isEmpty()) {
			return null;
		}

		hashCode = Math.abs(hashCode);
		int count = hashCode % servers.size();
		if (count <= 0) {
			count = servers.size();
		}
		int index = atomicInteger.incrementAndGet();
		if (index >= Integer.MAX_VALUE - 10000) {
			atomicInteger.set(0);
		}
		return servers.get(index % count);
	}
}
