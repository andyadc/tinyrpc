package io.tinyrpc.serialization.kryo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.JavaSerializer;
import io.tinyrpc.common.exception.SerializerException;
import io.tinyrpc.serialization.api.Serialization;
import io.tinyrpc.spi.annotation.SPIClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Kryo Serialization
 */
@SPIClass
public class KryoSerialization implements Serialization {

	private static final Logger logger = LoggerFactory.getLogger(KryoSerialization.class);

	@Override
	public <T> byte[] serialize(T obj) {
		if (logger.isDebugEnabled()) {
			logger.debug("--- kryo serialize ---");
		}
		if (obj == null) {
			throw new SerializerException("Serialize object is null");
		}

		Kryo kryo = new Kryo();
		kryo.setReferences(false);
		kryo.register(obj.getClass(), new JavaSerializer());
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		Output output = new Output(baos);
		kryo.writeClassAndObject(output, obj);
		output.flush();
		output.close();
		byte[] bytes = baos.toByteArray();
		try {
			baos.flush();
			baos.close();
		} catch (IOException e) {
			throw new SerializerException(e.getMessage(), e);
		}
		return bytes;
	}

	@Override
	public <T> T deserialize(byte[] data, Class<T> clazz) {
		if (logger.isDebugEnabled()) {
			logger.debug("--- kryo deserialize ---");
		}
		if (data == null) {
			throw new SerializerException("deserialize data is null");
		}

		Kryo kryo = new Kryo();
		kryo.setReferences(false);
		kryo.register(clazz, new JavaSerializer());
		ByteArrayInputStream bais = new ByteArrayInputStream(data);
		Input input = new Input(bais);
		return (T) kryo.readClassAndObject(input);
	}
}
