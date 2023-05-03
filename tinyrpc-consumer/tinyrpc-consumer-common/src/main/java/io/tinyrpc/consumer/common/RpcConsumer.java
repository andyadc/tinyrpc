package io.tinyrpc.consumer.common;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.tinyrpc.common.helper.RpcServiceHelper;
import io.tinyrpc.common.threadpool.ClientThreadPool;
import io.tinyrpc.common.utils.IPUtils;
import io.tinyrpc.common.utils.JsonUtils;
import io.tinyrpc.consumer.common.handler.RpcConsumerHandler;
import io.tinyrpc.consumer.common.helper.RpcConsumerHandlerHelper;
import io.tinyrpc.consumer.common.initializer.RpcConsumerInitializer;
import io.tinyrpc.consumer.common.manager.ConsumerConnectionManager;
import io.tinyrpc.loadbalancer.context.ConnectionsContext;
import io.tinyrpc.protocol.RpcProtocol;
import io.tinyrpc.protocol.meta.ServiceMeta;
import io.tinyrpc.protocol.request.RpcRequest;
import io.tinyrpc.proxy.api.consumer.Consumer;
import io.tinyrpc.proxy.api.future.RPCFuture;
import io.tinyrpc.registry.api.RegistryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 服务消费者
 */
public class RpcConsumer implements Consumer {

	private static final Logger logger = LoggerFactory.getLogger(RpcConsumer.class);
	private static volatile RpcConsumer instance;
	private final Bootstrap bootstrap;
	private final EventLoopGroup eventLoopGroup;
	private final String localIp;

	private ScheduledExecutorService scheduledExecutorService;

	//心跳间隔时间，默认30秒
	private int heartbeatInterval = 30000;
	//扫描并移除空闲连接时间，默认60秒
	private int scanNotActiveChannelInterval = 60000;

	private RpcConsumer(int heartbeatInterval, int scanNotActiveChannelInterval) {
		if (heartbeatInterval > 0) {
			this.heartbeatInterval = heartbeatInterval;
		}
		if (scanNotActiveChannelInterval > 0) {
			this.scanNotActiveChannelInterval = scanNotActiveChannelInterval;
		}

		localIp = IPUtils.getLocalHostIP();
		bootstrap = new Bootstrap();
		eventLoopGroup = new NioEventLoopGroup(4);
		bootstrap
			.group(eventLoopGroup)
			.channel(NioSocketChannel.class)
			.handler(new RpcConsumerInitializer());
		//TODO 启动心跳，后续优化
		this.startHeartbeat();
	}

	public static RpcConsumer getInstance(int heartbeatInterval, int scanNotActiveChannelInterval) {
		if (instance == null) {
			synchronized (RpcConsumer.class) {
				if (instance == null) {
					instance = new RpcConsumer(heartbeatInterval, scanNotActiveChannelInterval);
				}
			}
		}
		return instance;
	}

	private void startHeartbeat() {
		scheduledExecutorService = Executors.newScheduledThreadPool(2);
		// 扫描并处理所有不活跃的连接
		scheduledExecutorService.scheduleAtFixedRate(() -> {
			logger.info("=== scan Not active Channel ===");
			ConsumerConnectionManager.scanNotActiveChannel();
		}, 10 * 1000, scanNotActiveChannelInterval, TimeUnit.MILLISECONDS);

		scheduledExecutorService.scheduleAtFixedRate(() -> {
			logger.info("=== broadcast Ping message from consumer ===");
			ConsumerConnectionManager.broadcastPingMessageFromConsumer();
		}, 3 * 1000, heartbeatInterval, TimeUnit.MILLISECONDS);
	}

	public void close() {
		RpcConsumerHandlerHelper.closeRpcClientHandler();
		eventLoopGroup.shutdownGracefully();
		ClientThreadPool.shutdown();
	}

	@Override
	public RPCFuture sendRequest(RpcProtocol<RpcRequest> protocol, RegistryService registryService) throws Exception {
		RpcRequest request = protocol.getBody();
		String serviceKey = RpcServiceHelper.buildServiceKey(request.getClassName(), request.getVersion(), request.getGroup());

		Object[] params = request.getParameters();
		int invokerHashCode = (params == null || params.length <= 0) ? serviceKey.hashCode() : params[0].hashCode();

		ServiceMeta serviceMeta = registryService.discovery(serviceKey, invokerHashCode, localIp);
		logger.info(">>> serviceKey={}, serviceMeta={}", serviceKey, JsonUtils.toJSONString(serviceMeta));
		if (serviceMeta != null) {
			RpcConsumerHandler handler = RpcConsumerHandlerHelper.get(serviceMeta);
			//缓存中无RpcClientHandler
			if (handler == null) {
				handler = getRpcConsumerHandler(serviceMeta);
				RpcConsumerHandlerHelper.put(serviceMeta, handler);
			} else if (!handler.getChannel().isActive()) {  // 缓存中存在RpcClientHandler，但不活跃
				handler.close();
				handler = getRpcConsumerHandler(serviceMeta);
				RpcConsumerHandlerHelper.put(serviceMeta, handler);
			}
			return handler.sendRequest(protocol, request.getAsync(), request.getOneway());
		}
		return null;
	}

	/**
	 * 创建连接并返回RpcClientHandler
	 */
	private RpcConsumerHandler getRpcConsumerHandler(ServiceMeta serviceMeta) throws InterruptedException {
		String serviceAddress = serviceMeta.getServiceAddr();
		int port = serviceMeta.getServicePort();
		ChannelFuture channelFuture = bootstrap.connect(serviceAddress, port).sync();
		channelFuture.addListener(new ChannelFutureListener() {
			@Override
			public void operationComplete(ChannelFuture channelFuture) throws Exception {
				if (channelFuture.isSuccess()) {
					logger.info("connect rpc server {} on port {} success.", serviceAddress, port);
					// 添加连接信息，在服务消费者端记录每个服务提供者实例的连接次数
					ConnectionsContext.add(serviceMeta);
				} else {
					logger.error("connect rpc server {} on port {} failed.", serviceAddress, port);
					channelFuture.cause().printStackTrace();
					eventLoopGroup.shutdownGracefully();
				}
			}
		});
		return channelFuture.channel().pipeline().get(RpcConsumerHandler.class);
	}
}
