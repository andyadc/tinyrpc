package io.tinyrpc.registry.api.config;

import java.io.Serializable;

/**
 * 注册配置类
 */
public class RegistryConfig implements Serializable {

	private static final long serialVersionUID = -8600943039038234251L;

	/**
	 * 注册地址
	 */
	private String registryAddr;

	/**
	 * 注册类型
	 */
	private String registryType;

	public RegistryConfig(String registryAddr, String registryType) {
		this.registryAddr = registryAddr;
		this.registryType = registryType;
	}

	public String getRegistryAddr() {
		return registryAddr;
	}

	public void setRegistryAddr(String registryAddr) {
		this.registryAddr = registryAddr;
	}

	public String getRegistryType() {
		return registryType;
	}

	public void setRegistryType(String registryType) {
		this.registryType = registryType;
	}
}
