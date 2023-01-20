package io.tinyrpc.serialization.hessian;

import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;
import io.tinyrpc.common.exception.SerializerException;
import io.tinyrpc.serialization.api.Serialization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class Hessian2Serialization implements Serialization {

	private static final Logger logger = LoggerFactory.getLogger(Hessian2Serialization.class);

	@Override
	public <T> byte[] serialize(T obj) {
		if (obj == null) {
			throw new SerializerException("serialize object is null");
		}
		byte[] result;
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		Hessian2Output hessian2Output = new Hessian2Output(byteArrayOutputStream);
		try {
			hessian2Output.startMessage();
			hessian2Output.writeObject(obj);
			hessian2Output.flush();
			hessian2Output.completeMessage();
			result = byteArrayOutputStream.toByteArray();
		} catch (IOException e) {
			throw new SerializerException(e.getMessage(), e);
		} finally {
			try {
				if (hessian2Output != null) {
					hessian2Output.close();
					byteArrayOutputStream.close();
				}
			} catch (IOException e) {
				throw new SerializerException(e.getMessage(), e);
			}
		}
		return result;
	}

	@Override
	public <T> T deserialize(byte[] data, Class<T> clazz) {
		if (data == null) {
			throw new SerializerException("deserialize data is null");
		}
		ByteArrayInputStream byteInputStream = new ByteArrayInputStream(data);
		Hessian2Input hessian2Input = new Hessian2Input(byteInputStream);
		T object = null;
		try {
			hessian2Input.startMessage();
			object = (T) hessian2Input.readObject();
			hessian2Input.completeMessage();
		} catch (IOException e) {
			throw new SerializerException(e.getMessage(), e);
		} finally {
			try {
				if (null != hessian2Input) {
					hessian2Input.close();
					byteInputStream.close();
				}
			} catch (IOException e) {
				throw new SerializerException(e.getMessage(), e);
			}
			return object;
		}
	}
}