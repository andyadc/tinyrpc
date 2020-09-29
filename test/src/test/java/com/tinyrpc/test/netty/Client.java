package com.tinyrpc.test.netty;

import com.tinyrpc.test.netty.handler.IMClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

public class Client {

	public static void main(String[] args) {
		Bootstrap bootstrap = new Bootstrap();

		bootstrap
			.group(new NioEventLoopGroup())
			.channel(NioSocketChannel.class)
			.handler(new ChannelInitializer<Channel>() {
				@Override
				protected void initChannel(Channel channel) throws Exception {
//					channel.pipeline().addLast(new FirstClientHandler());
					channel.pipeline().addLast(new IMClientHandler());
				}
			})
			.connect("127.0.0.1", 9999)
		;
	}
}
