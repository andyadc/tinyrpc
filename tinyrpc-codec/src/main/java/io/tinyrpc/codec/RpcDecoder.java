package io.tinyrpc.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.CharsetUtil;
import io.tinyrpc.common.utils.SerializationUtils;
import io.tinyrpc.constant.RpcConstants;
import io.tinyrpc.flow.processor.FlowPostProcessor;
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

	private final FlowPostProcessor postProcessor;

	public RpcDecoder(FlowPostProcessor postProcessor) {
		this.postProcessor = postProcessor;
	}

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

		Serialization serialization = getJdkSerialization(serializationType);
		switch (msgTypeEnum) {
			case REQUEST:
				// TODO 新增CASE
				// 服务消费者发送给服务提供者的心跳数据
			case HEARTBEAT_FROM_CONSUMER:
				// 服务消费者响应服务提供者的心跳数据
			case HEARTBEAT_TO_PROVIDER:
				RpcRequest request = serialization.deserialize(data, RpcRequest.class);
				if (request != null) {
					RpcProtocol<RpcRequest> protocol = new RpcProtocol<>();
					protocol.setHeader(header);
					protocol.setBody(request);
					list.add(protocol);
				}
				break;
			case RESPONSE:
				// TODO 新增case
				// 服务提供者响应服务消费者的心跳数据
			case HEARTBEAT_TO_CONSUMER:
				// 服务提供者发送给服务消费者的心跳数据
			case HEARTBEAT_FROM_PROVIDER:
				RpcResponse response = serialization.deserialize(data, RpcResponse.class);
				if (response != null) {
					RpcProtocol<RpcResponse> protocol = new RpcProtocol<>();
					protocol.setHeader(header);
					protocol.setBody(response);
					list.add(protocol);
				}
				break;
		}

		this.postFlowProcessor(postProcessor, header);
	}
}
