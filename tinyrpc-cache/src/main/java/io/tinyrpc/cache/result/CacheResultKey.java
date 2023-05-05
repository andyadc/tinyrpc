package io.tinyrpc.cache.result;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;

/**
 * 缓存结果数据的Key
 */
public class CacheResultKey implements Serializable {

	private static final long serialVersionUID = -7695725036893713652L;

	/**
	 * 类名称
	 */
	private String className;
	/**
	 * 方法名称
	 */
	private String methodName;
	/**
	 * 参数类型数组
	 */
	private Class<?>[] parameterTypes;
	/**
	 * 参数数组
	 */
	private Object[] parameters;
	/**
	 * 版本号
	 */
	private String version;
	/**
	 * 服务分组
	 */
	private String group;
	/**
	 * 保存缓存时的时间戳
	 */
	private long cacheTimeStamp;

	public CacheResultKey(String className, String methodName, Class<?>[] parameterTypes, Object[] parameters, String version, String group) {
		this.className = className;
		this.methodName = methodName;
		this.parameterTypes = parameterTypes;
		this.parameters = parameters;
		this.version = version;
		this.group = group;
	}

	public long getCacheTimeStamp() {
		return cacheTimeStamp;
	}

	public void setCacheTimeStamp(long cacheTimeStamp) {
		this.cacheTimeStamp = cacheTimeStamp;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		CacheResultKey cacheKey = (CacheResultKey) o;
		return Objects.equals(className, cacheKey.className)
			&& Objects.equals(methodName, cacheKey.methodName)
			&& Arrays.equals(parameterTypes, cacheKey.parameterTypes)
			&& Arrays.equals(parameters, cacheKey.parameters)
			&& Objects.equals(version, cacheKey.version)
			&& Objects.equals(group, cacheKey.group);
	}

	@Override
	public int hashCode() {
		int result = Objects.hash(className, methodName, version, group);
		result = 31 * result + Arrays.hashCode(parameterTypes);
		result = 31 * result + Arrays.hashCode(parameters);
		return result;
	}

	@Override
	public String toString() {
		return "CacheResultKey{" +
			"className=" + className +
			", methodName=" + methodName +
			", parameterTypes=" + Arrays.toString(parameterTypes) +
			", parameters=" + Arrays.toString(parameters) +
			", version=" + version +
			", group=" + group +
			", cacheTimeStamp=" + cacheTimeStamp +
			'}';
	}
}
