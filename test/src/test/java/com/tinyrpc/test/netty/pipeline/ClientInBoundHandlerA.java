package com.tinyrpc.test.netty.pipeline;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class ClientInBoundHandlerA extends ChannelInboundHandlerAdapter {

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		System.out.println(this.getClass().getSimpleName());
		ByteBuf buf = ctx.channel().alloc().buffer();
		ctx.channel().writeAndFlush(buf);
	}
}
