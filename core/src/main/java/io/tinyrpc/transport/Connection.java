package io.tinyrpc.transport;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;

public class Connection implements Closeable {

    private static final AtomicLong ID_GEN = new AtomicLong(0);

    @Override
    public void close() throws IOException {

    }
}
