package com.tinyrpc.test;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

public class CuratorTests {

    public void testClient() {
        CuratorFramework client = CuratorFrameworkFactory.newClient("www.qq-server.com:2181",
                new ExponentialBackoffRetry(1000, 3));
        client.start();


    }
}
