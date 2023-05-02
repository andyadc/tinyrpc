package io.tinyrpc.loadbalancer.enhanced.ip.hash.weight;

import io.tinyrpc.loadbalancer.base.BaseEnhancedServiceLoadBalancer;
import io.tinyrpc.protocol.meta.ServiceMeta;
import io.tinyrpc.spi.annotation.SPIClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 增强型基于权重的源IP地址Hash的负载均衡策略
 */
@SPIClass
public class SourceIpHashWeightServiceEnhancedLoadBalancer extends BaseEnhancedServiceLoadBalancer {

	private static final Logger logger = LoggerFactory.getLogger(SourceIpHashWeightServiceEnhancedLoadBalancer.class);

	@Override
	public ServiceMeta select(List<ServiceMeta> servers, int hashCode, String sourceIp) {
		logger.info("--- Enhanced SourceIp Hash weight LoadBalancer ---");
		if (servers == null || servers.isEmpty()) {
			return null;
		}
		servers = this.getWeightServiceMetaList(servers);
		if (servers == null || servers.isEmpty()) {
			return null;
		}

		//传入的IP地址为空，则默认返回第一个服务实例m
		if (sourceIp == null || sourceIp.isEmpty()) {
			return servers.get(0);
		}
		int resultHashCode = Math.abs(sourceIp.hashCode() + hashCode);
		return servers.get(resultHashCode % servers.size());
	}
}
