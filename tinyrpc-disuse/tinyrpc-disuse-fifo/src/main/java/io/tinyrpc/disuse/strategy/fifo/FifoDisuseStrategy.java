package io.tinyrpc.disuse.strategy.fifo;

import io.tinyrpc.disuse.api.DisuseStrategy;
import io.tinyrpc.disuse.api.connection.ConnectionInfo;
import io.tinyrpc.spi.annotation.SPIClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.List;

/**
 * 判断被存储的时间，离目前最远的数据优先被淘汰。
 */
@SPIClass
public class FifoDisuseStrategy implements DisuseStrategy {

	private static final Logger logger = LoggerFactory.getLogger(FifoDisuseStrategy.class);

	private final Comparator<ConnectionInfo> connectionTimeComparator = (c1, c2) -> c1.getConnectionTime() - c2.getConnectionTime() > 0 ? 1 : -1;

	@Override
	public ConnectionInfo selectConnection(List<ConnectionInfo> connectionList) {
		logger.info("--- fifo disuse strategy ---");
		if (connectionList.isEmpty()) {
			return null;
		}
		connectionList.sort(connectionTimeComparator);
		return connectionList.get(0);
	}
}
