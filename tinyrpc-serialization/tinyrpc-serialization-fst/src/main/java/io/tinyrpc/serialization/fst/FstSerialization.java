package io.tinyrpc.serialization.fst;

import io.tinyrpc.common.exception.SerializerException;
import io.tinyrpc.serialization.api.Serialization;
import io.tinyrpc.spi.annotation.SPIClass;
import org.nustaq.serialization.FSTConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Fst Serialization
 */
@SPIClass
public class FstSerialization implements Serialization {

	private static final Logger logger = LoggerFactory.getLogger(FstSerialization.class);

	@Override
	public <T> byte[] serialize(T obj) {
		if (logger.isDebugEnabled()) {
			logger.debug("--- fst serialize ---");
		}
		if (obj == null) {
			throw new SerializerException("FST serialize object is null");
		}
		FSTConfiguration conf = FSTConfiguration.getDefaultConfiguration();
		return conf.asByteArray(obj);
	}

	@Override
	public <T> T deserialize(byte[] data, Class<T> clazz) {
		if (logger.isDebugEnabled()) {
			logger.debug("--- fst deserialize ---");
		}
		if (data == null) {
			throw new SerializerException("FST deserialize data is null");
		}
		FSTConfiguration conf = FSTConfiguration.getDefaultConfiguration();
		return (T) conf.asObject(data);
	}
}
