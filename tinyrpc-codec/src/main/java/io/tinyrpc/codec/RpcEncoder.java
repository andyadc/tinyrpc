package io.tinyrpc.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.tinyrpc.common.utils.SerializationUtils;
import io.tinyrpc.flow.processor.FlowPostProcessor;
import io.tinyrpc.protocol.RpcProtocol;
import io.tinyrpc.protocol.header.RpcHeader;
import io.tinyrpc.serialization.api.Serialization;

import java.nio.charset.StandardCharsets;

/**
 * 实现RPC编码
 */
public class RpcEncoder extends MessageToByteEncoder<RpcProtocol<Object>> implements RpcCodec {

	private final FlowPostProcessor postProcessor;

	public RpcEncoder(FlowPostProcessor postProcessor) {
		this.postProcessor = postProcessor;
	}

	@Override
	protected void encode(ChannelHandlerContext ctx, RpcProtocol<Object> msg, ByteBuf byteBuf) throws Exception {
		RpcHeader header = msg.getHeader();
		byteBuf.writeShort(header.getMagic());
		byteBuf.writeByte(header.getMsgType());
		byteBuf.writeByte(header.getStatus());
		byteBuf.writeLong(header.getRequestId());

		String serializationType = header.getSerializationType();
		byteBuf.writeBytes(SerializationUtils.paddingString(serializationType).getBytes(StandardCharsets.UTF_8));

		Serialization serialization = getJdkSerialization(serializationType);
		byte[] data = serialization.serialize(msg.getBody());
		byteBuf.writeInt(data.length);
		byteBuf.writeBytes(data);

		this.postFlowProcessor(postProcessor, header);
	}
}
