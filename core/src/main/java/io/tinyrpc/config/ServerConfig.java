package io.tinyrpc.config;

import java.io.Serializable;
import java.util.Map;

/**
 * 服务配置
 */
public class ServerConfig extends AbstractConfig implements Serializable {

	/**
	 * 默认启动端口，包括不配置或者随机，都从此端口开始计算
	 */
	public static final int DEFAULT_SERVER_PORT = 21217;

	/**
	 * 实际监听IP，与网卡对应
	 */
	protected String host;
	/**
	 * 监听端口
	 */
	protected Integer port;
	/**
	 * 是否启动epoll，
	 */
	protected Boolean epoll;

	/**
	 * 业务线程池类型
	 */
	protected String threadPool;
	/**
	 * 业务线程池core大小
	 */
	protected Integer coreThreads;
	/**
	 * 业务线程池max大小
	 */
	protected Integer maxThreads;
	/**
	 * 业务线程池队列类型
	 */
	protected String queueType;
	/**
	 * 业务线程池队列大小
	 */
	protected Integer queues;
	/**
	 * The Parameters. 自定义参数
	 */
	protected Map<String, String> parameters;

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	public Boolean getEpoll() {
		return epoll;
	}

	public void setEpoll(Boolean epoll) {
		this.epoll = epoll;
	}

	public String getThreadPool() {
		return threadPool;
	}

	public void setThreadPool(String threadPool) {
		this.threadPool = threadPool;
	}

	public Integer getCoreThreads() {
		return coreThreads;
	}

	public void setCoreThreads(Integer coreThreads) {
		this.coreThreads = coreThreads;
	}

	public Integer getMaxThreads() {
		return maxThreads;
	}

	public void setMaxThreads(Integer maxThreads) {
		this.maxThreads = maxThreads;
	}

	public String getQueueType() {
		return queueType;
	}

	public void setQueueType(String queueType) {
		this.queueType = queueType;
	}

	public Integer getQueues() {
		return queues;
	}

	public void setQueues(Integer queues) {
		this.queues = queues;
	}

	public Map<String, String> getParameters() {
		return parameters;
	}

	public void setParameters(Map<String, String> parameters) {
		this.parameters = parameters;
	}
}
