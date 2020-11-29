package org.apache.commons.math3.complex;

import java.io.Serializable;
import org.apache.commons.math3.Field;
import org.apache.commons.math3.FieldElement;

public class ComplexField implements Field<Complex>, Serializable {
    private static final long serialVersionUID = -6130362688700788798L;

    private ComplexField() {
    }

    public static ComplexField getInstance() {
        return LazyHolder.INSTANCE;
    }

    @Override // org.apache.commons.math3.Field
    public Complex getOne() {
        return Complex.ONE;
    }

    @Override // org.apache.commons.math3.Field
    public Complex getZero() {
        return Complex.ZERO;
    }

    @Override // org.apache.commons.math3.Field
    public Class<? extends FieldElement<Complex>> getRuntimeClass() {
        return Complex.class;
    }

    /* access modifiers changed from: private */
    public static class LazyHolder {
        private static final ComplexField INSTANCE = new ComplexField();

        private LazyHolder() {
        }
    }

    private Object readResolve() {
        return LazyHolder.INSTANCE;
    }
}
