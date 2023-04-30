package io.tinyrpc.registry.api;

import io.tinyrpc.protocol.meta.ServiceMeta;
import io.tinyrpc.registry.api.config.RegistryConfig;

import java.io.IOException;

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
	ServiceMeta discovery(String serviceName, int invokerHashCode) throws Exception;

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
