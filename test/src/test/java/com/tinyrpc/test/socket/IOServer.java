package com.tinyrpc.test.socket;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class IOServer {

	public static void main(String[] args) throws IOException {
		ServerSocket serverSocket = new ServerSocket(9999);

		new Thread(() -> {
			while (true) {
				try (Socket socket = serverSocket.accept()) {
					int len;
					byte[] data = new byte[1024];
					InputStream inputStream = socket.getInputStream();
					while ((len = inputStream.read(data)) != -1) {
						System.out.println(new String(data, 0, len, StandardCharsets.UTF_8));
					}
				} catch (Exception e) {
				}
			}
		}).start();
	}
}
