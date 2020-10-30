package com.tinyrpc.test.netty;

import com.tinyrpc.test.netty.codec.PacketDecoder;
import com.tinyrpc.test.netty.codec.PacketEncoder;
import com.tinyrpc.test.netty.codec.Spliter;
import com.tinyrpc.test.netty.handler.LoginResponseHandler;
import com.tinyrpc.test.netty.handler.MessageResponseHandler;
import com.tinyrpc.test.netty.protocol.LoginRequestPacket;
import com.tinyrpc.test.netty.protocol.MessageRequestPacket;
import com.tinyrpc.test.netty.protocol.PacketCodeC;
import com.tinyrpc.test.netty.util.LoginUtil;
import com.tinyrpc.test.netty.util.SessionUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class Client {

	public static void main(String[] args) {
		Bootstrap bootstrap = new Bootstrap();

		bootstrap
			.group(new NioEventLoopGroup())
			.channel(NioSocketChannel.class)
			.handler(new ChannelInitializer<Channel>() {
				@Override
				protected void initChannel(Channel channel) throws Exception {
					channel.pipeline().addLast(new Spliter());

//					channel.pipeline().addLast(new FirstClientHandler());

//					channel.pipeline().addLast(new IMClientHandler());

					channel.pipeline().addLast(new PacketDecoder());
					channel.pipeline().addLast(new LoginResponseHandler());
					channel.pipeline().addLast(new MessageResponseHandler());
					channel.pipeline().addLast(new PacketEncoder());
				}
			})
			.connect("127.0.0.1", 9999)
			.addListener(future -> {
				if (future.isSuccess()) {
					System.out.println("Connect succsss");
					Channel channel = ((ChannelFuture) future).channel();
					// 连接成功之后，启动控制台线程
					startConsoleThread2(channel);
				}
			})
		;
	}

	private static void startConsoleThread2(Channel channel) {
		Scanner scanner = new Scanner(System.in);

		LoginRequestPacket loginRequestPacket = new LoginRequestPacket();

		new Thread(() -> {
			while (!Thread.interrupted()) {
				if (!SessionUtil.hasLogin(channel)) {
					System.out.print("输入用户名登录: ");
					String uname = scanner.nextLine();
					loginRequestPacket.setUsername(uname);
					loginRequestPacket.setPassword("pwd");

					channel.writeAndFlush(loginRequestPacket);
					try {
						TimeUnit.SECONDS.sleep(1);
					} catch (InterruptedException e) {
						e.printStackTrace();
						Thread.currentThread().interrupt();
					}
				} else {
					System.out.print("输入接受者Uid: ");
					String toUid = scanner.nextLine();
					String message = scanner.nextLine();
					channel.writeAndFlush(new MessageRequestPacket(toUid, message));
				}
			}
		}).start();
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
