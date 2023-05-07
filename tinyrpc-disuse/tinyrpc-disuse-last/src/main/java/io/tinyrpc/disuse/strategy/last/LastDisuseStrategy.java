package io.tinyrpc.disuse.strategy.last;

import io.tinyrpc.disuse.api.DisuseStrategy;
import io.tinyrpc.disuse.api.connection.ConnectionInfo;
import io.tinyrpc.spi.annotation.SPIClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 获取连接列表中最后一个连接信息
 */
@SPIClass
public class LastDisuseStrategy implements DisuseStrategy {

	private static final Logger logger = LoggerFactory.getLogger(LastDisuseStrategy.class);

	@Override
	public ConnectionInfo selectConnection(List<ConnectionInfo> connectionList) {
		logger.info("--- last disuse strategy ---");
		if (connectionList.isEmpty()) {
			return null;
		}
		return connectionList.get(connectionList.size() - 1);
	}
}
