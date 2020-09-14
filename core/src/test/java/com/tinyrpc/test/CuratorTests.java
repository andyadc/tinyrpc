package com.tinyrpc.test;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.x.discovery.ServiceCache;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.details.JsonInstanceSerializer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class CuratorTests {

    private static CuratorFramework client;

    @BeforeAll
    public static void buildClient() throws Exception {
        client = CuratorFrameworkFactory.newClient("www.qq-server.com:2181",
                new ExponentialBackoffRetry(1000, 3));
        client.start();
        client.blockUntilConnected();
    }

    @Test
    public void testClient() throws Exception {
        String path = "/test";
        client.delete().deletingChildrenIfNeeded().forPath(path);

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
                .serializer(new JsonInstanceSerializer<>(ServerPayload.class))
                .watchInstances(true)
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

        TimeUnit.SECONDS.sleep(10L);

        List<ServiceInstance<ServerPayload>> cacheInstances = cache.getInstances();
        System.out.println(cacheInstances);

        System.in.read();
    }

    class ServerPayload {
        private String host;
        private int port;

        private String className;
        private String methodName;

        public ServerPayload() {
        }

        public ServerPayload(String host, int port) {
            this.host = host;
            this.port = port;
        }

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }

        public String getClassName() {
            return className;
        }

        public void setClassName(String className) {
            this.className = className;
        }

        public String getMethodName() {
            return methodName;
        }

        public void setMethodName(String methodName) {
            this.methodName = methodName;
        }

        @Override
        public String toString() {
            return "ServerPayload{" +
                    "host='" + host + '\'' +
                    ", port=" + port +
                    ", className='" + className + '\'' +
                    ", methodName='" + methodName + '\'' +
                    '}';
        }
    }
}
