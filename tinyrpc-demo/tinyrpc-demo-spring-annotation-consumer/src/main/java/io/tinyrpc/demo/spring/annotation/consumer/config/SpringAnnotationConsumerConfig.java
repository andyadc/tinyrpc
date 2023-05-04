package io.tinyrpc.demo.spring.annotation.consumer.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * 服务消费者注解配置类
 */
@Configuration
@ComponentScan(value = {"io.tinyrpc.*"})
public class SpringAnnotationConsumerConfig {
}
