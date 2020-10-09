package com.tinyrpc.test.netty.protocol;

public class MessageResponsePacket extends Packet {

	private String message;

	@Override
	public Byte getCmd() {
		return Cmd.MESSAGE_RESPONSE;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
