package io.tinyrpc.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.ArrayList;
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
    /**
     * 配置的协议列表
     */
    protected ServerConfig serverConfig;
    /**
     * 接口实现类引用
     */
    protected transient T ref;

    public List<RegistryConfig> getRegistry() {
        return registry;
    }

    public void setRegistry(List<RegistryConfig> registry) {
        this.registry = registry;
    }

    /**
     * 设置注册中心
     *
     * @param registry RegistryConfig
     */
    public void setRegistry(RegistryConfig registry) {
        if (registry != null) {
            if (this.registry == null) {
                this.registry = new ArrayList<>();
            }
            this.registry.add(registry);
        }
    }
}
