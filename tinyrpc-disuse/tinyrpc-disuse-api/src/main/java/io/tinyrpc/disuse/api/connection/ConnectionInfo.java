package io.tinyrpc.disuse.api.connection;

import io.netty.channel.Channel;

import java.io.Serializable;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 连接信息
 */
public class ConnectionInfo implements Serializable {

	private static final long serialVersionUID = -2228847060581547994L;
	/**
	 * 使用次数
	 */
	private final AtomicInteger useCount = new AtomicInteger(0);
	/**
	 * Channel连接
	 */
	private Channel channel;
	/**
	 * 连接的时间
	 */
	private long connectionTime;
	/**
	 * 最后使用时间
	 */
	private long lastUseTime;

	public ConnectionInfo() {
	}

	public ConnectionInfo(Channel channel) {
		this.channel = channel;
		long currentTimeStamp = System.currentTimeMillis();
		this.connectionTime = currentTimeStamp;
		this.lastUseTime = currentTimeStamp;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ConnectionInfo info = (ConnectionInfo) o;
		return Objects.equals(channel, info.channel);
	}

	@Override
	public int hashCode() {
		return Objects.hash(channel);
	}

	public Channel getChannel() {
		return channel;
	}

	public void setChannel(Channel channel) {
		this.channel = channel;
	}

	public long getConnectionTime() {
		return connectionTime;
	}

	public void setConnectionTime(long connectionTime) {
		this.connectionTime = connectionTime;
	}

	public long getLastUseTime() {
		return lastUseTime;
	}

	public void setLastUseTime(long lastUseTime) {
		this.lastUseTime = lastUseTime;
	}

	public int getUseCount() {
		return useCount.get();
	}

	public int incrementUseCount() {
		return this.useCount.incrementAndGet();
	}
}
