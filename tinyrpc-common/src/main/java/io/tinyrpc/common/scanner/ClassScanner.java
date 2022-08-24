package io.tinyrpc.common.scanner;

import java.util.ArrayList;
import java.util.List;

/**
 * 类扫描器
 */
public class ClassScanner {

	/**
	 * 文件
	 */
	private static final String PROTOCOL_FILE = "file";
	/**
	 * jar包
	 */
	private static final String PROTOCOL_JAR = "jar";
	/**
	 * class文件的后缀
	 */
	private static final String CLASS_FILE_SUFFIX = ".class";

	/**
	 * 扫描指定包下的所有类信息
	 *
	 * @param packageName 指定的包名
	 * @return 指定包下所有的完整类名的List集合
	 * @throws Exception error
	 */
	public static List<String> getClassNameList(String packageName) throws Exception {
		//第一个class类的集合
		List<String> classNameList = new ArrayList<>();
		//是否循环迭代
		boolean recursive = true;

		return classNameList;
	}
}
