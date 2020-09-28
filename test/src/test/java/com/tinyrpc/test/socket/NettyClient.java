package com.tinyrpc.test.socket;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.AttributeKey;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class NettyClient {
	final static int MAX_RETRY_TIMES = 3;

	public static void main(String[] args) throws InterruptedException {
		Bootstrap bootstrap = new Bootstrap();
		NioEventLoopGroup group = new NioEventLoopGroup();

		bootstrap.group(group) // 指定线程模型
			.channel(NioSocketChannel.class) // 指定 IO 类型为 NIO
			.handler(new ChannelInitializer<Channel>() { // IO 处理逻辑
				@Override
				protected void initChannel(Channel channel) throws Exception {
					channel.pipeline().addLast(new StringEncoder());
				}
			})
			.attr(AttributeKey.newInstance("clientName"), "nettyClient")
			.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
			.option(ChannelOption.SO_KEEPALIVE, true)
			.option(ChannelOption.TCP_NODELAY, true)
		;

		connect(bootstrap, "127.0.0.1", 9999, MAX_RETRY_TIMES);
		/**
		 Channel channel = bootstrap.connect("127.0.0.1", 9999)
		 .addListener(future -> {
		 if (future.isSuccess()) {
		 System.out.println("连接成功!");
		 } else {
		 System.err.println("连接失败!");
		 }
		 })
		 .channel();

		 while (true) {
		 channel.writeAndFlush("Hello netty " + LocalDateTime.now());
		 TimeUnit.SECONDS.sleep(1L);
		 }
		 */
	}

	private static void connect(final Bootstrap bootstrap, String host, int port, int retry) {
		bootstrap.connect(host, port).addListener(future -> {
			if (future.isSuccess()) {
				System.out.println("连接成功!");
			} else if (retry == 0) {
				System.err.println("重试次数已用完，放弃连接！");
			} else {
				// 第几次重连
				int order = (MAX_RETRY_TIMES - retry) + 1;
				// 本次重连的间隔
				long delay = 1 << order;
				System.err.println(new Date() + ": 连接失败, 第" + order + "次重连...");
				bootstrap.config().group().schedule(() -> {
					connect(bootstrap, host, port, retry - 1);
				}, delay, TimeUnit.SECONDS);
			}
		});
	}
}
