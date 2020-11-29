package org.apache.commons.math3.ode.events;

public interface EventHandler {

    public enum Action {
        STOP,
        RESET_STATE,
        RESET_DERIVATIVES,
        CONTINUE
    }

    Action eventOccurred(double d, double[] dArr, boolean z);

    /* renamed from: g */
    double mo2971g(double d, double[] dArr);

    void init(double d, double[] dArr, double d2);

    void resetState(double d, double[] dArr);
}
