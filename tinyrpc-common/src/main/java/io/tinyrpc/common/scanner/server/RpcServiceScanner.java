package io.tinyrpc.common.scanner.server;

import io.tinyrpc.annotation.RpcService;
import io.tinyrpc.common.scanner.ClassScanner;
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
		/*String host, int port, */ String scanPackage /*, RegistryService registryService*/) throws Exception {
		Map<String, Object> handlerMap = new HashMap<>();
		List<String> classNameList = getClassNameList(scanPackage);
		if (classNameList.isEmpty()) {
			return handlerMap;
		}

		classNameList.forEach((className) -> {
			try {
				Class<?> clazz = Class.forName(className);
				RpcService rpcService = clazz.getAnnotation(RpcService.class);
				if (rpcService != null) {
					//优先使用interfaceClass, interfaceClass的name为空，再使用interfaceClassName
					//TODO 后续逻辑向注册中心注册服务元数据，同时向handlerMap中记录标注了RpcService注解的类实例
					logger.info("当前标注了@RpcService注解的类实例名称===>>> " + clazz.getName());
					logger.info("@RpcService注解上标注的属性信息如下：");
					logger.info("interfaceClass===>>> " + rpcService.interfaceClass().getName());
					logger.info("interfaceClassName===>>> " + rpcService.interfaceClassName());
					logger.info("version===>>> " + rpcService.version());
					logger.info("group===>>> " + rpcService.group());
				}
			} catch (Exception e) {
				logger.error("RpcService Scanner throws exception.", e);
			}
		});

		return handlerMap;
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
