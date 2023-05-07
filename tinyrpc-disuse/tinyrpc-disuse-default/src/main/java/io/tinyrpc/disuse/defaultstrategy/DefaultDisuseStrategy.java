package io.tinyrpc.disuse.defaultstrategy;

import io.tinyrpc.disuse.api.DisuseStrategy;
import io.tinyrpc.disuse.api.connection.ConnectionInfo;
import io.tinyrpc.spi.annotation.SPIClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 默认连接，获取列表中的第一个元素
 */
@SPIClass
public class DefaultDisuseStrategy implements DisuseStrategy {

	private static final Logger logger = LoggerFactory.getLogger(DefaultDisuseStrategy.class);

	@Override
	public ConnectionInfo selectConnection(List<ConnectionInfo> connectionList) {
		logger.info("--- default disuse strategy ---");
		return connectionList.get(0);
	}
}
