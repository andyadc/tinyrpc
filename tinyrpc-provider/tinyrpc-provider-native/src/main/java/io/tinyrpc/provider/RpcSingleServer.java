package io.tinyrpc.provider;

import io.tinyrpc.common.scanner.server.RpcServiceScanner;
import io.tinyrpc.provider.common.server.base.BaseServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 以Java原生方式启动启动Rpc
 */
public class RpcSingleServer extends BaseServer {

	private final Logger logger = LoggerFactory.getLogger(RpcSingleServer.class);

	public RpcSingleServer(String serverAddress, String scanPackage) {
		super(serverAddress);

		try {
			this.handlerMap = RpcServiceScanner.doScannerWithRpcServiceAnnotationFilterAndRegistryService(scanPackage);
		} catch (Exception e) {
			logger.error("RPC Server init error", e);
		}
	}
}
