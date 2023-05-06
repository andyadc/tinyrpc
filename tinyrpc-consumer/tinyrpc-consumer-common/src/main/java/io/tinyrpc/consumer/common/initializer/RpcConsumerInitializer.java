package io.tinyrpc.consumer.common.initializer;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import io.tinyrpc.codec.RpcDecoder;
import io.tinyrpc.codec.RpcEncoder;
import io.tinyrpc.constant.RpcConstants;
import io.tinyrpc.common.threadpool.ConcurrentThreadPool;
import io.tinyrpc.consumer.common.handler.RpcConsumerHandler;
import io.tinyrpc.flow.processor.FlowPostProcessor;

import java.util.concurrent.TimeUnit;

/**
 * RpcConsumerInitializer
 */
public class RpcConsumerInitializer extends ChannelInitializer<SocketChannel> {

	private final ConcurrentThreadPool concurrentThreadPool;
	private final FlowPostProcessor flowPostProcessor;
	private int heartbeatInterval;

	public RpcConsumerInitializer(int heartbeatInterval,
								  ConcurrentThreadPool concurrentThreadPool,
								  FlowPostProcessor flowPostProcessor) {
		if (heartbeatInterval > 0) {
			this.heartbeatInterval = heartbeatInterval;
		}
		this.concurrentThreadPool = concurrentThreadPool;
		this.flowPostProcessor = flowPostProcessor;
	}

	@Override
	protected void initChannel(SocketChannel channel) throws Exception {
		ChannelPipeline pipeline = channel.pipeline();
		pipeline.addLast(RpcConstants.CODEC_ENCODER, new RpcEncoder(flowPostProcessor));
		pipeline.addLast(RpcConstants.CODEC_DECODER, new RpcDecoder(flowPostProcessor));
		pipeline.addLast(RpcConstants.CODEC_CLIENT_IDLE_HANDLER,
			new IdleStateHandler(heartbeatInterval, 0, 0, TimeUnit.MILLISECONDS));
		pipeline.addLast(RpcConstants.CODEC_HANDLER, new RpcConsumerHandler(concurrentThreadPool));
	}
}
