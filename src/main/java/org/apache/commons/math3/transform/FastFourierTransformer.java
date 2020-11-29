package org.apache.commons.math3.transform;

import java.io.Serializable;
import java.lang.reflect.Array;
import org.apache.commons.math3.analysis.FunctionUtils;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.MathIllegalStateException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.util.ArithmeticUtils;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.MathArrays;

public class FastFourierTransformer implements Serializable {
    static final /* synthetic */ boolean $assertionsDisabled = (!FastFourierTransformer.class.desiredAssertionStatus() ? true : $assertionsDisabled);
    private static final double[] W_SUB_N_I = {2.4492935982947064E-16d, -1.2246467991473532E-16d, -1.0d, -0.7071067811865475d, -0.3826834323650898d, -0.19509032201612825d, -0.0980171403295606d, -0.049067674327418015d, -0.024541228522912288d, -0.012271538285719925d, -0.006135884649154475d, -0.003067956762965976d, -0.0015339801862847655d, -7.669903187427045E-4d, -3.8349518757139556E-4d, -1.917475973107033E-4d, -9.587379909597734E-5d, -4.793689960306688E-5d, -2.396844980841822E-5d, -1.1984224905069705E-5d, -5.9921124526424275E-6d, -2.996056226334661E-6d, -1.4980281131690111E-6d, -7.490140565847157E-7d, -3.7450702829238413E-7d, -1.8725351414619535E-7d, -9.362675707309808E-8d, -4.681337853654909E-8d, -2.340668926827455E-8d, -1.1703344634137277E-8d, -5.8516723170686385E-9d, -2.9258361585343192E-9d, -1.4629180792671596E-9d, -7.314590396335798E-10d, -3.657295198167899E-10d, -1.8286475990839495E-10d, -9.143237995419748E-11d, -4.571618997709874E-11d, -2.285809498854937E-11d, -1.1429047494274685E-11d, -5.714523747137342E-12d, -2.857261873568671E-12d, -1.4286309367843356E-12d, -7.143154683921678E-13d, -3.571577341960839E-13d, -1.7857886709804195E-13d, -8.928943354902097E-14d, -4.4644716774510487E-14d, -2.2322358387255243E-14d, -1.1161179193627622E-14d, -5.580589596813811E-15d, -2.7902947984069054E-15d, -1.3951473992034527E-15d, -6.975736996017264E-16d, -3.487868498008632E-16d, -1.743934249004316E-16d, -8.71967124502158E-17d, -4.35983562251079E-17d, -2.179917811255395E-17d, -1.0899589056276974E-17d, -5.449794528138487E-18d, -2.7248972640692436E-18d, -1.3624486320346218E-18d};
    private static final double[] W_SUB_N_R = {1.0d, -1.0d, 6.123233995736766E-17d, 0.7071067811865476d, 0.9238795325112867d, 0.9807852804032304d, 0.9951847266721969d, 0.9987954562051724d, 0.9996988186962042d, 0.9999247018391445d, 0.9999811752826011d, 0.9999952938095762d, 0.9999988234517019d, 0.9999997058628822d, 0.9999999264657179d, 0.9999999816164293d, 0.9999999954041073d, 0.9999999988510269d, 0.9999999997127567d, 0.9999999999281892d, 0.9999999999820472d, 0.9999999999955118d, 0.999999999998878d, 0.9999999999997194d, 0.9999999999999298d, 0.9999999999999825d, 0.9999999999999957d, 0.9999999999999989d, 0.9999999999999998d, 0.9999999999999999d, 1.0d, 1.0d, 1.0d, 1.0d, 1.0d, 1.0d, 1.0d, 1.0d, 1.0d, 1.0d, 1.0d, 1.0d, 1.0d, 1.0d, 1.0d, 1.0d, 1.0d, 1.0d, 1.0d, 1.0d, 1.0d, 1.0d, 1.0d, 1.0d, 1.0d, 1.0d, 1.0d, 1.0d, 1.0d, 1.0d, 1.0d, 1.0d, 1.0d};
    static final long serialVersionUID = 20120210;
    private final DftNormalization normalization;

    public FastFourierTransformer(DftNormalization normalization2) {
        this.normalization = normalization2;
    }

