package io.tinyrpc.transport;

import io.netty.channel.Channel;
import io.netty.util.concurrent.Promise;
import io.tinyrpc.model.Message;

public class NettyResponseFuture<T> {

    private long createTime;
    private long timeout;
    private Message request;
    private Channel channel;
    private Promise<T> promise;

    public NettyResponseFuture(long createTime, long timeout, Message request, Channel channel, Promise<T> promise) {
        this.createTime = createTime;
        this.timeout = timeout;
        this.request = request;
        this.channel = channel;
        this.promise = promise;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public Message getRequest() {
        return request;
    }

    public void setRequest(Message request) {
        this.request = request;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public Promise<T> getPromise() {
        return promise;
    }

    public void setPromise(Promise<T> promise) {
        this.promise = promise;
    }
}
