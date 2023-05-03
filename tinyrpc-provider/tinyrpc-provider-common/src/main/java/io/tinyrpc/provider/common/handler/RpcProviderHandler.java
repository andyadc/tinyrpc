package io.tinyrpc.provider.common.handler;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.tinyrpc.common.constants.RpcConstants;
import io.tinyrpc.common.exception.RpcException;
import io.tinyrpc.common.helper.RpcServiceHelper;
import io.tinyrpc.common.threadpool.ServerThreadPool;
import io.tinyrpc.common.utils.JsonUtils;
import io.tinyrpc.protocol.RpcProtocol;
import io.tinyrpc.protocol.enumeration.RpcStatus;
import io.tinyrpc.protocol.enumeration.RpcType;
import io.tinyrpc.protocol.header.RpcHeader;
import io.tinyrpc.protocol.request.RpcRequest;
import io.tinyrpc.protocol.response.RpcResponse;
import io.tinyrpc.reflect.api.ReflectInvoker;
import io.tinyrpc.spi.loader.ExtensionLoader;
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

	private final ReflectInvoker reflectInvoker;

	public RpcProviderHandler(String reflectType, Map<String, Object> handlerMap) {
		this.handlerMap = handlerMap;
		this.reflectInvoker = ExtensionLoader.getExtension(ReflectInvoker.class, reflectType);
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, RpcProtocol<RpcRequest> protocol) {

		// 异步 ServerThreadPool.submit(() -> {})
		ServerThreadPool.submit(() -> {
			RpcProtocol<RpcResponse> responseRpcProtocol = handleMessage(protocol);

			ctx.writeAndFlush(responseRpcProtocol).addListener(new ChannelFutureListener() {
				@Override
				public void operationComplete(ChannelFuture channelFuture) {
					logger.info("Send response for request " + protocol.getHeader().getRequestId());
				}
			});
		});
	}

	/**
	 * 处理消息
	 */
	private RpcProtocol<RpcResponse> handleMessage(RpcProtocol<RpcRequest> protocol) {
		RpcProtocol<RpcResponse> responseRpcProtocol = null;
		RpcHeader header = protocol.getHeader();
		logger.info("Provider received request ===>>> {}", JsonUtils.toJSONString(protocol));

		if (header.getMsgType() == (byte) RpcType.HEARTBEAT_FROM_CONSUMER.getType()) { // 心跳消息
			responseRpcProtocol = handleHeartbeatMessage(protocol, header);
		} else if (header.getMsgType() == (byte) RpcType.REQUEST.getType()) { // 请求消息
			responseRpcProtocol = handleRequestMessage(protocol, header);
		}
		return responseRpcProtocol;
	}

	/**
	 * 处理请求消息
	 */
	private RpcProtocol<RpcResponse> handleRequestMessage(RpcProtocol<RpcRequest> protocol, RpcHeader header) {
		header.setMsgType((byte) RpcType.RESPONSE.getType());
		RpcRequest request = protocol.getBody();

		RpcProtocol<RpcResponse> responseRpcProtocol = new RpcProtocol<>();
		RpcResponse response = new RpcResponse();
		try {
			Object result = handle(request);
			response.setResult(result);
			response.setAsync(request.getAsync());
			response.setOneway(request.getOneway());
			header.setStatus((byte) RpcStatus.SUCCESS.getCode());
		} catch (Throwable t) {
			response.setError(t.toString());
			header.setStatus((byte) RpcStatus.FAIL.getCode());
			logger.error("RPC Server handle request error", t);
		}

		responseRpcProtocol.setHeader(header);
		responseRpcProtocol.setBody(response);
		return responseRpcProtocol;
	}

	/**
	 * 处理心跳消息
	 */
	private RpcProtocol<RpcResponse> handleHeartbeatMessage(RpcProtocol<RpcRequest> protocol, RpcHeader header) {
		header.setMsgType((byte) RpcType.HEARTBEAT_TO_CONSUMER.getType());
		RpcRequest request = protocol.getBody();
		RpcProtocol<RpcResponse> responseRpcProtocol = new RpcProtocol<>();
		RpcResponse response = new RpcResponse();
		response.setResult(RpcConstants.HEARTBEAT_PONG);
		response.setAsync(request.getAsync());
		response.setOneway(request.getOneway());
		header.setStatus((byte) RpcStatus.SUCCESS.getCode());

		responseRpcProtocol.setHeader(header);
		responseRpcProtocol.setBody(response);
		return responseRpcProtocol;
	}

	private Object handle(RpcRequest request) throws Throwable {
		String serviceKey = RpcServiceHelper.buildServiceKey(request.getClassName(), request.getVersion(), request.getGroup());
		Object serviceBean = handlerMap.get(serviceKey);
		if (serviceBean == null) {
			throw new RpcException(String.format("Service not exist: %s:%s", request.getClassName(), request.getMethodName()));
		}

		Class<?> serviceClass = serviceBean.getClass();
		String methodName = request.getMethodName();
		Class<?>[] parameterTypes = request.getParameterTypes();
		Object[] parameters = request.getParameters();

		logger.info(serviceClass.getName());
		logger.info(methodName);

		if (parameterTypes != null && parameterTypes.length > 0) {
			for (Class<?> parameterType : parameterTypes) {
				logger.info(parameterType.getName());
			}
		}

		if (parameters != null && parameters.length > 0) {
			for (Object parameter : parameters) {
				logger.info(parameter.toString());
			}
		}

		return this.reflectInvoker.invokeMethod(serviceBean, serviceClass, methodName, parameterTypes, parameters);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		logger.error("Server caught exception.", cause);
		ctx.close();
	}
}
