package com.tinyrpc.test.netty.protocol;

import com.tinyrpc.test.netty.serializer.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

public class PacketCodeC {

	public static final int MAGIC_NUMBER = 0x12345678;

	public static PacketCodeC INSTANCE = new PacketCodeC();

	public ByteBuf encode(ByteBuf buf, Packet packet) {
		byte[] bytes = Serializer.DEFAULT.serializer(packet);

		buf.writeInt(MAGIC_NUMBER);
		buf.writeByte(packet.getVersion());
		buf.writeByte(Serializer.DEFAULT.getSerializerAlgorithm());
		buf.writeByte(packet.getCmd());
		buf.writeInt(bytes.length);
		buf.writeBytes(bytes);

		return buf;
	}

	public ByteBuf encode(Packet packet) {
		ByteBuf buf = ByteBufAllocator.DEFAULT.ioBuffer();
		return this.encode(buf, packet);
	}

	public Packet decode(ByteBuf buf) {
		// 跳过 magic number
		buf.skipBytes(4);
		// 跳过版本号
		buf.skipBytes(1);
		// 序列化算法标识
		byte algorithm = buf.readByte();
		// 指令
		byte cmd = buf.readByte();
		// 数据包长度
		int length = buf.readInt();

		byte[] bytes = new byte[length];
		buf.readBytes(bytes);

		Class<? extends Packet> requestType = getRequestType(cmd);
		Serializer serializer = getSerializer(algorithm);

		if (requestType != null && serializer != null) {
			return serializer.deserialize(bytes, requestType);
		}
		return null;
	}

	private Serializer getSerializer(byte algorithm) {
		switch (algorithm) {
			case 1:
				return Serializer.DEFAULT;
			default:
				return null;
		}
	}

	private Class<? extends Packet> getRequestType(byte cmd) {
		switch (cmd) {
			case 1:
				return LoginRequestPacket.class;
			case 2:
				return LoginResponsePacket.class;
			case 3:
				return MessageRequestPacket.class;
			case 4:
				return MessageResponsePacket.class;
			default:
				System.out.println("Unknown cmd: " + cmd);
				return null;
		}
	}

}
