package io.tinyrpc.test.spi;

import io.tinyrpc.test.spi.service.SPIService;
import io.tinyrpc.spi.loader.ExtensionLoader;
import org.junit.jupiter.api.Test;

public class SPITests {

	@Test
	public void testSpiLoader() {
		SPIService service = ExtensionLoader.getExtension(SPIService.class, "spiService");
		String result = service.hello("adc");
		System.out.println(result);
	}
}
