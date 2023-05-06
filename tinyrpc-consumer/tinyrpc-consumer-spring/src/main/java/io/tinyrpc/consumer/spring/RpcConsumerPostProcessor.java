package io.tinyrpc.consumer.spring;

import io.tinyrpc.annotation.RpcReference;
import io.tinyrpc.constant.RpcConstants;
import io.tinyrpc.consumer.spring.context.RpcConsumerSpringContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * RpcConsumerPostProcessor
 * <p>
 * RpcConsumerPostProcessor类本质上是一个自定义的Spring后置处理器类，
 * 实现了Spring的ApplicationContextAware接口、BeanClassLoaderAware接口和BeanFactoryPostProcessor接口，
 * 最核心的功能就是解析<code>@RpcReference</code>注解，创建RpcReferenceBean类的BeanDefinition对象，并将BeanDefinition对象注册到IOC容器中。
 * </p>
 */
@Component
public class RpcConsumerPostProcessor implements ApplicationContextAware, BeanClassLoaderAware, BeanFactoryPostProcessor {

	private static final Logger logger = LoggerFactory.getLogger(RpcConsumerPostProcessor.class);

	private final Map<String, BeanDefinition> rpcRefBeanDefinitions = new LinkedHashMap<>();

	private ApplicationContext context;
	private ClassLoader classLoader;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.context = applicationContext;
		RpcConsumerSpringContext.getInstance().setContext(applicationContext);
	}

	@Override
	public void setBeanClassLoader(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		for (String beanDefinitionName : beanFactory.getBeanDefinitionNames()) {
			BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanDefinitionName);
			String beanClassName = beanDefinition.getBeanClassName();
			if (beanClassName != null) {
				Class<?> clazz = ClassUtils.resolveClassName(beanClassName, this.classLoader);
				ReflectionUtils.doWithFields(clazz, this::parseRpcReference);
			}
		}

		BeanDefinitionRegistry registry = (BeanDefinitionRegistry) beanFactory;
		this.rpcRefBeanDefinitions.forEach((beanName, beanDefinition) -> {
			if (context.containsBean(beanName)) {
				throw new IllegalArgumentException("spring context already has a bean named " + beanName);
			}
			registry.registerBeanDefinition(beanName, rpcRefBeanDefinitions.get(beanName));
			logger.info("registered RpcReferenceBean {} success.", beanName);
		});
	}

	private void parseRpcReference(Field field) {
		RpcReference annotation = AnnotationUtils.getAnnotation(field, RpcReference.class);
		if (annotation != null) {
			BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(RpcReferenceBean.class);
			builder.setInitMethodName(RpcConstants.INIT_METHOD_NAME); // init()
			builder.addPropertyValue("interfaceClass", field.getType());
			builder.addPropertyValue("version", annotation.version());
			builder.addPropertyValue("registryType", annotation.registryType());
			builder.addPropertyValue("registryAddress", annotation.registryAddress());
			builder.addPropertyValue("loadBalanceType", annotation.loadBalanceType());
			builder.addPropertyValue("serializationType", annotation.serializationType());
			builder.addPropertyValue("timeout", annotation.timeout());
			builder.addPropertyValue("async", annotation.async());
			builder.addPropertyValue("oneway", annotation.oneway());
			builder.addPropertyValue("proxy", annotation.proxy());
			builder.addPropertyValue("group", annotation.group());
			builder.addPropertyValue("scanNotActiveChannelInterval", annotation.scanNotActiveChannelInterval());
			builder.addPropertyValue("heartbeatInterval", annotation.heartbeatInterval());
			builder.addPropertyValue("retryInterval", annotation.retryInterval());
			builder.addPropertyValue("retryTimes", annotation.retryTimes());
			builder.addPropertyValue("enableResultCache", annotation.enableResultCache());
			builder.addPropertyValue("resultCacheExpire", annotation.resultCacheExpire());
			builder.addPropertyValue("enableDirectServer", annotation.enableDirectServer());
			builder.addPropertyValue("directServerUrl", annotation.directServerUrl());
			builder.addPropertyValue("corePoolSize", annotation.corePoolSize());
			builder.addPropertyValue("maximumPoolSize", annotation.maximumPoolSize());
			builder.addPropertyValue("flowType", annotation.flowType());

			BeanDefinition beanDefinition = builder.getBeanDefinition();
			rpcRefBeanDefinitions.put(field.getName(), beanDefinition);
		}
	}
}
