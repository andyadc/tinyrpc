package io.tinyrpc.ratelimiter.counter;


import io.tinyrpc.ratelimiter.base.AbstractRateLimiterInvoker;
import io.tinyrpc.spi.annotation.SPIClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 计数器限流
 */
@SPIClass
public class CounterRateLimiterInvoker extends AbstractRateLimiterInvoker {

	private static final Logger logger = LoggerFactory.getLogger(CounterRateLimiterInvoker.class);

	private final AtomicInteger currentCounter = new AtomicInteger(0);
	private final ThreadLocal<Boolean> threadLocal = ThreadLocal.withInitial(() -> Boolean.FALSE);
	private volatile long lastTimestamp = System.currentTimeMillis();

	@Override
	public boolean tryAcquire() {
		logger.info("--- counter rate limiter ---");
		logger.debug("lastTimestamp: {}, currentCount: {}", lastTimestamp, currentCounter.get());
		//获取当前时间
		long currentTimestamp = System.currentTimeMillis();
		//超过一个执行周期
		if (currentTimestamp - lastTimestamp >= milliSeconds) {
			lastTimestamp = currentTimestamp;
			currentCounter.set(0);
			return true;
		}
		//当前请求数小于配置的数量
		if (currentCounter.incrementAndGet() <= permits) {
			threadLocal.set(true);
			return true;
		}
		return false;
	}

	@Override
	public void release() {
		if (threadLocal.get()) {
			try {
				currentCounter.decrementAndGet();
				logger.debug("release count={}", currentCounter.get());
			} finally {
				threadLocal.remove();
			}
		}
	}
}
