package io.tinyrpc.config;

import java.util.HashMap;
import java.util.Map;

/**
 * 配置基类
 */
public abstract class AbstractConfig {

	public AbstractConfig() {
	}

	/**
	 * 创建配置项
	 *
	 * @return 配置项
	 */
	protected Map<String, String> createAttributeMap() {
		return new HashMap<>();
	}

	/**
	 * 添加配置项
	 *
	 * @param dest  目标
	 * @param key   键
	 * @param value 值
	 */
	protected void addElement2Map(final Map<String, String> dest, final String key, final Object value) {
		if (null != value) {
			String v = value.toString();
			if (v != null && !v.isEmpty()) {
				dest.put(key, value.toString());
			}
		}
	}
}
