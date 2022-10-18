package io.tinyrpc.common.exception;

public class SerializerException extends RpcException {

	public SerializerException() {
	}

	public SerializerException(String message) {
		super(message);
	}

	public SerializerException(Throwable cause) {
		super(cause);
	}

	public SerializerException(String message, Throwable cause) {
		super(message, cause);
	}
}
