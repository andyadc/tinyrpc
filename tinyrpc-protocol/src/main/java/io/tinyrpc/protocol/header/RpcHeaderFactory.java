package io.tinyrpc.protocol.header;

import io.tinyrpc.common.constants.RpcConstants;
import io.tinyrpc.common.id.IdFactory;
import io.tinyrpc.protocol.enumeration.RpcType;

/**
 * RpcHeaderFactory
 */
public class RpcHeaderFactory {

	public static RpcHeader getRequestHeader(String serializationType) {
		RpcHeader header = new RpcHeader();
		long requestId = IdFactory.getId();
		header.setMagic(RpcConstants.MAGIC);
		header.setRequestId(requestId);
		header.setMsgType((byte) RpcType.REQUEST.getType());
		header.setStatus((byte) 0x1);
		header.setSerializationType(serializationType);
		return header;
	}
}
