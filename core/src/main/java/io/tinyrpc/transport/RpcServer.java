package io.tinyrpc.transport;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;

public class RpcServer {

    protected int port;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private ServerBootstrap serverBootstrap;
    private Channel channel;

    public RpcServer(int port) {
        this.port = port;

    }

    public ChannelFuture start() {
        // 监听指定端口
        ChannelFuture bind = serverBootstrap.bind(port);
        channel = bind.channel();
        channel.closeFuture();
        return bind;
    }
}
