package io.tinyrpc.transport;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.tinyrpc.Constants;
import io.tinyrpc.model.Message;
import io.tinyrpc.model.Request;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class RpcServerHandler extends SimpleChannelInboundHandler<Message<Request>> {

    private static Executor executor = Executors.newCachedThreadPool();

    @Override
    protected void channelRead0(ChannelHandlerContext context, Message<Request> message) throws Exception {
        byte extraInfo = message.getHeader().getExtraInfo();
        if (Constants.isHeartBeat(extraInfo)) { // 心跳消息，直接返回即可
            context.writeAndFlush(message);
            return;
        }
        // 非心跳消息，直接封装成Runnable提交到业务线程池
        executor.execute(new InvokeRunnable(context, message));
    }
}
