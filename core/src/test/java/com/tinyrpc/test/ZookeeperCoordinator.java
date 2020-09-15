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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ZookeeperCoordinator {

    // 这里的JsonInstanceSerializer是将ServerInfo序列化成Json
    private static final InstanceSerializer serializer = new JsonInstanceSerializer<>(ServerInfo.class);
    private ServiceDiscovery<ServerInfo> serviceDiscovery;
    private ServiceCache<ServerInfo> serviceCache;
    private String root;

    public ZookeeperCoordinator() throws Exception {
        this.root = "/test";
        // 创建Curator客户端
        CuratorFramework client = CuratorFrameworkFactory.newClient("www.qq-server.com:2181",
                new ExponentialBackoffRetry(1000, 3));
        client.start(); // 启动Curator客户端
        client.blockUntilConnected();  // 阻塞当前线程，等待连接成功

        // 创建ServiceDiscovery
        serviceDiscovery = ServiceDiscoveryBuilder
                .builder(ServerInfo.class)
                .client(client) // 依赖Curator客户端
                .basePath(root) // 管理的Zk路径
                .watchInstances(true) // 当ServiceInstance加载
                .serializer(serializer)
                .build();
        serviceDiscovery.start(); // 启动ServiceDiscovery

        // 创建ServiceCache，监Zookeeper相应节点的变化，也方便后续的读取
        serviceCache = serviceDiscovery.serviceCacheBuilder()
                .name(root)
                .build();
        serviceCache.start(); // 启动ServiceCache
    }

    public static void main(String[] args) throws Exception {
        ZookeeperCoordinator coordinator = new ZookeeperCoordinator();
        ServerInfo serverInfo = new ServerInfo();
        serverInfo.setHost("127.0.0.1");
        serverInfo.setPort(10101);
        coordinator.registerRemote(serverInfo);
        List<ServerInfo> serverInfoList = coordinator.queryRemoteNodes();
        System.out.println(serverInfoList);
    }

    public void registerRemote(ServerInfo serverInfo) throws Exception {
        // 将ServerInfo对象转换成ServiceInstance对象
        ServiceInstance<ServerInfo> thisInstance =
                ServiceInstance.<ServerInfo>builder()
                        .name(root)
                        .id(UUID.randomUUID().toString()) // 随机生成的UUID
                        .address(serverInfo.getHost()) // host
                        .port(serverInfo.getPort()) // port
                        .payload(serverInfo) // payload
                        .build();
        // 将ServiceInstance写入到Zookeeper中
        serviceDiscovery.registerService(thisInstance);
    }

    public List<ServerInfo> queryRemoteNodes() {
        List<ServerInfo> ServerInfoDetails = new ArrayList<>();
        // 查询 ServiceCache 获取全部的 ServiceInstance 对象
        List<ServiceInstance<ServerInfo>> serviceInstances = serviceCache.getInstances();
        serviceInstances.forEach(serviceInstance -> {
            ServerInfo instance = serviceInstance.getPayload();
            ServerInfoDetails.add(instance);
        });
        return ServerInfoDetails;
    }
}
