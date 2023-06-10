package io.tinyrpc.common.threadpool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 异步启动服务提供者
 */
public class AsyncStartProviderThreadPool {

	private static final ExecutorService executorService;

	static {
		executorService = Executors.newFixedThreadPool(1);
	}

	public static void submit(Runnable task) {
		executorService.submit(task);
	}

	public static void close() {
		executorService.shutdown();
	}
}
