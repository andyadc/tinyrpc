package io.tinyrpc.annotation;

import io.tinyrpc.constant.RpcConstants;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RpcService {

	/**
	 * 接口的 Class
	 */
	Class<?> interfaceClass() default void.class;

	/**
	 * 接口的ClassName
	 */
	String interfaceClassName() default "";

	/**
	 * 版本号
	 */
	String version() default RpcConstants.RPC_COMMON_DEFAULT_VERSION;

	/**
	 * 服务分组，默认为空
	 */
	String group() default "";

	/**
	 * 权重
	 */
	int weight() default RpcConstants.SERVICE_WEIGHT_MIN;
}
