package io.tinyrpc.consumer.common;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.tinyrpc.common.constants.RpcConstants;
import io.tinyrpc.common.exception.RpcException;
import io.tinyrpc.common.helper.RpcServiceHelper;
import io.tinyrpc.common.threadpool.ClientThreadPool;
import io.tinyrpc.common.utils.IPUtils;
import io.tinyrpc.common.utils.JsonUtils;
import io.tinyrpc.common.utils.StringUtil;
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

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 服务消费者
 */
public class RpcConsumer implements Consumer {

	private static final Logger logger = LoggerFactory.getLogger(RpcConsumer.class);
	private static volatile RpcConsumer instance;
	private final Bootstrap bootstrap;
	private final EventLoopGroup eventLoopGroup;
	private final String localIp;
	// 当前重试次数
	private final AtomicInteger currentConnectRetryTimes = new AtomicInteger(0);
	// 重试间隔时间
	private int retryInterval;
	// 重试次数
	private int retryTimes;
	private ScheduledExecutorService scheduledExecutorService;
	// 心跳间隔时间，默认30秒
	private int heartbeatInterval = 30000;
	// 扫描并移除空闲连接时间，默认60秒
	private int scanNotActiveChannelInterval = 60000;

	//是否开启直连服务
	private boolean enableDirectServer = false;
	//直连服务的地址
	private String directServerUrl;

	public RpcConsumer() {
		localIp = IPUtils.getLocalHostIP();
		bootstrap = new Bootstrap();
		eventLoopGroup = new NioEventLoopGroup(4);
		bootstrap.group(eventLoopGroup).channel(NioSocketChannel.class)
			.handler(new RpcConsumerInitializer(heartbeatInterval));
		//TODO 启动心跳，后续优化
		this.startHeartbeat();
	}

	private RpcConsumer(int heartbeatInterval, int scanNotActiveChannelInterval, int retryInterval, int retryTimes) {
		if (heartbeatInterval > 0) {
			this.heartbeatInterval = heartbeatInterval;
		}
		if (scanNotActiveChannelInterval > 0) {
			this.scanNotActiveChannelInterval = scanNotActiveChannelInterval;
		}
		this.retryInterval = retryInterval <= 0 ? RpcConstants.DEFAULT_RETRY_INTERVAL : retryInterval;
		this.retryTimes = retryTimes <= 0 ? RpcConstants.DEFAULT_RETRY_TIMES : retryTimes;

		localIp = IPUtils.getLocalHostIP();
		bootstrap = new Bootstrap();
		eventLoopGroup = new NioEventLoopGroup(4);
		bootstrap
			.group(eventLoopGroup)
			.channel(NioSocketChannel.class)
			.handler(new RpcConsumerInitializer(heartbeatInterval));
		//TODO 启动心跳，后续优化
		this.startHeartbeat();
	}

	public static RpcConsumer getInstance() {
		if (instance == null) {
			synchronized (RpcConsumer.class) {
				if (instance == null) {
					instance = new RpcConsumer();
				}
			}
		}
		return instance;
	}

