package io.tinyrpc.circuitbreaker.counter;

import io.tinyrpc.circuitbreaker.base.AbstractCircuitBreakerInvoker;
import io.tinyrpc.constant.RpcConstants;
import io.tinyrpc.spi.annotation.SPIClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SPIClass
public class CounterCircuitBreakerInvoker extends AbstractCircuitBreakerInvoker {

	private static final Logger logger = LoggerFactory.getLogger(CounterCircuitBreakerInvoker.class);

	@Override
	public boolean invokeCircuitBreakerStrategy() {
		logger.info("--- execute counter circuit breaker strategy ---");
		logger.info("current counter circuit breaker status is {}", circuitBreakerStatus.get());
		switch (circuitBreakerStatus.get()) {
			//关闭状态
			case RpcConstants.CIRCUIT_BREAKER_STATUS_CLOSED:
				return this.invokeClosedFusingStrategy();
			//半开启状态
			case RpcConstants.CIRCUIT_BREAKER_STATUS_HALF_OPEN:
				return this.invokeHalfOpenFusingStrategy();
			//开启状态
			case RpcConstants.CIRCUIT_BREAKER_STATUS_OPEN:
				return this.invokeOpenFusingStrategy();
			default:
				return this.invokeClosedFusingStrategy();
		}
	}

	/**
	 * 处理开启状态
	 */
	private boolean invokeOpenFusingStrategy() {
		//获取当前时间
		long currentTimeStamp = System.currentTimeMillis();
		//超过一个指定的时间范围，则将状态设置为半开启状态
		if (currentTimeStamp - lastTimestamp >= milliSeconds) {
			circuitBreakerStatus.set(RpcConstants.CIRCUIT_BREAKER_STATUS_HALF_OPEN);
			lastTimestamp = currentTimeStamp;
			this.resetCount();
			return false;
		}
		return true;
	}

	/**
	 * 处理半开启状态
	 */
	private boolean invokeHalfOpenFusingStrategy() {
		//获取当前时间
		long currentTimeStamp = System.currentTimeMillis();
		//服务已经恢复
		if (currentFailureCounter.get() <= 0) {
			circuitBreakerStatus.set(RpcConstants.CIRCUIT_BREAKER_STATUS_CLOSED);
			lastTimestamp = currentTimeStamp;
			this.resetCount();
			return false;
		}
		//服务未恢复
		circuitBreakerStatus.set(RpcConstants.CIRCUIT_BREAKER_STATUS_OPEN);
		lastTimestamp = currentTimeStamp;
		return true;
	}

	/**
	 * 处理关闭状态逻辑
	 */
	private boolean invokeClosedFusingStrategy() {
		//获取当前时间
		long currentTimeStamp = System.currentTimeMillis();
		//超过一个指定的时间范围
		if (currentTimeStamp - lastTimestamp >= milliSeconds) {
			lastTimestamp = currentTimeStamp;
			this.resetCount();
			return false;
		}
		//超出配置的错误数量
		if (currentFailureCounter.get() >= totalFailure) {
			lastTimestamp = currentTimeStamp;
			circuitBreakerStatus.set(RpcConstants.CIRCUIT_BREAKER_STATUS_OPEN);
			return true;
		}
		return false;
	}
}
