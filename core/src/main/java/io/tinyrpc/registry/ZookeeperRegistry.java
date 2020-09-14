package io.tinyrpc.registry;

import com.google.common.collect.Maps;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.x.discovery.ServiceCache;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.details.InstanceSerializer;
import org.apache.curator.x.discovery.details.JsonInstanceSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ZookeeperRegistry<T> implements Registry<T> {

    private static final Logger logger = LoggerFactory.getLogger(ZookeeperRegistry.class);

    private Map<String, List<ServiceInstanceListener<T>>> listeners = Maps.newConcurrentMap();

    private InstanceSerializer serializer = new JsonInstanceSerializer<>(ServerInfo.class);
    private ServiceDiscovery<T> serviceDiscovery;
    private ServiceCache<T> serviceCache;

    private String address = "localhost:2181";

    public ZookeeperRegistry() {
    }

    public ZookeeperRegistry(String address) {
        this.address = address;
    }

    public void start() throws Exception {
        String root = "/tinyrpc/rpc";
        // 初始化CuratorFramework
        CuratorFramework client = CuratorFrameworkFactory.newClient(
                address,
                new ExponentialBackoffRetry(1000, 3));
        client.start();  // 启动Curator客户端
        client.blockUntilConnected();  // 阻塞当前线程，等待连接成功

        // 初始化ServiceDiscovery
        serviceDiscovery = ServiceDiscoveryBuilder.builder(ServerInfo.class)
                .client(client)
                .basePath(root)
                .serializer(serializer)
                .build();
        serviceDiscovery.start(); // 启动ServiceDiscovery

        // 创建ServiceCache，监Zookeeper相应节点的变化，也方便后续的读取
        serviceCache = serviceDiscovery.serviceCacheBuilder()
                .name(root)
                .build();
        serviceCache.start(); // 启动ServiceCache
    }

    @Override
    public void registerService(ServiceInstance<T> service) throws Exception {
        serviceDiscovery.registerService(service);
    }

    @Override
    public void unregisterService(ServiceInstance<T> service) throws Exception {
        serviceDiscovery.unregisterService(service);
    }

    @Override
    public List<ServiceInstance<T>> queryForInstances(String name) throws Exception {

        List<ServiceInstance<T>> all = serviceCache.getInstances();
        System.out.println("serviceCache all >>>" + all);

        List<ServiceInstance<T>> serviceInstances = serviceCache.getInstances()
                .stream()
                .filter(s -> s.getName().equalsIgnoreCase(name))
                .collect(Collectors.toList());

        System.out.println("serviceCache serviceInstances >>>" + serviceInstances);

        return serviceDiscovery.queryForInstances(name)
                .stream()
                .filter(s -> s.getName().equalsIgnoreCase(name))
                .collect(Collectors.toList());
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
