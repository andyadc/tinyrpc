package io.tinyrpc.serialization.fst;

import io.tinyrpc.common.exception.SerializerException;
import io.tinyrpc.serialization.api.Serialization;
import org.nustaq.serialization.FSTConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FstSerialization implements Serialization {

	private static final Logger logger = LoggerFactory.getLogger(FstSerialization.class);

	@Override
	public <T> byte[] serialize(T obj) {
		if (obj == null) {
			throw new SerializerException("FST serialize object is null");
		}
		FSTConfiguration conf = FSTConfiguration.getDefaultConfiguration();
		return conf.asByteArray(obj);
	}

	@Override
	public <T> T deserialize(byte[] data, Class<T> clazz) {
		if (data == null) {
			throw new SerializerException("FST deserialize data is null");
		}
		FSTConfiguration conf = FSTConfiguration.getDefaultConfiguration();
		return (T) conf.asObject(data);
	}
}
