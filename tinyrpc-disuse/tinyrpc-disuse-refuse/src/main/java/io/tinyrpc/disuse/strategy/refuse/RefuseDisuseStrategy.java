package io.tinyrpc.disuse.strategy.refuse;

import io.tinyrpc.common.exception.RefuseException;
import io.tinyrpc.disuse.api.DisuseStrategy;
import io.tinyrpc.disuse.api.connection.ConnectionInfo;
import io.tinyrpc.spi.annotation.SPIClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 拒绝新连接
 */
@SPIClass
public class RefuseDisuseStrategy implements DisuseStrategy {

	private static final Logger logger = LoggerFactory.getLogger(RefuseDisuseStrategy.class);

	@Override
	public ConnectionInfo selectConnection(List<ConnectionInfo> connectionList) {
		logger.info("--- refuse disuse strategy ---");
		if (connectionList.isEmpty()) {
			return null;
		}
		throw new RefuseException("refuse new connection...");
	}
}
