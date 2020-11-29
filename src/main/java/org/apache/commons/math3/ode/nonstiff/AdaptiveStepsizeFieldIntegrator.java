package org.apache.commons.math3.ode.nonstiff;

import org.apache.commons.math3.Field;
import org.apache.commons.math3.RealFieldElement;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.ode.AbstractFieldIntegrator;
import org.apache.commons.math3.ode.FieldEquationsMapper;
import org.apache.commons.math3.ode.FieldODEState;
import org.apache.commons.math3.ode.FieldODEStateAndDerivative;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.MathArrays;
import org.apache.commons.math3.util.MathUtils;

public abstract class AdaptiveStepsizeFieldIntegrator<T extends RealFieldElement<T>> extends AbstractFieldIntegrator<T> {
    private T initialStep;
    protected int mainSetDimension;
    private T maxStep;
    private T minStep;
    protected double scalAbsoluteTolerance;
    protected double scalRelativeTolerance;
    protected double[] vecAbsoluteTolerance;
    protected double[] vecRelativeTolerance;

    public AdaptiveStepsizeFieldIntegrator(Field<T> field, String name, double minStep2, double maxStep2, double scalAbsoluteTolerance2, double scalRelativeTolerance2) {
        super(field, name);
        setStepSizeControl(minStep2, maxStep2, scalAbsoluteTolerance2, scalRelativeTolerance2);
        resetInternalState();
    }

    public AdaptiveStepsizeFieldIntegrator(Field<T> field, String name, double minStep2, double maxStep2, double[] vecAbsoluteTolerance2, double[] vecRelativeTolerance2) {
        super(field, name);
        setStepSizeControl(minStep2, maxStep2, vecAbsoluteTolerance2, vecRelativeTolerance2);
        resetInternalState();
    }

    public void setStepSizeControl(double minimalStep, double maximalStep, double absoluteTolerance, double relativeTolerance) {
        this.minStep = (T) ((RealFieldElement) getField().getZero().add(FastMath.abs(minimalStep)));
        this.maxStep = (T) ((RealFieldElement) getField().getZero().add(FastMath.abs(maximalStep)));
        this.initialStep = (T) ((RealFieldElement) getField().getOne().negate());
        this.scalAbsoluteTolerance = absoluteTolerance;
        this.scalRelativeTolerance = relativeTolerance;
        this.vecAbsoluteTolerance = null;
        this.vecRelativeTolerance = null;
    }

    public void setStepSizeControl(double minimalStep, double maximalStep, double[] absoluteTolerance, double[] relativeTolerance) {
        this.minStep = (T) ((RealFieldElement) getField().getZero().add(FastMath.abs(minimalStep)));
        this.maxStep = (T) ((RealFieldElement) getField().getZero().add(FastMath.abs(maximalStep)));
        this.initialStep = (T) ((RealFieldElement) getField().getOne().negate());
        this.scalAbsoluteTolerance = 0.0d;
        this.scalRelativeTolerance = 0.0d;
        this.vecAbsoluteTolerance = (double[]) absoluteTolerance.clone();
        this.vecRelativeTolerance = (double[]) relativeTolerance.clone();
    }

    public void setInitialStepSize(T initialStepSize) {
        if (((RealFieldElement) initialStepSize.subtract(this.minStep)).getReal() < 0.0d || ((RealFieldElement) initialStepSize.subtract(this.maxStep)).getReal() > 0.0d) {
            this.initialStep = (T) ((RealFieldElement) getField().getOne().negate());
        } else {
            this.initialStep = initialStepSize;
        }
    }