	public static RpcConsumer getInstance(int heartbeatInterval, int scanNotActiveChannelInterval, int retryInterval, int retryTimes) {
		if (instance == null) {
			synchronized (RpcConsumer.class) {
				if (instance == null) {
					instance = new RpcConsumer(heartbeatInterval, scanNotActiveChannelInterval, retryInterval, retryTimes);
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

		// 方式一
//		ServiceMeta serviceMeta = this.getServiceMetaWithRetry(registryService, serviceKey, invokerHashCode);
//		logger.info(">>> serviceKey={}, serviceMeta={}", serviceKey, JsonUtils.toJSONString(serviceMeta));
//
//		RpcConsumerHandler handler = null;
//		if (serviceMeta != null) {
//			handler = getRpcConsumerHandlerWithRetry(serviceMeta);
//		}
//		RPCFuture rpcFuture = null;
//		if (handler != null) {
//			rpcFuture = handler.sendRequest(protocol, request.getAsync(), request.getOneway());
//		}

		// 方式二
		RpcConsumerHandler handler = getRpcConsumerHandlerWithRetry(registryService, serviceKey, invokerHashCode);
		RPCFuture rpcFuture = null;
		if (handler != null) {
			rpcFuture = handler.sendRequest(protocol, request.getAsync(), request.getOneway());
		}

		return rpcFuture;
	}

	/**
	 * 重试获取服务提供者元数据
	 */
	private ServiceMeta getServiceMetaWithRetry(RegistryService registryService, String serviceKey, int invokerHashCode) throws Exception {
		// 首次获取服务元数据信息，如果获取到，则直接返回，否则进行重试
		logger.info("retrieve service provider meta data");
		ServiceMeta serviceMeta = registryService.discovery(serviceKey, invokerHashCode, localIp);
		if (serviceMeta == null) {
			for (int i = 1; i <= retryTimes; i++) {
				logger.info("retrieve meta data. 【{}】 retry ...", i);
				serviceMeta = registryService.discovery(serviceKey, invokerHashCode, localIp);
				if (serviceMeta != null) {
					break;
				}
				Thread.sleep(retryInterval);
			}
		}
		return serviceMeta;
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
					//连接成功，将当前连接重试次数设置为0
					currentConnectRetryTimes.set(0);
				} else {
					logger.error("connect rpc server {} on port {} failed.", serviceAddress, port);
					channelFuture.cause().printStackTrace();
					eventLoopGroup.shutdownGracefully();
				}
			}
		});
		return channelFuture.channel().pipeline().get(RpcConsumerHandler.class);
	}

	/**
	 * 获取RpcConsumerHandler
	 */
	private RpcConsumerHandler getRpcConsumerHandlerWithRetry(ServiceMeta serviceMeta) throws InterruptedException {
		logger.info("Consumer connecting server provider...");
		RpcConsumerHandler handler = null;
		try {
			handler = this.getRpcConsumerHandlerWithCache(serviceMeta);
		} catch (Exception e) {
			// 连接异常
			if (e instanceof ConnectException) {
				// 启动重试机制
				if (handler == null) {
					if (currentConnectRetryTimes.get() < retryTimes) {
						handler = this.getRpcConsumerHandlerWithRetry(serviceMeta);
						logger.info("connect server provider 【{}】 retry...", currentConnectRetryTimes.incrementAndGet());
						Thread.sleep(retryInterval);
					}
				}
			}
		}
		return handler;
	}

	/**
	 * 从缓存中获取RpcConsumerHandler，缓存中没有，再创建
	 */
	private RpcConsumerHandler getRpcConsumerHandlerWithCache(ServiceMeta serviceMeta) throws InterruptedException {
		RpcConsumerHandler handler = RpcConsumerHandlerHelper.get(serviceMeta);
		if (handler == null) { // 缓存中无RpcClientHandler
			handler = getRpcConsumerHandler(serviceMeta);
			RpcConsumerHandlerHelper.put(serviceMeta, handler);
		} else if (!handler.getChannel().isActive()) {  // 缓存中存在RpcClientHandler，但不活跃
			handler.close();
			handler = getRpcConsumerHandler(serviceMeta);
			RpcConsumerHandlerHelper.put(serviceMeta, handler);
		}
		return handler;
	}

	/**
	 * 基于重试获取发送消息的handler
	 */
	private RpcConsumerHandler getRpcConsumerHandlerWithRetry(RegistryService registryService, String serviceKey, int invokerHashCode) throws Exception {
		RpcConsumerHandler handler = getRpcConsumerHandler(registryService, serviceKey, invokerHashCode);
		//获取的handler为空，启动重试机制
		if (handler == null) {
			for (int i = 1; i <= retryTimes; i++) {
				logger.info("connect server provider 【{}】 retry...", i);
				handler = getRpcConsumerHandler(registryService, serviceKey, invokerHashCode);
				if (handler != null) {
					break;
				}
				Thread.sleep(retryInterval);
			}
		}
		return handler;
	}

	/**
	 * 获取发送消息的handler
	 */
	private RpcConsumerHandler getRpcConsumerHandler(RegistryService registryService, String serviceKey, int invokerHashCode) throws Exception {
		ServiceMeta serviceMeta = this.getDirectServiceMetaOrWithRetry(registryService, serviceKey, invokerHashCode);
		RpcConsumerHandler handler = null;
		if (serviceMeta != null) {
			logger.info(">>> serviceKey={}, serviceMeta={}", serviceKey, JsonUtils.toJSONString(serviceMeta));
			handler = getRpcConsumerHandlerWithRetry(serviceMeta);
		}
		return handler;
	}

	/**
	 * 直连服务提供者或者结合重试获取服务元数据信息
	 */
	private ServiceMeta getDirectServiceMetaOrWithRetry(RegistryService registryService, String serviceKey, int invokerHashCode) throws Exception {
		ServiceMeta serviceMeta = null;
		if (enableDirectServer) {
//			serviceMeta = this.getDirectServiceMeta();
			serviceMeta = this.getServiceMeta(directServerUrl, registryService, invokerHashCode);
			logger.info("--- direct connect [{}:{}] from [{}] ---", serviceMeta.getServiceAddr(), serviceMeta.getServicePort(), directServerUrl);
		} else {
			serviceMeta = this.getServiceMetaWithRetry(registryService, serviceKey, invokerHashCode);
		}
		return serviceMeta;
	}

	/**
	 * 服务消费者直连服务提供者
	 */
	private ServiceMeta getDirectServiceMeta() {
		if (StringUtil.isEmpty(directServerUrl)) {
			throw new RpcException("direct server url is null ...");
		}
		if (!directServerUrl.contains(RpcConstants.IP_PORT_SPLIT)) {
			throw new RpcException("direct server url not contains : ");
		}
		logger.info("--- direct connect {} ---", directServerUrl);
		ServiceMeta serviceMeta = new ServiceMeta();
		String[] directServerUrlArray = directServerUrl.split(RpcConstants.IP_PORT_SPLIT);
		serviceMeta.setServiceAddr(directServerUrlArray[0]);
		serviceMeta.setServicePort(Integer.parseInt(directServerUrlArray[1]));
		return serviceMeta;
	}

	/**
	 * 直连服务提供者
	 */
	private ServiceMeta getServiceMeta(String directServerUrl, RegistryService registryService, int invokerHashCode) {
		//只配置了一个服务提供者地址
		if (!directServerUrl.contains(RpcConstants.RPC_MULTI_DIRECT_SERVERS_SEPARATOR)) {
			return getDirectServiceMetaWithCheck(directServerUrl);
		}
		//配置了多个服务提供者地址
		return registryService.select(this.getMultiServiceMeta(directServerUrl), invokerHashCode, localIp);
	}

	/**
	 * 获取多个服务提供者元数据
	 */
	private List<ServiceMeta> getMultiServiceMeta(String directServerUrl) {
		List<ServiceMeta> serviceMetaList = new ArrayList<>();
		String[] directServerUrlArray = directServerUrl.split(RpcConstants.RPC_MULTI_DIRECT_SERVERS_SEPARATOR);
		if (directServerUrlArray.length > 0) {
			for (String directUrl : directServerUrlArray) {
				serviceMetaList.add(getDirectServiceMeta(directUrl));
			}
		}
		return serviceMetaList;
	}

	/**
	 * 服务消费者直连服务提供者
	 */
	private ServiceMeta getDirectServiceMetaWithCheck(String directServerUrl) {
		if (StringUtil.isEmpty(directServerUrl)) {
			throw new RpcException("direct server url is null ...");
		}
		if (!directServerUrl.contains(RpcConstants.IP_PORT_SPLIT)) {
			throw new RpcException("direct server url not contains [:] ");
		}
		return this.getDirectServiceMeta(directServerUrl);
	}

	/**
	 * 获取直连服务提供者元数据
	 */
	private ServiceMeta getDirectServiceMeta(String directServerUrl) {
		ServiceMeta serviceMeta = new ServiceMeta();
		String[] directServerUrlArray = directServerUrl.split(RpcConstants.IP_PORT_SPLIT);
		serviceMeta.setServiceAddr(directServerUrlArray[0].trim());
		serviceMeta.setServicePort(Integer.parseInt(directServerUrlArray[1].trim()));
		return serviceMeta;
	}

	public RpcConsumer setEnableDirectServer(boolean enableDirectServer) {
		this.enableDirectServer = enableDirectServer;
		return this;
	}

	public RpcConsumer setDirectServerUrl(String directServerUrl) {
		this.directServerUrl = directServerUrl;
		return this;
	}

	public RpcConsumer setHeartbeatInterval(int heartbeatInterval) {
		if (heartbeatInterval > 0) {
			this.heartbeatInterval = heartbeatInterval;
		}
		return this;
	}

	public RpcConsumer setScanNotActiveChannelInterval(int scanNotActiveChannelInterval) {
		if (scanNotActiveChannelInterval > 0) {
			this.scanNotActiveChannelInterval = scanNotActiveChannelInterval;
		}
		return this;
	}

	public RpcConsumer setRetryInterval(int retryInterval) {
		this.retryInterval = retryInterval <= 0 ? RpcConstants.DEFAULT_RETRY_INTERVAL : retryInterval;
		return this;
	}

	public RpcConsumer setRetryTimes(int retryTimes) {
		this.retryTimes = retryTimes <= 0 ? RpcConstants.DEFAULT_RETRY_TIMES : retryTimes;
		return this;
	}
}
