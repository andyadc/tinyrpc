package io.tinyrpc.test.registry;

import io.tinyrpc.protocol.meta.ServiceMeta;
import io.tinyrpc.registry.api.RegistryService;
import io.tinyrpc.registry.api.config.RegistryConfig;
import io.tinyrpc.registry.zookeeper.ZookeeperRegistryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ZookeeperRegistryTests {

	private RegistryService registryService;
	private ServiceMeta serviceMeta;

	@BeforeEach
	public void init() throws Exception {
		RegistryConfig registryConfig = new RegistryConfig("127.0.0.1:2181", "zookeeper");
		this.registryService = new ZookeeperRegistryService();
		this.registryService.init(registryConfig);
		this.serviceMeta = new ServiceMeta(ZookeeperRegistryTests.class.getName(), "1.0.0", "andyadc", "127.0.0.1", 8080);
	}

	@Test
	public void testRegister() throws Exception {
		registryService.register(serviceMeta);
	}

	@Test
	public void testUnregister() throws Exception {
		registryService.unregister(serviceMeta);
	}

	@Test
	public void testDiscovery() throws Exception {
		registryService.discovery(RegistryService.class.getName(), "adc".hashCode());
	}

	@Test
	public void testDestroy() throws Exception {
		registryService.destroy();
	}
}
