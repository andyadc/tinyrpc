package com.tinyrpc.test.netty.serializer;

import com.alibaba.fastjson.JSON;

public class JSONSerializer implements Serializer {

	@Override
	public byte getSerializerAlgorithm() {
		return SerializerAlgorithm.JSON;
	}

	@Override
	public byte[] serializer(Object o) {
		return JSON.toJSONBytes(o);
	}

	@Override
	public <T> T deserialize(byte[] bytes, Class<T> clazz) {
		return JSON.parseObject(bytes, clazz);
	}
}
