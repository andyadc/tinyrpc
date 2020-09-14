package io.tinyrpc;

import java.io.Serializable;

public final class URL implements Serializable {

    // 协议
    private final String protocol;
    // 名称
    private final String username;
    // 密码
    private final String password;
    // 主机
    private final String host;
    // 端口
    private final int port;
    // 路径
    private final String path;

    public URL() {
        this.protocol = null;
        this.username = null;
        this.password = null;
        this.host = null;
        this.port = 0;
        this.path = null;
    }

    public URL(String protocol, String username, String password, String host, int port, String path) {
        this.protocol = protocol;
        this.username = username;
        this.password = password;
        this.host = host;
        this.port = port;
        this.path = path;
    }
}
