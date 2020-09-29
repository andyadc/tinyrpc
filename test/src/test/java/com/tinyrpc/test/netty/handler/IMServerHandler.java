package com.tinyrpc.test.netty.handler;

import com.tinyrpc.test.netty.protocol.LoginRequestPacket;
import com.tinyrpc.test.netty.protocol.LoginResponsePacket;
import com.tinyrpc.test.netty.protocol.Packet;
import com.tinyrpc.test.netty.protocol.PacketCodeC;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.Date;

public class IMServerHandler extends ChannelInboundHandlerAdapter {

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		ByteBuf buf = (ByteBuf) msg;

		Packet packet = PacketCodeC.INSTANCE.decode(buf);

		LoginResponsePacket loginResponsePacket = new LoginResponsePacket();
		loginResponsePacket.setVersion(packet.getVersion());

		if (packet instanceof LoginRequestPacket) {
			LoginRequestPacket loginRequestPacket = (LoginRequestPacket) packet;
			if (valid(loginRequestPacket)) {
				System.out.println(new Date() + "登录成功");
				loginResponsePacket.setCode("000");
				loginResponsePacket.setMessage("登录成功");
			} else {
				loginResponsePacket.setCode("999");
				System.out.println(new Date() + "登录失败");
				loginResponsePacket.setMessage("登录失败");
			}
		}

		ByteBuf byteBuf = PacketCodeC.INSTANCE.encode(loginResponsePacket);
		ctx.channel().writeAndFlush(byteBuf);
	}

	private boolean valid(LoginRequestPacket loginRequestPacket) {
		return true;
	}
}
