package com.tinyrpc.test.netty.protocol;

public class LoginResponsePacket extends Packet {

	private String code;
	private String message;

	public boolean isSuccess() {
		return this != null && "000".equals(code);
	}

	@Override
	public Byte getCmd() {
		return Cmd.LOGIN_RESPONSE;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return "LoginResponsePacket{" +
			"code=" + code +
			", message=" + message +
			'}';
	}
}
