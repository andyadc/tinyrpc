package com.tinyrpc.test;

import io.tinyrpc.proxy.RpcProxy;
import io.tinyrpc.registry.ServerInfo;
import io.tinyrpc.registry.ZookeeperRegistry;

public class Consumer {

    public static void main(String[] args) throws Exception {
        // 创建ZookeeperRegistr对象
        ZookeeperRegistry<ServerInfo> registry = new ZookeeperRegistry<>();
        registry.start();

        // 创建代理对象，通过代理调用远端Server
        HelloService helloService = RpcProxy.newInstance(HelloService.class, registry);
        String message = helloService.hello("World");

        System.out.println(message);
    }
}
