package io.tinyrpc.model;

import java.io.Serializable;

public class Request implements Serializable {

    private String serviceName; // 请求的Service类名
    private String methodName; // 请求的方法名称
    private Class[] argTypes; // 请求方法的参数类型
    private Object[] args; // 请求方法的参数

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Class[] getArgTypes() {
        return argTypes;
    }

    public void setArgTypes(Class[] argTypes) {
        this.argTypes = argTypes;
    }

    public Object[] getArgs() {
        return args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }
}
