package io.tinyrpc.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 抽象消费者配置
 */
public abstract class AbstractConsumerConfig<T> extends AbstractInterfaceConfig {

    private static final Logger logger = LoggerFactory.getLogger(AbstractConsumerConfig.class);

    /**
     * 注册中心配置，只能配置一个
     */
    protected RegistryConfig registry;
    /**
     * 直连调用地址
     */
    protected String url;

    public AbstractConsumerConfig() {
    }

    public RegistryConfig getRegistry() {
        return registry;
    }

    public void setRegistry(RegistryConfig registry) {
        this.registry = registry;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
