package com.tinyrpc.test.netty.serializer;

public interface Serializer {

	Serializer DEFAULT = new JSONSerializer();

	/**
	 * 序列化算法
	 */
	byte getSerializerAlgorithm();

	byte[] serializer(Object o);

	<T> T deserialize(byte[] bytes, Class<T> clazz);

	interface SerializerAlgorithm {
		/**
		 * json 序列化标识
		 */
		byte JSON = 1;
	}
}
