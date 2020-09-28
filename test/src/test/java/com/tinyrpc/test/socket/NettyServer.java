package com.tinyrpc.test.socket;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

/**
 * childHandler()用于指定处理新连接数据的读写处理逻辑, handler()用于指定在服务端启动过程中的一些逻辑;
 * childOption()可以给每条连接设置一些TCP底层相关的属性, option()给服务端channel设置属性
 */
public class NettyServer {
	public static void main(String[] args) {
		ServerBootstrap bootstrap = new ServerBootstrap();

		NioEventLoopGroup boss = new NioEventLoopGroup();
		NioEventLoopGroup worker = new NioEventLoopGroup();

		bootstrap
			.group(boss, worker) // 线程模型
			.channel(NioServerSocketChannel.class) // 指定服务端的 IO 模型为 NIO, OioServerSocketChannel
			.handler(new ChannelInitializer<NioServerSocketChannel>() {
				@Override
				protected void initChannel(NioServerSocketChannel nioServerSocketChannel) throws Exception {
					System.out.println("服务端启动中");
				}
			})
			.childHandler(new ChannelInitializer<NioSocketChannel>() {
				@Override
				protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
					nioSocketChannel.pipeline().addLast(new StringDecoder());
					nioSocketChannel.pipeline().addLast(new SimpleChannelInboundHandler<String>() {
						@Override
						protected void channelRead0(ChannelHandlerContext channelHandlerContext, String message) throws Exception {
							System.out.println(message);
						}
					});
				}
			})
			.attr(AttributeKey.newInstance("serverName"), "nettyServer") // attr()方法可以给服务端的 channel，也就是NioServerSocketChannel指定一些自定义属性，然后我们可以通过channel.attr()取出这个属性
			.childAttr(AttributeKey.newInstance("clientKey"), "clientValue") // childAttr可以给每一条连接指定自定义属性，然后后续我们可以通过channel.attr()取出该属性
			.option(ChannelOption.SO_BACKLOG, 1024) // 系统用于临时存放已完成三次握手的请求的队列的最大长度，如果连接建立频繁，服务器处理创建新连接较慢，可以适当调大这个参数
			.childOption(ChannelOption.SO_KEEPALIVE, true) // 是否开启TCP底层心跳机制，true为开启
			.childOption(ChannelOption.TCP_NODELAY, true) // 是否开启Nagle算法，true表示关闭. 如果要求高实时性，有数据发送时就马上发送，就关闭，如果需要减少发送次数减少网络交互，就开启
		;
		//.bind(9999);
		bind(bootstrap, 9999);
	}

	private static void bind(final ServerBootstrap bootstrap, final int port) {
		bootstrap.bind(port).addListener(new GenericFutureListener<Future<? super Void>>() {
			@Override
			public void operationComplete(Future<? super Void> future) throws Exception {
				if (future.isSuccess()) {
					System.out.println("端口[" + port + "]绑定成功!");
				} else {
					System.err.println("端口[" + port + "]绑定失败!");
					bind(bootstrap, port + 1);
				}
			}
		});
	}
}
