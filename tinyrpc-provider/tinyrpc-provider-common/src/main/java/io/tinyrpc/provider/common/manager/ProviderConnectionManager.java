package io.tinyrpc.provider.common.manager;

import io.netty.channel.Channel;
import io.tinyrpc.constant.RpcConstants;
import io.tinyrpc.protocol.RpcProtocol;
import io.tinyrpc.protocol.enumeration.RpcType;
import io.tinyrpc.protocol.header.RpcHeader;
import io.tinyrpc.protocol.header.RpcHeaderFactory;
import io.tinyrpc.protocol.response.RpcResponse;
import io.tinyrpc.provider.common.cache.ProviderChannelCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

/**
 * 服务提供者连接管理器
 *
 * <p>
 * 其实心跳机制中，服务消费者与服务提供者之间的数据交互有一个非常重要的点需要注意：
 * 服务消费者向服务提供者发送消息时，需要将消息封装成RpcProtocol<RpcRequest>对象，
 * 而服务提供者向服务消费者发送消息时，需要将消息封装成RpcProtocol<RpcResponse>对象。
 * 所以，在实现服务提供者主动向服务消费者发送心跳消息时，需要将心跳消息封装成 RpcProtocol<RpcResponse>对象，
 * 而服务消费者响应服务提供者的心跳消息时，需要将消息封装成RpcProtocol<RpcRequest>对象。
 * </p>
 */
public class ProviderConnectionManager {

	private static final Logger logger = LoggerFactory.getLogger(ProviderConnectionManager.class);

	/**
	 * 扫描并移除不活跃的连接
	 */
	public static void scanNotActiveChannel() {
		Set<Channel> channelCache = ProviderChannelCache.getChannelCache();
		if (channelCache == null || channelCache.isEmpty()) return;
		channelCache.forEach((channel) -> {
			if (!channel.isOpen() || !channel.isActive()) {
				channel.close();
				ProviderChannelCache.remove(channel);
			}
		});
	}

	/**
	 * 发送ping消息
	 */
	public static void broadcastPingMessageFromProvider() {
		Set<Channel> channelCache = ProviderChannelCache.getChannelCache();
		if (channelCache == null || channelCache.isEmpty()) {
			logger.warn("No active channel.");
			return;
		}
		RpcHeader header = RpcHeaderFactory.getRequestHeader(RpcConstants.SERIALIZATION_PROTOSTUFF, RpcType.HEARTBEAT_FROM_PROVIDER.getType());
		RpcProtocol<RpcResponse> responseRpcProtocol = new RpcProtocol<>();
		RpcResponse rpcResponse = new RpcResponse();
		rpcResponse.setResult(RpcConstants.HEARTBEAT_PING);
		responseRpcProtocol.setHeader(header);
		responseRpcProtocol.setBody(rpcResponse);
		channelCache.forEach((channel) -> {
			if (channel.isOpen() && channel.isActive()) {
				logger.info("send heartbeat message to service consumer, the consumer is: {}, the heartbeat message is: {}", channel.remoteAddress(), rpcResponse.getResult());
				channel.writeAndFlush(responseRpcProtocol);
			}
		});
	}
}
