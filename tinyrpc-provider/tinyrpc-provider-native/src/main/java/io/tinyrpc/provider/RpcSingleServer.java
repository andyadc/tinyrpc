package io.tinyrpc.provider;

import io.tinyrpc.provider.common.scanner.RpcServiceScanner;
import io.tinyrpc.provider.common.server.base.BaseServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 以Java原生方式启动启动Rpc
 */
public class RpcSingleServer extends BaseServer {

	private static final Logger logger = LoggerFactory.getLogger(RpcSingleServer.class);

	public RpcSingleServer(String serverAddress, String registryAddress,
						   String registryType, String registryLoadBalanceType,
						   String scanPackage, String reflectType,
						   int heartbeatInterval, int scanNotActiveChannelInterval,
						   boolean enableResultCache, int resultCacheExpire,
						   int corePoolSize, int maximumPoolSize, String flowType,
						   int maxConnections, String disuseStrategyType, boolean enableBuffer, int bufferSize) {

		super(serverAddress, registryAddress, registryType, registryLoadBalanceType, reflectType,
			heartbeatInterval, scanNotActiveChannelInterval, enableResultCache, resultCacheExpire,
			corePoolSize, maximumPoolSize, flowType, maxConnections, disuseStrategyType,
			enableBuffer, bufferSize);

		try {
			this.handlerMap = RpcServiceScanner.doScannerWithRpcServiceAnnotationFilterAndRegistryService(
				this.host, this.port,
				scanPackage, registryService);
		} catch (Exception e) {
			logger.error("RPC Server init error.", e);
		}
	}
}
