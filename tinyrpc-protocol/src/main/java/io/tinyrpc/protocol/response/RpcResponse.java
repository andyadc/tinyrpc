package io.tinyrpc.protocol.response;

import io.tinyrpc.protocol.base.RpcMessage;

/**
 * RPC的响应类，对应的请求id在响应头中
 */
public class RpcResponse extends RpcMessage {

	private static final long serialVersionUID = 8711526791902227826L;

	private String error;
	private Object result;

	public boolean isError() {
		return error != null;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public Object getResult() {
		return result;
	}

	public void setResult(Object result) {
		this.result = result;
	}
}
