package io.tinyrpc.transport;

import io.netty.channel.ChannelHandlerContext;
import io.tinyrpc.model.Header;
import io.tinyrpc.model.Message;
import io.tinyrpc.model.Request;
import io.tinyrpc.model.Response;
import io.tinyrpc.service.BeanManager;

import java.lang.reflect.Method;

public class InvokeRunnable implements Runnable {

	private final ChannelHandlerContext context;
	private final Message<Request> message;

	public InvokeRunnable(ChannelHandlerContext context, Message<Request> message) {
		this.context = context;
		this.message = message;
	}

	@Override
	public void run() {
		Response response = new Response();

        Object result = null;
        try {
            Request request = message.getContent();
            String serviceName = request.getServiceName();
            Object bean = BeanManager.getBean(serviceName);

            Method method = bean.getClass().getMethod(request.getMethodName(), request.getArgTypes());
            result = method.invoke(bean, request.getArgs());
        } catch (Exception e) {
            e.printStackTrace();
            response.setCode(-1);
        }

        Header header = message.getHeader();
        header.setExtraInfo((byte) 1);
        response.setResult(result); // 设置响应结果
        // 将响应消息返回给客户端
        context.writeAndFlush(new Message<>(header, response));
    }
}
