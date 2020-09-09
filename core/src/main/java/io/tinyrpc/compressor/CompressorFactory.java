package io.tinyrpc.compressor;

public class CompressorFactory {

    private static final Compressor snappyCompressor = new SnappyCompressor();

    public static Compressor getCompressor(short ext) {
        return snappyCompressor;
    }

}
