package com.tinyrpc.test.netty.pipeline;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class Server {

	public static void main(String[] args) {
		ServerBootstrap bootstrap = new ServerBootstrap();

		NioEventLoopGroup boss = new NioEventLoopGroup();
		NioEventLoopGroup worker = new NioEventLoopGroup();

		bootstrap
			.group(boss, worker)
			.channel(NioServerSocketChannel.class)
			.childHandler(new ChannelInitializer<NioSocketChannel>() {
				@Override
				protected void initChannel(NioSocketChannel channel) throws Exception {
					// inBound，处理读数据的逻辑链
					channel.pipeline().addLast(new InBoundHandlerA());
					channel.pipeline().addLast(new InBoundHandlerB());
					channel.pipeline().addLast(new InBoundHandlerC());

					// outBound，处理写数据的逻辑链
					channel.pipeline().addLast(new OutBoundHandlerA());
					channel.pipeline().addLast(new OutBoundHandlerB());
					channel.pipeline().addLast(new OutBoundHandlerC());
				}
			})
			.bind(9999)
			.addListener(future -> {
				if (future.isSuccess()) {
					System.out.println("Bind success");
				}
			})
		;
	}
}
