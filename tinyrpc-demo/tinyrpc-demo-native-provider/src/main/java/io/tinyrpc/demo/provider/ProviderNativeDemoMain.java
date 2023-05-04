package io.tinyrpc.demo.provider;

import io.tinyrpc.provider.RpcSingleServer;

public class ProviderNativeDemoMain {

	public static void main(String[] args) {
		RpcSingleServer singleServer = new RpcSingleServer(
			"127.0.0.1:27880",
			"127.0.0.1:2181",
			"zookeeper",
			"enhanced_leastconnections",
			"io.tinyrpc.demo",
			"asm",
			30000,
			60000
		);

		singleServer.startNettyServer();
	}
}
