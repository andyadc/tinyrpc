package io.tinyrpc.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.util.Objects;

public class IPUtil {

	private static final Logger logger = LoggerFactory.getLogger(IPUtil.class);

	public static InetAddress getLocalInetAddress() {
		try {
			return InetAddress.getLocalHost();
		} catch (Exception e) {
			logger.error("Get local IP address error.", e);
		}
		return null;
	}

	public static String getLocalAddress() {
		return Objects.requireNonNull(getLocalInetAddress()).toString();
	}

	public static String getLocalHostName() {
		return Objects.requireNonNull(getLocalInetAddress()).getHostName();
	}

	public static String getLocalHostIP() {
		return Objects.requireNonNull(getLocalInetAddress()).getHostAddress();
	}
}
