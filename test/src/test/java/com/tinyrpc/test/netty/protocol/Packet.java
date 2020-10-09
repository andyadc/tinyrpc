package com.tinyrpc.test.netty.protocol;

public abstract class Packet {

	/**
	 * 协议版本
	 */
	private Byte version = 1;

	/**
	 * 指令
	 */
	public abstract Byte getCmd();

	public Byte getVersion() {
		return version;
	}

	public void setVersion(Byte version) {
		this.version = version;
	}

	interface Cmd {
		Byte LOGIN_REQUEST = 1;
		Byte LOGIN_RESPONSE = 2;
		Byte MESSAGE_REQUEST = 3;
		Byte MESSAGE_RESPONSE = 4;
	}
}
