package io.tinyrpc.loadbalancer.random;

import io.tinyrpc.loadbalancer.api.ServiceLoadBalancer;
import io.tinyrpc.spi.annotation.SPIClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.SecureRandom;
import java.util.List;

/**
 * 基于随机算法的负载均衡策略
 *
 * @param <T>
 */
@SPIClass
public class RandomServiceLoadBalancer<T> implements ServiceLoadBalancer<T> {

	private static final Logger logger = LoggerFactory.getLogger(RandomServiceLoadBalancer.class);

	@Override
	public T select(List<T> servers, int hashCode, String sourceIp) {
		logger.info("--- Random LoadBalancer ---");
		if (servers == null || servers.isEmpty()){
			return null;
		}
		SecureRandom random = new SecureRandom();
		int idx = random.nextInt(servers.size());
		return servers.get(idx);
	}
}
