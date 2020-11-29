package org.apache.commons.math3.ode.sampling;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.ode.EquationsMapper;

public abstract class AbstractStepInterpolator implements StepInterpolator {
    protected double[] currentState;
    private boolean dirtyState;
    private boolean finalized;
    private boolean forward;
    private double globalCurrentTime;
    private double globalPreviousTime;

    /* renamed from: h */
    protected double f310h;
    protected double[] interpolatedDerivatives;
    protected double[] interpolatedPrimaryDerivatives;
    protected double[] interpolatedPrimaryState;
    protected double[][] interpolatedSecondaryDerivatives;
    protected double[][] interpolatedSecondaryState;
    protected double[] interpolatedState;
    protected double interpolatedTime;
    private EquationsMapper primaryMapper;
    private EquationsMapper[] secondaryMappers;
    private double softCurrentTime;
    private double softPreviousTime;

    /* access modifiers changed from: protected */
    public abstract void computeInterpolatedStateAndDerivatives(double d, double d2) throws MaxCountExceededException;

    /* access modifiers changed from: protected */
    public abstract StepInterpolator doCopy();

    @Override // java.io.Externalizable
    public abstract void readExternal(ObjectInput objectInput) throws IOException, ClassNotFoundException;

    @Override // java.io.Externalizable
    public abstract void writeExternal(ObjectOutput objectOutput) throws IOException;

    protected AbstractStepInterpolator() {
        this.globalPreviousTime = Double.NaN;
        this.globalCurrentTime = Double.NaN;
        this.softPreviousTime = Double.NaN;
        this.softCurrentTime = Double.NaN;
        this.f310h = Double.NaN;
        this.interpolatedTime = Double.NaN;
        this.currentState = null;
        this.finalized = false;
        this.forward = true;
        this.dirtyState = true;
        this.primaryMapper = null;
        this.secondaryMappers = null;
        allocateInterpolatedArrays(-1);
    }

    protected AbstractStepInterpolator(double[] y, boolean forward2, EquationsMapper primaryMapper2, EquationsMapper[] secondaryMappers2) {
        this.globalPreviousTime = Double.NaN;
        this.globalCurrentTime = Double.NaN;
        this.softPreviousTime = Double.NaN;
        this.softCurrentTime = Double.NaN;
        this.f310h = Double.NaN;
        this.interpolatedTime = Double.NaN;
        this.currentState = y;
        this.finalized = false;
        this.forward = forward2;
        this.dirtyState = true;
        this.primaryMapper = primaryMapper2;
        this.secondaryMappers = secondaryMappers2 == null ? null : (EquationsMapper[]) secondaryMappers2.clone();
        allocateInterpolatedArrays(y.length);
    }

    protected AbstractStepInterpolator(AbstractStepInterpolator interpolator) {
        this.globalPreviousTime = interpolator.globalPreviousTime;
        this.globalCurrentTime = interpolator.globalCurrentTime;
        this.softPreviousTime = interpolator.softPreviousTime;
        this.softCurrentTime = interpolator.softCurrentTime;
        this.f310h = interpolator.f310h;
        this.interpolatedTime = interpolator.interpolatedTime;
        if (interpolator.currentState == null) {
            this.currentState = null;
            this.primaryMapper = null;
            this.secondaryMappers = null;
            allocateInterpolatedArrays(-1);
        } else {
            this.currentState = (double[]) interpolator.currentState.clone();
            this.interpolatedState = (double[]) interpolator.interpolatedState.clone();
            this.interpolatedDerivatives = (double[]) interpolator.interpolatedDerivatives.clone();
            this.interpolatedPrimaryState = (double[]) interpolator.interpolatedPrimaryState.clone();
            this.interpolatedPrimaryDerivatives = (double[]) interpolator.interpolatedPrimaryDerivatives.clone();
            this.interpolatedSecondaryState = new double[interpolator.interpolatedSecondaryState.length][];
            this.interpolatedSecondaryDerivatives = new double[interpolator.interpolatedSecondaryDerivatives.length][];
            for (int i = 0; i < this.interpolatedSecondaryState.length; i++) {
                this.interpolatedSecondaryState[i] = (double[]) interpolator.interpolatedSecondaryState[i].clone();
                this.interpolatedSecondaryDerivatives[i] = (double[]) interpolator.interpolatedSecondaryDerivatives[i].clone();
            }
        }
        this.finalized = interpolator.finalized;
        this.forward = interpolator.forward;
        this.dirtyState = interpolator.dirtyState;
        this.primaryMapper = interpolator.primaryMapper;
        this.secondaryMappers = interpolator.secondaryMappers == null ? null : (EquationsMapper[]) interpolator.secondaryMappers.clone();
    }

