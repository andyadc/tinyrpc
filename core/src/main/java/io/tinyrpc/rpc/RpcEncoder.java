package io.tinyrpc.rpc;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.tinyrpc.compressor.Compressor;
import io.tinyrpc.compressor.CompressorFactory;
import io.tinyrpc.model.Header;
import io.tinyrpc.model.Message;
import io.tinyrpc.serialization.Serialization;
import io.tinyrpc.serialization.SerializationFactory;

public class RpcEncoder extends MessageToByteEncoder<Message<R>> {

    @Override
    protected void encode(ChannelHandlerContext context, Message<R> message, ByteBuf byteBuf) throws Exception {
        Header header = message.getHeader();
        // 依次序列化消息头中的魔数、版本、附加信息以及消息ID
        byteBuf.writeShort(header.getMagic());
        byteBuf.writeByte(header.getVersion());
        byteBuf.writeByte(header.getExtraInfo());
        byteBuf.writeLong(header.getMessageId());

        Serialization serialization = SerializationFactory.getSerialization(header.getExtraInfo());
        Compressor compressor = CompressorFactory.getCompressor(header.getExtraInfo());

        byte[] payload = compressor.compress(serialization.serialize(message.getContent()));
        byteBuf.writeInt(payload.length);
        byteBuf.writeBytes(payload);
    }
}
