package org.apache.commons.math3.stat.descriptive;

import org.apache.commons.math3.exception.MathIllegalArgumentException;

public interface StorelessUnivariateStatistic extends UnivariateStatistic {
    void clear();

    @Override // org.apache.commons.math3.stat.descriptive.UnivariateStatistic
    StorelessUnivariateStatistic copy();

    long getN();

    double getResult();

    void increment(double d);

    void incrementAll(double[] dArr) throws MathIllegalArgumentException;

    void incrementAll(double[] dArr, int i, int i2) throws MathIllegalArgumentException;
}
