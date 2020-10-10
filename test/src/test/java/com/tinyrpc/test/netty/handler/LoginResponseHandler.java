package com.tinyrpc.test.netty.handler;

import com.tinyrpc.test.netty.protocol.LoginResponsePacket;
import com.tinyrpc.test.netty.util.LoginUtil;
import com.tinyrpc.test.netty.util.Session;
import com.tinyrpc.test.netty.util.SessionUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.Date;

public class LoginResponseHandler extends SimpleChannelInboundHandler<LoginResponsePacket> {

//	@Override
//	public void channelActive(ChannelHandlerContext ctx) throws Exception {
//		LoginRequestPacket packet = new LoginRequestPacket();
//		packet.setUserId(UUID.randomUUID().toString());
//		packet.setUsername("andy");
//		packet.setPassword("pwd");
//
//		SessionUtil.bindSession(new Session(packet.getUserId(), packet.getUsername()), ctx.channel());
//
//		ctx.channel().writeAndFlush(packet);
//	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		System.out.println("客户端连接被关闭!");
		SessionUtil.unbindSession(ctx.channel());
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, LoginResponsePacket loginResponsePacket) throws Exception {
		String uid = loginResponsePacket.getUid();
		String uname = loginResponsePacket.getUname();
		if (loginResponsePacket.isSuccess()) {
			LoginUtil.markAsLogin(ctx.channel());
			SessionUtil.bindSession(new Session(uid, uname), ctx.channel());
			System.out.println("[" + uname + "]登录成功，uid 为: " + uid);
//			System.out.println(new Date() + ": 客户端登录成功");
		} else {
			System.out.println(new Date() + ": 客户端登录失败，原因：" + loginResponsePacket.getMessage());
		}
	}
}
