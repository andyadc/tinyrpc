package io.tinyrpc.flow.processor.print;

import io.tinyrpc.flow.processor.FlowPostProcessor;
import io.tinyrpc.protocol.header.RpcHeader;
import io.tinyrpc.spi.annotation.SPIClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 打印处理
 */
@SPIClass
public class PrintFlowPostProcessor implements FlowPostProcessor {

	private static final Logger logger = LoggerFactory.getLogger(PrintFlowPostProcessor.class);

	@Override
	public void postRpcHeaderProcessor(RpcHeader rpcHeader) {
		logger.info(getRpcHeaderString(rpcHeader));
	}

	private String getRpcHeaderString(RpcHeader rpcHeader) {
		return "\n---\n" +
			"magic: " + rpcHeader.getMagic() +
			", requestId: " + rpcHeader.getRequestId() +
			", msgType: " + rpcHeader.getMsgType() +
			", serializationType: " + rpcHeader.getSerializationType() +
			", status: " + rpcHeader.getStatus() +
			", msgLen: " + rpcHeader.getMsgLen()
			+ "\n---"
			;
	}
}
