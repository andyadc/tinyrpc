package io.tinyrpc.disuse.strategy.random;

import io.tinyrpc.disuse.api.DisuseStrategy;
import io.tinyrpc.disuse.api.connection.ConnectionInfo;
import io.tinyrpc.spi.annotation.SPIClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.SecureRandom;
import java.util.List;

/**
 * 从列表中随机获取一个
 */
@SPIClass
public class RandomDisuseStrategy implements DisuseStrategy {

	private static final Logger logger = LoggerFactory.getLogger(RandomDisuseStrategy.class);

	@Override
	public ConnectionInfo selectConnection(List<ConnectionInfo> connectionList) {
		logger.info("--- random disuse strategy ---");
		if (connectionList.isEmpty()) {
			return null;
		}
		return connectionList.get(new SecureRandom().nextInt(connectionList.size()));
	}
}
