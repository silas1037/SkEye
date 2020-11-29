package org.apache.commons.math3.util;

import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;
import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.util.Incrementor;
import org.apache.commons.math3.util.IntegerSequence;

public class IterationManager {
    private IntegerSequence.Incrementor iterations;
    private final Collection<IterationListener> listeners;

    public IterationManager(int maxIterations) {
        this.iterations = IntegerSequence.Incrementor.create().withMaximalCount(maxIterations);
        this.listeners = new CopyOnWriteArrayList();
    }

    @Deprecated
    public IterationManager(int maxIterations, final Incrementor.MaxCountExceededCallback callBack) {
        this(maxIterations, new IntegerSequence.Incrementor.MaxCountExceededCallback() {
            /* class org.apache.commons.math3.util.IterationManager.C03531 */

            @Override // org.apache.commons.math3.util.IntegerSequence.Incrementor.MaxCountExceededCallback
            public void trigger(int maximalCount) throws MaxCountExceededException {
                Incrementor.MaxCountExceededCallback.this.trigger(maximalCount);
            }
        });
    }

    public IterationManager(int maxIterations, IntegerSequence.Incrementor.MaxCountExceededCallback callBack) {
        this.iterations = IntegerSequence.Incrementor.create().withMaximalCount(maxIterations).withCallback(callBack);
        this.listeners = new CopyOnWriteArrayList();
    }

    public void addIterationListener(IterationListener listener) {
        this.listeners.add(listener);
    }

    public void fireInitializationEvent(IterationEvent e) {
        for (IterationListener l : this.listeners) {
            l.initializationPerformed(e);
        }
    }

    public void fireIterationPerformedEvent(IterationEvent e) {
        for (IterationListener l : this.listeners) {
            l.iterationPerformed(e);
        }
    }

    public void fireIterationStartedEvent(IterationEvent e) {
        for (IterationListener l : this.listeners) {
            l.iterationStarted(e);
        }
    }

    public void fireTerminationEvent(IterationEvent e) {
        for (IterationListener l : this.listeners) {
            l.terminationPerformed(e);
        }
    }

    public int getIterations() {
        return this.iterations.getCount();
    }

    public int getMaxIterations() {
        return this.iterations.getMaximalCount();
    }

    public void incrementIterationCount() throws MaxCountExceededException {
        this.iterations.increment();
    }

    public void removeIterationListener(IterationListener listener) {
        this.listeners.remove(listener);
    }

    public void resetIterationCount() {
        this.iterations = this.iterations.withStart(0);
    }
}
