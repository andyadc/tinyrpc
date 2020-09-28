package com.tinyrpc.test.socket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
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
		}).start();

		new Thread(() -> {
			try {
				while (true) {
					// (2) 批量轮询是否有哪些连接有数据可读，这里的1指的是阻塞的时间为 1ms
					if (clientSelector.select(1) > 0) {
						Set<SelectionKey> keySet = clientSelector.selectedKeys();
						Iterator<SelectionKey> keyIterator = keySet.iterator();

						while (keyIterator.hasNext()) {
							SelectionKey key = keyIterator.next();

							if (key.isReadable()) {
								try {
									SocketChannel channel = (SocketChannel) key.channel();
									ByteBuffer buffer = ByteBuffer.allocate(1024);
									// (3) 面向 Buffer
									channel.read(buffer);
									buffer.flip();
									System.out.println(Charset.defaultCharset().newDecoder().decode(buffer).toString());
								} finally {
									keyIterator.remove();
									key.interestOps(SelectionKey.OP_READ);
								}
							}
						}
					}
				}
			} catch (Exception e) {
			}
		}).start();
	}
}
