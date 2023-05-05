package io.tinyrpc.cache.result;

import io.tinyrpc.common.constants.RpcConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 结果缓存管理器
 */
public class CacheResultManager<T> {

	private static final Logger logger = LoggerFactory.getLogger(CacheResultManager.class);

	private static volatile CacheResultManager instance;
	/**
	 * 缓存结果信息
	 */
	private final Map<CacheResultKey, T> cacheResult = new ConcurrentHashMap<>(4096);
	/**
	 * 读写锁
	 */
	private final ReadWriteLock lock = new ReentrantReadWriteLock();
	/**
	 * 读锁
	 */
	private final Lock readLock = lock.readLock();
	/**
	 * 写锁
	 */
	private final Lock writeLock = lock.writeLock();
	/**
	 * 扫描结果缓存的线程池
	 */
	private final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
	/**
	 * 结果缓存过期时长，单位毫秒
	 */
	private int resultCacheExpire;

	public CacheResultManager(int resultCacheExpire, boolean enableResultCache) {
		this.resultCacheExpire = resultCacheExpire;
		if (enableResultCache) {
			this.startScanTask();
		}
	}

	public static <T> CacheResultManager<T> getInstance(int resultCacheExpire, boolean enableResultCache) {
		if (instance == null) {
			synchronized (CacheResultManager.class) {
				if (instance == null) {
					instance = new CacheResultManager(resultCacheExpire, enableResultCache);
				}
			}
		}
		return instance;
	}

	/**
	 * 扫描结果缓存
	 */
	private void startScanTask() {
		scheduledExecutorService.scheduleAtFixedRate(() -> {
			if (cacheResult.size() > 0) {
				writeLock.lock();
				try {
					Iterator<Map.Entry<CacheResultKey, T>> iterator = cacheResult.entrySet().iterator();
					while (iterator.hasNext()) {
						Map.Entry<CacheResultKey, T> entry = iterator.next();
						CacheResultKey cacheKey = entry.getKey();
						//当时间减去保存数据的缓存时间大于配置的时间间隔，则需要剔除缓存数据
						if (System.currentTimeMillis() - cacheKey.getCacheTimeStamp() > resultCacheExpire) {
							cacheResult.remove(cacheKey);
							logger.info("removed cache key: {}", cacheKey);
						}
					}
				} finally {
					writeLock.unlock();
				}
			}
		}, 0, RpcConstants.RPC_SCAN_RESULT_CACHE_TIME_INTERVAL, TimeUnit.MILLISECONDS);
	}


	/**
	 * 获取缓存中的数据
	 */
	public T get(CacheResultKey key) {
		return cacheResult.get(key);
	}

	/**
	 * 缓存数据
	 */
	public void put(CacheResultKey key, T value) {
		writeLock.lock();
		try {
			cacheResult.put(key, value);
		} finally {
			writeLock.unlock();
		}
	}
}
