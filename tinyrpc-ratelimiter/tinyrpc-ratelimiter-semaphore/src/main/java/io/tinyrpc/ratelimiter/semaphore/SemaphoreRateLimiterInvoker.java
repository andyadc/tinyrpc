package io.tinyrpc.ratelimiter.semaphore;

import io.tinyrpc.ratelimiter.base.AbstractRateLimiterInvoker;
import io.tinyrpc.spi.annotation.SPIClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Semaphore;

/**
 * 基于Semaphore的限流策略
 */
@SPIClass
public class SemaphoreRateLimiterInvoker extends AbstractRateLimiterInvoker {

	private static final Logger logger = LoggerFactory.getLogger(SemaphoreRateLimiterInvoker.class);

	private Semaphore semaphore;

	@Override
	public void init(int permits, int milliSeconds) {
		super.init(permits, milliSeconds);
		this.semaphore = new Semaphore(permits);
	}

	@Override
	public boolean tryAcquire() {
		logger.info("--- semaphore rate limiter ---");
		return semaphore.tryAcquire();
	}

	@Override
	public void release() {
		semaphore.release();
	}
}
