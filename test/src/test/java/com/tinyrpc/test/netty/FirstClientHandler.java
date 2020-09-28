package com.tinyrpc.test.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.nio.charset.StandardCharsets;
import java.util.Date;

public class FirstClientHandler extends ChannelInboundHandlerAdapter {
	/**
	 * 会在客户端连接建立成功之后被调用
	 */
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		System.out.println("客户端写出数据: " + new Date());

		// 1. 获取数据
		ByteBuf buffer = getByteBuf(ctx);

		// 2. 写数据
		ctx.channel().writeAndFlush(buffer);
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		ByteBuf buf = (ByteBuf) msg;
		System.out.println("客户端读到数据-> " + buf.toString(StandardCharsets.UTF_8) + " - " + new Date());
	}

	private ByteBuf getByteBuf(ChannelHandlerContext ctx) {
		// 1. 获取二进制抽象 ByteBuf
		ByteBuf buf = ctx.alloc().buffer();

		// 2. 准备数据，指定字符串的字符集为 utf-8
		byte[] data = "你好, 澜柯!".getBytes(StandardCharsets.UTF_8);

		// 3. 填充数据到 ByteBuf
		buf.writeBytes(data);
		return buf;
	}
}
