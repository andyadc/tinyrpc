package io.tinyrpc.disuse.strategy.lfu;

import io.tinyrpc.disuse.api.DisuseStrategy;
import io.tinyrpc.disuse.api.connection.ConnectionInfo;
import io.tinyrpc.spi.annotation.SPIClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.List;

/**
 * 在一段时间内，数据被使用次数最少的，优先被淘汰。
 */
@SPIClass
public class LfuDisuseStrategy implements DisuseStrategy {

	private static final Logger logger = LoggerFactory.getLogger(LfuDisuseStrategy.class);

	private final Comparator<ConnectionInfo> useCountComparator = (c1, c2) -> c1.getUseCount() - c2.getUseCount() > 0 ? 1 : -1;

	@Override
	public ConnectionInfo selectConnection(List<ConnectionInfo> connectionList) {
		logger.info("--- lfu disuse strategy ---");
		if (connectionList.isEmpty()) {
			return null;
		}
		connectionList.sort(useCountComparator);
		return connectionList.get(0);
	}
}