    private static void bitReversalShuffle2(double[] a, double[] b) {
        int n = a.length;
        if ($assertionsDisabled || b.length == n) {
            int halfOfN = n >> 1;
            int j = 0;
            for (int i = 0; i < n; i++) {
                if (i < j) {
                    double temp = a[i];
                    a[i] = a[j];
                    a[j] = temp;
                    double temp2 = b[i];
                    b[i] = b[j];
                    b[j] = temp2;
                }
                int k = halfOfN;
                while (k <= j && k > 0) {
                    j -= k;
                    k >>= 1;
                }
                j += k;
            }
            return;
        }
        throw new AssertionError();
    }

    private static void normalizeTransformedData(double[][] dataRI, DftNormalization normalization2, TransformType type) {
        double[] dataR = dataRI[0];
        double[] dataI = dataRI[1];
        int n = dataR.length;
        if ($assertionsDisabled || dataI.length == n) {
            switch (normalization2) {
                case STANDARD:
                    if (type == TransformType.INVERSE) {
                        double scaleFactor = 1.0d / ((double) n);
                        for (int i = 0; i < n; i++) {
                            dataR[i] = dataR[i] * scaleFactor;
                            dataI[i] = dataI[i] * scaleFactor;
                        }
                        return;
                    }
                    return;
                case UNITARY:
                    double scaleFactor2 = 1.0d / FastMath.sqrt((double) n);
                    for (int i2 = 0; i2 < n; i2++) {
                        dataR[i2] = dataR[i2] * scaleFactor2;
                        dataI[i2] = dataI[i2] * scaleFactor2;
                    }
                    return;
                default:
                    throw new MathIllegalStateException();
            }
        } else {
            throw new AssertionError();
        }
    }

    public static void transformInPlace(double[][] dataRI, DftNormalization normalization2, TransformType type) {
        if (dataRI.length != 2) {
            throw new DimensionMismatchException(dataRI.length, 2);
        }
        double[] dataR = dataRI[0];
        double[] dataI = dataRI[1];
        if (dataR.length != dataI.length) {
            throw new DimensionMismatchException(dataI.length, dataR.length);
        }
        int n = dataR.length;
        if (!ArithmeticUtils.isPowerOfTwo((long) n)) {
            throw new MathIllegalArgumentException(LocalizedFormats.NOT_POWER_OF_TWO_CONSIDER_PADDING, Integer.valueOf(n));
        } else if (n != 1) {
            if (n == 2) {
                double srcR0 = dataR[0];
                double srcI0 = dataI[0];
                double srcR1 = dataR[1];
                double srcI1 = dataI[1];
                dataR[0] = srcR0 + srcR1;
                dataI[0] = srcI0 + srcI1;
                dataR[1] = srcR0 - srcR1;
                dataI[1] = srcI0 - srcI1;
                normalizeTransformedData(dataRI, normalization2, type);
                return;
            }
            bitReversalShuffle2(dataR, dataI);
            if (type == TransformType.INVERSE) {
                for (int i0 = 0; i0 < n; i0 += 4) {
                    int i1 = i0 + 1;
                    int i2 = i0 + 2;
                    int i3 = i0 + 3;
                    double srcR02 = dataR[i0];
                    double srcI02 = dataI[i0];
                    double srcR12 = dataR[i2];
                    double srcI12 = dataI[i2];
                    double srcR2 = dataR[i1];
                    double srcI2 = dataI[i1];
                    double srcR3 = dataR[i3];
                    double srcI3 = dataI[i3];
                    dataR[i0] = srcR02 + srcR12 + srcR2 + srcR3;
                    dataI[i0] = srcI02 + srcI12 + srcI2 + srcI3;
                    dataR[i1] = (srcR02 - srcR2) + (srcI3 - srcI12);
                    dataI[i1] = (srcI02 - srcI2) + (srcR12 - srcR3);
                    dataR[i2] = ((srcR02 - srcR12) + srcR2) - srcR3;
                    dataI[i2] = ((srcI02 - srcI12) + srcI2) - srcI3;
                    dataR[i3] = (srcR02 - srcR2) + (srcI12 - srcI3);
                    dataI[i3] = (srcI02 - srcI2) + (srcR3 - srcR12);
                }
            } else {
                for (int i02 = 0; i02 < n; i02 += 4) {
                    int i12 = i02 + 1;
                    int i22 = i02 + 2;
                    int i32 = i02 + 3;
                    double srcR03 = dataR[i02];
                    double srcI03 = dataI[i02];
                    double srcR13 = dataR[i22];
                    double srcI13 = dataI[i22];
                    double srcR22 = dataR[i12];
                    double srcI22 = dataI[i12];
                    double srcR32 = dataR[i32];
                    double srcI32 = dataI[i32];
                    dataR[i02] = srcR03 + srcR13 + srcR22 + srcR32;
                    dataI[i02] = srcI03 + srcI13 + srcI22 + srcI32;
                    dataR[i12] = (srcR03 - srcR22) + (srcI13 - srcI32);
                    dataI[i12] = (srcI03 - srcI22) + (srcR32 - srcR13);
                    dataR[i22] = ((srcR03 - srcR13) + srcR22) - srcR32;
                    dataI[i22] = ((srcI03 - srcI13) + srcI22) - srcI32;
                    dataR[i32] = (srcR03 - srcR22) + (srcI32 - srcI13);
                    dataI[i32] = (srcI03 - srcI22) + (srcR13 - srcR32);
                }
            }
            int lastN0 = 4;
            int lastLogN0 = 2;
            while (lastN0 < n) {
                int n0 = lastN0 << 1;
                int logN0 = lastLogN0 + 1;
                double wSubN0R = W_SUB_N_R[logN0];
                double wSubN0I = W_SUB_N_I[logN0];
                if (type == TransformType.INVERSE) {
                    wSubN0I = -wSubN0I;
                }
                for (int destEvenStartIndex = 0; destEvenStartIndex < n; destEvenStartIndex += n0) {
                    int destOddStartIndex = destEvenStartIndex + lastN0;
                    double wSubN0ToRR = 1.0d;
                    double wSubN0ToRI = 0.0d;
                    for (int r = 0; r < lastN0; r++) {
                        double grR = dataR[destEvenStartIndex + r];
                        double grI = dataI[destEvenStartIndex + r];
                        double hrR = dataR[destOddStartIndex + r];
                        double hrI = dataI[destOddStartIndex + r];
                        dataR[destEvenStartIndex + r] = ((wSubN0ToRR * hrR) + grR) - (wSubN0ToRI * hrI);
                        dataI[destEvenStartIndex + r] = (wSubN0ToRR * hrI) + grI + (wSubN0ToRI * hrR);
                        dataR[destOddStartIndex + r] = grR - ((wSubN0ToRR * hrR) - (wSubN0ToRI * hrI));
                        dataI[destOddStartIndex + r] = grI - ((wSubN0ToRR * hrI) + (wSubN0ToRI * hrR));
                        wSubN0ToRR = (wSubN0ToRR * wSubN0R) - (wSubN0ToRI * wSubN0I);
                        wSubN0ToRI = (wSubN0ToRR * wSubN0I) + (wSubN0ToRI * wSubN0R);
                    }
                }
                lastN0 = n0;
                lastLogN0 = logN0;
            }
            normalizeTransformedData(dataRI, normalization2, type);
        }
    }

