package com.tinyrpc.test.netty;

import com.tinyrpc.test.netty.handler.IMClientHandler;
import com.tinyrpc.test.netty.protocol.MessageRequestPacket;
import com.tinyrpc.test.netty.protocol.PacketCodeC;
import com.tinyrpc.test.netty.util.LoginUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.Scanner;

public class Client {

	public static void main(String[] args) {
		Bootstrap bootstrap = new Bootstrap();

		bootstrap
			.group(new NioEventLoopGroup())
			.channel(NioSocketChannel.class)
			.handler(new ChannelInitializer<Channel>() {
				@Override
				protected void initChannel(Channel channel) throws Exception {
//					channel.pipeline().addLast(new FirstClientHandler());
					channel.pipeline().addLast(new IMClientHandler());
				}
			})
			.connect("127.0.0.1", 9999)
			.addListener(future -> {
				if (future.isSuccess()) {
					Channel channel = ((ChannelFuture) future).channel();
					// 连接成功之后，启动控制台线程
					startConsoleThread(channel);
				}
			})
		;
	}

	private static void startConsoleThread(Channel channel) {
		new Thread(() -> {
			while (!Thread.interrupted()) {
				if (LoginUtil.hasLogin(channel)) {
					System.out.println("输入消息发送至服务端: ");
					Scanner scanner = new Scanner(System.in);
					String line = scanner.nextLine();

					MessageRequestPacket packet = new MessageRequestPacket();
					packet.setMessage(line);
					ByteBuf buf = PacketCodeC.INSTANCE.encode(packet);
					channel.writeAndFlush(buf);
				}
			}
		}).start();
	}
}
