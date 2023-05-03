package io.tinyrpc.provider.common.server.base;

import com.google.common.base.Strings;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import io.tinyrpc.codec.RpcDecoder;
import io.tinyrpc.codec.RpcEncoder;
import io.tinyrpc.common.constants.RpcConstants;
import io.tinyrpc.provider.common.handler.RpcProviderHandler;
import io.tinyrpc.provider.common.manager.ProviderConnectionManager;
import io.tinyrpc.provider.common.server.api.Server;
import io.tinyrpc.registry.api.RegistryService;
import io.tinyrpc.registry.api.config.RegistryConfig;
import io.tinyrpc.spi.loader.ExtensionLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 基础服务
 */
public class BaseServer implements Server {

	private final Logger logger = LoggerFactory.getLogger(BaseServer.class);

	private final String reflectType;
	// 默认主机域名或者IP地址
	protected String host = "127.0.0.1";
	// 默认端口号
	protected int port = 27110;
	// 存储的是实体类关系
	protected Map<String, Object> handlerMap = new HashMap<>();
	protected RegistryService registryService;

	//心跳定时任务线程池
	private ScheduledExecutorService scheduledExecutorService;

	//心跳间隔时间，默认30秒
	private int heartbeatInterval = 30000;
	//扫描并移除空闲连接时间，默认60秒
	private int scanNotActiveChannelInterval = 60000;

	public BaseServer(String serverAddress, String registryAddress, String registryType, String registryLoadBalanceType, String reflectType,
					  int heartbeatInterval, int scanNotActiveChannelInterval) {
		if (!Strings.isNullOrEmpty(serverAddress)) {
			String[] serverArray = serverAddress.split(":");
			this.host = serverArray[0];
			this.port = Integer.parseInt(serverArray[1]);
		}

		if (heartbeatInterval > 0) {
			this.heartbeatInterval = heartbeatInterval;
		}
		if (scanNotActiveChannelInterval > 0) {
			this.scanNotActiveChannelInterval = scanNotActiveChannelInterval;
		}

		this.reflectType = reflectType;
		this.registryService = getRegistryService(registryAddress, registryType, registryLoadBalanceType);
	}

	private RegistryService getRegistryService(String registryAddress, String registryType, String registryLoadBalanceType) {
		RegistryService registryService = ExtensionLoader.getExtension(RegistryService.class, registryType);
		try {
			registryService.init(new RegistryConfig(registryAddress, registryType, registryLoadBalanceType));
		} catch (Exception e) {
			logger.error("RPC Server init error", e);
		}
		return registryService;
	}

	@Override
	public void startNettyServer() {
		// 启动心跳
		this.startHeartbeat();

		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try {
			ServerBootstrap bootstrap = new ServerBootstrap();
			bootstrap
				.group(bossGroup, workerGroup)
				.channel(NioServerSocketChannel.class)
				.childHandler(new ChannelInitializer<SocketChannel>() {
					@Override
					protected void initChannel(SocketChannel ch) throws Exception {
						ch.pipeline()
							.addLast(RpcConstants.CODEC_DECODER, new RpcDecoder())
							.addLast(RpcConstants.CODEC_ENCODER, new RpcEncoder())
							.addLast(RpcConstants.CODEC_SERVER_IDLE_HANDLER,
								new IdleStateHandler(0, 0, heartbeatInterval, TimeUnit.MILLISECONDS))
							.addLast(RpcConstants.CODEC_HANDLER, new RpcProviderHandler(reflectType, handlerMap));
					}
				})
				.option(ChannelOption.SO_BACKLOG, 128)
				.childOption(ChannelOption.SO_KEEPALIVE, true);

			ChannelFuture future = bootstrap.bind(host, port).sync();
			logger.info("Server started on {}:{}", host, port);
			future.channel().closeFuture().sync();
		} catch (Exception e) {
			logger.error("RPC Server start error", e);
		} finally {
			workerGroup.shutdownGracefully();
			bossGroup.shutdownGracefully();
		}
	}

	private void startHeartbeat() {
		scheduledExecutorService = Executors.newScheduledThreadPool(2);

		//扫描并处理所有不活跃的连接
		scheduledExecutorService.scheduleAtFixedRate(() -> {
			logger.info("=== scan Not active Channel ===");
			ProviderConnectionManager.scanNotActiveChannel();
		}, 10 * 1000, scanNotActiveChannelInterval, TimeUnit.MILLISECONDS);

		scheduledExecutorService.scheduleAtFixedRate(() -> {
			logger.info("=== broadcast Ping message from provoder ===");
			ProviderConnectionManager.broadcastPingMessageFromProvider();
		}, 3 * 1000, heartbeatInterval, TimeUnit.MILLISECONDS);
	}
}
