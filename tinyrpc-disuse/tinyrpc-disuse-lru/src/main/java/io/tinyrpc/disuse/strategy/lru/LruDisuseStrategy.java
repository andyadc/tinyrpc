package io.tinyrpc.disuse.strategy.lru;

import io.tinyrpc.disuse.api.DisuseStrategy;
import io.tinyrpc.disuse.api.connection.ConnectionInfo;
import io.tinyrpc.spi.annotation.SPIClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.List;

/**
 * 断最近被使用的时间，目前最远的数据优先被淘汰。
 */
@SPIClass
public class LruDisuseStrategy implements DisuseStrategy {

	private static final Logger logger = LoggerFactory.getLogger(LruDisuseStrategy.class);

	private final Comparator<ConnectionInfo> lastUseTimeComparator = (c1, c2) -> c1.getLastUseTime() - c2.getLastUseTime() > 0 ? 1 : -1;

	@Override
	public ConnectionInfo selectConnection(List<ConnectionInfo> connectionList) {
		logger.info("--- lru disuse strategy ---");
		if (connectionList.isEmpty()) {
			return null;
		}
		connectionList.sort(lastUseTimeComparator);
		return connectionList.get(0);
	}
}
