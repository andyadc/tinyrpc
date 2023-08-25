package io.tinyrpc.common.helper;

import io.tinyrpc.annotation.RpcService;

/**
 * RPC服务帮助类
 */
public class RpcServiceHelper {

	/**
	 * 拼接字符串
	 *
	 * @param serviceName    服务名称
	 * @param serviceVersion 服务版本号
	 * @param group          服务分组
	 * @return 服务名称#服务版本号#服务分组
	 */
	public static String buildServiceKey(String serviceName, String serviceVersion, String group) {
		return String.join("#", serviceName, serviceVersion, group);
	}

	/**
	 * 获取serviceName
	 */
	public static String getServiceName(RpcService rpcService) {
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
