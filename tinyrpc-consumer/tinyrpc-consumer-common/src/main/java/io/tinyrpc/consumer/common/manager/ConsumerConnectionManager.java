package io.tinyrpc.consumer.common.manager;

import io.netty.channel.Channel;
import io.tinyrpc.constant.RpcConstants;
import io.tinyrpc.consumer.common.cache.ConsumerChannelCache;
import io.tinyrpc.protocol.RpcProtocol;
import io.tinyrpc.protocol.enumeration.RpcType;
import io.tinyrpc.protocol.header.RpcHeader;
import io.tinyrpc.protocol.header.RpcHeaderFactory;
import io.tinyrpc.protocol.request.RpcRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

/**
 * 服务消费者连接管理器
 */
public class ConsumerConnectionManager {

	private static final Logger logger = LoggerFactory.getLogger(ConsumerConnectionManager.class);

	/**
	 * 扫描并移除不活跃的连接
	 */
	public static void scanNotActiveChannel() {
		Set<Channel> channelCache = ConsumerChannelCache.getChannelCache();
		if (channelCache == null || channelCache.isEmpty()) {
			return;
		}
		channelCache.forEach((channel) -> {
			if (!channel.isOpen() || !channel.isActive()) {
				channel.close();
				ConsumerChannelCache.remove(channel);
			}
		});
	}

	/**
	 * 发送ping消息
	 */
	public static void broadcastPingMessageFromConsumer() {
		Set<Channel> channelCache = ConsumerChannelCache.getChannelCache();
		if (channelCache == null || channelCache.isEmpty()) {
			logger.warn("No active channel.");
			return;
		}

		RpcHeader header = RpcHeaderFactory.getRequestHeader(RpcConstants.SERIALIZATION_PROTOSTUFF, RpcType.HEARTBEAT_FROM_CONSUMER.getType());
		RpcProtocol<RpcRequest> requestRpcProtocol = new RpcProtocol<>();
		RpcRequest rpcRequest = new RpcRequest();
		rpcRequest.setParameters(new Object[]{RpcConstants.HEARTBEAT_PING});
		requestRpcProtocol.setHeader(header);
		requestRpcProtocol.setBody(rpcRequest);
		channelCache.forEach((channel) -> {
			if (channel.isOpen() && channel.isActive()) {
				logger.info("Send heartbeat message to service provider, the provider is: {}, the heartbeat message is: {}", channel.remoteAddress(), RpcConstants.HEARTBEAT_PING);
				channel.writeAndFlush(requestRpcProtocol);
			}
		});
	}
}
