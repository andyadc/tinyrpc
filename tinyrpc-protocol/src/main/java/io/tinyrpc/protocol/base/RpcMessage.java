package io.tinyrpc.protocol.base;

import java.io.Serializable;

/**
 * 消息体基础类
 */
public class RpcMessage implements Serializable {

	private static final long serialVersionUID = 4110544299343434186L;

	/**
	 * 是否单向发送
	 */
	private boolean oneway;

	/**
	 * 是否异步调用
	 */
	private boolean async;

	public boolean getOneway() {
		return oneway;
	}

	public void setOneway(boolean oneway) {
		this.oneway = oneway;
	}

	public boolean getAsync() {
		return async;
	}

	public void setAsync(boolean async) {
		this.async = async;
	}
}
