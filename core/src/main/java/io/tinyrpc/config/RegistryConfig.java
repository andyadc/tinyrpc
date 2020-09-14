package io.tinyrpc.config;

import java.io.Serializable;
import java.util.Map;

public class RegistryConfig implements Serializable {

    /**
     * 注册中心实现
     */
    protected String registry;
    /**
     * 地址
     */
    protected String address;
    /**
     * 调用注册中心超时时间
     */
    protected Integer timeout;
    /**
     * 最大连接重试次数
     */
    protected Integer maxConnectRetryTimes;
    /**
     * The Parameters. 自定义参数
     */
    protected Map<String, String> parameters;

    public RegistryConfig() {
    }

    public String getRegistry() {
        return registry;
    }

    public void setRegistry(String registry) {
        this.registry = registry;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getTimeout() {
        return timeout;
    }

    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }

    public Integer getMaxConnectRetryTimes() {
        return maxConnectRetryTimes;
    }

    public void setMaxConnectRetryTimes(Integer maxConnectRetryTimes) {
        this.maxConnectRetryTimes = maxConnectRetryTimes;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }
}
