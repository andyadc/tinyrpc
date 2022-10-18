package io.tinyrpc.common.exception;

public class RpcException extends RuntimeException {

	public RpcException() {
	}

	public RpcException(String message) {
		super(message);
	}

	public RpcException(Throwable cause) {
		super(cause);
	}

	public RpcException(String message, Throwable cause) {
		super(message, cause);
	}
}
