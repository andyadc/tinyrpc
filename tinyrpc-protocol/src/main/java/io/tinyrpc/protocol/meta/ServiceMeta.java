package io.tinyrpc.protocol.meta;

import java.io.Serializable;

/**
 * 服务元数据，注册到注册中心的元数据信息
 */
public class ServiceMeta implements Serializable {

	private static final long serialVersionUID = -1806163011001544412L;

	/**
	 * 服务名称
	 */
	private String name;

	/**
	 * 服务版本号
	 */
	private String version;

	/**
	 * 服务地址
	 */
	private String address;

	/**
	 * 服务端口
	 */
	private int port;

	/**
	 * 服务分组
	 */
	private String group;

	public ServiceMeta() {
	}

	public ServiceMeta(String name, String version, String address, int port, String group) {
		this.name = name;
		this.version = version;
		this.address = address;
		this.port = port;
		this.group = group;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}
}
