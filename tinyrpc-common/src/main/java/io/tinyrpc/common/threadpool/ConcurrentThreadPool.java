package io.tinyrpc.common.threadpool;

import io.tinyrpc.constant.RpcConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ConcurrentThreadPool {

	private static final Logger logger = LoggerFactory.getLogger(ConcurrentThreadPool.class);
	private static final ThreadLocal<Instant> duration = new ThreadLocal<>();
	/**
	 * 线程池
	 */
	private static volatile ConcurrentThreadPool instance;
	/**
	 * 线程池
	 */
	private ThreadPoolExecutor threadPoolExecutor;

	private ConcurrentThreadPool() {
	}

	private ConcurrentThreadPool(int corePoolSize, int maximumPoolSize) {
		if (corePoolSize <= 0) {
			corePoolSize = RpcConstants.DEFAULT_CORE_POOL_SIZE;
		}
		if (maximumPoolSize <= 0) {
			maximumPoolSize = RpcConstants.DEFAULT_MAXI_NUM_POOL_SIZE;
		}
		if (corePoolSize > maximumPoolSize) {
			maximumPoolSize = corePoolSize;
		}

		BlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(RpcConstants.DEFAULT_QUEUE_CAPACITY);
		ThreadFactory threadFactory = new ThreadFactory() {
			private int count = 0;

			@Override
			public Thread newThread(Runnable r) {
				return new Thread(r, RpcConstants.DEFAULT_THREADPOOL_NAME_PREFIX + "-" + (count++));
			}
		};

		this.threadPoolExecutor = new ThreadPoolExecutor(
			corePoolSize,
			maximumPoolSize,
			RpcConstants.DEFAULT_KEEP_ALIVE_TIME, TimeUnit.SECONDS,
			queue,
			threadFactory
		) {
			@Override
			protected void beforeExecute(Thread t, Runnable r) {
				duration.set(Instant.now());
				super.beforeExecute(t, r);
			}

			@Override
			protected void afterExecute(Runnable r, Throwable t) {
				super.afterExecute(r, t);
				logger.info("Thread executed time in {}ms", Duration.between(duration.get(), Instant.now()).toMillis());
			}

			@Override
			public void shutdown() {
				logger.info("{}\r\n activeCount: {}, poolSize: {}, corePoolSize: {}, maximumPoolSize: {}, completedTaskCount: {}, taskCount: {}, largestPoolSize: {}",
					Thread.currentThread().getName(),
					this.getActiveCount(),
					this.getPoolSize(),
					this.getCorePoolSize(),
					this.getMaximumPoolSize(),
					this.getCompletedTaskCount(),
					this.getTaskCount(),
					this.getLargestPoolSize()
				);
				super.shutdown();
			}
		};

		this.threadPoolExecutor.prestartAllCoreThreads();
	}

	/**
	 * 单例传递参数创建对象，只以第一次传递的参数为准
	 */
	public static ConcurrentThreadPool getInstance(int corePoolSize, int maximumPoolSize) {
		if (instance == null) {
			synchronized (ConcurrentThreadPool.class) {
				if (instance == null) {
					instance = new ConcurrentThreadPool(corePoolSize, maximumPoolSize);
				}
			}
		}
		return instance;
	}

	public void submit(Runnable task) {
		threadPoolExecutor.submit(task);
	}

	public <T> T submit(Callable<T> task) {
		Future<T> future = threadPoolExecutor.submit(task);
		try {
			return future.get();
		} catch (Exception e) {
			logger.error("submit callable task error", e);
		}
		return null;
	}

	public void stop() {
		threadPoolExecutor.shutdown();
	}
}
