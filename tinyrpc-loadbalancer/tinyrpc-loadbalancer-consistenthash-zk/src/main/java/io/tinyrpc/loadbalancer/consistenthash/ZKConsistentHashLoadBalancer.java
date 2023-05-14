package io.tinyrpc.loadbalancer.consistenthash;

import io.tinyrpc.common.exception.RpcException;
import io.tinyrpc.loadbalancer.api.ServiceLoadBalancer;
import io.tinyrpc.spi.annotation.SPIClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

/**
 * 基于Zookeeper的一致性Hash
 */
@SPIClass
public class ZKConsistentHashLoadBalancer<T> implements ServiceLoadBalancer<T> {

	private static final Logger logger = LoggerFactory.getLogger(ZKConsistentHashLoadBalancer.class);

	private final static int VIRTUAL_NODE_SIZE = 10;
	private final static String VIRTUAL_NODE_SPLIT = "#";

	@Override
	public T select(List<T> servers, int hashCode, String sourceIp) {
		logger.info("--- ZKConsistentHash LoadBalancer ---");
		if (servers == null || servers.isEmpty()) {
			return null;
		}

		TreeMap<Integer, T> ring = makeConsistentHashRing(servers);
		return allocateNode(ring, hashCode);
	}

	private T allocateNode(TreeMap<Integer, T> ring, int hashCode) {
		Map.Entry<Integer, T> entry = ring.ceilingEntry(hashCode);
		if (entry == null) {
			entry = ring.firstEntry();
		}
		if (entry == null) {
			throw new RpcException("not discover useful service, please register service in registry center.");
		}
		return entry.getValue();
	}

	private TreeMap<Integer, T> makeConsistentHashRing(List<T> servers) {
		TreeMap<Integer, T> ring = new TreeMap<>();
		for (T instance : servers) {
			for (int i = 0; i < VIRTUAL_NODE_SIZE; i++) {
				ring.put((buildServiceInstanceKey(instance) + VIRTUAL_NODE_SPLIT + i).hashCode(), instance);
			}
		}
		return ring;
	}

	private String buildServiceInstanceKey(T instance) {
		return Objects.toString(instance);
	}
}
