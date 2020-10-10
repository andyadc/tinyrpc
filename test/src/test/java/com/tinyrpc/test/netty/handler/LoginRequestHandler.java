package com.tinyrpc.test.netty.handler;

import com.tinyrpc.test.netty.protocol.LoginRequestPacket;
import com.tinyrpc.test.netty.protocol.LoginResponsePacket;
import com.tinyrpc.test.netty.util.LoginUtil;
import com.tinyrpc.test.netty.util.Session;
import com.tinyrpc.test.netty.util.SessionUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.Date;
import java.util.UUID;

public class LoginRequestHandler extends SimpleChannelInboundHandler<LoginRequestPacket> {

	private static String randomUid() {
		return UUID.randomUUID().toString().split("-")[0];
	}

	@Override
	protected void channelRead0(ChannelHandlerContext context, LoginRequestPacket loginRequestPacket) throws Exception {
		System.out.println(new Date() + ": 收到客户端登录请求......");

		LoginResponsePacket loginResponsePacket = new LoginResponsePacket();
		loginResponsePacket.setVersion(loginRequestPacket.getVersion());
		loginResponsePacket.setUname(loginRequestPacket.getUsername());

		if (valid(loginRequestPacket)) {
			System.out.println("[" + loginRequestPacket.getUsername() + "]登录成功");
			loginResponsePacket.setCode("000");
			loginResponsePacket.setMessage("登录成功");
			loginResponsePacket.setUid(randomUid());
			LoginUtil.markAsLogin(context.channel());

			SessionUtil.bindSession(new Session(loginResponsePacket.getUid(), loginResponsePacket.getUname()), context.channel());
		} else {
			loginResponsePacket.setCode("999");
			System.out.println(new Date() + ", 登录失败");
			loginResponsePacket.setMessage("登录失败");
		}

		// 登录响应
		context.channel().writeAndFlush(loginResponsePacket);
	}

	private boolean valid(LoginRequestPacket loginRequestPacket) {
		return true;
	}
}
