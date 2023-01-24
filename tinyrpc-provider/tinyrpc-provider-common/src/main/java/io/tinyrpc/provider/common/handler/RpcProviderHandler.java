package io.tinyrpc.provider.common.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.tinyrpc.protocol.RpcProtocol;
import io.tinyrpc.protocol.enumeration.RpcType;
import io.tinyrpc.protocol.header.RpcHeader;
import io.tinyrpc.protocol.request.RpcRequest;
import io.tinyrpc.protocol.response.RpcResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * RPC服务提供者的 Handler处理类
 */
public class RpcProviderHandler extends SimpleChannelInboundHandler<RpcProtocol<RpcRequest>> {

	private final Logger logger = LoggerFactory.getLogger(RpcProviderHandler.class);

	/**
	 * 存储服务提供者中被@RpcService注解标注的类的对象
	 * key为：serviceName#serviceVersion#group
	 * value为：@RpcService注解标注的类的对象
	 */
	private final Map<String, Object> handlerMap;

	public RpcProviderHandler(Map<String, Object> handlerMap) {
		this.handlerMap = handlerMap;
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, RpcProtocol<RpcRequest> protocol) throws Exception {
		logger.info("RPC提供者收到的数据为===>>> {}", protocol.toString());
		logger.info("handlerMap中存放的数据如下: ");
		for (Map.Entry<String, Object> entry : handlerMap.entrySet()) {
			logger.info(entry.getKey() + " === " + entry.getValue());
		}

		RpcHeader header = protocol.getHeader();
		RpcRequest request = protocol.getBody();

		//将header中的消息类型设置为响应类型的消息
		header.setMsgType((byte) RpcType.RESPONSE.getType());

		//构建响应协议数据
		RpcProtocol<RpcResponse> responseRpcProtocol = new RpcProtocol<>();
		RpcResponse response = new RpcResponse();
		response.setResult("数据交互成功");
		response.setAsync(request.getAsync());
		response.setOneway(request.getOneway());
		responseRpcProtocol.setHeader(header);
		responseRpcProtocol.setBody(response);
		ctx.writeAndFlush(responseRpcProtocol);
	}
}
