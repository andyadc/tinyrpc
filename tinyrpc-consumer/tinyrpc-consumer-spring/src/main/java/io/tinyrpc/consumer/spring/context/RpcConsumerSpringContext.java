package io.tinyrpc.consumer.spring.context;

import org.springframework.context.ApplicationContext;

/**
 * Spring上下文
 */
public class RpcConsumerSpringContext {

	/**
	 * Spring ApplicationContext
	 */
	private ApplicationContext context;

	private RpcConsumerSpringContext() {
	}

	public static RpcConsumerSpringContext getInstance() {
		return Holder.INSTANCE;
	}

	public ApplicationContext getContext() {
		return context;
	}

	public void setContext(ApplicationContext context) {
		this.context = context;
	}

	private static class Holder {
		private static final RpcConsumerSpringContext INSTANCE = new RpcConsumerSpringContext();
	}
}