    private void allocateInterpolatedArrays(int dimension) {
        if (dimension < 0) {
            this.interpolatedState = null;
            this.interpolatedDerivatives = null;
            this.interpolatedPrimaryState = null;
            this.interpolatedPrimaryDerivatives = null;
            this.interpolatedSecondaryState = null;
            this.interpolatedSecondaryDerivatives = null;
            return;
        }
        this.interpolatedState = new double[dimension];
        this.interpolatedDerivatives = new double[dimension];
        this.interpolatedPrimaryState = new double[this.primaryMapper.getDimension()];
        this.interpolatedPrimaryDerivatives = new double[this.primaryMapper.getDimension()];
        if (this.secondaryMappers == null) {
            this.interpolatedSecondaryState = null;
            this.interpolatedSecondaryDerivatives = null;
            return;
        }
        this.interpolatedSecondaryState = new double[this.secondaryMappers.length][];
        this.interpolatedSecondaryDerivatives = new double[this.secondaryMappers.length][];
        for (int i = 0; i < this.secondaryMappers.length; i++) {
            this.interpolatedSecondaryState[i] = new double[this.secondaryMappers[i].getDimension()];
            this.interpolatedSecondaryDerivatives[i] = new double[this.secondaryMappers[i].getDimension()];
        }
    }

    /* access modifiers changed from: protected */
    public void reinitialize(double[] y, boolean isForward, EquationsMapper primary, EquationsMapper[] secondary) {
        this.globalPreviousTime = Double.NaN;
        this.globalCurrentTime = Double.NaN;
        this.softPreviousTime = Double.NaN;
        this.softCurrentTime = Double.NaN;
        this.f310h = Double.NaN;
        this.interpolatedTime = Double.NaN;
        this.currentState = y;
        this.finalized = false;
        this.forward = isForward;
        this.dirtyState = true;
        this.primaryMapper = primary;
        this.secondaryMappers = (EquationsMapper[]) secondary.clone();
        allocateInterpolatedArrays(y.length);
    }

    @Override // org.apache.commons.math3.ode.sampling.StepInterpolator
    public StepInterpolator copy() throws MaxCountExceededException {
        finalizeStep();
        return doCopy();
    }

    public void shift() {
        this.globalPreviousTime = this.globalCurrentTime;
        this.softPreviousTime = this.globalPreviousTime;
        this.softCurrentTime = this.globalCurrentTime;
    }

    public void storeTime(double t) {
        this.globalCurrentTime = t;
        this.softCurrentTime = this.globalCurrentTime;
        this.f310h = this.globalCurrentTime - this.globalPreviousTime;
        setInterpolatedTime(t);
        this.finalized = false;
    }

    public void setSoftPreviousTime(double softPreviousTime2) {
        this.softPreviousTime = softPreviousTime2;
    }

    public void setSoftCurrentTime(double softCurrentTime2) {
        this.softCurrentTime = softCurrentTime2;
    }

    public double getGlobalPreviousTime() {
        return this.globalPreviousTime;
    }

    public double getGlobalCurrentTime() {
        return this.globalCurrentTime;
    }

    @Override // org.apache.commons.math3.ode.sampling.StepInterpolator
    public double getPreviousTime() {
        return this.softPreviousTime;
    }

    @Override // org.apache.commons.math3.ode.sampling.StepInterpolator
    public double getCurrentTime() {
        return this.softCurrentTime;
    }

    @Override // org.apache.commons.math3.ode.sampling.StepInterpolator
    public double getInterpolatedTime() {
        return this.interpolatedTime;
    }

    @Override // org.apache.commons.math3.ode.sampling.StepInterpolator
    public void setInterpolatedTime(double time) {
        this.interpolatedTime = time;
        this.dirtyState = true;
    }

    @Override // org.apache.commons.math3.ode.sampling.StepInterpolator
    public boolean isForward() {
        return this.forward;
    }

