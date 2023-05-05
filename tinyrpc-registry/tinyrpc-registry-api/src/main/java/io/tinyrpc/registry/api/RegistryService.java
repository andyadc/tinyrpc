package io.tinyrpc.registry.api;

import io.tinyrpc.protocol.meta.ServiceMeta;
import io.tinyrpc.registry.api.config.RegistryConfig;
import io.tinyrpc.spi.annotation.SPI;

import java.io.IOException;
import java.util.List;

@SPI
public interface RegistryService {

	/**
	 * 服务注册
	 *
	 * @param serviceMeta 服务元数据
	 */
	void register(ServiceMeta serviceMeta) throws Exception;

	/**
	 * 服务取消注册
	 *
	 * @param serviceMeta 服务元数据
	 */
	void unregister(ServiceMeta serviceMeta) throws Exception;

	/**
	 * 服务发现
	 *
	 * @param serviceName     服务名称
	 * @param invokerHashCode HashCode值
	 * @return 服务元数据
	 */
	ServiceMeta discovery(String serviceName, int invokerHashCode, String sourceIp) throws Exception;

	/**
	 * 从多个元数据列表中根据一定的规则获取一个元数据
	 * @param serviceMetaList 元数据列表
	 * @return 某个特定的元数据
	 */
	ServiceMeta select(List<ServiceMeta> serviceMetaList, int invokerHashCode, String sourceIp);

	/**
	 * 服务销毁
	 */
	void destroy() throws IOException;

	/**
	 * 默认初始化方法
	 */
	default void init(RegistryConfig registryConfig) throws Exception {
	}

}
