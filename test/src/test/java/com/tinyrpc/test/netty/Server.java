package com.tinyrpc.test.netty;

import com.tinyrpc.test.netty.codec.PacketDecoder;
import com.tinyrpc.test.netty.codec.PacketEncoder;
import com.tinyrpc.test.netty.codec.Spliter;
import com.tinyrpc.test.netty.handler.AuthHandler;
import com.tinyrpc.test.netty.handler.LifeCyCleTestHandler;
import com.tinyrpc.test.netty.handler.LoginRequestHandler;
import com.tinyrpc.test.netty.handler.MessageRequestHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class Server {

	public static void main(String[] args) {
		ServerBootstrap bootstrap = new ServerBootstrap();

		NioEventLoopGroup boss = new NioEventLoopGroup();
		NioEventLoopGroup worker = new NioEventLoopGroup();

		bootstrap
			.group(boss, worker)
			.channel(NioServerSocketChannel.class)
			.childHandler(new ChannelInitializer<NioSocketChannel>() {
				@Override
				protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {

					nioSocketChannel.pipeline().addLast(new LifeCyCleTestHandler());
					nioSocketChannel.pipeline().addLast(new Spliter());

//					nioSocketChannel.pipeline().addLast(new FirstServerHandler());

//					nioSocketChannel.pipeline().addLast(new IMServerHandler());

//					nioSocketChannel.pipeline().addLast(new InBoundHandlerA());
//					nioSocketChannel.pipeline().addLast(new InBoundHandlerB());
//					nioSocketChannel.pipeline().addLast(new InBoundHandlerC());

					nioSocketChannel.pipeline().addLast(new PacketDecoder());
					nioSocketChannel.pipeline().addLast(new LoginRequestHandler());
					nioSocketChannel.pipeline().addLast(new AuthHandler()); // 新增加用户认证handler
					nioSocketChannel.pipeline().addLast(new MessageRequestHandler());
					nioSocketChannel.pipeline().addLast(new PacketEncoder());
				}
			})
			.bind(9999)
			.addListener(future -> {
				if (future.isSuccess()) {
					System.out.println("Bind succsss");
				}
			})
		;
	}
}
