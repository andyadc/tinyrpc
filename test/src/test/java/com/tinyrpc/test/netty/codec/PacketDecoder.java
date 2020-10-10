package com.tinyrpc.test.netty.codec;

import com.tinyrpc.test.netty.protocol.PacketCodeC;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class PacketDecoder extends ByteToMessageDecoder {

	@Override
	protected void decode(ChannelHandlerContext context, ByteBuf buf, List<Object> list) throws Exception {
		list.add(PacketCodeC.INSTANCE.decode(buf));
	}
}
