package io.tinyrpc.proxy.asm.classloader;

import java.util.HashMap;
import java.util.Map;

/**
 * 自定义类加载器
 */
public class ASMClassLoader extends ClassLoader {

	private final Map<String, byte[]> classMap = new HashMap<>();

	public void add(String name, byte[] bytes) {
		classMap.put(name, bytes);
	}

	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		if (classMap.containsKey(name)) {
			byte[] bytes = classMap.get(name);
			classMap.remove(name);
			return defineClass(name, bytes, 0, bytes.length);
		}
		return super.findClass(name);
	}
}
