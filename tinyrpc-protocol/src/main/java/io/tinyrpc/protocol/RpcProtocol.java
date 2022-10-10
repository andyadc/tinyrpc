package io.tinyrpc.protocol;

import io.tinyrpc.protocol.header.RpcHeader;

import java.io.Serializable;

/**
 * Rpc协议
 */
public class RpcProtocol<T> implements Serializable {

	private static final long serialVersionUID = 5418167381894960520L;

	/**
	 * 消息头
	 */
	private RpcHeader header;
	/**
	 * 消息体
	 */
	private T body;

	public RpcHeader getHeader() {
		return header;
	}

	public void setHeader(RpcHeader header) {
		this.header = header;
	}

	public T getBody() {
		return body;
	}

	public void setBody(T body) {
		this.body = body;
	}
}
