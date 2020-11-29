package org.apache.commons.math3.ode;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.math3.RealFieldElement;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.ode.sampling.FieldStepHandler;
import org.apache.commons.math3.ode.sampling.FieldStepInterpolator;
import org.apache.commons.math3.util.FastMath;

public class ContinuousOutputFieldModel<T extends RealFieldElement<T>> implements FieldStepHandler<T> {
    private T finalTime = null;
    private boolean forward = true;
    private int index = 0;
    private T initialTime = null;
    private List<FieldStepInterpolator<T>> steps = new ArrayList();

    public void append(ContinuousOutputFieldModel<T> model) throws MathIllegalArgumentException, MaxCountExceededException {
        if (model.steps.size() != 0) {
            if (this.steps.size() == 0) {
                this.initialTime = model.initialTime;
                this.forward = model.forward;
            } else {
                FieldODEStateAndDerivative<T> s1 = this.steps.get(0).getPreviousState();
                FieldODEStateAndDerivative<T> s2 = model.steps.get(0).getPreviousState();
                checkDimensionsEquality(s1.getStateDimension(), s2.getStateDimension());
                checkDimensionsEquality(s1.getNumberOfSecondaryStates(), s2.getNumberOfSecondaryStates());
                for (int i = 0; i < s1.getNumberOfSecondaryStates(); i++) {
                    checkDimensionsEquality(s1.getSecondaryStateDimension(i), s2.getSecondaryStateDimension(i));
                }
                if (this.forward ^ model.forward) {
                    throw new MathIllegalArgumentException(LocalizedFormats.PROPAGATION_DIRECTION_MISMATCH, new Object[0]);
                }
                FieldStepInterpolator<T> lastInterpolator = this.steps.get(this.index);
                T current = lastInterpolator.getCurrentState().getTime();
                RealFieldElement realFieldElement = (RealFieldElement) model.getInitialTime().subtract(current);
                if (((RealFieldElement) ((RealFieldElement) realFieldElement.abs()).subtract(((RealFieldElement) ((RealFieldElement) current.subtract(lastInterpolator.getPreviousState().getTime())).abs()).multiply(0.001d))).getReal() > 0.0d) {
                    throw new MathIllegalArgumentException(LocalizedFormats.HOLE_BETWEEN_MODELS_TIME_RANGES, Double.valueOf(((RealFieldElement) realFieldElement.abs()).getReal()));
                }
            }
            for (FieldStepInterpolator<T> interpolator : model.steps) {
                this.steps.add(interpolator);
            }
            this.index = this.steps.size() - 1;
            this.finalTime = this.steps.get(this.index).getCurrentState().getTime();
        }
    }

    private void checkDimensionsEquality(int d1, int d2) throws DimensionMismatchException {
        if (d1 != d2) {
            throw new DimensionMismatchException(d2, d1);
        }
    }

    @Override // org.apache.commons.math3.ode.sampling.FieldStepHandler
    public void init(FieldODEStateAndDerivative<T> initialState, T t) {
        this.initialTime = initialState.getTime();
        this.finalTime = t;
        this.forward = true;
        this.index = 0;
        this.steps.clear();
    }

    @Override // org.apache.commons.math3.ode.sampling.FieldStepHandler
    public void handleStep(FieldStepInterpolator<T> interpolator, boolean isLast) throws MaxCountExceededException {
        if (this.steps.size() == 0) {
            this.initialTime = interpolator.getPreviousState().getTime();
            this.forward = interpolator.isForward();
        }
        this.steps.add(interpolator);
        if (isLast) {
            this.finalTime = interpolator.getCurrentState().getTime();
            this.index = this.steps.size() - 1;
        }
    }

    public T getInitialTime() {
        return this.initialTime;
    }

    public T getFinalTime() {
        return this.finalTime;
    }