    public Complex[] transform(double[] f, TransformType type) {
        double[][] dataRI = {MathArrays.copyOf(f, f.length), new double[f.length]};
        transformInPlace(dataRI, this.normalization, type);
        return TransformUtils.createComplexArray(dataRI);
    }

    public Complex[] transform(UnivariateFunction f, double min, double max, int n, TransformType type) {
        return transform(FunctionUtils.sample(f, min, max, n), type);
    }

    public Complex[] transform(Complex[] f, TransformType type) {
        double[][] dataRI = TransformUtils.createRealImaginaryArray(f);
        transformInPlace(dataRI, this.normalization, type);
        return TransformUtils.createComplexArray(dataRI);
    }

    @Deprecated
    public Object mdfft(Object mdca, TransformType type) {
        MultiDimensionalComplexMatrix mdcm = (MultiDimensionalComplexMatrix) new MultiDimensionalComplexMatrix(mdca).clone();
        int[] dimensionSize = mdcm.getDimensionSizes();
        for (int i = 0; i < dimensionSize.length; i++) {
            mdfft(mdcm, type, i, new int[0]);
        }
        return mdcm.getArray();
    }

    @Deprecated
    private void mdfft(MultiDimensionalComplexMatrix mdcm, TransformType type, int d, int[] subVector) {
        int[] dimensionSize = mdcm.getDimensionSizes();
        if (subVector.length == dimensionSize.length) {
            Complex[] temp = new Complex[dimensionSize[d]];
            for (int i = 0; i < dimensionSize[d]; i++) {
                subVector[d] = i;
                temp[i] = mdcm.get(subVector);
            }
            Complex[] temp2 = transform(temp, type);
            for (int i2 = 0; i2 < dimensionSize[d]; i2++) {
                subVector[d] = i2;
                mdcm.set(temp2[i2], subVector);
            }
            return;
        }
        int[] vector = new int[(subVector.length + 1)];
        System.arraycopy(subVector, 0, vector, 0, subVector.length);
        if (subVector.length == d) {
            vector[d] = 0;
            mdfft(mdcm, type, d, vector);
            return;
        }
        for (int i3 = 0; i3 < dimensionSize[subVector.length]; i3++) {
            vector[subVector.length] = i3;
            mdfft(mdcm, type, d, vector);
        }
    }

