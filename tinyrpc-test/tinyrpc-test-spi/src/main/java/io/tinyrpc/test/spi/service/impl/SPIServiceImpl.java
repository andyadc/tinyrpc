package io.tinyrpc.test.spi.service.impl;

import io.tinyrpc.test.spi.service.SPIService;
import io.tinyrpc.spi.annotation.SPIClass;

@SPIClass
public class SPIServiceImpl implements SPIService {
	@Override
	public String hello(String msg) {
		return "hello " + msg;
	}
}
