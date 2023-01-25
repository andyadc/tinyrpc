package io.tinyrpc.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.tinyrpc.common.utils.SerializationUtils;
import io.tinyrpc.protocol.RpcProtocol;
import io.tinyrpc.protocol.header.RpcHeader;
import io.tinyrpc.serialization.api.Serialization;

import java.nio.charset.StandardCharsets;

/**
 * 实现RPC编码
 */
public class RpcEncoder extends MessageToByteEncoder<RpcProtocol<Object>> implements RpcCodec {

	@Override
	protected void encode(ChannelHandlerContext ctx, RpcProtocol<Object> msg, ByteBuf byteBuf) throws Exception {
		RpcHeader header = msg.getHeader();
		byteBuf.writeShort(header.getMagic());
		byteBuf.writeByte(header.getMsgType());
		byteBuf.writeByte(header.getStatus());
		byteBuf.writeLong(header.getRequestId());
		String serializationType = header.getSerializationType();
		//TODO Serialization是扩展点
		Serialization serialization = getJdkSerialization();
		byteBuf.writeBytes(SerializationUtils.paddingString(serializationType).getBytes(StandardCharsets.UTF_8));
		byte[] data = serialization.serialize(msg.getBody());
		byteBuf.writeInt(data.length);
		byteBuf.writeBytes(data);
	}
}
