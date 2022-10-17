package io.tinyrpc.serialization.api;

/**
 * 序列化接口
 */
public interface Serialization {

	/**
	 * 序列化
	 */
	<T> byte[] serialize(T obj);

	/**
	 * 反序列化
	 */
	<T> T deserialize(byte[] data, Class<T> clazz);
}
