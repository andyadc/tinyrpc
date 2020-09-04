package io.tinyrpc.compressor;

import java.io.IOException;

public interface Compressor {
    byte[] compress(byte[] data) throws IOException;

    byte[] uncompress(byte[] data) throws IOException;
}
