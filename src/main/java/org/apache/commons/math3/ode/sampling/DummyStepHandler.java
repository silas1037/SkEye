package org.apache.commons.math3.ode.sampling;

public class DummyStepHandler implements StepHandler {
    private DummyStepHandler() {
    }

    public static DummyStepHandler getInstance() {
        return LazyHolder.INSTANCE;
    }

    @Override // org.apache.commons.math3.ode.sampling.StepHandler
    public void init(double t0, double[] y0, double t) {
    }

    @Override // org.apache.commons.math3.ode.sampling.StepHandler
    public void handleStep(StepInterpolator interpolator, boolean isLast) {
    }

    private static class LazyHolder {
        private static final DummyStepHandler INSTANCE = new DummyStepHandler();

        private LazyHolder() {
        }
    }

    private Object readResolve() {
        return LazyHolder.INSTANCE;
    }
}
