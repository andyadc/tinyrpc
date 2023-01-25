package io.tinyrpc.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.CharsetUtil;
import io.tinyrpc.common.constants.RpcConstants;
import io.tinyrpc.common.utils.SerializationUtils;
import io.tinyrpc.protocol.RpcProtocol;
import io.tinyrpc.protocol.enumeration.RpcType;
import io.tinyrpc.protocol.header.RpcHeader;
import io.tinyrpc.protocol.request.RpcRequest;
import io.tinyrpc.protocol.response.RpcResponse;
import io.tinyrpc.serialization.api.Serialization;

import java.util.List;

/**
 * 实现RPC解码操作
 */
public class RpcDecoder extends ByteToMessageDecoder implements RpcCodec {

	@Override
	protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
		if (byteBuf.readableBytes() < RpcConstants.HEADER_TOTAL_LEN) {
			return;
		}
		byteBuf.markReaderIndex();
		short magic = byteBuf.readShort();
		if (magic != RpcConstants.MAGIC) {
			throw new IllegalArgumentException("Magic number is illegal: " + magic);
		}

		byte msgType = byteBuf.readByte();
		byte status = byteBuf.readByte();
		long requestId = byteBuf.readLong();

		ByteBuf serializationTypeByteBuf = byteBuf.readBytes(SerializationUtils.MAX_SERIALIZATION_TYPE_COUNR);
		String serializationType = SerializationUtils.subString(serializationTypeByteBuf.toString(CharsetUtil.UTF_8));

		int dataLength = byteBuf.readInt();
		if (byteBuf.readableBytes() < dataLength) {
			byteBuf.resetReaderIndex();
			return;
		}

		byte[] data = new byte[dataLength];
		byteBuf.readBytes(data);

		RpcType msgTypeEnum = RpcType.findByType(msgType);
		if (msgTypeEnum == null) {
			return;
		}

		RpcHeader header = new RpcHeader();
		header.setMagic(magic);
		header.setStatus(status);
		header.setRequestId(requestId);
		header.setMsgType(msgType);
		header.setSerializationType(serializationType);
		header.setMsgLen(dataLength);
		// TODO Serialization是扩展点
		Serialization serialization = getJdkSerialization();

		switch (msgTypeEnum) {
			case REQUEST:
				RpcRequest request = serialization.deserialize(data, RpcRequest.class);
				if (request != null) {
					RpcProtocol<RpcRequest> protocol = new RpcProtocol<>();
					protocol.setHeader(header);
					protocol.setBody(request);
					list.add(protocol);
				}
				break;
			case RESPONSE:
				RpcResponse response = serialization.deserialize(data, RpcResponse.class);
				if (response != null) {
					RpcProtocol<RpcResponse> protocol = new RpcProtocol<>();
					protocol.setHeader(header);
					protocol.setBody(response);
					list.add(protocol);
				}
				break;
			case HEARTBEAT:
				//TODO
				break;

		}
	}
}
