package io.tinyrpc.test;

import io.tinyrpc.annotation.RpcReference;
import io.tinyrpc.annotation.RpcService;
import io.tinyrpc.common.scanner.ClassScanner;
import io.tinyrpc.common.scanner.reference.RpcReferenceScanner;
import io.tinyrpc.provider.common.scanner.RpcServiceScanner;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * 扫描测试
 */
public class ScannerTests {

	/**
	 * 扫描 `io.tinyrpc.test` 包下所有的类
	 */
	@Test
	public void testScannerClassNameList() throws Exception {
		List<String> classNameList = ClassScanner.getClassNameList("io.tinyrpc.test");
		classNameList.forEach(System.out::println);
	}

	/**
	 * 扫描`io.tinyrpc.test`包下所有标注了{@link RpcService}注解的类
	 */
	@Test
	public void testScannerClassNameListByRpcService() throws Exception {
		RpcServiceScanner.doScannerWithRpcServiceAnnotationFilterAndRegistryService(
			"127.0.0.1", 2181,
			"io.tinyrpc.test", null);
	}

	/**
	 * 扫描io.binghe.rpc.test.scanner包下所有标注了{@link RpcReference}注解的类
	 */
	@Test
	public void testScannerClassNameListByRpcReference() throws Exception {
		RpcReferenceScanner.doScannerWithRpcReferenceAnnotationFilter("io.tinyrpc.test");
	}
}
