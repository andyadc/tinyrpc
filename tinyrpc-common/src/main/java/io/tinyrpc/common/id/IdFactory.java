package io.tinyrpc.common.id;

import java.util.concurrent.atomic.LongAdder;

public class IdFactory {

	private final static LongAdder REQUEST_ID_GEN = new LongAdder();

	public static Long getId() {
		REQUEST_ID_GEN.increment();
		return REQUEST_ID_GEN.longValue();
	}
}
