package io.tinyrpc.ratelimiter.guava;

import com.google.common.annotations.Beta;
import com.google.common.util.concurrent.RateLimiter;
import io.tinyrpc.ratelimiter.base.AbstractRateLimiterInvoker;
import io.tinyrpc.spi.annotation.SPIClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 基于Guava的限流策略
 */
@Beta
@SPIClass
public class GuavaRateLimiterInvoker extends AbstractRateLimiterInvoker {

	private static final Logger logger = LoggerFactory.getLogger(GuavaRateLimiterInvoker.class);

	private RateLimiter rateLimiter;

	@Override
	public void init(int permits, int milliSeconds) {
		super.init(permits, milliSeconds);
		//转换成每秒钟最多允许的个数
		double permitsPerSecond = ((double) permits) / milliSeconds * 1000;
		this.rateLimiter = RateLimiter.create(permitsPerSecond);
	}

	@Override
	public boolean tryAcquire() {
		logger.info("--- guava rate limiter ---");
		return this.rateLimiter.tryAcquire();
	}

	@Override
	public void release() {
		//TODO ignore
	}
}
