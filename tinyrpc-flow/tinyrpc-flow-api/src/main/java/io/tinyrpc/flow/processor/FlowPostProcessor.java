package io.tinyrpc.flow.processor;

import io.tinyrpc.constant.RpcConstants;
import io.tinyrpc.protocol.header.RpcHeader;
import io.tinyrpc.spi.annotation.SPI;

/**
 * 流量分析后置处理器接口
 */
@SPI(RpcConstants.FLOW_POST_PROCESSOR_PRINT)
public interface FlowPostProcessor {

	/**
	 * 流控分析后置处理器方法
	 */
	void postRpcHeaderProcessor(RpcHeader rpcHeader);
}
