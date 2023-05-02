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
import io.tinyrpc.codec.RpcDecoder;
import io.tinyrpc.codec.RpcEncoder;
import io.tinyrpc.provider.common.handler.RpcProviderHandler;
import io.tinyrpc.provider.common.server.api.Server;
import io.tinyrpc.registry.api.RegistryService;
import io.tinyrpc.registry.api.config.RegistryConfig;
import io.tinyrpc.spi.loader.ExtensionLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * 基础服务
 */
public class BaseServer implements Server {

	private final Logger logger = LoggerFactory.getLogger(BaseServer.class);
	private final String reflectType;
	//主机域名或者IP地址
	protected String host = "127.0.0.1";
	//端口号
	protected int port = 27110;
	//存储的是实体类关系
	protected Map<String, Object> handlerMap = new HashMap<>();
	protected RegistryService registryService;

	public BaseServer(String serverAddress, String registryAddress, String registryType, String registryLoadBalanceType, String reflectType) {
		if (!Strings.isNullOrEmpty(serverAddress)) {
			String[] serverArray = serverAddress.split(":");
			this.host = serverArray[0];
			this.port = Integer.parseInt(serverArray[1]);
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
						// TODO
						ch.pipeline()
							.addLast(new RpcDecoder())
							.addLast(new RpcEncoder())
							.addLast(new RpcProviderHandler(reflectType, handlerMap));
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
}