    /* access modifiers changed from: private */
    @Deprecated
    public static class MultiDimensionalComplexMatrix implements Cloneable {
        protected int[] dimensionSize;
        protected Object multiDimensionalComplexArray;

        MultiDimensionalComplexMatrix(Object multiDimensionalComplexArray2) {
            this.multiDimensionalComplexArray = multiDimensionalComplexArray2;
            int numOfDimensions = 0;
            for (Object lastDimension = multiDimensionalComplexArray2; lastDimension instanceof Object[]; lastDimension = ((Object[]) lastDimension)[0]) {
                numOfDimensions++;
            }
            this.dimensionSize = new int[numOfDimensions];
            int numOfDimensions2 = 0;
            Object lastDimension2 = multiDimensionalComplexArray2;
            while (lastDimension2 instanceof Object[]) {
                Object array = (Object[]) lastDimension2;
                this.dimensionSize[numOfDimensions2] = array.length;
                lastDimension2 = array[0];
                numOfDimensions2++;
            }
        }

        public Complex get(int... vector) throws DimensionMismatchException {
            if (vector == null) {
                if (this.dimensionSize.length <= 0) {
                    return null;
                }
                throw new DimensionMismatchException(0, this.dimensionSize.length);
            } else if (vector.length != this.dimensionSize.length) {
                throw new DimensionMismatchException(vector.length, this.dimensionSize.length);
            } else {
                Object lastDimension = this.multiDimensionalComplexArray;
                for (int i = 0; i < this.dimensionSize.length; i++) {
                    lastDimension = ((Object[]) lastDimension)[vector[i]];
                }
                return (Complex) lastDimension;
            }
        }

        public Complex set(Complex magnitude, int... vector) throws DimensionMismatchException {
            if (vector == null) {
                if (this.dimensionSize.length <= 0) {
                    return null;
                }
                throw new DimensionMismatchException(0, this.dimensionSize.length);
            } else if (vector.length != this.dimensionSize.length) {
                throw new DimensionMismatchException(vector.length, this.dimensionSize.length);
            } else {
                Object[] lastDimension = (Object[]) this.multiDimensionalComplexArray;
                for (int i = 0; i < this.dimensionSize.length - 1; i++) {
                    lastDimension = (Object[]) lastDimension[vector[i]];
                }
                Complex complex = (Complex) lastDimension[vector[this.dimensionSize.length - 1]];
                lastDimension[vector[this.dimensionSize.length - 1]] = magnitude;
                return complex;
            }
        }

        public int[] getDimensionSizes() {
            return (int[]) this.dimensionSize.clone();
        }

        public Object getArray() {
            return this.multiDimensionalComplexArray;
        }

        @Override // java.lang.Object
        public Object clone() {
            MultiDimensionalComplexMatrix mdcm = new MultiDimensionalComplexMatrix(Array.newInstance(Complex.class, this.dimensionSize));
            clone(mdcm);
            return mdcm;
        }

        private void clone(MultiDimensionalComplexMatrix mdcm) {
            int[] vector = new int[this.dimensionSize.length];
            int size = 1;
            for (int i = 0; i < this.dimensionSize.length; i++) {
                size *= this.dimensionSize[i];
            }
            int[][] vectorList = (int[][]) Array.newInstance(Integer.TYPE, size, this.dimensionSize.length);
            for (int[] nextVector : vectorList) {
                System.arraycopy(vector, 0, nextVector, 0, this.dimensionSize.length);
                for (int i2 = 0; i2 < this.dimensionSize.length; i2++) {
                    vector[i2] = vector[i2] + 1;
                    if (vector[i2] < this.dimensionSize[i2]) {
                        break;
                    }
                    vector[i2] = 0;
                }
            }
            for (int[] nextVector2 : vectorList) {
                mdcm.set(get(nextVector2), nextVector2);
            }
        }
    }
}
