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
				return this.invokeClosedCircuitBreakerStrategy();
			//半开启状态
			case RpcConstants.CIRCUIT_BREAKER_STATUS_HALF_OPEN:
				return this.invokeHalfOpenCircuitBreakerStrategy();
			//开启状态
			case RpcConstants.CIRCUIT_BREAKER_STATUS_OPEN:
				return this.invokeOpenCircuitBreakerStrategy();
			default:
				return this.invokeClosedCircuitBreakerStrategy();
		}
	}

	@Override
	public double getFailureStrategyValue() {
		return currentFailureCounter.doubleValue();
	}
}
