package com.andyadc.test.spi.service;

import io.tinyrpc.spi.annotation.SPI;

@SPI("spiService")
public interface SPIService {
	String hello(String msg);
}
