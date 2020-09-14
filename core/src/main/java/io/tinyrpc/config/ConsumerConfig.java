package io.tinyrpc.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

/**
 * 消费者配置
 */
public class ConsumerConfig<T> extends AbstractConsumerConfig<T> implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger(ConsumerConfig.class);

    public ConsumerConfig() {
    }


}
