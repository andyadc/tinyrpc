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

	@Override
	public double getFailureStrategyValue() {
		if (currentCounter.get() <= 0) return 0;
		return currentFailureCounter.doubleValue() / currentCounter.doubleValue() * 100;
	}
}
