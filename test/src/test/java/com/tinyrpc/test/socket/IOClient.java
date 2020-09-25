package com.tinyrpc.test.socket;

import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class IOClient {

	public static void main(String[] args) {
		new Thread(() -> {
			try {
				Socket socket = new Socket("127.0.0.1", 9999);
				while (true) {
					socket.getOutputStream().write(("hello netty-" + new Date()).getBytes(StandardCharsets.UTF_8));
					TimeUnit.SECONDS.sleep(1L);
				}
			} catch (Exception e) {
			}
		}).start();
	}
}
