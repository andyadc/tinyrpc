package com.tinyrpc.test.netty.pipeline;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class InBoundHandlerC extends ChannelInboundHandlerAdapter {

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		System.out.println(this.getClass().getSimpleName() + " - " + msg);
		super.channelRead(ctx, msg);
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		System.out.println(this.getClass().getSimpleName());
		super.channelActive(ctx);
	}
}
