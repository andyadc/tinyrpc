package io.tinyrpc.loadbalancer.consistenthash;

import io.tinyrpc.common.exception.RpcException;
import io.tinyrpc.loadbalancer.api.ServiceLoadBalancer;
import io.tinyrpc.protocol.meta.ServiceMeta;
import io.tinyrpc.spi.annotation.SPIClass;
import org.apache.curator.x.discovery.ServiceInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * 基于Zookeeper的一致性Hash
 */
@SPIClass
public class ZKConsistentHashLoadBalancer implements ServiceLoadBalancer<ServiceInstance<ServiceMeta>> {

	private static final Logger logger = LoggerFactory.getLogger(ZKConsistentHashLoadBalancer.class);

	private final static int VIRTUAL_NODE_SIZE = 10;
	private final static String VIRTUAL_NODE_SPLIT = "#";

	@Override
	public ServiceInstance<ServiceMeta> select(List<ServiceInstance<ServiceMeta>> servers, int hashCode, String sourceIp) {
		logger.info("--- ZKConsistentHash LoadBalancer ---");
		if (servers == null || servers.isEmpty()) {
			return null;
		}

		TreeMap<Integer, ServiceInstance<ServiceMeta>> ring = makeConsistentHashRing(servers);
		return allocateNode(ring, hashCode);
	}

	private ServiceInstance<ServiceMeta> allocateNode(TreeMap<Integer, ServiceInstance<ServiceMeta>> ring, int hashCode) {
		Map.Entry<Integer, ServiceInstance<ServiceMeta>> entry = ring.ceilingEntry(hashCode);
		if (entry == null) {
			entry = ring.firstEntry();
		}
		if (entry == null) {
			throw new RpcException("not discover useful service, please register service in registry center.");
		}
		return entry.getValue();
	}

	private TreeMap<Integer, ServiceInstance<ServiceMeta>> makeConsistentHashRing(List<ServiceInstance<ServiceMeta>> servers) {
		TreeMap<Integer, ServiceInstance<ServiceMeta>> ring = new TreeMap<>();
		for (ServiceInstance<ServiceMeta> instance : servers) {
			for (int i = 0; i < VIRTUAL_NODE_SIZE; i++) {
				ring.put((buildServiceInstanceKey(instance) + VIRTUAL_NODE_SPLIT + i).hashCode(), instance);
			}
		}
		return ring;
	}

	private String buildServiceInstanceKey(ServiceInstance<ServiceMeta> instance) {
		ServiceMeta payload = instance.getPayload();
		return String.join(":", payload.getServiceAddr(), String.valueOf(payload.getServicePort()));
	}
}
