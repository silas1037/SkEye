package org.apache.commons.math3.ode;

import java.io.Serializable;
import org.apache.commons.math3.RealFieldElement;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.util.MathArrays;

public class FieldEquationsMapper<T extends RealFieldElement<T>> implements Serializable {
    private static final long serialVersionUID = 20151114;
    private final int[] start;

    FieldEquationsMapper(FieldEquationsMapper<T> mapper, int dimension) {
        int index = mapper == null ? 0 : mapper.getNumberOfEquations();
        this.start = new int[(index + 2)];
        if (mapper == null) {
            this.start[0] = 0;
        } else {
            System.arraycopy(mapper.start, 0, this.start, 0, index + 1);
        }
        this.start[index + 1] = this.start[index] + dimension;
    }

    public int getNumberOfEquations() {
        return this.start.length - 1;
    }

    public int getTotalDimension() {
        return this.start[this.start.length - 1];
    }

    public T[] mapState(FieldODEState<T> state) {
        T[] y = (T[]) ((RealFieldElement[]) MathArrays.buildArray(state.getTime().getField(), getTotalDimension()));
        int index = 0;
        insertEquationData(0, state.getState(), y);
        while (true) {
            index++;
            if (index >= getNumberOfEquations()) {
                return y;
            }
            insertEquationData(index, state.getSecondaryState(index), y);
        }
    }

    public T[] mapDerivative(FieldODEStateAndDerivative<T> state) {
        T[] yDot = (T[]) ((RealFieldElement[]) MathArrays.buildArray(state.getTime().getField(), getTotalDimension()));
        int index = 0;
        insertEquationData(0, state.getDerivative(), yDot);
        while (true) {
            index++;
            if (index >= getNumberOfEquations()) {
                return yDot;
            }
            insertEquationData(index, state.getSecondaryDerivative(index), yDot);
        }
    }

    public FieldODEStateAndDerivative<T> mapStateAndDerivative(T t, T[] y, T[] yDot) throws DimensionMismatchException {
        if (y.length != getTotalDimension()) {
            throw new DimensionMismatchException(y.length, getTotalDimension());
        } else if (yDot.length != getTotalDimension()) {
            throw new DimensionMismatchException(yDot.length, getTotalDimension());
        } else {
            int n = getNumberOfEquations();
            int index = 0;
            T[] state = extractEquationData(0, y);
            T[] derivative = extractEquationData(0, yDot);
            if (n < 2) {
                return new FieldODEStateAndDerivative<>(t, state, derivative);
            }
            RealFieldElement[][] realFieldElementArr = (RealFieldElement[][]) MathArrays.buildArray(t.getField(), n - 1, -1);
            RealFieldElement[][] realFieldElementArr2 = (RealFieldElement[][]) MathArrays.buildArray(t.getField(), n - 1, -1);
            while (true) {
                index++;
                if (index >= getNumberOfEquations()) {
                    return new FieldODEStateAndDerivative<>(t, state, derivative, realFieldElementArr, realFieldElementArr2);
                }
                realFieldElementArr[index - 1] = extractEquationData(index, y);
                realFieldElementArr2[index - 1] = extractEquationData(index, yDot);
            }
        }
    }

    public T[] extractEquationData(int index, T[] complete) throws MathIllegalArgumentException, DimensionMismatchException {
        checkIndex(index);
        int begin = this.start[index];
        int end = this.start[index + 1];
        if (complete.length < end) {
            throw new DimensionMismatchException(complete.length, end);
        }
        int dimension = end - begin;
        T[] equationData = (T[]) ((RealFieldElement[]) MathArrays.buildArray(complete[0].getField(), dimension));
        System.arraycopy(complete, begin, equationData, 0, dimension);
        return equationData;
    }

    public void insertEquationData(int index, T[] equationData, T[] complete) throws DimensionMismatchException {
        checkIndex(index);
        int begin = this.start[index];
        int end = this.start[index + 1];
        int dimension = end - begin;
        if (complete.length < end) {
            throw new DimensionMismatchException(complete.length, end);
        } else if (equationData.length != dimension) {
            throw new DimensionMismatchException(equationData.length, dimension);
        } else {
            System.arraycopy(equationData, 0, complete, begin, dimension);
        }
    }

    private void checkIndex(int index) throws MathIllegalArgumentException {
        if (index < 0 || index > this.start.length - 2) {
            throw new MathIllegalArgumentException(LocalizedFormats.ARGUMENT_OUTSIDE_DOMAIN, Integer.valueOf(index), 0, Integer.valueOf(this.start.length - 2));
        }
    }
}
