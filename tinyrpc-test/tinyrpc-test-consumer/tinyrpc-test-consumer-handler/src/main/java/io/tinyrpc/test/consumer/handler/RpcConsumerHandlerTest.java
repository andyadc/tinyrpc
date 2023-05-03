package io.tinyrpc.test.consumer.handler;

import io.tinyrpc.common.exception.RegistryException;
import io.tinyrpc.consumer.common.RpcConsumer;
import io.tinyrpc.consumer.common.context.RpcContext;
import io.tinyrpc.protocol.RpcProtocol;
import io.tinyrpc.protocol.enumeration.RpcType;
import io.tinyrpc.protocol.header.RpcHeaderFactory;
import io.tinyrpc.protocol.request.RpcRequest;
import io.tinyrpc.proxy.api.callback.AsyncRPCCallback;
import io.tinyrpc.proxy.api.future.RPCFuture;
import io.tinyrpc.registry.api.RegistryService;
import io.tinyrpc.registry.api.config.RegistryConfig;
import io.tinyrpc.registry.zookeeper.ZookeeperRegistryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RpcConsumerHandlerTest {

	private static final Logger logger = LoggerFactory.getLogger(RpcConsumerHandlerTest.class);

	public static void main(String[] args) throws Exception {
		RpcConsumer consumer = RpcConsumer.getInstance(30000, 60000, 1000, 3);

		RPCFuture future = consumer.sendRequest(getRpcRequestProtocol(), getRegistryService("127.0.0.1:2181", "zookeeper", "random"));
		future.addCallback(new AsyncRPCCallback() {

			@Override
			public void onSuccess(Object result) {
				logger.info("从服务消费者获取到的数据 ===>>> " + result);
			}

			@Override
			public void onException(Exception e) {
				logger.info("抛出了异常 ===>>> " + e);
			}
		});

		Thread.sleep(2000);
		consumer.close();
	}

	public static void mainAsync(String[] args) throws Exception {
		RpcConsumer consumer = RpcConsumer.getInstance(30000, 60000, 1000, 3);

		consumer.sendRequest(getRpcRequestProtocolAsync(), getRegistryService("127.0.0.1:2181", "zookeeper", "random"));
		RPCFuture future = RpcContext.getContext().getRPCFuture();
		logger.info("从服务消费者获取到的数据 ===>>> " + future.get());

		consumer.close();
	}

	public static void mainOneway(String[] args) throws Exception {
		RpcConsumer consumer = RpcConsumer.getInstance(30000, 60000, 1000, 3);

		consumer.sendRequest(getRpcRequestProtocolOneway(), getRegistryService("127.0.0.1:2181", "zookeeper", "random"));
		logger.info("无需获取返回的结果数据");

		consumer.close();
	}

	private static RegistryService getRegistryService(String registryAddress, String registryType, String registryLoadBalanceType) {
		if (registryType == null) {
			throw new IllegalArgumentException("registry type is null");
		}
		//TODO 后续SPI扩展
		RegistryService registryService = new ZookeeperRegistryService();
		try {
			registryService.init(new RegistryConfig(registryAddress, registryType, registryLoadBalanceType));
		} catch (Exception e) {
			logger.error("RpcClient init registry service throws exception", e);
			throw new RegistryException(e.getMessage(), e);
		}
		return registryService;
	}

	private static RpcProtocol<RpcRequest> getRpcRequestProtocol() {
		//模拟发送数据
		RpcProtocol<RpcRequest> protocol = new RpcProtocol<>();
		protocol.setHeader(RpcHeaderFactory.getRequestHeader("jdk", RpcType.REQUEST.getType()));

		RpcRequest request = new RpcRequest();
		request.setClassName("io.tinyrpc.test.api.TestService");
		request.setGroup("g-1");
		request.setVersion("1.0.0");
		request.setMethodName("hello");
		request.setParameterTypes(new Class[]{String.class});
		request.setParameters(new Object[]{"adc"});
		request.setAsync(false);
		request.setOneway(false);

		protocol.setBody(request);
		return protocol;
	}

	private static RpcProtocol<RpcRequest> getRpcRequestProtocolAsync() {
		//模拟发送数据
		RpcProtocol<RpcRequest> protocol = new RpcProtocol<>();
		protocol.setHeader(RpcHeaderFactory.getRequestHeader("jdk", RpcType.REQUEST.getType()));

		RpcRequest request = new RpcRequest();
		request.setClassName("io.tinyrpc.test.api.TestService");
		request.setGroup("g-1");
		request.setVersion("1.0.0");
		request.setMethodName("hello");
		request.setParameterTypes(new Class[]{String.class});
		request.setParameters(new Object[]{"adc"});
		request.setAsync(true);
		request.setOneway(false);

		protocol.setBody(request);
		return protocol;
	}

	private static RpcProtocol<RpcRequest> getRpcRequestProtocolOneway() {
		//模拟发送数据
		RpcProtocol<RpcRequest> protocol = new RpcProtocol<>();
		protocol.setHeader(RpcHeaderFactory.getRequestHeader("jdk", RpcType.REQUEST.getType()));

		RpcRequest request = new RpcRequest();
		request.setClassName("io.tinyrpc.test.api.TestService");
		request.setGroup("g-1");
		request.setVersion("1.0.0");
		request.setMethodName("hello");
		request.setParameterTypes(new Class[]{String.class});
		request.setParameters(new Object[]{"adc"});
		request.setAsync(false);
		request.setOneway(true);

		protocol.setBody(request);
		return protocol;
	}
}
