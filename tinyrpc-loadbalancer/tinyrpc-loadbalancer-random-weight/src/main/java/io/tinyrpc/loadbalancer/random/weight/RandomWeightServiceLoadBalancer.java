package io.tinyrpc.loadbalancer.random.weight;

import io.tinyrpc.loadbalancer.api.ServiceLoadBalancer;
import io.tinyrpc.spi.annotation.SPIClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.SecureRandom;
import java.util.List;

/**
 * 加权随机
 *
 * @param <T>
 */
@SPIClass
public class RandomWeightServiceLoadBalancer<T> implements ServiceLoadBalancer<T> {

	private static final Logger logger = LoggerFactory.getLogger(RandomWeightServiceLoadBalancer.class);

	@Override
	public T select(List<T> servers, int hashCode, String sourceIp) {
		logger.info("--- Random-Weight LoadBalancer ---");
		if (servers == null || servers.isEmpty()) {
			return null;
		}

		hashCode = Math.abs(hashCode);
		int count = hashCode % servers.size();
		if (count <= 1) {
			count = servers.size();
		}

		SecureRandom random = new SecureRandom();
		int idx = random.nextInt(count);
		return servers.get(idx);
	}
}
