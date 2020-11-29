package org.apache.commons.math3.util;

import java.io.Serializable;
import org.apache.commons.math3.Field;
import org.apache.commons.math3.FieldElement;

public class BigRealField implements Field<BigReal>, Serializable {
    private static final long serialVersionUID = 4756431066541037559L;

    private BigRealField() {
    }

    public static BigRealField getInstance() {
        return LazyHolder.INSTANCE;
    }

    @Override // org.apache.commons.math3.Field
    public BigReal getOne() {
        return BigReal.ONE;
    }

    @Override // org.apache.commons.math3.Field
    public BigReal getZero() {
        return BigReal.ZERO;
    }

    @Override // org.apache.commons.math3.Field
    public Class<? extends FieldElement<BigReal>> getRuntimeClass() {
        return BigReal.class;
    }

    private static class LazyHolder {
        private static final BigRealField INSTANCE = new BigRealField();

        private LazyHolder() {
        }
    }

    private Object readResolve() {
        return LazyHolder.INSTANCE;
    }
}
