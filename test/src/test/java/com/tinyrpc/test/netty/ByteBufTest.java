package com.tinyrpc.test.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

/**
 * https://juejin.im/book/6844733738119593991/section/6844733738274799624
 */
public class ByteBufTest {

	public static void main(String[] args) {
		ByteBuf buf = ByteBufAllocator.DEFAULT.buffer();
		print("initial", buf);

		buf = ByteBufAllocator.DEFAULT.buffer(9, 100);
		print("allocate ByteBuf(9, 100)", buf);

		// write 方法改变写指针，写完之后写指针未到 capacity 的时候，buffer 仍然可写
		buf.writeBytes(new byte[]{1, 2, 3, 4});
		print("writeBytes{1, 2, 3, 4}", buf);

		// write 方法改变写指针，写完之后写指针未到 capacity 的时候，buffer 仍然可写, 写完 int 类型之后，写指针增加4
		buf.writeInt(12);
		print("writeInt(12)", buf);

		// write 方法改变写指针, 写完之后写指针等于 capacity 的时候，buffer 不可写
		buf.writeBytes(new byte[]{5});
		print("writeBytes{5}", buf);

		// write 方法改变写指针，写的时候发现 buffer 不可写则开始扩容，扩容之后 capacity 随即改变
		buf.writeBytes(new byte[]{6});
		print("writeBytes{6}", buf);

		// get 方法不改变读写指针
		System.out.println(buf.getByte(3));
		System.out.println(buf.getShort(3));
		System.out.println(buf.getInt(3));
		print("getByte()", buf);

		// set 方法不改变读写指针
		buf.setByte(buf.readableBytes() + 1, 0);
		print("setByte()", buf);

		// read 方法改变读指针
		byte[] data = new byte[buf.readableBytes()];
		buf.readBytes(data);
		print("readBytes(" + data.length + ")", buf);
	}

	private static void print(String action, ByteBuf buffer) {
		System.out.println("after ===========" + action + "============");
		System.out.println("capacity(): " + buffer.capacity());
		System.out.println("maxCapacity(): " + buffer.maxCapacity());
		System.out.println("readerIndex(): " + buffer.readerIndex());
		System.out.println("readableBytes(): " + buffer.readableBytes());
		System.out.println("isReadable(): " + buffer.isReadable());
		System.out.println("writerIndex(): " + buffer.writerIndex());
		System.out.println("writableBytes(): " + buffer.writableBytes());
		System.out.println("isWritable(): " + buffer.isWritable());
		System.out.println("maxWritableBytes(): " + buffer.maxWritableBytes());
		System.out.println();
	}
}
