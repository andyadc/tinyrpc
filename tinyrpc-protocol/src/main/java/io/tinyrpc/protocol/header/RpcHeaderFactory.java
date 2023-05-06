package io.tinyrpc.protocol.header;

import io.tinyrpc.constant.RpcConstants;
import io.tinyrpc.common.id.IdFactory;

/**
 * RpcHeaderFactory
 */
public class RpcHeaderFactory {

	public static RpcHeader getRequestHeader(String serializationType, int messageType) {
		RpcHeader header = new RpcHeader();
		long requestId = IdFactory.getId();
		header.setMagic(RpcConstants.MAGIC);
		header.setRequestId(requestId);
		header.setMsgType((byte) messageType);
		header.setStatus((byte) 0x1);
		header.setSerializationType(serializationType);
		return header;
	}
}
