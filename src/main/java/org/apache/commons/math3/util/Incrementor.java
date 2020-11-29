package org.apache.commons.math3.util;

import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.util.IntegerSequence;

@Deprecated
public class Incrementor {
    private int count;
    private final MaxCountExceededCallback maxCountCallback;
    private int maximalCount;

    public interface MaxCountExceededCallback {
        void trigger(int i) throws MaxCountExceededException;
    }

    public Incrementor() {
        this(0);
    }

    public Incrementor(int max) {
        this(max, new MaxCountExceededCallback() {
            /* class org.apache.commons.math3.util.Incrementor.C03501 */

            @Override // org.apache.commons.math3.util.Incrementor.MaxCountExceededCallback
            public void trigger(int max) throws MaxCountExceededException {
                throw new MaxCountExceededException(Integer.valueOf(max));
            }
        });
    }

    public Incrementor(int max, MaxCountExceededCallback cb) throws NullArgumentException {
        this.count = 0;
        if (cb == null) {
            throw new NullArgumentException();
        }
        this.maximalCount = max;
        this.maxCountCallback = cb;
    }

    public void setMaximalCount(int max) {
        this.maximalCount = max;
    }

    public int getMaximalCount() {
        return this.maximalCount;
    }

    public int getCount() {
        return this.count;
    }

    public boolean canIncrement() {
        return this.count < this.maximalCount;
    }

    public void incrementCount(int value) throws MaxCountExceededException {
        for (int i = 0; i < value; i++) {
            incrementCount();
        }
    }

    public void incrementCount() throws MaxCountExceededException {
        int i = this.count + 1;
        this.count = i;
        if (i > this.maximalCount) {
            this.maxCountCallback.trigger(this.maximalCount);
        }
    }

    public void resetCount() {
        this.count = 0;
    }

    public static Incrementor wrap(final IntegerSequence.Incrementor incrementor) {
        return new Incrementor() {
            /* class org.apache.commons.math3.util.Incrementor.C03512 */
            private IntegerSequence.Incrementor delegate = incrementor;

            {
                Incrementor.super.setMaximalCount(this.delegate.getMaximalCount());
                Incrementor.super.incrementCount(this.delegate.getCount());
            }

            @Override // org.apache.commons.math3.util.Incrementor
            public void setMaximalCount(int max) {
                Incrementor.super.setMaximalCount(max);
                this.delegate = this.delegate.withMaximalCount(max);
            }

            @Override // org.apache.commons.math3.util.Incrementor
            public void resetCount() {
                Incrementor.super.resetCount();
                this.delegate = this.delegate.withStart(0);
            }

            @Override // org.apache.commons.math3.util.Incrementor
            public void incrementCount() {
                Incrementor.super.incrementCount();
                this.delegate.increment();
            }
        };
    }
}
