package io.tinyrpc.common.test;

import io.tinyrpc.common.scanner.ClassScanner;
import io.tinyrpc.common.scanner.reference.RpcReferenceScanner;
import io.tinyrpc.common.scanner.server.RpcServiceScanner;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

public class Tests {

	@Test
	public void test_getClassNameList() throws Exception {
		String packageName = "io.tinyrpc.common.test.service";
		List<String> classNameList = ClassScanner.getClassNameList(packageName);
		classNameList.forEach(
			System.out::println
		);
	}

	@Test
	public void test_doScannerWithRpcServiceAnnotationFilterAndRegistryService() throws Exception {
		String packageName = "io.tinyrpc.common.test.service";
		Map<String, Object> map = RpcServiceScanner.doScannerWithRpcServiceAnnotationFilterAndRegistryService(packageName);
		map.forEach((k, v) -> {
			System.out.println(k + " - " + v);
		});
	}

	@Test
	public void test_doScannerWithRpcReferenceAnnotationFilter() throws Exception {
		String packageName = "io.tinyrpc.common.test.service";
		Map<String, Object> map = RpcReferenceScanner.doScannerWithRpcReferenceAnnotationFilter(packageName);
		map.forEach((k, v) -> {
			System.out.println(k + " - " + v);
		});
	}
}
