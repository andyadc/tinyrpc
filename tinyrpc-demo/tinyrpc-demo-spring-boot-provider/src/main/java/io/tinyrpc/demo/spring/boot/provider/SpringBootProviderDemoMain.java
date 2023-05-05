package io.tinyrpc.demo.spring.boot.provider;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan(value = {"io.tinyrpc"})
@SpringBootApplication
public class SpringBootProviderDemoMain {

	public static void main(String[] args) {
		SpringApplication.run(SpringBootProviderDemoMain.class, args);
	}
}
