package com.tinyrpc.test.netty.util;

import com.tinyrpc.test.netty.message.Attributes;
import io.netty.channel.Channel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SessionUtil {

	private static final Map<String, Channel> uidChannelMap = new ConcurrentHashMap<>();

	public static void bindSession(Session session, Channel channel) {
		uidChannelMap.put(session.getUid(), channel);
		channel.attr(Attributes.SUCCESS).set(session);
	}

	public static void unbindSession(Channel channel) {
		if (hasLogin(channel)) {
			uidChannelMap.remove(getSession(channel).getUid());
			channel.attr(Attributes.SUCCESS).set(null);
		}
	}

	public static boolean hasLogin(Channel channel) {
		return channel.hasAttr(Attributes.SUCCESS);
	}

	public static Session getSession(Channel channel) {
		return channel.attr(Attributes.SUCCESS).get();
	}

	public static Channel getChannel(String uid) {
		return uidChannelMap.get(uid);
	}
}
