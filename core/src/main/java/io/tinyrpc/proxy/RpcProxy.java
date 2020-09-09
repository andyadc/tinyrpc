package io.tinyrpc.proxy;

import io.netty.channel.ChannelFuture;
import io.tinyrpc.model.Header;
import io.tinyrpc.model.Message;
import io.tinyrpc.model.Request;
import io.tinyrpc.model.Response;
import io.tinyrpc.registry.Registry;
import io.tinyrpc.registry.ServerInfo;
import io.tinyrpc.transport.Connection;
import io.tinyrpc.transport.NettyResponseFuture;
import io.tinyrpc.transport.RpcClient;
import io.tinyrpc.util.ClassLoaderUtil;
import org.apache.curator.x.discovery.ServiceInstance;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import static io.tinyrpc.Constants.*;

public class RpcProxy implements InvocationHandler {

    public Map<Method, Header> headerCache = new ConcurrentHashMap<>();
    private String serviceName; // 需要代理的服务(接口)名称
    // 用于与Zookeeper交互，其中自带缓存
    private Registry<ServerInfo> registry;

    public RpcProxy(String serviceName, Registry<ServerInfo> registry) throws Exception {
        this.serviceName = serviceName;
        this.registry = registry;
    }

    public static <T> T newInstance(Class<T> clazz, Registry<ServerInfo> registry) throws Exception {
        // 创建代理对象
        return (T) Proxy.newProxyInstance(ClassLoaderUtil.getCurrentClassLoader(),
                new Class[]{clazz},
                new RpcProxy("demoService", registry));
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 从Zookeeper缓存中获取可用的Server地址,并随机从中选择一个
        List<ServiceInstance<ServerInfo>> serviceInstances = registry.queryForInstances(serviceName);
        ServiceInstance<ServerInfo> serviceInstance = serviceInstances.get(ThreadLocalRandom.current().nextInt(serviceInstances.size()));

        // 创建请求消息，然后调用remoteCall()方法请求上面选定的Server端
        String methodName = method.getName();
        Header header = headerCache.computeIfAbsent(method, h -> new Header(MAGIC, VERSION_1));
        Message<Request> message = new Message<>(header, new Request(serviceName, methodName, args));
        return remoteCall(serviceInstance.getPayload(), message);
    }

    private Object remoteCall(ServerInfo serverInfo, Message<Request> message) throws Exception {
        if (serverInfo == null) {
            throw new RuntimeException("get available server error");
        }
        Object result;
        try {
            // 创建RpcClient连接指定的Server端
            RpcClient rpcClient = new RpcClient(serverInfo.getHost(), serverInfo.getPort());
            ChannelFuture channelFuture = rpcClient.connect().awaitUninterruptibly();

            // 创建对应的Connection对象，并发送请求
            Connection connection = new Connection(channelFuture, true);
            NettyResponseFuture<Response> responseFuture = connection.request(message, CONNECTION_TIMEOUT);
            // 等待请求对应的响应
            result = responseFuture.getPromise().get(DEFAULT_TIMEOUT, TimeUnit.MICROSECONDS);
        } catch (Exception e) {
            throw e;
        }
        return result;
    }
}
