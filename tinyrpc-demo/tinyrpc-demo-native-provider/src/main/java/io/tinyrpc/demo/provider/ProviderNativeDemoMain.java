package io.tinyrpc.demo.provider;

import io.tinyrpc.provider.RpcSingleServer;

public class ProviderNativeDemoMain {

	public static void main(String[] args) {
		RpcSingleServer singleServer = new RpcSingleServer(
			"127.0.0.1:27880",
			"127.0.0.1:2181",
			"zookeeper",
			"enhanced_zkconsistenthash",
			"io.tinyrpc.demo",
			"asm",
			30000,
			60000,
			false,
			15000,
			5,
			10,
			"print",
			1,
			"strategy_default",
			false,
			2,
			false,
			"guava",
			1,
			5000,
			"exception",
			true,
			"counter",
			1D,
			5000
		);

		singleServer.startNettyServer();
	}
}
