package io.tinyrpc.serialization.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.tinyrpc.common.exception.SerializerException;
import io.tinyrpc.serialization.api.Serialization;
import io.tinyrpc.spi.annotation.SPIClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * Json系列化与反序列化
 */
@SPIClass
public class JsonSerialization implements Serialization {

	private static final Logger logger = LoggerFactory.getLogger(JsonSerialization.class);

	private static final ObjectMapper objectMapper = new ObjectMapper();

	static {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
		objectMapper.setDateFormat(dateFormat);
		objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
		objectMapper.disable(SerializationFeature.FLUSH_AFTER_WRITE_VALUE);
		objectMapper.disable(SerializationFeature.CLOSE_CLOSEABLE);
		objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
		objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		objectMapper.configure(JsonGenerator.Feature.AUTO_CLOSE_TARGET, false);
		objectMapper.configure(JsonGenerator.Feature.AUTO_CLOSE_JSON_CONTENT, false);
		objectMapper.configure(JsonParser.Feature.IGNORE_UNDEFINED, true);
	}

	@Override
	public <T> byte[] serialize(T obj) {
		if (logger.isDebugEnabled()) {
			logger.debug("--- json serialize ---");
		}
		if (obj == null) {
			throw new SerializerException("Serialize object is null");
		}
		byte[] bytes;
		try {
			bytes = objectMapper.writeValueAsBytes(obj);
		} catch (JsonProcessingException e) {
			throw new SerializerException(e.getMessage(), e);
		}
		return bytes;
	}

	@Override
	public <T> T deserialize(byte[] data, Class<T> clazz) {
		if (logger.isDebugEnabled()) {
			logger.debug("--- json deserialize ---");
		}
		if (data == null) {
			throw new SerializerException("Deserialize data is null");
		}
		T obj;
		try {
			obj = objectMapper.readValue(data, clazz);
		} catch (IOException e) {
			throw new SerializerException(e.getMessage(), e);
		}
		return obj;
	}
}
