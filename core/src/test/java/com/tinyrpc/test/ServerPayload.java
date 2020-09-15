package com.tinyrpc.test;

import java.io.Serializable;

public class ServerPayload implements Serializable {

    private String host;
    private int port;

    private String className;
    private String methodName;

    public ServerPayload() {
    }

    public ServerPayload(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    @Override
    public String toString() {
        return "ServerPayload{" +
                "host='" + host + '\'' +
                ", port=" + port +
                ", className='" + className + '\'' +
                ", methodName='" + methodName + '\'' +
                '}';
    }
}
