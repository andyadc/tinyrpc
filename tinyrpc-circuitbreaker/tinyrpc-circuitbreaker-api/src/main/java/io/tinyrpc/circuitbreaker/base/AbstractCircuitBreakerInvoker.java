package io.tinyrpc.circuitbreaker.base;

import io.tinyrpc.circuitbreaker.api.CircuitBreakerInvoker;
import io.tinyrpc.constant.RpcConstants;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 抽象熔断类
 */
public abstract class AbstractCircuitBreakerInvoker implements CircuitBreakerInvoker {

	/**
	 * 熔断状态，1：关闭； 2：半开启； 3：开启
	 */
	protected static final AtomicInteger fusingStatus = new AtomicInteger(RpcConstants.CIRCUIT_BREAKER_STATUS_CLOSED);
	/**
	 * 熔断状态，1：关闭； 2：半开启； 3：开启
	 */
	protected final AtomicInteger circuitBreakerStatus = new AtomicInteger(RpcConstants.CIRCUIT_BREAKER_STATUS_CLOSED);
	/**
	 * 当前调用次数
	 */
	protected final AtomicInteger currentCounter = new AtomicInteger(0);

	/**
	 * 当前调用失败的次数
	 */
	protected final AtomicInteger currentFailureCounter = new AtomicInteger(0);

	/**
	 * 半开启状态下的等待状态
	 */
	protected final AtomicInteger circuitBreakerWaitStatus = new AtomicInteger(RpcConstants.CIRCUIT_BREAKER_WAIT_STATUS_INIT);

	/**
	 * 熔断时间范围的开始时间点
	 */
	protected volatile long lastTimestamp = System.currentTimeMillis();

	/**
	 * 在milliSeconds毫秒内触发熔断操作的上限值
	 * 可能是错误个数，也可能是错误率
	 */
	protected double totalFailure;

	/**
	 * 毫秒数
	 */
	protected int milliSeconds;

	/**
	 * 获取失败策略的结果值
	 */
	public abstract double getFailureStrategyValue();

	/**
	 * 重置数量
	 */
	protected void resetCount() {
		currentFailureCounter.set(0);
		currentCounter.set(0);
	}

	@Override
	public void incrementCount() {
		currentCounter.incrementAndGet();
	}

	@Override
	public void markSuccess() {
		if (fusingStatus.get() == RpcConstants.CIRCUIT_BREAKER_STATUS_HALF_OPEN) {
			circuitBreakerWaitStatus.compareAndSet(RpcConstants.CIRCUIT_BREAKER_WAIT_STATUS_WAITINF, RpcConstants.CIRCUIT_BREAKER_WAIT_STATUS_SUCCESS);
		}
	}

	@Override
	public void markFailed() {
		currentFailureCounter.incrementAndGet();
		if (fusingStatus.get() == RpcConstants.CIRCUIT_BREAKER_STATUS_HALF_OPEN) {
			circuitBreakerWaitStatus.compareAndSet(RpcConstants.CIRCUIT_BREAKER_WAIT_STATUS_WAITINF, RpcConstants.CIRCUIT_BREAKER_WAIT_STATUS_FAILED);
		}
	}

	@Override
	public void init(double totalFailure, int milliSeconds) {
		this.totalFailure = totalFailure <= 0 ? RpcConstants.DEFAULT_CIRCUIT_BREAKER_TOTAL_FAILURE : totalFailure;
		this.milliSeconds = milliSeconds <= 0 ? RpcConstants.DEFAULT_CIRCUIT_BREAKER_MILLI_SECONDS : milliSeconds;
	}

	/**
	 * 处理开启状态的逻辑
	 */
	protected boolean invokeOpenCircuitBreakerStrategy() {
		//获取当前时间
		long currentTimeStamp = System.currentTimeMillis();
		//超过一个指定的时间范围
		if (currentTimeStamp - lastTimestamp >= milliSeconds) {
			//修改等待状态，让修改成功的线程进入半开启状态
			if (circuitBreakerWaitStatus.compareAndSet(RpcConstants.CIRCUIT_BREAKER_WAIT_STATUS_INIT, RpcConstants.CIRCUIT_BREAKER_WAIT_STATUS_WAITINF)) {
				fusingStatus.set(RpcConstants.CIRCUIT_BREAKER_STATUS_HALF_OPEN);
				lastTimestamp = currentTimeStamp;
				this.resetCount();
				return false;
			}
		}
		return true;
	}

	/**
	 * 处理半开启状态的逻辑
	 */
	protected boolean invokeHalfOpenCircuitBreakerStrategy() {
		//此时熔断状态还是半开启状态，等待状态可能是等待，可能是成功，可能是失败
		//获取当前时间
		long currentTimeStamp = System.currentTimeMillis();
		//成功了，表示服务已经恢复
		if (circuitBreakerWaitStatus.compareAndSet(RpcConstants.CIRCUIT_BREAKER_WAIT_STATUS_SUCCESS, RpcConstants.CIRCUIT_BREAKER_WAIT_STATUS_INIT)) {
			fusingStatus.set(RpcConstants.CIRCUIT_BREAKER_STATUS_CLOSED);
			lastTimestamp = currentTimeStamp;
			this.resetCount();
			return false;
		}
		//失败了，表示服务还未恢复
		if (circuitBreakerWaitStatus.compareAndSet(RpcConstants.CIRCUIT_BREAKER_WAIT_STATUS_FAILED, RpcConstants.CIRCUIT_BREAKER_WAIT_STATUS_INIT)) {
			//服务未恢复
			fusingStatus.set(RpcConstants.CIRCUIT_BREAKER_STATUS_OPEN);
			lastTimestamp = currentTimeStamp;
			return true;
		}
		//1.半开启状态的线程还未执行完逻辑，并发情况下的其他线程状态不变，直接返回true，执行熔断逻辑，此时熔断状态仍为半开启状态
		//2.并发情况下，只有一个线程会检测到服务是否已经恢复，其他线程状态不变，直接返回true，执行熔断逻辑，此时熔断状态为开启或者关闭
		//3.执行熔断逻辑的线程，不会执行真实方法的逻辑，会调用降级方法返回数据。
		return true;
	}

	/**
	 * 处理关闭状态的逻辑
	 */
	protected boolean invokeClosedCircuitBreakerStrategy() {
		//获取当前时间
		long currentTimeStamp = System.currentTimeMillis();
		//超过一个指定的时间范围
		if (currentTimeStamp - lastTimestamp >= milliSeconds) {
			lastTimestamp = currentTimeStamp;
			this.resetCount();
			return false;
		}
		//如果当前错误数或者百分比大于或等于配置的百分比
		if (this.getFailureStrategyValue() >= totalFailure) {
			lastTimestamp = currentTimeStamp;
			fusingStatus.set(RpcConstants.CIRCUIT_BREAKER_STATUS_OPEN);
			return true;
		}
		return false;
	}
}
