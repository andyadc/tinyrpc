package io.tinyrpc.consumer.common.handler;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;
import io.tinyrpc.buffer.cache.BufferCacheManager;
import io.tinyrpc.common.threadpool.BufferCacheThreadPool;
import io.tinyrpc.common.threadpool.ConcurrentThreadPool;
import io.tinyrpc.common.utils.JsonUtil;
import io.tinyrpc.constant.RpcConstants;
import io.tinyrpc.consumer.common.cache.ConsumerChannelCache;
import io.tinyrpc.consumer.common.context.RpcContext;
import io.tinyrpc.protocol.RpcProtocol;
import io.tinyrpc.protocol.enumeration.RpcStatus;
import io.tinyrpc.protocol.enumeration.RpcType;
import io.tinyrpc.protocol.header.RpcHeader;
import io.tinyrpc.protocol.header.RpcHeaderFactory;
import io.tinyrpc.protocol.request.RpcRequest;
import io.tinyrpc.protocol.response.RpcResponse;
import io.tinyrpc.proxy.api.future.RPCFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.SocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * RPC消费者处理器
 */
public class RpcConsumerHandler extends SimpleChannelInboundHandler<RpcProtocol<RpcResponse>> {

	private static final Logger logger = LoggerFactory.getLogger(RpcConsumerHandler.class);
	private final Map<Long, RPCFuture> pendingRPC = new ConcurrentHashMap<>();
	private final ConcurrentThreadPool concurrentThreadPool;

	// 是否开启缓冲区
	private final boolean enableBuffer;
	private volatile Channel channel;
	// 存储请求ID与RpcResponse协议的映射关系
//	private final Map<Long, RpcProtocol<RpcResponse>> pendingResponse = new ConcurrentHashMap<>();
	private SocketAddress remotePeer;
	// 缓冲区管理器
	private BufferCacheManager<RpcProtocol<RpcResponse>> bufferCacheManager;

	public RpcConsumerHandler(boolean enableBuffer, int bufferSize, ConcurrentThreadPool concurrentThreadPool) {
		this.concurrentThreadPool = concurrentThreadPool;
		this.enableBuffer = enableBuffer;
		if (enableBuffer) {
			this.bufferCacheManager = BufferCacheManager.getInstance(bufferSize);
			BufferCacheThreadPool.submit(this::consumeBufferCache);
		}
	}

	/**
	 * 消费缓冲区数据
	 */
	private void consumeBufferCache() {
		//不断消息缓冲区的数据
		while (true) {
			RpcProtocol<RpcResponse> protocol = this.bufferCacheManager.take();
			if (protocol != null) {
				this.handleResponseMessage(protocol);
			}
		}
	}

	/**
	 * 包含是否开启了缓冲区的响应消息
	 */
	private void handleResponseMessageOrBuffer(RpcProtocol<RpcResponse> protocol) {
		if (enableBuffer) {
			logger.info("--- enable buffer ---");
			this.bufferCacheManager.put(protocol);
		} else {
			this.handleResponseMessage(protocol);
		}
	}

	public Channel getChannel() {
		return channel;
	}

	public SocketAddress getRemotePeer() {
		return remotePeer;
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		super.channelActive(ctx);
		this.remotePeer = this.channel.remoteAddress();

		ConsumerChannelCache.add(channel);
	}