    private void evaluateCompleteInterpolatedState() throws MaxCountExceededException {
        double theta = 0.0d;
        if (this.dirtyState) {
            double oneMinusThetaH = this.globalCurrentTime - this.interpolatedTime;
            if (this.f310h != 0.0d) {
                theta = (this.f310h - oneMinusThetaH) / this.f310h;
            }
            computeInterpolatedStateAndDerivatives(theta, oneMinusThetaH);
            this.dirtyState = false;
        }
    }

    @Override // org.apache.commons.math3.ode.sampling.StepInterpolator
    public double[] getInterpolatedState() throws MaxCountExceededException {
        evaluateCompleteInterpolatedState();
        this.primaryMapper.extractEquationData(this.interpolatedState, this.interpolatedPrimaryState);
        return this.interpolatedPrimaryState;
    }

    @Override // org.apache.commons.math3.ode.sampling.StepInterpolator
    public double[] getInterpolatedDerivatives() throws MaxCountExceededException {
        evaluateCompleteInterpolatedState();
        this.primaryMapper.extractEquationData(this.interpolatedDerivatives, this.interpolatedPrimaryDerivatives);
        return this.interpolatedPrimaryDerivatives;
    }

    @Override // org.apache.commons.math3.ode.sampling.StepInterpolator
    public double[] getInterpolatedSecondaryState(int index) throws MaxCountExceededException {
        evaluateCompleteInterpolatedState();
        this.secondaryMappers[index].extractEquationData(this.interpolatedState, this.interpolatedSecondaryState[index]);
        return this.interpolatedSecondaryState[index];
    }

    @Override // org.apache.commons.math3.ode.sampling.StepInterpolator
    public double[] getInterpolatedSecondaryDerivatives(int index) throws MaxCountExceededException {
        evaluateCompleteInterpolatedState();
        this.secondaryMappers[index].extractEquationData(this.interpolatedDerivatives, this.interpolatedSecondaryDerivatives[index]);
        return this.interpolatedSecondaryDerivatives[index];
    }

    public final void finalizeStep() throws MaxCountExceededException {
        if (!this.finalized) {
            doFinalize();
            this.finalized = true;
        }
    }

    /* access modifiers changed from: protected */
    public void doFinalize() throws MaxCountExceededException {
    }

    /* access modifiers changed from: protected */
    public void writeBaseExternal(ObjectOutput out) throws IOException {
        if (this.currentState == null) {
            out.writeInt(-1);
        } else {
            out.writeInt(this.currentState.length);
        }
        out.writeDouble(this.globalPreviousTime);
        out.writeDouble(this.globalCurrentTime);
        out.writeDouble(this.softPreviousTime);
        out.writeDouble(this.softCurrentTime);
        out.writeDouble(this.f310h);
        out.writeBoolean(this.forward);
        out.writeObject(this.primaryMapper);
        out.write(this.secondaryMappers.length);
        for (EquationsMapper mapper : this.secondaryMappers) {
            out.writeObject(mapper);
        }
        if (this.currentState != null) {
            for (int i = 0; i < this.currentState.length; i++) {
                out.writeDouble(this.currentState[i]);
            }
        }
        out.writeDouble(this.interpolatedTime);
        try {
            finalizeStep();
        } catch (MaxCountExceededException mcee) {
            IOException ioe = new IOException(mcee.getLocalizedMessage());
            ioe.initCause(mcee);
            throw ioe;
        }
    }

    /* access modifiers changed from: protected */
    public double readBaseExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        int dimension = in.readInt();
        this.globalPreviousTime = in.readDouble();
        this.globalCurrentTime = in.readDouble();
        this.softPreviousTime = in.readDouble();
        this.softCurrentTime = in.readDouble();
        this.f310h = in.readDouble();
        this.forward = in.readBoolean();
        this.primaryMapper = (EquationsMapper) in.readObject();
        this.secondaryMappers = new EquationsMapper[in.read()];
        for (int i = 0; i < this.secondaryMappers.length; i++) {
            this.secondaryMappers[i] = (EquationsMapper) in.readObject();
        }
        this.dirtyState = true;
        if (dimension < 0) {
            this.currentState = null;
        } else {
            this.currentState = new double[dimension];
            for (int i2 = 0; i2 < this.currentState.length; i2++) {
                this.currentState[i2] = in.readDouble();
            }
        }
        this.interpolatedTime = Double.NaN;
        allocateInterpolatedArrays(dimension);
        this.finalized = true;
        return in.readDouble();
    }
}
