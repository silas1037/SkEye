package org.apache.commons.math3.ode;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.math3.RealFieldElement;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.util.MathArrays;

public class FieldExpandableODE<T extends RealFieldElement<T>> {
    private List<FieldSecondaryEquations<T>> components = new ArrayList();
    private FieldEquationsMapper<T> mapper;
    private final FirstOrderFieldDifferentialEquations<T> primary;

    public FieldExpandableODE(FirstOrderFieldDifferentialEquations<T> primary2) {
        this.primary = primary2;
        this.mapper = new FieldEquationsMapper<>(null, primary2.getDimension());
    }

    public FieldEquationsMapper<T> getMapper() {
        return this.mapper;
    }

    public int addSecondaryEquations(FieldSecondaryEquations<T> secondary) {
        this.components.add(secondary);
        this.mapper = new FieldEquationsMapper<>(this.mapper, secondary.getDimension());
        return this.components.size();
    }

    public void init(T t0, T[] y0, T finalTime) {
        int index = 0;
        T[] primary0 = this.mapper.extractEquationData(0, y0);
        this.primary.init(t0, primary0, finalTime);
        while (true) {
            index++;
            if (index < this.mapper.getNumberOfEquations()) {
                this.components.get(index - 1).init(t0, primary0, this.mapper.extractEquationData(index, y0), finalTime);
            } else {
                return;
            }
        }
    }

    public T[] computeDerivatives(T t, T[] y) throws MaxCountExceededException, DimensionMismatchException {
        T[] yDot = (T[]) ((RealFieldElement[]) MathArrays.buildArray(t.getField(), this.mapper.getTotalDimension()));
        int index = 0;
        T[] primaryState = this.mapper.extractEquationData(0, y);
        T[] primaryStateDot = this.primary.computeDerivatives(t, primaryState);
        this.mapper.insertEquationData(0, primaryStateDot, yDot);
        while (true) {
            index++;
            if (index >= this.mapper.getNumberOfEquations()) {
                return yDot;
            }
            this.mapper.insertEquationData(index, this.components.get(index - 1).computeDerivatives(t, primaryState, primaryStateDot, this.mapper.extractEquationData(index, y)), yDot);
        }
    }
}
