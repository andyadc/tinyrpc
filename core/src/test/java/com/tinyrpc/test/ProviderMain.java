package com.tinyrpc.test;

import io.tinyrpc.registry.ServerInfo;
import io.tinyrpc.registry.ZookeeperRegistry;
import io.tinyrpc.service.BeanManager;
import io.tinyrpc.transport.RpcServer;
import org.apache.curator.x.discovery.ServiceInstance;

public class ProviderMain {

    public static void main(String[] args) throws Exception {
        BeanManager.registerBean("helloService", new HelloServiceImpl());

        ZookeeperRegistry<ServerInfo> registry = new ZookeeperRegistry<>();
        registry.setAddress("www.qq-server.com:2181");
        registry.start();

        ServerInfo serverInfo = new ServerInfo("127.0.0.1", 20880);
        ServiceInstance<ServerInfo> serviceInstance = ServiceInstance.<ServerInfo>builder().name("HelloService").payload(serverInfo).build();
        registry.registerService(serviceInstance);

        RpcServer server = new RpcServer(20880);
        server.start();

        System.out.println(registry.queryForInstances("HelloService"));
        System.in.read();
    }
}
