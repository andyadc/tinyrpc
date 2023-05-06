package io.tinyrpc.codec;

import io.tinyrpc.common.threadpool.FlowPostProcessorThreadPool;
import io.tinyrpc.flow.processor.FlowPostProcessor;
import io.tinyrpc.protocol.header.RpcHeader;
import io.tinyrpc.serialization.api.Serialization;
import io.tinyrpc.spi.loader.ExtensionLoader;

/**
 * 实现编解码的接口，提供序列化和反序列化的默认方法
 */
public interface RpcCodec {

	/**
	 * 根据serializationType通过SPI获取序列化句柄
	 *
	 * @param serializationType 序列化方式
	 * @return Serialization对象
	 */
	default Serialization getJdkSerialization(String serializationType) {
		return ExtensionLoader.getExtension(Serialization.class, serializationType);
	}

	/**
	 * 调用RPC框架流量分析后置处理器
	 *
	 * @param postProcessor 后置处理器
	 * @param header        封装了流量信息的消息头
	 */
	default void postFlowProcessor(FlowPostProcessor postProcessor, RpcHeader header) {
		FlowPostProcessorThreadPool.submit(() -> {
			postProcessor.postRpcHeaderProcessor(header);
		});
	}
}
