package io.tinyrpc.transport;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.tinyrpc.Constants;
import io.tinyrpc.codec.RpcDecoder;
import io.tinyrpc.codec.RpcEncoder;

public class RpcServer {

	private final EventLoopGroup bossGroup;
	private final EventLoopGroup workerGroup;
	private final ServerBootstrap serverBootstrap;
	protected int port;
	private Channel channel;

	public RpcServer(int port) {
		this.port = port;

		bossGroup = NettyEventLoopFactory.eventLoopGroup(1, "NettyServerBoss");
		workerGroup = NettyEventLoopFactory.eventLoopGroup(
			Constants.DEFAULT_IO_THREADS,
			"NettyServerWorker"
		);

		serverBootstrap = new ServerBootstrap().group(bossGroup, workerGroup)
			.channel(NioServerSocketChannel.class)
			.option(ChannelOption.SO_REUSEADDR, Boolean.TRUE)
			.childOption(ChannelOption.TCP_NODELAY, Boolean.TRUE)
			.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
			// 指定每个 Channel 上注册的 ChannelHandler 以及顺序
			.handler(new LoggingHandler(LogLevel.INFO)).childHandler(new ChannelInitializer<SocketChannel>() {
				@Override
				protected void initChannel(SocketChannel channel) throws Exception {
					channel.pipeline().addLast("rpc-decoder", new RpcDecoder());
					channel.pipeline().addLast("rpc-encoder", new RpcEncoder());
					channel.pipeline().addLast("server-handler", new RpcServerHandler());
				}
			});
	}

	public ChannelFuture start() {
		// 监听指定端口
		ChannelFuture bind = serverBootstrap.bind(port);
		channel = bind.channel();
		channel.closeFuture();
		return bind;
	}

	public void startAndWait() {
		try {
			channel.closeFuture().await();
		} catch (InterruptedException e) {
			Thread.interrupted();
			e.printStackTrace();
		}
	}

	public void shutdown() throws InterruptedException {
		channel.close().sync();
		if (bossGroup != null)
			bossGroup.shutdownGracefully().awaitUninterruptibly(15_000);
		if (workerGroup != null)
			workerGroup.shutdownGracefully().awaitUninterruptibly(15_000);
	}
}
