package io.tinyrpc.transport;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.tinyrpc.model.Message;
import io.tinyrpc.model.Request;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class RpcServerHandler extends SimpleChannelInboundHandler<Message<Request>> {

    private static Executor executor = Executors.newCachedThreadPool();

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Message<Request> requestMessage) throws Exception {

    }
}
