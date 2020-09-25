package com.tinyrpc.test.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class NIOServer {

	public static void main(String[] args) throws IOException {
		Selector serverSelector = Selector.open();
		Selector clientSelector = Selector.open();

		new Thread(() -> {
			try {
				// 对应IO编程中服务端启动
				ServerSocketChannel listenerChannel = ServerSocketChannel.open();
				listenerChannel.socket().bind(new InetSocketAddress(9999));
				listenerChannel.configureBlocking(false);
				listenerChannel.register(serverSelector, SelectionKey.OP_ACCEPT);

				while (true) {
					// 监测是否有新的连接，这里的1指的是阻塞的时间为 1ms
					if (serverSelector.select(1) > 0) {
						Set<SelectionKey> keySet = serverSelector.selectedKeys();
						Iterator<SelectionKey> keyIterator = keySet.iterator();

						while (keyIterator.hasNext()) {
							SelectionKey selectionKey = keyIterator.next();
							if (selectionKey.isAcceptable()) {
								try {
									// (1) 每来一个新连接，不需要创建一个线程，而是直接注册到clientSelector
									SocketChannel clientChannel = ((ServerSocketChannel) selectionKey.channel()).accept();
									clientChannel.configureBlocking(false);
									clientChannel.register(clientSelector, SelectionKey.OP_READ);
								} finally {
									keyIterator.remove();
								}
							}
						}
					}
				}

			} catch (IOException e) {
			}

			new Thread(() -> {

			}).start();

		}).start();
	}
}
