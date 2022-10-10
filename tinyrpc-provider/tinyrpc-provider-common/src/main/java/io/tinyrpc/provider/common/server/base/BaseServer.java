package io.tinyrpc.provider.common.server.base;

import io.tinyrpc.provider.common.server.api.Server;

/**
 * 基础服务
 */
public class BaseServer implements Server {

	private final Logger logger = LoggerFactory.getLogger(BaseServer.class);

	@Override
	public void startNettyServer() {

	}
}
