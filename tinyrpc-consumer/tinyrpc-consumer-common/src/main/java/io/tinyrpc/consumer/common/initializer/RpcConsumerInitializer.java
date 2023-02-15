package io.tinyrpc.consumer.common.initializer;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.tinyrpc.codec.RpcDecoder;
import io.tinyrpc.codec.RpcEncoder;
import io.tinyrpc.consumer.common.handler.RpcConsumerHandler;

/**
 * RpcConsumerInitializer
 */
public class RpcConsumerInitializer extends ChannelInitializer<SocketChannel> {

	@Override
	protected void initChannel(SocketChannel channel) throws Exception {
		ChannelPipeline pipeline = channel.pipeline();
		pipeline.addLast(new RpcEncoder());
		pipeline.addLast(new RpcDecoder());
		pipeline.addLast(new RpcConsumerHandler());
	}
}
