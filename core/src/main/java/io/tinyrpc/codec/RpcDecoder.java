package io.tinyrpc.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.tinyrpc.Constants;
import io.tinyrpc.compressor.Compressor;
import io.tinyrpc.compressor.CompressorFactory;
import io.tinyrpc.model.Header;
import io.tinyrpc.model.Message;
import io.tinyrpc.model.Request;
import io.tinyrpc.model.Response;
import io.tinyrpc.serialization.Serialization;
import io.tinyrpc.serialization.SerializationFactory;

import java.util.List;

public class RpcDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        if (byteBuf.readableBytes() < 16) {
            return;
        }
        // 记录当前readIndex指针的位置，方便重置
        byteBuf.markReaderIndex();
        // 尝试读取消息头的魔数部分
        short magic = byteBuf.readShort();
        if (magic != 666) {
            byteBuf.resetReaderIndex();
            throw new RuntimeException("magic number error:" + magic);
        }
        // 依次读取消息版本、附加信息、消息ID以及消息体长度四部分
        byte version = byteBuf.readByte();
        byte extraInfo = byteBuf.readByte();
        long messageId = byteBuf.readLong();
        int size = byteBuf.readInt();

        Object body = null;
        // 心跳消息是没有消息体的，无须读取
        if (Constants.isHeartBeat(extraInfo)) {
            // 对于非心跳消息，没有积累到足够的数据是无法进行反序列化的
            if (byteBuf.readableBytes() < size) {
                byteBuf.resetReaderIndex();
                return;
            }
            // 读取消息体并进行反序列化
            byte[] payload = new byte[size];
            byteBuf.readBytes(payload);
            // 这里根据消息头中的extraInfo部分选择相应的序列化和压缩方式
            Serialization serialization = SerializationFactory.get(extraInfo);
            Compressor compressor = CompressorFactory.get(extraInfo);

            if (Constants.isRequest(extraInfo)) {
                body = serialization.deserialize(compressor.uncompress(payload), Request.class);
            } else {
                body = serialization.deserialize(compressor.uncompress(payload), Response.class);
            }
        }

        Header header = new Header(magic, version, extraInfo, messageId, size);
        Message<Object> message = new Message<>(header, body);
        list.add(message);
    }
}
