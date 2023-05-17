package io.tinyrpc.exception.processor.print;

import io.tinyrpc.exception.processor.ExceptionPostProcessor;
import io.tinyrpc.spi.annotation.SPIClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 打印异常
 */
@SPIClass
public class PrintExceptionPostProcessor implements ExceptionPostProcessor {

	private static final Logger logger = LoggerFactory.getLogger(PrintExceptionPostProcessor.class);

	@Override
	public void postExceptionProcessor(Throwable e) {
		logger.warn("--- print server exception ---", e);
	}
}
