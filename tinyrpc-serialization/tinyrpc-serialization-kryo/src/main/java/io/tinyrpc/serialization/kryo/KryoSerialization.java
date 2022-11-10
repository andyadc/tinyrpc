package io.tinyrpc.serialization.kryo;

import io.tinyrpc.serialization.api.Serialization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KryoSerialization implements Serialization {

	private static final Logger logger = LoggerFactory.getLogger(KryoSerialization.class);

	@Override
	public <T> byte[] serialize(T obj) {
		return new byte[0];
	}

	@Override
	public <T> T deserialize(byte[] data, Class<T> clazz) {
		return null;
	}
}
