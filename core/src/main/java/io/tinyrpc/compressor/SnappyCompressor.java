package io.tinyrpc.compressor;

import org.xerial.snappy.Snappy;

import java.io.IOException;

public class SnappyCompressor implements Compressor {

    @Override
    public byte[] compress(byte[] data) throws IOException {
        if (data != null) {
            return Snappy.compress(data);
        }
        return new byte[0];
    }

    @Override
    public byte[] uncompress(byte[] data) throws IOException {
        if (data != null) {
            return Snappy.uncompress(data);
        }
        return new byte[0];
    }
}
