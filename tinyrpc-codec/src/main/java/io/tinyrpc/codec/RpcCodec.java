package io.tinyrpc.codec;

import io.tinyrpc.serialization.api.Serialization;
import io.tinyrpc.serialization.jdk.JdkSerialization;

/**
 * 实现编解码的接口，提供序列化和反序列化的默认方法
 */
public interface RpcCodec {

	default Serialization getJdkSerialization() {
		return new JdkSerialization();
	}
}
