package io.tinyrpc.consumer.common;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
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

	public void sendRequest(RpcProtocol<RpcRequest> protocol) throws Exception {
		//TODO 暂时写死，后续在引入注册中心时，从注册中心获取
		String serviceAddress = "127.0.0.1";
		int port = 27880;
		String key = serviceAddress.concat("_").concat(String.valueOf(port));

		RpcConsumerHandler handler = handlerMap.get(key);

	}

}
