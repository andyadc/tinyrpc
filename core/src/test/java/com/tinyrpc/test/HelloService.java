package com.tinyrpc.test;

public interface HelloService {

    String hello(String message);

    static void main(String[] args) {
        System.out.println(HelloService.class.getName());
        System.out.println(HelloService.class.getCanonicalName());
        System.out.println(HelloService.class.getSimpleName());
        System.out.println(HelloService.class.getTypeName());
    }
}
