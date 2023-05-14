package io.tinyrpc.registry.zookeeper;

import io.tinyrpc.common.helper.RpcServiceHelper;
import io.tinyrpc.loadbalancer.api.ServiceLoadBalancer;
import io.tinyrpc.loadbalancer.helper.ServiceLoadBalancerHelper;
import io.tinyrpc.protocol.meta.ServiceMeta;
import io.tinyrpc.registry.api.RegistryService;
import io.tinyrpc.registry.api.config.RegistryConfig;
import io.tinyrpc.spi.annotation.SPIClass;
import io.tinyrpc.spi.loader.ExtensionLoader;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.details.JsonInstanceSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

/**
 * 基于Zookeeper的注册服务
 */
@SPIClass
public class ZookeeperRegistryService implements RegistryService {

	private static final Logger logger = LoggerFactory.getLogger(ZookeeperRegistryService.class);

	private static final int BASE_SLEEP_TIME_MS = 1000;
	private static final int MAX_RETRIES = 3;
	private static final String ZK_BASE_PATH = "/tiny_rpc";

	private ServiceDiscovery<ServiceMeta> serviceDiscovery;

	// 负载均衡接口
	private ServiceLoadBalancer<ServiceMeta> serviceLoadBalancer;

	@Override
	public void init(RegistryConfig registryConfig) throws Exception {
		if (logger.isDebugEnabled()) {
			logger.debug("--- Zookeeper Registry ---");
		}
		CuratorFramework client = CuratorFrameworkFactory.newClient(
			registryConfig.getRegistryAddr(),
			new ExponentialBackoffRetry(BASE_SLEEP_TIME_MS, MAX_RETRIES)
		);
		client.start();
		client.blockUntilConnected();

		JsonInstanceSerializer<ServiceMeta> serializer = new JsonInstanceSerializer<>(ServiceMeta.class);
		this.serviceDiscovery = ServiceDiscoveryBuilder.builder(ServiceMeta.class)
			.client(client)
			.serializer(serializer)
			.basePath(ZK_BASE_PATH)
			.build();
		this.serviceDiscovery.start();
		this.serviceLoadBalancer = ExtensionLoader.getExtension(ServiceLoadBalancer.class, registryConfig.getRegistryLoadBalanceType());
	}

	@Override
	public void register(ServiceMeta serviceMeta) throws Exception {
		String registryName = RpcServiceHelper.buildServiceKey(serviceMeta.getServiceName(), serviceMeta.getServiceVersion(), serviceMeta.getServiceGroup());
		ServiceInstance<ServiceMeta> serviceInstance = ServiceInstance
			.<ServiceMeta>builder()
			.name(registryName)
			.address(serviceMeta.getServiceAddr())
			.port(serviceMeta.getServicePort())
			.payload(serviceMeta)
			.build();
		serviceDiscovery.registerService(serviceInstance);
		if (logger.isDebugEnabled()) {
			logger.debug(" --- registered name: {}, payload: {} ---", registryName, serviceMeta);
		}
	}

	@Override
	public void unregister(ServiceMeta serviceMeta) throws Exception {
		ServiceInstance<ServiceMeta> serviceInstance = ServiceInstance
			.<ServiceMeta>builder()
			.name(serviceMeta.getServiceName())
			.address(serviceMeta.getServiceAddr())
			.port(serviceMeta.getServicePort())
			.payload(serviceMeta)
			.build();
		serviceDiscovery.unregisterService(serviceInstance);
		if (logger.isDebugEnabled()) {
			logger.debug("--- unregister service: {} ---", serviceMeta.getServiceName());
		}
	}

	@Override
	public ServiceMeta discovery(String serviceName, int invokerHashCode, String sourceIp) throws Exception {
		Collection<ServiceInstance<ServiceMeta>> serviceInstances = serviceDiscovery.queryForInstances(serviceName);
		return this.serviceLoadBalancer.select(ServiceLoadBalancerHelper.getServiceMetaList((List<ServiceInstance<ServiceMeta>>) serviceInstances), invokerHashCode, sourceIp);
	}

	// TODO
	@Override
	public ServiceMeta select(List<ServiceMeta> serviceMetaList, int invokerHashCode, String sourceIp) {
		return this.serviceLoadBalancer.select(serviceMetaList, invokerHashCode, sourceIp);
	}

	@Override
	public void destroy() throws IOException {
		serviceDiscovery.close();
	}
}
