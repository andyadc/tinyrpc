package io.tinyrpc.transport;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;

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
