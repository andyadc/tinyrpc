package com.tinyrpc.test.netty.handler;

import com.tinyrpc.test.netty.protocol.MessageRequestPacket;
import com.tinyrpc.test.netty.protocol.MessageResponsePacket;
import com.tinyrpc.test.netty.util.Session;
import com.tinyrpc.test.netty.util.SessionUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.Date;

public class MessageRequestHandler extends SimpleChannelInboundHandler<MessageRequestPacket> {

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, MessageRequestPacket messageRequestPacket) throws Exception {
		String message = messageRequestPacket.getMessage();
		System.out.println(new Date() + ": 收到客户端消息: " + message);

		if ("quit".equalsIgnoreCase(message)) {
			ctx.channel().close();
			return;
		}
		Session session = SessionUtil.getSession(ctx.channel());

		MessageResponsePacket messageResponsePacket = new MessageResponsePacket();
		messageResponsePacket.setFromUid(session.getUid());
		messageResponsePacket.setFromUname(session.getName());
		messageResponsePacket.setMessage(message);

		// 3.拿到消息接收方的 channel
		Channel toUChannel = SessionUtil.getChannel(messageRequestPacket.getToUid());

		if (toUChannel != null && SessionUtil.hasLogin(toUChannel)) {
			toUChannel.writeAndFlush(messageResponsePacket);
		} else {
			System.err.println("[" + messageRequestPacket.getToUid() + "] 不在线，发送失败!");
		}

//		ctx.channel().writeAndFlush(messageResponsePacket);
	}
}
