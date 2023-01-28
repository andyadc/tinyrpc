package io.tinyrpc.test.consumer.codec.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.tinyrpc.common.utils.JsonUtils;
import io.tinyrpc.protocol.RpcProtocol;
import io.tinyrpc.protocol.header.RpcHeaderFactory;
import io.tinyrpc.protocol.request.RpcRequest;
import io.tinyrpc.protocol.response.RpcResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RpcTestConsumerHandler extends SimpleChannelInboundHandler<RpcProtocol<RpcResponse>> {

	private static final Logger logger = LoggerFactory.getLogger(RpcTestConsumerHandler.class);

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		logger.info("发送数据开始...");
		//模拟发送数据
		RpcProtocol<RpcRequest> protocol = new RpcProtocol<>();
		protocol.setHeader(RpcHeaderFactory.getRequestHeader("jdk"));
		RpcRequest request = new RpcRequest();

		request.setClassName("io.tinyrpc.test.api.TestService");
		request.setGroup("adc");
		request.setMethodName("hello");
		request.setParameters(new Object[]{"andyadc"});
		request.setParameterTypes(new Class[]{String.class});
		request.setVersion("1.0.0");
		request.setAsync(false);
		request.setOneway(false);
		protocol.setBody(request);
		logger.info("服务消费者发送的数据===>>>{}", JsonUtils.toJSONString(protocol));
		ctx.writeAndFlush(protocol);
		logger.info("发送数据完毕...");
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, RpcProtocol<RpcResponse> protocol) throws Exception {
		logger.info("服务消费者接收到的数据===>>>{}", JsonUtils.toJSONString(protocol));
	}
}
