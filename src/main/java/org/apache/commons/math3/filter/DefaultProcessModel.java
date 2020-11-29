package org.apache.commons.math3.filter;

import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.NoDataException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

public class DefaultProcessModel implements ProcessModel {
    private RealMatrix controlMatrix;
    private RealMatrix initialErrorCovMatrix;
    private RealVector initialStateEstimateVector;
    private RealMatrix processNoiseCovMatrix;
    private RealMatrix stateTransitionMatrix;

    public DefaultProcessModel(double[][] stateTransition, double[][] control, double[][] processNoise, double[] initialStateEstimate, double[][] initialErrorCovariance) throws NullArgumentException, NoDataException, DimensionMismatchException {
        this(new Array2DRowRealMatrix(stateTransition), new Array2DRowRealMatrix(control), new Array2DRowRealMatrix(processNoise), new ArrayRealVector(initialStateEstimate), new Array2DRowRealMatrix(initialErrorCovariance));
    }

    public DefaultProcessModel(double[][] stateTransition, double[][] control, double[][] processNoise) throws NullArgumentException, NoDataException, DimensionMismatchException {
        this(new Array2DRowRealMatrix(stateTransition), new Array2DRowRealMatrix(control), new Array2DRowRealMatrix(processNoise), (RealVector) null, (RealMatrix) null);
    }

    public DefaultProcessModel(RealMatrix stateTransition, RealMatrix control, RealMatrix processNoise, RealVector initialStateEstimate, RealMatrix initialErrorCovariance) {
        this.stateTransitionMatrix = stateTransition;
        this.controlMatrix = control;
        this.processNoiseCovMatrix = processNoise;
        this.initialStateEstimateVector = initialStateEstimate;
        this.initialErrorCovMatrix = initialErrorCovariance;
    }

    @Override // org.apache.commons.math3.filter.ProcessModel
    public RealMatrix getStateTransitionMatrix() {
        return this.stateTransitionMatrix;
    }

    @Override // org.apache.commons.math3.filter.ProcessModel
    public RealMatrix getControlMatrix() {
        return this.controlMatrix;
    }

    @Override // org.apache.commons.math3.filter.ProcessModel
    public RealMatrix getProcessNoise() {
        return this.processNoiseCovMatrix;
    }

    @Override // org.apache.commons.math3.filter.ProcessModel
    public RealVector getInitialStateEstimate() {
        return this.initialStateEstimateVector;
    }

    @Override // org.apache.commons.math3.filter.ProcessModel
    public RealMatrix getInitialErrorCovariance() {
        return this.initialErrorCovMatrix;
    }
}
