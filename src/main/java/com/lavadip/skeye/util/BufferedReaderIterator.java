package com.lavadip.skeye.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Iterator;

public class BufferedReaderIterator implements Iterable<String> {

    /* renamed from: r */
    private final BufferedReader f93r;

    public BufferedReaderIterator(BufferedReader r) {
        this.f93r = r;
    }

    @Override // java.lang.Iterable
    public Iterator<String> iterator() {
        return new Iterator<String>() {
            /* class com.lavadip.skeye.util.BufferedReaderIterator.C01331 */

            public boolean hasNext() {
                try {
                    BufferedReaderIterator.this.f93r.mark(1);
                    if (BufferedReaderIterator.this.f93r.read() < 0) {
                        return false;
                    }
                    BufferedReaderIterator.this.f93r.reset();
                    return true;
                } catch (IOException e) {
                    return false;
                }
            }

            @Override // java.util.Iterator
            public String next() {
                try {
                    return BufferedReaderIterator.this.f93r.readLine();
                } catch (IOException e) {
                    return null;
                }
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }
}
