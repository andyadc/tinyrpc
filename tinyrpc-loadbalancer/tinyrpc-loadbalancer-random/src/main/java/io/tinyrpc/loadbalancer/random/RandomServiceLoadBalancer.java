package io.tinyrpc.loadbalancer.random;

import io.tinyrpc.loadbalancer.api.ServiceLoadBalancer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 基于随机算法的负载均衡策略
 *
 * @param <T>
 */
public class RandomServiceLoadBalancer<T> implements ServiceLoadBalancer<T> {

	private static final Logger logger = LoggerFactory.getLogger(RandomServiceLoadBalancer.class);

	@Override
	public T select(List<T> servers, int hashCode, String sourceIp) {
		return null;
	}
}
