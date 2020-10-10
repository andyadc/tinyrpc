package com.tinyrpc.test.netty.handler;

import com.tinyrpc.test.netty.protocol.MessageResponsePacket;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class MessageResponseHandler extends SimpleChannelInboundHandler<MessageResponsePacket> {

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, MessageResponsePacket messageResponsePacket) throws Exception {
//		System.out.println(new Date() + ": 收到服务端的消息: " + messageResponsePacket.getMessage());

		String fromUid = messageResponsePacket.getFromUid();
		String fromUname = messageResponsePacket.getFromUname();
		System.out.println(fromUid + ":" + fromUname + " -> " + messageResponsePacket.getMessage());
	}
}