	@Override
	public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
		super.channelRegistered(ctx);
		this.channel = ctx.channel();
	}

	@Override
	protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcProtocol<RpcResponse> protocol) throws Exception {
		if (protocol == null) {
			return;
		}
		concurrentThreadPool.submit(() -> {
			handleMessage(protocol, channelHandlerContext.channel());
		});
	}

	@Override
	public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
		super.channelUnregistered(ctx);

		ConsumerChannelCache.remove(channel);
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		super.channelInactive(ctx);

		ConsumerChannelCache.remove(channel);
	}

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		if (evt instanceof IdleStateEvent) {
			// 发送一次心跳数据
			RpcHeader header = RpcHeaderFactory.getRequestHeader(RpcConstants.SERIALIZATION_PROTOSTUFF, RpcType.HEARTBEAT_FROM_CONSUMER.getType());
			RpcProtocol<RpcRequest> requestRpcProtocol = new RpcProtocol<>();
			RpcRequest rpcRequest = new RpcRequest();
			rpcRequest.setParameters(new Object[]{RpcConstants.HEARTBEAT_PING});
			requestRpcProtocol.setHeader(header);
			requestRpcProtocol.setBody(rpcRequest);
			ctx.writeAndFlush(requestRpcProtocol);
		} else {
			super.userEventTriggered(ctx, evt);
		}
	}

	private void handleMessage(RpcProtocol<RpcResponse> protocol, Channel channel) {
		logger.info("Consumer received request ===>>> {}", JsonUtil.toJSONString(protocol));

		RpcHeader header = protocol.getHeader();

		if (header.getMsgType() == (byte) RpcType.RESPONSE.getType()) { // 响应消息
//			this.handleResponseMessage(protocol);
			this.handleResponseMessageOrBuffer(protocol);
		} else if (header.getMsgType() == (byte) RpcType.HEARTBEAT_TO_CONSUMER.getType()) { // 接收到服务提供者响应的心跳消息
			this.handleHeartbeatMessageToConsumer(protocol);
		} else if (header.getMsgType() == (byte) RpcType.HEARTBEAT_FROM_PROVIDER.getType()) { // 接收到服务提供者发送的心跳消息
			this.handleHeartbeatMessageFromProvider(protocol, channel);
		}
	}

	/**
	 * 处理从服务提供者发送过来的心跳消息
	 */
	private void handleHeartbeatMessageFromProvider(RpcProtocol<RpcResponse> protocol, Channel channel) {
		RpcHeader header = protocol.getHeader();
		header.setMsgType((byte) RpcType.HEARTBEAT_TO_PROVIDER.getType());
		RpcProtocol<RpcRequest> requestRpcProtocol = new RpcProtocol<>();
		RpcRequest request = new RpcRequest();
		request.setParameters(new Object[]{RpcConstants.HEARTBEAT_PONG});
		header.setStatus((byte) RpcStatus.SUCCESS.getCode());
		requestRpcProtocol.setHeader(header);
		requestRpcProtocol.setBody(request);
		channel.writeAndFlush(requestRpcProtocol);
	}

	/**
	 * 处理心跳消息
	 */
	private void handleHeartbeatMessageToConsumer(RpcProtocol<RpcResponse> protocol) {
		// 此处简单打印即可,实际场景可不做处理
		logger.info("receive service provider heartbeat message, the provider is: {}, the heartbeat message is: {}", channel.remoteAddress(), protocol.getBody().getResult());
	}

	/**
	 * 处理响应消息
	 */
	private void handleResponseMessage(RpcProtocol<RpcResponse> protocol) {
		long requestId = protocol.getHeader().getRequestId();
		RPCFuture rpcFuture = pendingRPC.remove(requestId);
		if (rpcFuture != null) {
			rpcFuture.done(protocol);
		}
	}

	public void close() {
		channel.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);

		ConsumerChannelCache.remove(channel);
	}

	public RPCFuture sendRequest(RpcProtocol<RpcRequest> protocol, boolean async, boolean oneway) {
		logger.info("Consumer sending request ===>>> {}", JsonUtil.toJSONString(protocol));

		return concurrentThreadPool.submit(() -> {
			return oneway ? sendRequestOneway(protocol)
				: async ? sendRequestAsync(protocol)
				: sendRequestSync(protocol);
		});
	}

	private RPCFuture sendRequestSync(RpcProtocol<RpcRequest> protocol) {
		logger.info("-- Sync request --");
		RPCFuture rpcFuture = this.getRpcFuture(protocol);
		channel.writeAndFlush(protocol);
		return rpcFuture;
	}

	private RPCFuture sendRequestAsync(RpcProtocol<RpcRequest> protocol) {
		logger.info("-- Async request --");
		RPCFuture rpcFuture = this.getRpcFuture(protocol);
		//如果是异步调用，则将RPCFuture放入RpcContext
		RpcContext.getContext().setRPCFuture(rpcFuture);
		channel.writeAndFlush(protocol);
		return null;
	}

	private RPCFuture sendRequestOneway(RpcProtocol<RpcRequest> protocol) {
		logger.info("-- Oneway --");
		channel.writeAndFlush(protocol);
		return null;
	}

	private RPCFuture getRpcFuture(RpcProtocol<RpcRequest> protocol) {
		RPCFuture rpcFuture = new RPCFuture(protocol, concurrentThreadPool);
		RpcHeader header = protocol.getHeader();
		long requestId = header.getRequestId();
		pendingRPC.put(requestId, rpcFuture);
		return rpcFuture;
	}
}
