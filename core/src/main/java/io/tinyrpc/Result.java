package io.tinyrpc;

import java.io.Serializable;

public class Result implements Serializable {

    /**
     * 异常
     */
    protected Throwable exception;

    /**
     * 返回值
     */
    protected Object value;
}
