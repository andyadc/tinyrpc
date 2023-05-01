package io.tinyrpc.consumer.common.handler;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.tinyrpc.common.utils.JsonUtils;
import io.tinyrpc.consumer.common.context.RpcContext;
import io.tinyrpc.protocol.RpcProtocol;
import io.tinyrpc.protocol.header.RpcHeader;
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
	private volatile Channel channel;

	//存储请求ID与RpcResponse协议的映射关系
//	private final Map<Long, RpcProtocol<RpcResponse>> pendingResponse = new ConcurrentHashMap<>();
	private SocketAddress remotePeer;

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

	public RPCFuture sendRequest(RpcProtocol<RpcRequest> protocol, boolean async, boolean oneway) {
		logger.info("服务消费者发送的数据 ===>>> {}", JsonUtils.toJSONString(protocol));

		return oneway ? sendRequestOneway(protocol)
			: async ? sendRequestAsync(protocol)
			: sendRequestSync(protocol);
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
		RPCFuture rpcFuture = new RPCFuture(protocol);
		RpcHeader header = protocol.getHeader();
		long requestId = header.getRequestId();
		pendingRPC.put(requestId, rpcFuture);
		return rpcFuture;
	}
}
