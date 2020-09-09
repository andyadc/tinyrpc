package io.tinyrpc.model;

public class Message<R> {

    private Header header;
    private Request request;
    private Object content;

    public Message(Header header, Request request) {
        this.header = header;
        this.request = request;
    }

    public Header getHeader() {
        return header;
    }

    public void setHeader(Header header) {
        this.header = header;
    }

    public Request getRequest() {
        return request;
    }

    public void setRequest(Request request) {
        this.request = request;
    }

    public Object getContent() {
        return content;
    }

    public void setContent(Object content) {
        this.content = content;
    }
}
