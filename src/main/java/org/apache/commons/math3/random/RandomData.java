package org.apache.commons.math3.random;

import java.util.Collection;
import org.apache.commons.math3.exception.NotANumberException;
import org.apache.commons.math3.exception.NotFiniteNumberException;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.exception.NumberIsTooLargeException;

@Deprecated
public interface RandomData {
    double nextExponential(double d) throws NotStrictlyPositiveException;

    double nextGaussian(double d, double d2) throws NotStrictlyPositiveException;

    String nextHexString(int i) throws NotStrictlyPositiveException;

    int nextInt(int i, int i2) throws NumberIsTooLargeException;

    long nextLong(long j, long j2) throws NumberIsTooLargeException;

    int[] nextPermutation(int i, int i2) throws NumberIsTooLargeException, NotStrictlyPositiveException;

    long nextPoisson(double d) throws NotStrictlyPositiveException;

    Object[] nextSample(Collection<?> collection, int i) throws NumberIsTooLargeException, NotStrictlyPositiveException;

    String nextSecureHexString(int i) throws NotStrictlyPositiveException;

    int nextSecureInt(int i, int i2) throws NumberIsTooLargeException;

    long nextSecureLong(long j, long j2) throws NumberIsTooLargeException;

    double nextUniform(double d, double d2) throws NumberIsTooLargeException, NotFiniteNumberException, NotANumberException;

    double nextUniform(double d, double d2, boolean z) throws NumberIsTooLargeException, NotFiniteNumberException, NotANumberException;
}
