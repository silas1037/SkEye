package com.lavadip.skeye;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import java.nio.channels.FileChannel;
import java.util.IllegalFormatWidthException;

final class NumComponents {
    private static final int COMP_LEN = 19902;
    private static final int MAX_NUM = 10000;
    static final byte[] components = new byte[COMP_LEN];
    static final byte[] lengths = new byte[10000];
    static final short[] offsets = new short[10000];

    NumComponents() {
    }

    public static void readFrom(FileInputStream in) {
        try {
            FileChannel ch = in.getChannel();
            ByteBuffer byteBuffer = ByteBuffer.allocate(49906);
            ch.read(byteBuffer);
            byteBuffer.position(0);
            byteBuffer.order(ByteOrder.BIG_ENDIAN);
            int length = byteBuffer.getInt();
            if (length != COMP_LEN) {
                throw new IllegalFormatWidthException(length);
            }
            byteBuffer.get(components, 0, COMP_LEN);
            byteBuffer.get(lengths, 0, 10000);
            ShortBuffer shortBuffer = byteBuffer.asShortBuffer();
            shortBuffer.get(offsets, 0, 10000);
            int remaining = shortBuffer.remaining();
            if (remaining != 0) {
                throw new IllegalFormatWidthException(remaining);
            }
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
