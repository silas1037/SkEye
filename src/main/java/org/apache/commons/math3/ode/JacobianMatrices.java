package org.apache.commons.math3.ode;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.exception.util.LocalizedFormats;

public class JacobianMatrices {
    private boolean dirtyParameter;
    private ExpandableStatefulODE efode;
    private int index;
    private List<ParameterJacobianProvider> jacobianProviders;
    private MainStateJacobianProvider jode;
    private double[] matricesData;
    private int paramDim;
    private ParameterizedODE pode;
    private ParameterConfiguration[] selectedParameters;
    private int stateDim;

    public JacobianMatrices(FirstOrderDifferentialEquations fode, double[] hY, String... parameters) throws DimensionMismatchException {
        this(new MainStateJacobianWrapper(fode, hY), parameters);
    }

    public JacobianMatrices(MainStateJacobianProvider jode2, String... parameters) {
        this.efode = null;
        this.index = -1;
        this.jode = jode2;
        this.pode = null;
        this.stateDim = jode2.getDimension();
        if (parameters == null) {
            this.selectedParameters = null;
            this.paramDim = 0;
        } else {
            this.selectedParameters = new ParameterConfiguration[parameters.length];
            for (int i = 0; i < parameters.length; i++) {
                this.selectedParameters[i] = new ParameterConfiguration(parameters[i], Double.NaN);
            }
            this.paramDim = parameters.length;
        }
        this.dirtyParameter = false;
        this.jacobianProviders = new ArrayList();
        this.matricesData = new double[((this.stateDim + this.paramDim) * this.stateDim)];
        for (int i2 = 0; i2 < this.stateDim; i2++) {
            this.matricesData[(this.stateDim + 1) * i2] = 1.0d;
        }
    }

    public void registerVariationalEquations(ExpandableStatefulODE expandable) throws DimensionMismatchException, MismatchedEquations {
        if (expandable.getPrimary() != (this.jode instanceof MainStateJacobianWrapper ? ((MainStateJacobianWrapper) this.jode).ode : this.jode)) {
            throw new MismatchedEquations();
        }
        this.efode = expandable;
        this.index = this.efode.addSecondaryEquations(new JacobiansSecondaryEquations());
        this.efode.setSecondaryState(this.index, this.matricesData);
    }

    public void addParameterJacobianProvider(ParameterJacobianProvider provider) {
        this.jacobianProviders.add(provider);
    }

    public void setParameterizedODE(ParameterizedODE parameterizedOde) {
        this.pode = parameterizedOde;
        this.dirtyParameter = true;
    }

    public void setParameterStep(String parameter, double hP) throws UnknownParameterException {
        ParameterConfiguration[] arr$ = this.selectedParameters;
        for (ParameterConfiguration param : arr$) {
            if (parameter.equals(param.getParameterName())) {
                param.setHP(hP);
                this.dirtyParameter = true;
                return;
            }
        }
        throw new UnknownParameterException(parameter);
    }

    public void setInitialMainStateJacobian(double[][] dYdY0) throws DimensionMismatchException {
        checkDimension(this.stateDim, dYdY0);
        checkDimension(this.stateDim, dYdY0[0]);
        int i = 0;
        for (double[] row : dYdY0) {
            System.arraycopy(row, 0, this.matricesData, i, this.stateDim);
            i += this.stateDim;
        }
        if (this.efode != null) {
            this.efode.setSecondaryState(this.index, this.matricesData);
        }
    }

    public void setInitialParameterJacobian(String pName, double[] dYdP) throws UnknownParameterException, DimensionMismatchException {
        checkDimension(this.stateDim, dYdP);
        int i = this.stateDim * this.stateDim;
        for (ParameterConfiguration param : this.selectedParameters) {
            if (pName.equals(param.getParameterName())) {
                System.arraycopy(dYdP, 0, this.matricesData, i, this.stateDim);
                if (this.efode != null) {
                    this.efode.setSecondaryState(this.index, this.matricesData);
                    return;
                }
                return;
            }
            i += this.stateDim;
        }
        throw new UnknownParameterException(pName);
    }

    public void getCurrentMainSetJacobian(double[][] dYdY0) {
        double[] p = this.efode.getSecondaryState(this.index);
        int j = 0;
        for (int i = 0; i < this.stateDim; i++) {
            System.arraycopy(p, j, dYdY0[i], 0, this.stateDim);
            j += this.stateDim;
        }
    }

    public void getCurrentParameterJacobian(String pName, double[] dYdP) {
        double[] p = this.efode.getSecondaryState(this.index);
        int i = this.stateDim * this.stateDim;
        for (ParameterConfiguration param : this.selectedParameters) {
            if (param.getParameterName().equals(pName)) {
                System.arraycopy(p, i, dYdP, 0, this.stateDim);
                return;
            } else {
                i += this.stateDim;
            }
        }
    }

    private void checkDimension(int expected, Object array) throws DimensionMismatchException {
        int arrayDimension = array == null ? 0 : Array.getLength(array);
        if (arrayDimension != expected) {
            throw new DimensionMismatchException(arrayDimension, expected);
        }
    }

    private class JacobiansSecondaryEquations implements SecondaryEquations {
        private JacobiansSecondaryEquations() {
        }

