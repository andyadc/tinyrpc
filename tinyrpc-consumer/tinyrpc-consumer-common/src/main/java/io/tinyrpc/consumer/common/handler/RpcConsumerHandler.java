package io.tinyrpc.consumer.common.handler;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.tinyrpc.common.utils.JsonUtils;
import io.tinyrpc.consumer.common.future.RPCFuture;
import io.tinyrpc.protocol.RpcProtocol;
import io.tinyrpc.protocol.header.RpcHeader;
import io.tinyrpc.protocol.request.RpcRequest;
import io.tinyrpc.protocol.response.RpcResponse;
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

	private volatile Channel channel;
	private SocketAddress remotePeer;

	//存储请求ID与RpcResponse协议的映射关系
//	private final Map<Long, RpcProtocol<RpcResponse>> pendingResponse = new ConcurrentHashMap<>();

	private final Map<Long, RPCFuture> pendingRPC = new ConcurrentHashMap<>();

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
		logger.info("服务消费者接收到的数据 ===>>> {}", JsonUtils.toJSONString(protocol));
		RpcHeader header = protocol.getHeader();
		long requestId = header.getRequestId();
		RPCFuture rpcFuture = pendingRPC.remove(requestId);
		if (rpcFuture != null) {
			rpcFuture.done(protocol);
		}
	}

	public void close() {
		channel.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
	}

	public RPCFuture sendRequest(RpcProtocol<RpcRequest> protocol) {
		logger.info("服务消费者发送的数据 ===>>>{}", JsonUtils.toJSONString(protocol));

		RPCFuture rpcFuture = this.getRpcFuture(protocol);

		channel.writeAndFlush(protocol);
		return rpcFuture;
	}

	private RPCFuture getRpcFuture(RpcProtocol<RpcRequest> protocol) {
		RPCFuture rpcFuture = new RPCFuture(protocol);
		RpcHeader header = protocol.getHeader();
		long requestId = header.getRequestId();
		pendingRPC.put(requestId, rpcFuture);
		return rpcFuture;
	}
}
