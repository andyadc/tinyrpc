package io.tinyrpc.exception.processor;

import io.tinyrpc.constant.RpcConstants;
import io.tinyrpc.spi.annotation.SPI;

/**
 * 异常信息后置处理器
 */
@SPI(RpcConstants.EXCEPTION_POST_PROCESSOR_PRINT)
public interface ExceptionPostProcessor {

	/**
	 * 处理异常信息，进行统计等
	 */
	void postExceptionProcessor(Throwable e);
}