    public FieldODEStateAndDerivative<T> getInterpolatedState(T time) {
        int iMin = 0;
        FieldStepInterpolator<T> sMin = this.steps.get(0);
        RealFieldElement realFieldElement = (RealFieldElement) ((RealFieldElement) sMin.getPreviousState().getTime().add(sMin.getCurrentState().getTime())).multiply(0.5d);
        int iMax = this.steps.size() - 1;
        FieldStepInterpolator<T> sMax = this.steps.get(iMax);
        RealFieldElement realFieldElement2 = (RealFieldElement) ((RealFieldElement) sMax.getPreviousState().getTime().add(sMax.getCurrentState().getTime())).multiply(0.5d);
        if (locatePoint(time, sMin) <= 0) {
            this.index = 0;
            return sMin.getInterpolatedState(time);
        } else if (locatePoint(time, sMax) >= 0) {
            this.index = iMax;
            return sMax.getInterpolatedState(time);
        } else {
            while (iMax - iMin > 5) {
                FieldStepInterpolator<T> si = this.steps.get(this.index);
                int location = locatePoint(time, si);
                if (location < 0) {
                    iMax = this.index;
                    realFieldElement2 = (RealFieldElement) ((RealFieldElement) si.getPreviousState().getTime().add(si.getCurrentState().getTime())).multiply(0.5d);
                } else if (location <= 0) {
                    return si.getInterpolatedState(time);
                } else {
                    iMin = this.index;
                    realFieldElement = (RealFieldElement) ((RealFieldElement) si.getPreviousState().getTime().add(si.getCurrentState().getTime())).multiply(0.5d);
                }
                int iMed = (iMin + iMax) / 2;
                FieldStepInterpolator<T> sMed = this.steps.get(iMed);
                RealFieldElement realFieldElement3 = (RealFieldElement) ((RealFieldElement) sMed.getPreviousState().getTime().add(sMed.getCurrentState().getTime())).multiply(0.5d);
                if (((RealFieldElement) ((RealFieldElement) ((RealFieldElement) realFieldElement3.subtract(realFieldElement)).abs()).subtract(1.0E-6d)).getReal() < 0.0d || ((RealFieldElement) ((RealFieldElement) ((RealFieldElement) realFieldElement2.subtract(realFieldElement3)).abs()).subtract(1.0E-6d)).getReal() < 0.0d) {
                    this.index = iMed;
                } else {
                    RealFieldElement realFieldElement4 = (RealFieldElement) realFieldElement2.subtract(realFieldElement3);
                    RealFieldElement realFieldElement5 = (RealFieldElement) realFieldElement3.subtract(realFieldElement);
                    RealFieldElement realFieldElement6 = (RealFieldElement) realFieldElement2.subtract(realFieldElement);
                    RealFieldElement realFieldElement7 = (RealFieldElement) time.subtract(realFieldElement2);
                    RealFieldElement realFieldElement8 = (RealFieldElement) time.subtract(realFieldElement3);
                    RealFieldElement realFieldElement9 = (RealFieldElement) time.subtract(realFieldElement);
                    this.index = (int) FastMath.rint(((RealFieldElement) ((RealFieldElement) ((RealFieldElement) ((RealFieldElement) ((RealFieldElement) ((RealFieldElement) realFieldElement8.multiply(realFieldElement9)).multiply(realFieldElement5)).multiply(iMax)).subtract(((RealFieldElement) ((RealFieldElement) realFieldElement7.multiply(realFieldElement9)).multiply(realFieldElement6)).multiply(iMed))).add(((RealFieldElement) ((RealFieldElement) realFieldElement7.multiply(realFieldElement8)).multiply(realFieldElement4)).multiply(iMin))).divide(((RealFieldElement) realFieldElement4.multiply(realFieldElement5)).multiply(realFieldElement6))).getReal());
                }
                int low = FastMath.max(iMin + 1, ((iMin * 9) + iMax) / 10);
                int high = FastMath.min(iMax - 1, ((iMax * 9) + iMin) / 10);
                if (this.index < low) {
                    this.index = low;
                } else if (this.index > high) {
                    this.index = high;
                }
            }
            this.index = iMin;
            while (this.index <= iMax && locatePoint(time, this.steps.get(this.index)) > 0) {
                this.index++;
            }
            return this.steps.get(this.index).getInterpolatedState(time);
        }
    }

    private int locatePoint(T time, FieldStepInterpolator<T> interval) {
        if (this.forward) {
            if (((RealFieldElement) time.subtract(interval.getPreviousState().getTime())).getReal() < 0.0d) {
                return -1;
            }
            return ((RealFieldElement) time.subtract(interval.getCurrentState().getTime())).getReal() > 0.0d ? 1 : 0;
        } else if (((RealFieldElement) time.subtract(interval.getPreviousState().getTime())).getReal() > 0.0d) {
            return -1;
        } else {
            return ((RealFieldElement) time.subtract(interval.getCurrentState().getTime())).getReal() < 0.0d ? 1 : 0;
        }
    }
}
