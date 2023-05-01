package io.tinyrpc.loadbalancer.ip.hash;

import io.tinyrpc.loadbalancer.api.ServiceLoadBalancer;
import io.tinyrpc.spi.annotation.SPIClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 基于源IP地址Hash的负载均衡策略
 *
 * @param <T>
 */
@SPIClass
public class SourceIpHashServiceLoadBalancer<T> implements ServiceLoadBalancer<T> {

	private static final Logger logger = LoggerFactory.getLogger(SourceIpHashServiceLoadBalancer.class);

	@Override
	public T select(List<T> servers, int hashCode, String sourceIp) {
		logger.info("--- SourceIp Hash LoadBalancer ---");
		if (servers == null || servers.isEmpty()) {
			return null;
		}

		//传入的IP地址为空，则默认返回第一个服务实例
		if (sourceIp == null || sourceIp.isEmpty()) {
			return servers.get(0);
		}
		int resultHashCode = Math.abs(sourceIp.hashCode() + hashCode);
		return servers.get(resultHashCode % servers.size());
	}
}
