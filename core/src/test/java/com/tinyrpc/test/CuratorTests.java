package com.tinyrpc.test;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.x.discovery.ServiceCache;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.details.JsonInstanceSerializer;
import org.junit.jupiter.api.Test;

import java.util.Collection;

public class CuratorTests {

    @Test
    public void testClient() throws Exception {
        CuratorFramework client = CuratorFrameworkFactory.newClient("www.qq-server.com:2181",
                new ExponentialBackoffRetry(1000, 3));
        client.start();

        ServiceInstance<ServerPayload> instance1 = ServiceInstance.<ServerPayload>builder()
                .id("serviceInstance-1")
                .name("serviceInstance-1")
                .build();

        ServiceInstance<ServerPayload> instance2 = ServiceInstance.<ServerPayload>builder()
                .id("serviceInstance-2")
                .name("serviceInstance-2")
                .build();

        ServiceDiscovery<ServerPayload> discovery = ServiceDiscoveryBuilder.builder(ServerPayload.class)
                .client(client)
                .basePath("/test")
                .serializer(new JsonInstanceSerializer<>(ServerPayload.class))
                .build();

//        discovery.registerService(instance1);
//        discovery.registerService(instance2);

//        TimeUnit.SECONDS.sleep(3L);

        ServiceCache<ServerPayload> cache = discovery.serviceCacheBuilder()
                .name("/services")
                .build();

        client.blockUntilConnected();
        discovery.start();
        cache.start();

        discovery.registerService(instance1);
        discovery.registerService(instance2);

        Collection<String> instanceNames = discovery.queryForNames();
        System.out.println(instanceNames);
    }

    class ServerPayload {

    }
}
