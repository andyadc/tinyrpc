package com.tinyrpc.test.netty.protocol;

public class LoginResponsePacket extends Packet {

	private String uid;
	private String uname;
	private String code;
	private String message;

	public boolean isSuccess() {
		return this != null && "000".equals(code);
	}

	@Override
	public Byte getCmd() {
		return Cmd.LOGIN_RESPONSE;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getUname() {
		return uname;
	}

	public void setUname(String uname) {
		this.uname = uname;
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
