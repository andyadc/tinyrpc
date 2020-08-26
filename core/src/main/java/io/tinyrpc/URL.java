package io.tinyrpc;

import java.io.Serializable;

public final class URL implements Serializable {

    private final String protocol;

    private final String username;

    private final String password;

    private final String host;

    private final int port;

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
