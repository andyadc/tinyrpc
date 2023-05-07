package io.tinyrpc.disuse.strategy.first;

import io.tinyrpc.disuse.api.DisuseStrategy;
import io.tinyrpc.disuse.api.connection.ConnectionInfo;
import io.tinyrpc.spi.annotation.SPIClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 获取列表中的第一个
 */
@SPIClass
public class FirstDisuseStrategy implements DisuseStrategy {

	private static final Logger logger = LoggerFactory.getLogger(FirstDisuseStrategy.class);

	@Override
	public ConnectionInfo selectConnection(List<ConnectionInfo> connectionList) {
		logger.info("--- first disuse strategy ---");
		if (connectionList.isEmpty()) {
			return null;
		}
		return connectionList.get(0);
	}
}
