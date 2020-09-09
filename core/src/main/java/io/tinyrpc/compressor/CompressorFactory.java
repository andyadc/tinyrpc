package io.tinyrpc.compressor;

public class CompressorFactory {

    private static final Compressor snappyCompressor = new SnappyCompressor();

    public static Compressor get(byte extraInfo) {
        switch (extraInfo & 24) {
            case 0x0:
                return snappyCompressor;
            default:
                return new SnappyCompressor();
        }
    }

}
