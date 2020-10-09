package com.tinyrpc.test.netty.protocol;

public class MessageRequestPacket extends Packet {

	private String message;

	@Override
	public Byte getCmd() {
		return Cmd.MESSAGE_REQUEST;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
