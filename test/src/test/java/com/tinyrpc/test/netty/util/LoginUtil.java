package com.tinyrpc.test.netty.util;

import com.tinyrpc.test.netty.message.Attributes;
import io.netty.channel.Channel;
import io.netty.util.Attribute;

public class LoginUtil {

	public static void markAsLogin(Channel channel) {
		channel.attr(Attributes.LOGIN).set(true);
	}

	public static boolean hasLogin(Channel channel) {
		Attribute<Boolean> attr = channel.attr(Attributes.LOGIN);
		return attr.get() != null && attr.get();
	}
}
