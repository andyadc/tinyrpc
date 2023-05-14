package io.tinyrpc.ratelimiter.semaphore;

import io.tinyrpc.ratelimiter.base.AbstractRateLimiterInvoker;
import io.tinyrpc.spi.annotation.SPIClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 基于Semaphore的限流策略
 */
@SPIClass
public class SemaphoreRateLimiterInvoker extends AbstractRateLimiterInvoker {

	private static final Logger logger = LoggerFactory.getLogger(SemaphoreRateLimiterInvoker.class);

	private final AtomicInteger currentCounter = new AtomicInteger(0);
	private volatile long lastTimestamp = System.currentTimeMillis();
	private Semaphore semaphore;

	@Override
	public void init(int permits, int milliSeconds) {
		super.init(permits, milliSeconds);
		this.semaphore = new Semaphore(permits);
	}

	@Override
	public boolean tryAcquire() {
		logger.info("--- semaphore rate limiter ---");
		//获取当前时间
		long currentTimeStamp = System.currentTimeMillis();
		//超过一个时间周期
		if (currentTimeStamp - lastTimestamp >= milliSeconds) {
			//重置窗口开始时间
			lastTimestamp = currentTimeStamp;
			//释放所有资源
			semaphore.release(currentCounter.get());
			//重置计数
			currentCounter.set(0);
		}
		boolean result = semaphore.tryAcquire();
		//成功获取资源
		if (result) {
			currentCounter.incrementAndGet();
		}
		return result;
	}

	@Override
	public void release() {
		//TODO ignore
		//semaphore.release();
	}
}