        @Override // org.apache.commons.math3.ode.SecondaryEquations
        public int getDimension() {
            return JacobianMatrices.this.stateDim * (JacobianMatrices.this.stateDim + JacobianMatrices.this.paramDim);
        }

        @Override // org.apache.commons.math3.ode.SecondaryEquations
        public void computeDerivatives(double t, double[] y, double[] yDot, double[] z, double[] zDot) throws MaxCountExceededException, DimensionMismatchException {
            if (JacobianMatrices.this.dirtyParameter && JacobianMatrices.this.paramDim != 0) {
                JacobianMatrices.this.jacobianProviders.add(new ParameterJacobianWrapper(JacobianMatrices.this.jode, JacobianMatrices.this.pode, JacobianMatrices.this.selectedParameters));
                JacobianMatrices.this.dirtyParameter = false;
            }
            double[][] dFdY = (double[][]) Array.newInstance(Double.TYPE, JacobianMatrices.this.stateDim, JacobianMatrices.this.stateDim);
            JacobianMatrices.this.jode.computeMainStateJacobian(t, y, yDot, dFdY);
            for (int i = 0; i < JacobianMatrices.this.stateDim; i++) {
                double[] dFdYi = dFdY[i];
                for (int j = 0; j < JacobianMatrices.this.stateDim; j++) {
                    double s = 0.0d;
                    int zIndex = j;
                    for (int l = 0; l < JacobianMatrices.this.stateDim; l++) {
                        s += dFdYi[l] * z[zIndex];
                        zIndex += JacobianMatrices.this.stateDim;
                    }
                    zDot[(JacobianMatrices.this.stateDim * i) + j] = s;
                }
            }
            if (JacobianMatrices.this.paramDim != 0) {
                double[] dFdP = new double[JacobianMatrices.this.stateDim];
                int startIndex = JacobianMatrices.this.stateDim * JacobianMatrices.this.stateDim;
                ParameterConfiguration[] arr$ = JacobianMatrices.this.selectedParameters;
                int len$ = arr$.length;
                for (int i$ = 0; i$ < len$; i$++) {
                    ParameterConfiguration param = arr$[i$];
                    boolean found = false;
                    int k = 0;
                    while (!found && k < JacobianMatrices.this.jacobianProviders.size()) {
                        ParameterJacobianProvider provider = (ParameterJacobianProvider) JacobianMatrices.this.jacobianProviders.get(k);
                        if (provider.isSupported(param.getParameterName())) {
                            provider.computeParameterJacobian(t, y, yDot, param.getParameterName(), dFdP);
                            for (int i2 = 0; i2 < JacobianMatrices.this.stateDim; i2++) {
                                double[] dFdYi2 = dFdY[i2];
                                int zIndex2 = startIndex;
                                double s2 = dFdP[i2];
                                for (int l2 = 0; l2 < JacobianMatrices.this.stateDim; l2++) {
                                    s2 += dFdYi2[l2] * z[zIndex2];
                                    zIndex2++;
                                }
                                zDot[startIndex + i2] = s2;
                            }
                            found = true;
                        }
                        k++;
                    }
                    if (!found) {
                        Arrays.fill(zDot, startIndex, JacobianMatrices.this.stateDim + startIndex, 0.0d);
                    }
                    startIndex += JacobianMatrices.this.stateDim;
                }
            }
        }
    }

    private static class MainStateJacobianWrapper implements MainStateJacobianProvider {

        /* renamed from: hY */
        private final double[] f251hY;
        private final FirstOrderDifferentialEquations ode;

        MainStateJacobianWrapper(FirstOrderDifferentialEquations ode2, double[] hY) throws DimensionMismatchException {
            this.ode = ode2;
            this.f251hY = (double[]) hY.clone();
            if (hY.length != ode2.getDimension()) {
                throw new DimensionMismatchException(ode2.getDimension(), hY.length);
            }
        }

        @Override // org.apache.commons.math3.ode.FirstOrderDifferentialEquations
        public int getDimension() {
            return this.ode.getDimension();
        }

        @Override // org.apache.commons.math3.ode.FirstOrderDifferentialEquations
        public void computeDerivatives(double t, double[] y, double[] yDot) throws MaxCountExceededException, DimensionMismatchException {
            this.ode.computeDerivatives(t, y, yDot);
        }

        @Override // org.apache.commons.math3.ode.MainStateJacobianProvider
        public void computeMainStateJacobian(double t, double[] y, double[] yDot, double[][] dFdY) throws MaxCountExceededException, DimensionMismatchException {
            int n = this.ode.getDimension();
            double[] tmpDot = new double[n];
            for (int j = 0; j < n; j++) {
                double savedYj = y[j];
                y[j] = y[j] + this.f251hY[j];
                this.ode.computeDerivatives(t, y, tmpDot);
                for (int i = 0; i < n; i++) {
                    dFdY[i][j] = (tmpDot[i] - yDot[i]) / this.f251hY[j];
                }
                y[j] = savedYj;
            }
        }
    }

    public static class MismatchedEquations extends MathIllegalArgumentException {
        private static final long serialVersionUID = 20120902;

        public MismatchedEquations() {
            super(LocalizedFormats.UNMATCHED_ODE_IN_EXPANDED_SET, new Object[0]);
        }
    }
}
