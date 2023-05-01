package com.andyadc.test.spi.service.impl;

import com.andyadc.test.spi.service.SPIService;
import io.tinyrpc.spi.annotation.SPIClass;

@SPIClass
public class SPIServiceImpl implements SPIService {
	@Override
	public String hello(String msg) {
		return "hello " + msg;
	}
}
