package io.tinyrpc.serialization;

import java.io.IOException;

public interface Serialization {

    <T> byte[] serialize(T t) throws IOException;

    <T> T deserialize(byte[] data, Class<T> clazz) throws IOException;
}
