package io.tinyrpc.spi.factory;

import io.tinyrpc.spi.annotation.SPIClass;

/**
 * SpiExtensionFactory
 */
@SPIClass
public class SpiExtensionFactory implements ExtensionFactory{

	@Override
	public <T> T getExtension(String key, Class<T> clazz) {
		return null;
	}
}
