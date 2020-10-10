package com.tinyrpc.test.netty.message;

import com.tinyrpc.test.netty.util.Session;
import io.netty.util.AttributeKey;

public interface Attributes {
	AttributeKey<Boolean> LOGIN = AttributeKey.newInstance("login");
	AttributeKey<Session> SUCCESS = AttributeKey.newInstance("session");
}
