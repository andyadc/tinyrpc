package io.tinyrpc.loadbalancer.random;

import io.tinyrpc.loadbalancer.api.ServiceLoadBalancer;

import java.util.List;

public class RandomServiceLoadBalancer<T> implements ServiceLoadBalancer<T> {

	@Override
	public T select(List<T> servers, int hashCode) {
		return null;
	}
}
