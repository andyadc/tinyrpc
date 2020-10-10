package com.tinyrpc.test.netty.handler;

import com.tinyrpc.test.netty.protocol.*;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.Date;

public class IMServerHandler extends ChannelInboundHandlerAdapter {

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		ByteBuf buf = (ByteBuf) msg;

		Packet packet = PacketCodeC.INSTANCE.decode(buf);

		ByteBuf byteBuf = null;
		if (packet instanceof LoginRequestPacket) {
			LoginResponsePacket loginResponsePacket = new LoginResponsePacket();
			loginResponsePacket.setVersion(packet.getVersion());

			LoginRequestPacket loginRequestPacket = (LoginRequestPacket) packet;
			if (valid(loginRequestPacket)) {
				System.out.println(new Date() + " 登录成功");
				loginResponsePacket.setCode("000");
				loginResponsePacket.setMessage("登录成功");
			} else {
				loginResponsePacket.setCode("999");
				System.out.println(new Date() + " 登录失败");
				loginResponsePacket.setMessage("登录失败");
			}

			byteBuf = PacketCodeC.INSTANCE.encode(loginResponsePacket);
		} else if (packet instanceof MessageRequestPacket) {
			MessageRequestPacket messageRequestPacket = (MessageRequestPacket) packet;
			System.out.println(new Date() + ": 收到客户端消息: " + messageRequestPacket.getMessage());

			MessageResponsePacket messageResponsePacket = new MessageResponsePacket();
			messageResponsePacket.setMessage("服务端回复【" + messageRequestPacket.getMessage() + "】");

			byteBuf = PacketCodeC.INSTANCE.encode(messageResponsePacket);
		}

		ctx.channel().writeAndFlush(byteBuf);
	}

	private boolean valid(LoginRequestPacket loginRequestPacket) {
		return true;
	}
}
