package org.apache.commons.math3.util;

import java.util.EventListener;

public interface IterationListener extends EventListener {
    void initializationPerformed(IterationEvent iterationEvent);

    void iterationPerformed(IterationEvent iterationEvent);

    void iterationStarted(IterationEvent iterationEvent);

    void terminationPerformed(IterationEvent iterationEvent);
}
