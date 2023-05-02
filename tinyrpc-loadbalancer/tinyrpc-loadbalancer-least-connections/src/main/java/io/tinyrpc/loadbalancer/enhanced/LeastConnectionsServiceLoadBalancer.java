package io.tinyrpc.loadbalancer.enhanced;

import io.tinyrpc.loadbalancer.api.ServiceLoadBalancer;
import io.tinyrpc.loadbalancer.context.ConnectionsContext;
import io.tinyrpc.protocol.meta.ServiceMeta;
import io.tinyrpc.spi.annotation.SPIClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 基于最少连接数的负载均衡策略
 */
@SPIClass
public class LeastConnectionsServiceLoadBalancer implements ServiceLoadBalancer<ServiceMeta> {

	private static final Logger logger = LoggerFactory.getLogger(LeastConnectionsServiceLoadBalancer.class);

	@Override
	public ServiceMeta select(List<ServiceMeta> servers, int hashCode, String sourceIp) {
		logger.info("--- Least Connections LoadBalancer ---");
		if (servers == null || servers.isEmpty()) {
			return null;
		}

		ServiceMeta serviceMeta = this.getNullServiceMeta(servers);
		if (serviceMeta == null) {
			serviceMeta = this.getServiceMeta(servers);
		}
		return serviceMeta;
	}

	private ServiceMeta getServiceMeta(List<ServiceMeta> servers) {
		ServiceMeta serviceMeta = servers.get(0);
		Integer serviceMetaCount = ConnectionsContext.getValue(serviceMeta);
		for (int i = 1; i < servers.size(); i++) {
			ServiceMeta meta = servers.get(i);
			Integer metaCount = ConnectionsContext.getValue(meta);
			if (serviceMetaCount > metaCount) {
				serviceMetaCount = metaCount;
				serviceMeta = meta;
			}
		}
		return serviceMeta;
	}

	//获取服务元数据列表中连接数为空的元数据，说明没有连接
	private ServiceMeta getNullServiceMeta(List<ServiceMeta> servers) {
		for (ServiceMeta serviceMeta : servers) {
			if (ConnectionsContext.getValue(serviceMeta) == null) {
				return serviceMeta;
			}
		}
		return null;
	}
}
