package io.tinyrpc.test.consumer.codec.init;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.tinyrpc.codec.RpcDecoder;
import io.tinyrpc.codec.RpcEncoder;
import io.tinyrpc.test.consumer.codec.handler.RpcTestConsumerHandler;

public class RpcTestConsumerInitializer extends ChannelInitializer<SocketChannel> {

	@Override
	protected void initChannel(SocketChannel socketChannel) throws Exception {
		ChannelPipeline cp = socketChannel.pipeline();
		cp.addLast(new RpcEncoder(null));
		cp.addLast(new RpcDecoder(null));
		cp.addLast(new RpcTestConsumerHandler());
	}
}
