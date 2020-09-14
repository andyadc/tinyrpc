package io.tinyrpc.config;

import java.util.Map;

/**
 * 抽象接口配置
 */
public class AbstractInterfaceConfig extends AbstractConfig {

    /**
     * 不管普通调用和泛化调用，都是设置实际的接口类名称
     */
    protected String interfaceClass;
    /**
     * 服务名称
     */
    protected String serviceName;
    /**
     * 别名
     */
    protected String alias;
    /**
     * 远程调用超时时间(毫秒)
     */
    protected Integer timeout;
    /**
     * 自定义参数
     */
    protected Map<String, String> parameters;
    /**
     * 压缩算法，为空则不压缩
     */
    protected String compress;
    /**
     * 代理类型
     */
    protected String proxy;

    public AbstractInterfaceConfig() {
    }

    public String getInterfaceClass() {
        return interfaceClass;
    }

    public void setInterfaceClass(String interfaceClass) {
        this.interfaceClass = interfaceClass;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public Integer getTimeout() {
        return timeout;
    }

    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }

    public String getCompress() {
        return compress;
    }

    public void setCompress(String compress) {
        this.compress = compress;
    }

    public String getProxy() {
        return proxy;
    }

    public void setProxy(String proxy) {
        this.proxy = proxy;
    }
}
