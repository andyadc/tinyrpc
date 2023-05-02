package io.tinyrpc.loadbalancer.helper;

import io.tinyrpc.protocol.meta.ServiceMeta;
import org.apache.curator.x.discovery.ServiceInstance;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 服务负载均衡辅助类
 */
public class ServiceLoadBalancerHelper {

	private static final List<ServiceMeta> cacheServiceMeta = new CopyOnWriteArrayList<>();

	public static List<ServiceMeta> getServiceMetaList(List<ServiceInstance<ServiceMeta>> serviceInstances){
		if (serviceInstances == null || serviceInstances.isEmpty() || cacheServiceMeta.size() == serviceInstances.size()){
			return cacheServiceMeta;
		}
		//先清空cacheServiceMeta中的数据
		cacheServiceMeta.clear();
		serviceInstances.forEach((serviceMetaServiceInstance) -> {
			cacheServiceMeta.add(serviceMetaServiceInstance.getPayload());
		});
		return cacheServiceMeta;
	}
}
