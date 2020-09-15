package com.tinyrpc.test;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.x.discovery.ServiceCache;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.details.InstanceSerializer;
import org.apache.curator.x.discovery.details.JsonInstanceSerializer;
import org.apache.zookeeper.KeeperException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.List;

public class CuratorTests {

    private static CuratorFramework client;
    private InstanceSerializer<ServerPayload> serializer = new JsonInstanceSerializer<>(ServerPayload.class);

    private String path = "/test";

    @BeforeAll
    public static void buildClient() throws Exception {
        client = CuratorFrameworkFactory.newClient("www.qq-server.com:2181",
                new ExponentialBackoffRetry(1000, 3));
        client.start();
        client.blockUntilConnected();
    }

    @Test
    public void testList() throws Exception {
        client.checkExists().forPath(path);
    }

    @Test
    public void testDel() {
        try {
            client.delete().deletingChildrenIfNeeded().forPath(path);
        } catch (KeeperException.NoNodeException e) {
            System.out.println(path + "　deleted");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testCache() throws Exception {
        ServiceDiscovery<ServerPayload> discovery = ServiceDiscoveryBuilder.builder(ServerPayload.class)
                .client(client)
                .basePath(path)
                .watchInstances(true)
                .serializer(serializer)
                .build();

        discovery.start();

        ServiceCache<ServerPayload> cache = discovery.serviceCacheBuilder()
                .name(path)
                .build();

        cache.start();

        List<ServiceInstance<ServerPayload>> cacheInstances;
        for (int i = 0; i < 10; i++) {
            cacheInstances = cache.getInstances();
            System.out.println(cacheInstances);
        }
    }

    @Test
    public void testRegister() throws Exception {
        ServerPayload payload1 = new ServerPayload("127.0.0.1", 8888);
        payload1.setClassName(this.getClass().getSimpleName());
        payload1.setMethodName("test");
        ServiceInstance<ServerPayload> instance1 = ServiceInstance.<ServerPayload>builder()
                .id("service-1")
                .name("service-1")
                .registrationTimeUTC(System.currentTimeMillis())
                .payload(payload1)
                .build();

        ServiceInstance<ServerPayload> instance2 = ServiceInstance.<ServerPayload>builder()
                .id("service-2")
                .name("service-2")
                .payload(new ServerPayload("127.0.0.1", 8880))
                .build();

        ServiceDiscovery<ServerPayload> discovery = ServiceDiscoveryBuilder.builder(ServerPayload.class)
                .client(client)
                .basePath(path)
                .watchInstances(true)
                .serializer(serializer)
                .build();

        discovery.start();

        ServiceCache<ServerPayload> cache = discovery.serviceCacheBuilder()
                .name(path)
//                .executorService(Executors.newCachedThreadPool())
                .build();

        cache.start();

        discovery.registerService(instance1);
        discovery.registerService(instance2);

        Collection<String> instanceNames = discovery.queryForNames();
        System.out.println(instanceNames);

        System.in.read();
    }

}
