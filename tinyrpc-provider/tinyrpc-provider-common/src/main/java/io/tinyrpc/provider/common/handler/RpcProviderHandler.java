package io.tinyrpc.provider.common.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.tinyrpc.protocol.RpcProtocol;
import io.tinyrpc.protocol.request.RpcRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * RPC服务提供者的 Handler处理类
 */
public class RpcProviderHandler extends SimpleChannelInboundHandler<RpcProtocol<RpcRequest>> {

	private final Logger logger = LoggerFactory.getLogger(RpcProviderHandler.class);

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, RpcProtocol<RpcRequest> msg) throws Exception {

	}
}
