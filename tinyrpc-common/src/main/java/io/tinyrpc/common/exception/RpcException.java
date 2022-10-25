package io.tinyrpc.common.exception;

public class RpcException extends RuntimeException {

	private static final long serialVersionUID = -4840799054543263687L;

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
