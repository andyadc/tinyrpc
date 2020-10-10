package com.tinyrpc.test.netty.protocol;

public class MessageRequestPacket extends Packet {

	private String toUid;
	private String message;

	public MessageRequestPacket() {
	}

	public MessageRequestPacket(String toUid, String message) {
		this.toUid = toUid;
		this.message = message;
	}

	@Override
	public Byte getCmd() {
		return Cmd.MESSAGE_REQUEST;
	}

	public String getToUid() {
		return toUid;
	}

	public void setToUid(String toUid) {
		this.toUid = toUid;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
