package io.tinyrpc.provider.common.scanner;

import io.tinyrpc.annotation.RpcService;
import io.tinyrpc.constant.RpcConstants;
import io.tinyrpc.common.helper.RpcServiceHelper;
import io.tinyrpc.common.scanner.ClassScanner;
import io.tinyrpc.protocol.meta.ServiceMeta;
import io.tinyrpc.registry.api.RegistryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * {@link RpcService} 注解扫描器
 */
public class RpcServiceScanner extends ClassScanner {

	private static final Logger logger = LoggerFactory.getLogger(RpcServiceScanner.class);

	/**
	 * 扫描指定包下的类，并筛选使用@RpcService注解标注的类
	 */
	public static Map<String, Object> doScannerWithRpcServiceAnnotationFilterAndRegistryService(
		String host, int port, String scanPackage, RegistryService registryService) throws Exception {
		Map<String, Object> handlerMap = new HashMap<>();
		List<String> classNameList = getClassNameList(scanPackage);
		if (classNameList.isEmpty()) {
			return handlerMap;
		}

		for (String className : classNameList) {
			try {
				Class<?> clazz = Class.forName(className);
				RpcService rpcService = clazz.getAnnotation(RpcService.class);
				if (rpcService != null) {
					//优先使用interfaceClass, interfaceClass的name为空，再使用interfaceClassName
					//handlerMap中的 Key先简单存储为serviceName+version+group，后续根据实际情况处理key
					String serviceName = getServiceName(rpcService);
					String key = RpcServiceHelper.buildServiceKey(serviceName, rpcService.version(), rpcService.group());
					handlerMap.put(key, clazz.newInstance());

					//将元数据注册到注册中心
					ServiceMeta serviceMeta = new ServiceMeta(serviceName, rpcService.version(), rpcService.group(), host, port, getWeight(rpcService.weight()));
					registryService.register(serviceMeta);
				}
			} catch (Exception e) {
				logger.error("RpcService Scanner error.", e);
			}
		}
		return handlerMap;
	}

	private static int getWeight(int weight) {
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
	private static String getServiceName(RpcService rpcService) {
		//优先使用interfaceClass
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
