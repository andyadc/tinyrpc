package io.tinyrpc.transport;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.tinyrpc.Constants;
import io.tinyrpc.codec.RpcDecoder;
import io.tinyrpc.codec.RpcEncoder;

import java.io.Closeable;
import java.io.IOException;

public class RpcClient implements Closeable {

    protected Bootstrap bootstrap;
    protected EventLoopGroup group;
    private String host;
    private int port;

    public RpcClient(String host, int port) {
        this.host = host;
        this.port = port;

        bootstrap = new Bootstrap();
        group = NettyEventLoopFactory.eventLoopGroup(Constants.DEFAULT_IO_THREADS, "NettyClientWorker");
        bootstrap.group(group)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .channel(NioSocketChannel.class) // 创建的Channel类型
                // 指定ChannelHandler的顺序
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel channel) throws Exception {
                        channel.pipeline().addLast("rpc-encoder", new RpcEncoder());
                        channel.pipeline().addLast("rpc-decoder", new RpcDecoder());
                        channel.pipeline().addLast("client-handler", new RpcClientHandler());
                    }
                });
    }

    public ChannelFuture connect() {
        ChannelFuture connect = bootstrap.connect(host, port);
        connect.awaitUninterruptibly();
        return connect;
    }

    @Override
    public void close() throws IOException {
        group.shutdownGracefully();
    }
}
