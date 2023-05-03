package io.tinyrpc.common.utils;

import java.util.concurrent.TimeUnit;

public final class ThreadUtil {

	public static void sleep(long duration, TimeUnit unit) {
		try {
			Thread.sleep(unit.toMillis(duration));
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}
}
