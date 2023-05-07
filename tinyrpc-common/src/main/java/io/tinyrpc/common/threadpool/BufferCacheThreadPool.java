package io.tinyrpc.common.threadpool;

import io.tinyrpc.constant.RpcConstants;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 *
 */
public class BufferCacheThreadPool {

	private static final ThreadPoolExecutor threadPoolExecutor;

	static {
		threadPoolExecutor = new ThreadPoolExecutor(
			6,
			8,
			RpcConstants.DEFAULT_KEEP_ALIVE_TIME,
			TimeUnit.SECONDS,
			new ArrayBlockingQueue<>(RpcConstants.DEFAULT_QUEUE_CAPACITY),
			new ThreadFactory() {
				private int count = 0;

				@Override
				public Thread newThread(Runnable r) {
					return new Thread(r, "BufferCache" + "-" + (count++));
				}
			}
		);
	}

	public static void submit(Runnable task) {
		threadPoolExecutor.submit(task);
	}

	public static void shutdown() {
		threadPoolExecutor.shutdown();
	}
}
