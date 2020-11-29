package org.apache.commons.math3.ode;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MaxCountExceededException;

class ParameterJacobianWrapper implements ParameterJacobianProvider {
    private final FirstOrderDifferentialEquations fode;
    private final Map<String, Double> hParam = new HashMap();
    private final ParameterizedODE pode;

    ParameterJacobianWrapper(FirstOrderDifferentialEquations fode2, ParameterizedODE pode2, ParameterConfiguration[] paramsAndSteps) {
        this.fode = fode2;
        this.pode = pode2;
        for (ParameterConfiguration param : paramsAndSteps) {
            String name = param.getParameterName();
            if (pode2.isSupported(name)) {
                this.hParam.put(name, Double.valueOf(param.getHP()));
            }
        }
    }

    @Override // org.apache.commons.math3.ode.Parameterizable
    public Collection<String> getParametersNames() {
        return this.pode.getParametersNames();
    }

    @Override // org.apache.commons.math3.ode.Parameterizable
    public boolean isSupported(String name) {
        return this.pode.isSupported(name);
    }

    @Override // org.apache.commons.math3.ode.ParameterJacobianProvider
    public void computeParameterJacobian(double t, double[] y, double[] yDot, String paramName, double[] dFdP) throws DimensionMismatchException, MaxCountExceededException {
        int n = this.fode.getDimension();
        if (this.pode.isSupported(paramName)) {
            double[] tmpDot = new double[n];
            double p = this.pode.getParameter(paramName);
            double hP = this.hParam.get(paramName).doubleValue();
            this.pode.setParameter(paramName, p + hP);
            this.fode.computeDerivatives(t, y, tmpDot);
            for (int i = 0; i < n; i++) {
                dFdP[i] = (tmpDot[i] - yDot[i]) / hP;
            }
            this.pode.setParameter(paramName, p);
            return;
        }
        Arrays.fill(dFdP, 0, n, 0.0d);
    }
}
