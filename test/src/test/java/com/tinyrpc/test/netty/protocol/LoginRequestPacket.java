package com.tinyrpc.test.netty.protocol;

public class LoginRequestPacket extends Packet {

	private String userId;

	private String username;

	private String password;

	@Override
	public Byte getCmd() {
		return Cmd.LOGIN_REQUEST;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
