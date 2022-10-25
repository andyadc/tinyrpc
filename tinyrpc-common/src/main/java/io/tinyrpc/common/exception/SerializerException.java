package io.tinyrpc.common.exception;

public class SerializerException extends RpcException {

	private static final long serialVersionUID = -2019410071619485804L;

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
