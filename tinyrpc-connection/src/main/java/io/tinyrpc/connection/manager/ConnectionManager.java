package io.tinyrpc.connection.manager;

import io.netty.channel.Channel;
import io.tinyrpc.common.exception.RefuseException;
import io.tinyrpc.common.utils.StringUtil;
import io.tinyrpc.constant.RpcConstants;
import io.tinyrpc.disuse.api.DisuseStrategy;
import io.tinyrpc.disuse.api.connection.ConnectionInfo;
import io.tinyrpc.spi.loader.ExtensionLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 连接管理器
 */
public class ConnectionManager {

	private static final Logger logger = LoggerFactory.getLogger(ConnectionManager.class);

	private static volatile ConnectionManager instance;
	private final DisuseStrategy disuseStrategy;
	private final int maxConnections;
	private final Map<String, ConnectionInfo> connectionMap = new ConcurrentHashMap<>();

	private ConnectionManager(int maxConnections, String disuseStrategyType) {
		this.maxConnections = maxConnections <= 0 ? Integer.MAX_VALUE : maxConnections;
		disuseStrategyType = StringUtil.isEmpty(disuseStrategyType) ? RpcConstants.RPC_CONNECTION_DISUSE_STRATEGY_DEFAULT : disuseStrategyType;
		this.disuseStrategy = ExtensionLoader.getExtension(DisuseStrategy.class, disuseStrategyType);
	}

	/**
	 * 单例模式
	 */
	public static ConnectionManager getInstance(int maxConnections, String disuseStrategyType) {
		if (instance == null) {
			synchronized (ConnectionManager.class) {
				if (instance == null) {
					instance = new ConnectionManager(maxConnections, disuseStrategyType);
				}
			}
		}
		return instance;
	}

	/**
	 * 添加连接
	 */
	public void add(Channel channel) {
		ConnectionInfo info = new ConnectionInfo(channel);
		if (this.checkConnectionList(info)) {
			connectionMap.put(getKey(channel), info);
		}
	}

	/**
	 * 移除连接
	 */
	public void remove(Channel channel) {
		connectionMap.remove(getKey(channel));
	}

	/**
	 * 更新连接信息
	 */
	public void update(Channel channel) {
		ConnectionInfo info = connectionMap.get(getKey(channel));
		info.setLastUseTime(System.currentTimeMillis());
		info.incrementUseCount();
		connectionMap.put(getKey(channel), info);
	}

	/**
	 * 检测连接列表
	 */
	private boolean checkConnectionList(ConnectionInfo info) {
		List<ConnectionInfo> connectionList = new ArrayList<>(connectionMap.values());
		if (connectionList.size() >= maxConnections) {
			try {
				ConnectionInfo cacheConnectionInfo = disuseStrategy.selectConnection(connectionList);
				if (cacheConnectionInfo != null) {
					cacheConnectionInfo.getChannel().close();
					connectionMap.remove(getKey(cacheConnectionInfo.getChannel()));
				}
			} catch (RefuseException e) {
				logger.info("connection refuse", e);
				info.getChannel().close();
				return false;
			}
		}
		return true;
	}

	private String getKey(Channel channel) {
		return channel.id().asLongText();
	}
}
