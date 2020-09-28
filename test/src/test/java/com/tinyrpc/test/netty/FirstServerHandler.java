package com.tinyrpc.test.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.nio.charset.StandardCharsets;
import java.util.Date;

public class FirstServerHandler extends ChannelInboundHandlerAdapter {

	/**
	 * 在接收到客户端发来的数据之后被回调
	 *
	 * @param ctx
	 * @param msg Netty 里面数据读写的载体
	 */
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		ByteBuf buf = (ByteBuf) msg;

		System.out.println("服务端读到数据-> " + buf.toString(StandardCharsets.UTF_8) + " - " + new Date());

		// 回复数据到客户端
		System.out.println("服务端写出数据: " + new Date());
		ByteBuf out = getByteBuf(ctx);
		ctx.channel().writeAndFlush(out);
	}

	/**
	 * 可以主动向客户端发送信息
	 */
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		ByteBuf buf = getByteBuf(ctx);

		ctx.channel().writeAndFlush(buf);
	}

	private ByteBuf getByteBuf(ChannelHandlerContext ctx) {
		ByteBuf buf = ctx.alloc().buffer();

		byte[] data = "终于等到你".getBytes(StandardCharsets.UTF_8);
		buf.writeBytes(data);
		return buf;
	}
}
