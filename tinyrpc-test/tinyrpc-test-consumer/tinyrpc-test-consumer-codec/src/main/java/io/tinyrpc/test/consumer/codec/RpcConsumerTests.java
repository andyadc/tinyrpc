package io.tinyrpc.test.consumer.codec;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.tinyrpc.test.consumer.codec.init.RpcTestConsumerInitializer;

public class RpcConsumerTests {

	public static void main(String[] args) throws Exception {
		Bootstrap bootstrap = new Bootstrap();
		EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
		try {
			bootstrap.group(eventLoopGroup)
				.channel(NioSocketChannel.class)
				.handler(new RpcTestConsumerInitializer());
			bootstrap.connect("127.0.0.1", 27880).sync();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			Thread.sleep(2000L);
			eventLoopGroup.shutdownGracefully();
		}
	}
}
