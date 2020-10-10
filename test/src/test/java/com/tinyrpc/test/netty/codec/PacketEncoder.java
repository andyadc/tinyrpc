package com.tinyrpc.test.netty.codec;

import com.tinyrpc.test.netty.protocol.Packet;
import com.tinyrpc.test.netty.protocol.PacketCodeC;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class PacketEncoder extends MessageToByteEncoder<Packet> {

	@Override
	protected void encode(ChannelHandlerContext context, Packet packet, ByteBuf byteBuf) throws Exception {
		PacketCodeC.INSTANCE.encode(byteBuf, packet);
	}
}
