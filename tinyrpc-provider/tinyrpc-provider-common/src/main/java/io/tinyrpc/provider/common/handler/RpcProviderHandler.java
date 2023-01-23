package io.tinyrpc.provider.common.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * RPC服务提供者的 Handler处理类
 */
//public class RpcProviderHandler extends SimpleChannelInboundHandler<RpcProtocol<RpcRequest>> {
public class RpcProviderHandler extends SimpleChannelInboundHandler<Object> {

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
	protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
		logger.info("RPC提供者收到的数据为===>>> {}", msg.toString());
		logger.info("handlerMap中存放的数据如下: ");
		for (Map.Entry<String, Object> entry : handlerMap.entrySet()) {
			logger.info(entry.getKey() + " === " + entry.getValue());
		}
		// 直接返回数据
		ctx.writeAndFlush(msg);
	}
}
