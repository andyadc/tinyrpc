package com.tinyrpc.test.netty.message;

import io.netty.util.AttributeKey;

public interface Attributes {
	AttributeKey<Boolean> LOGIN = AttributeKey.newInstance("login");
}
