package io.tinyrpc.protocol.enumeration;

/**
 * RPC服务状态
 */
public enum RpcStatus {

	SUCCESS(1),
	FAIL(2);

	private final int code;

	RpcStatus(int code) {
		this.code = code;
	}

	public int getCode() {
		return code;
	}
}
