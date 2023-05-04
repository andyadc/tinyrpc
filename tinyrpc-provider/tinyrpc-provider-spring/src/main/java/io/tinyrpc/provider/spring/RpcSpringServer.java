package io.tinyrpc.provider.spring;

import io.tinyrpc.annotation.RpcService;
import io.tinyrpc.common.constants.RpcConstants;
import io.tinyrpc.common.helper.RpcServiceHelper;
import io.tinyrpc.protocol.meta.ServiceMeta;
import io.tinyrpc.provider.common.server.base.BaseServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Map;

/**
 * 基于Spring启动RPC服务
 */
public class RpcSpringServer extends BaseServer implements ApplicationContextAware, InitializingBean {

	private static final Logger logger = LoggerFactory.getLogger(RpcSpringServer.class);

	public RpcSpringServer(String serverAddress, String registryAddress, String registryType, String registryLoadBalanceType, String reflectType, int heartbeatInterval, int scanNotActiveChannelInterval) {
		super(serverAddress, registryAddress, registryType, registryLoadBalanceType, reflectType, heartbeatInterval, scanNotActiveChannelInterval);
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		Map<String, Object> serviceBeanMap = applicationContext.getBeansWithAnnotation(RpcService.class);
		if (serviceBeanMap.size() > 0) {
			for (Object serviceBean : serviceBeanMap.values()) {
				RpcService rpcService = serviceBean.getClass().getAnnotation(RpcService.class);
				ServiceMeta serviceMeta = new ServiceMeta(this.getServiceName(rpcService), rpcService.version(), rpcService.group(), host, port, getWeight(rpcService.weight()));
				handlerMap.put(RpcServiceHelper.buildServiceKey(serviceMeta.getServiceName(), serviceMeta.getServiceVersion(), serviceMeta.getServiceGroup()), serviceBean);

				try {
					registryService.register(serviceMeta);
				} catch (Exception e) {
					logger.error("spring rpc init error", e);
				}
			}
		} else {
			logger.info("Not found Bean annotated for [RpcService]");
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		this.startNettyServer();
	}

	private int getWeight(int weight) {
		if (weight < RpcConstants.SERVICE_WEIGHT_MIN) {
			weight = RpcConstants.SERVICE_WEIGHT_MIN;
		}
		if (weight > RpcConstants.SERVICE_WEIGHT_MAX) {
			weight = RpcConstants.SERVICE_WEIGHT_MAX;
		}
		return weight;
	}

	/**
	 * 获取serviceName
	 */
	private String getServiceName(RpcService rpcService) {
		// 优先使用interfaceClass
		Class<?> clazz = rpcService.interfaceClass();
		if (clazz == void.class) {
			return rpcService.interfaceClassName();
		}
		String serviceName = clazz.getName();
		if (serviceName.trim().isEmpty()) {
			serviceName = rpcService.interfaceClassName();
		}
		return serviceName;
	}
}