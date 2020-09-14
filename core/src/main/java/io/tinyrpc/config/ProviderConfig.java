package io.tinyrpc.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.List;

/**
 * 服务发布者配置
 */
public class ProviderConfig<T> extends AbstractInterfaceConfig implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger(ProviderConfig.class);

    /**
     * 注册中心配置，可配置多个
     */
    protected List<RegistryConfig> registry;
}
