package io.tinyrpc.consumer.common;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.tinyrpc.consumer.common.future.RPCFuture;
import io.tinyrpc.consumer.common.handler.RpcConsumerHandler;
import io.tinyrpc.consumer.common.initializer.RpcConsumerInitializer;
import io.tinyrpc.protocol.RpcProtocol;
import io.tinyrpc.protocol.request.RpcRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RpcConsumer {

	private static final Logger logger = LoggerFactory.getLogger(RpcConsumer.class);
	private static final Map<String, RpcConsumerHandler> handlerMap = new ConcurrentHashMap<>();
	private static volatile RpcConsumer instance;
	private final Bootstrap bootstrap;
	private final EventLoopGroup eventLoopGroup;

	private RpcConsumer() {
		bootstrap = new Bootstrap();
		eventLoopGroup = new NioEventLoopGroup(4);
		bootstrap
			.group(eventLoopGroup)
			.channel(NioSocketChannel.class)
			.handler(new RpcConsumerInitializer());
	}

	public static RpcConsumer getInstance() {
		if (instance == null) {
			synchronized (RpcConsumer.class) {
				if (instance == null) {
					instance = new RpcConsumer();
				}
			}
		}
		return instance;
	}

	public void close() {
		eventLoopGroup.shutdownGracefully();
	}

	public RPCFuture sendRequest(RpcProtocol<RpcRequest> protocol) throws Exception {
		//TODO 暂时写死，后续在引入注册中心时，从注册中心获取
		String serviceAddress = "127.0.0.1";
		int port = 27880;
		String key = serviceAddress.concat("_").concat(String.valueOf(port));

		RpcConsumerHandler handler = handlerMap.get(key);
		//缓存中无RpcClientHandler
		if (handler == null) {
			handler = getRpcConsumerHandler(serviceAddress, port);
			handlerMap.put(key, handler);
		} else if (!handler.getChannel().isActive()) {  // 缓存中存在RpcClientHandler，但不活跃
			handler.close();
			handler = getRpcConsumerHandler(serviceAddress, port);
			handlerMap.put(key, handler);
		}
		return handler.sendRequest(protocol);
	}

	/**
	 * 创建连接并返回RpcClientHandler
	 */
	private RpcConsumerHandler getRpcConsumerHandler(String serviceAddress, int port) throws InterruptedException {
		ChannelFuture channelFuture = bootstrap.connect(serviceAddress, port).sync();
		channelFuture.addListener(new ChannelFutureListener() {
			@Override
			public void operationComplete(ChannelFuture channelFuture) throws Exception {
				if (channelFuture.isSuccess()) {
					logger.info("connect rpc server {} on port {} success.", serviceAddress, port);
				} else {
					logger.error("connect rpc server {} on port {} failed.", serviceAddress, port);
					channelFuture.cause().printStackTrace();
					eventLoopGroup.shutdownGracefully();
				}
			}
		});
		return channelFuture.channel().pipeline().get(RpcConsumerHandler.class);
	}
}