    /* access modifiers changed from: protected */
    @Override // org.apache.commons.math3.ode.AbstractFieldIntegrator
    public void sanityChecks(FieldODEState<T> eqn, T t) throws DimensionMismatchException, NumberIsTooSmallException {
        super.sanityChecks(eqn, t);
        this.mainSetDimension = eqn.getStateDimension();
        if (this.vecAbsoluteTolerance != null && this.vecAbsoluteTolerance.length != this.mainSetDimension) {
            throw new DimensionMismatchException(this.mainSetDimension, this.vecAbsoluteTolerance.length);
        } else if (this.vecRelativeTolerance != null && this.vecRelativeTolerance.length != this.mainSetDimension) {
            throw new DimensionMismatchException(this.mainSetDimension, this.vecRelativeTolerance.length);
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for r24v0, resolved type: org.apache.commons.math3.ode.nonstiff.AdaptiveStepsizeFieldIntegrator<T extends org.apache.commons.math3.RealFieldElement<T>> */
    /* JADX WARN: Multi-variable type inference failed */
    public T initializeStep(boolean forward, int order, T[] scale, FieldODEStateAndDerivative<T> state0, FieldEquationsMapper<T> mapper) throws MaxCountExceededException, DimensionMismatchException {
        RealFieldElement realFieldElement;
        if (this.initialStep.getReal() > 0.0d) {
            return forward ? this.initialStep : (T) ((RealFieldElement) this.initialStep.negate());
        }
        T[] y0 = mapper.mapState(state0);
        T[] yDot0 = mapper.mapDerivative(state0);
        RealFieldElement realFieldElement2 = (RealFieldElement) getField().getZero();
        RealFieldElement realFieldElement3 = (RealFieldElement) getField().getZero();
        for (int j = 0; j < scale.length; j++) {
            RealFieldElement realFieldElement4 = (RealFieldElement) y0[j].divide(scale[j]);
            realFieldElement2 = (RealFieldElement) realFieldElement2.add(realFieldElement4.multiply(realFieldElement4));
            RealFieldElement realFieldElement5 = (RealFieldElement) yDot0[j].divide(scale[j]);
            realFieldElement3 = (RealFieldElement) realFieldElement3.add(realFieldElement5.multiply(realFieldElement5));
        }
        if (realFieldElement2.getReal() < 1.0E-10d || realFieldElement3.getReal() < 1.0E-10d) {
            realFieldElement = (RealFieldElement) ((RealFieldElement) getField().getZero()).add(1.0E-6d);
        } else {
            realFieldElement = (RealFieldElement) ((RealFieldElement) ((RealFieldElement) realFieldElement2.divide(realFieldElement3)).sqrt()).multiply(0.01d);
        }
        if (!forward) {
            realFieldElement = (RealFieldElement) realFieldElement.negate();
        }
        RealFieldElement[] realFieldElementArr = (RealFieldElement[]) MathArrays.buildArray(getField(), y0.length);
        for (int j2 = 0; j2 < y0.length; j2++) {
            realFieldElementArr[j2] = (RealFieldElement) y0[j2].add(yDot0[j2].multiply(realFieldElement));
        }
        T[] yDot1 = computeDerivatives((RealFieldElement) state0.getTime().add(realFieldElement), realFieldElementArr);
        RealFieldElement realFieldElement6 = (RealFieldElement) getField().getZero();
        for (int j3 = 0; j3 < scale.length; j3++) {
            RealFieldElement realFieldElement7 = (RealFieldElement) ((RealFieldElement) yDot1[j3].subtract(yDot0[j3])).divide(scale[j3]);
            realFieldElement6 = (RealFieldElement) realFieldElement6.add(realFieldElement7.multiply(realFieldElement7));
        }
        RealFieldElement max = MathUtils.max((RealFieldElement) realFieldElement3.sqrt(), (RealFieldElement) ((RealFieldElement) realFieldElement6.sqrt()).divide(realFieldElement));
        T h = (T) MathUtils.max(this.minStep, MathUtils.min(this.maxStep, MathUtils.max(MathUtils.min((RealFieldElement) ((RealFieldElement) realFieldElement.abs()).multiply(100), max.getReal() < 1.0E-15d ? MathUtils.max((RealFieldElement) ((RealFieldElement) getField().getZero()).add(1.0E-6d), (RealFieldElement) ((RealFieldElement) realFieldElement.abs()).multiply(0.001d)) : (RealFieldElement) ((RealFieldElement) ((RealFieldElement) max.multiply(100)).reciprocal()).pow(1.0d / ((double) order))), (RealFieldElement) ((RealFieldElement) state0.getTime().abs()).multiply(1.0E-12d))));
        if (!forward) {
            h = (T) ((RealFieldElement) h.negate());
        }
        return h;
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r1v19, types: [org.apache.commons.math3.RealFieldElement] */
    /* access modifiers changed from: protected */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public T filterStep(T r7, boolean r8, boolean r9) throws org.apache.commons.math3.exception.NumberIsTooSmallException {
        /*
        // Method dump skipped, instructions count: 118
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.math3.ode.nonstiff.AdaptiveStepsizeFieldIntegrator.filterStep(org.apache.commons.math3.RealFieldElement, boolean, boolean):org.apache.commons.math3.RealFieldElement");
    }

    /* JADX DEBUG: Multi-variable search result rejected for r2v0, resolved type: org.apache.commons.math3.ode.nonstiff.AdaptiveStepsizeFieldIntegrator<T extends org.apache.commons.math3.RealFieldElement<T>> */
    /* JADX WARN: Multi-variable type inference failed */
    /* access modifiers changed from: protected */
    public void resetInternalState() {
        setStepStart(null);
        setStepSize((RealFieldElement) ((RealFieldElement) this.minStep.multiply(this.maxStep)).sqrt());
    }

    public T getMinStep() {
        return this.minStep;
    }

    public T getMaxStep() {
        return this.maxStep;
    }
}
