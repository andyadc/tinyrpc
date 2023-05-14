package io.tinyrpc.circuitbreaker.percent;

import io.tinyrpc.circuitbreaker.base.AbstractCircuitBreakerInvoker;
import io.tinyrpc.constant.RpcConstants;
import io.tinyrpc.spi.annotation.SPIClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 在一段时间内基于错误率的熔断策略
 */
@SPIClass
public class PercentCircuitBreakerInvoker extends AbstractCircuitBreakerInvoker {

	private static final Logger logger = LoggerFactory.getLogger(PercentCircuitBreakerInvoker.class);

	@Override
	public boolean invokeCircuitBreakerStrategy() {
		logger.info("--- execute percent fusing strategy ---");
		logger.info("current fusing status is {}", circuitBreakerStatus.get());
		boolean result;
		switch (circuitBreakerStatus.get()) {
			//关闭状态
			case RpcConstants.CIRCUIT_BREAKER_STATUS_CLOSED:
				result = this.invokeClosedCircuitBreakerStrategy();
				break;
			//半开启状态
			case RpcConstants.CIRCUIT_BREAKER_STATUS_HALF_OPEN:
				result = this.invokeHalfOpenCircuitBreakerStrategy();
				break;
			//开启状态
			case RpcConstants.CIRCUIT_BREAKER_STATUS_OPEN:
				result = this.invokeOpenCircuitBreakerStrategy();
				break;
			default:
				result = this.invokeClosedCircuitBreakerStrategy();
				break;
		}
		return result;
	}

	/**
	 * 处理开启状态的逻辑
	 */
	private boolean invokeOpenCircuitBreakerStrategy() {
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
	 * 处理半开启状态的逻辑
	 */
	private boolean invokeHalfOpenCircuitBreakerStrategy() {
		//获取当前时间
		long currentTimestamp = System.currentTimeMillis();
		//服务已经恢复
		if (currentFailureCounter.get() <= 0) {
			circuitBreakerStatus.set(RpcConstants.CIRCUIT_BREAKER_STATUS_CLOSED);
			lastTimestamp = currentTimestamp;
			this.resetCount();
			return false;
		}
		//服务未恢复
		circuitBreakerStatus.set(RpcConstants.CIRCUIT_BREAKER_STATUS_OPEN);
		lastTimestamp = currentTimestamp;
		return true;
	}

	/**
	 * 处理关闭状态的逻辑
	 */
	private boolean invokeClosedCircuitBreakerStrategy() {
		//获取当前时间
		long currentTimeStamp = System.currentTimeMillis();
		//超过一个指定的时间范围
		if (currentTimeStamp - lastTimestamp >= milliSeconds) {
			lastTimestamp = currentTimeStamp;
			this.resetCount();
			return false;
		}
		//如果当前错误百分比大于或等于配置的百分比
		if (this.getCurrentPercent() >= totalFailure) {
			lastTimestamp = currentTimeStamp;
			circuitBreakerStatus.set(RpcConstants.CIRCUIT_BREAKER_STATUS_OPEN);
			return true;
		}
		return false;
	}

	/**
	 * 计算当前错误百分比
	 */
	private double getCurrentPercent() {
		if (currentCounter.get() <= 0) return 0;
		return (double) currentFailureCounter.get() / currentCounter.get() * 100;
	}
}
