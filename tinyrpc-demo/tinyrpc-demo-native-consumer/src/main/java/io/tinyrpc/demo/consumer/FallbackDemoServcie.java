package io.tinyrpc.demo.consumer;

import io.tinyrpc.demo.api.DemoService;

public class FallbackDemoServcie implements DemoService {

	@Override
	public String hello(String message) {
		return "fallback hello " + message;
	}
}
