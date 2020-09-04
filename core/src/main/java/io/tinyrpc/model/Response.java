package io.tinyrpc.model;

import java.io.Serializable;

public class Response implements Serializable {

    private int code = 0; // 响应的错误码，正常响应为0，非0表示异常响应
    private String message; // 异常信息
    private Object result; // 响应结果

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }
}
