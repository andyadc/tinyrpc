package io.tinyrpc.disuse.api;

import io.tinyrpc.constant.RpcConstants;
import io.tinyrpc.disuse.api.connection.ConnectionInfo;
import io.tinyrpc.spi.annotation.SPI;

import java.util.List;

/**
 * 淘汰策略
 */
@SPI(RpcConstants.RPC_CONNECTION_DISUSE_STRATEGY_DEFAULT)
public interface DisuseStrategy {

	/**
	 * 从连接列表中根据规则获取一个连接对象
	 */
	ConnectionInfo selectConnection(List<ConnectionInfo> connectionList);
}
