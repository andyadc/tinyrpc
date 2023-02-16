package io.tinyrpc.test.consumer.handler;

import io.tinyrpc.consumer.common.RpcConsumer;
import io.tinyrpc.protocol.RpcProtocol;
import io.tinyrpc.protocol.header.RpcHeaderFactory;
import io.tinyrpc.protocol.request.RpcRequest;

public class RpcConsumerHandlerTest {

	public static void main(String[] args) throws Exception {
		RpcConsumer consumer = RpcConsumer.getInstance();
		consumer.sendRequest(getRpcRequestProtocol());

		Thread.sleep(2000);
		consumer.close();
	}

	private static RpcProtocol<RpcRequest> getRpcRequestProtocol() {
		//模拟发送数据
		RpcProtocol<RpcRequest> protocol = new RpcProtocol<RpcRequest>();
		protocol.setHeader(RpcHeaderFactory.getRequestHeader("jdk"));

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
}
