package io.tinyrpc.buffer.object;

import io.netty.channel.ChannelHandlerContext;
import io.tinyrpc.protocol.RpcProtocol;

import java.io.Serializable;

/**
 * 缓冲对象
 *
 * @param <T>
 */
public class BufferObject<T> implements Serializable {
	private static final long serialVersionUID = -7153202158089795131L;

	// Netty读写数据的ChannelHandlerContext
	private ChannelHandlerContext ctx;

	// 网络传输协议对象
	private RpcProtocol<T> protocol;

	public BufferObject() {
	}

	public BufferObject(ChannelHandlerContext ctx, RpcProtocol<T> protocol) {
		this.ctx = ctx;
		this.protocol = protocol;
	}

	public ChannelHandlerContext getCtx() {
		return ctx;
	}

	public void setCtx(ChannelHandlerContext ctx) {
		this.ctx = ctx;
	}

	public RpcProtocol<T> getProtocol() {
		return protocol;
	}

	public void setProtocol(RpcProtocol<T> protocol) {
		this.protocol = protocol;
	}
}
