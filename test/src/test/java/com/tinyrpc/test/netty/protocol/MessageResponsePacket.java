package com.tinyrpc.test.netty.protocol;

public class MessageResponsePacket extends Packet {

	private String fromUid;
	private String fromUname;
	private String message;

	@Override
	public Byte getCmd() {
		return Cmd.MESSAGE_RESPONSE;
	}

	public String getFromUid() {
		return fromUid;
	}

	public void setFromUid(String fromUid) {
		this.fromUid = fromUid;
	}

	public String getFromUname() {
		return fromUname;
	}

	public void setFromUname(String fromUname) {
		this.fromUname = fromUname;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
