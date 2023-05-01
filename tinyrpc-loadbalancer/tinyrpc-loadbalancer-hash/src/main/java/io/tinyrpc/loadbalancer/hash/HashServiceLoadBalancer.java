package io.tinyrpc.loadbalancer.hash;

import io.tinyrpc.loadbalancer.api.ServiceLoadBalancer;
import io.tinyrpc.spi.annotation.SPIClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 基于Hash算法的负载均衡策略
 *
 * @param <T>
 */
@SPIClass
public class HashServiceLoadBalancer<T> implements ServiceLoadBalancer<T> {

	private static final Logger logger = LoggerFactory.getLogger(HashServiceLoadBalancer.class);

	@Override
	public T select(List<T> servers, int hashCode) {
		logger.info("--- Hash LoadBalancer ---");
		if (servers == null || servers.isEmpty()) {
			return null;
		}

		int index = Math.abs(hashCode) % servers.size();
		return servers.get(index);
	}
}
