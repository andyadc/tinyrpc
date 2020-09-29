package com.tinyrpc.test.netty.handler;

import com.tinyrpc.test.netty.protocol.LoginRequestPacket;
import com.tinyrpc.test.netty.protocol.LoginResponsePacket;
import com.tinyrpc.test.netty.protocol.Packet;
import com.tinyrpc.test.netty.protocol.PacketCodeC;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.Date;
import java.util.UUID;

public class IMClientHandler extends ChannelInboundHandlerAdapter {

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		System.out.println(new Date() + ": 客户端开始登录");

		// 创建登录对象
		LoginRequestPacket packet = new LoginRequestPacket();
		packet.setUserId(UUID.randomUUID().toString());
		packet.setUsername("andy");
		packet.setPassword("pwd");

		//编码
		ByteBuf byteBuf = PacketCodeC.INSTANCE.encode(packet);

		// 写数据
		ctx.channel().writeAndFlush(byteBuf);
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		ByteBuf buf = (ByteBuf) msg;

		Packet packet = PacketCodeC.INSTANCE.decode(buf);
		if (packet instanceof LoginResponsePacket) {
			System.out.println(new Date() + ", 收到响应: " + packet);
		}
	}
}
