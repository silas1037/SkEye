package org.apache.commons.math3.analysis.differentiation;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MathArithmeticException;
import org.apache.commons.math3.exception.MathInternalError;
import org.apache.commons.math3.exception.NotPositiveException;
import org.apache.commons.math3.exception.NumberIsTooLargeException;
import org.apache.commons.math3.util.CombinatoricsUtils;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.MathArrays;

public class DSCompiler {
    private static AtomicReference<DSCompiler[][]> compilers = new AtomicReference<>(null);
    private final int[][][] compIndirection;
    private final int[][] derivativesIndirection;
    private final int[] lowerIndirection;
    private final int[][][] multIndirection;
    private final int order;
    private final int parameters;
    private final int[][] sizes;

    private DSCompiler(int parameters2, int order2, DSCompiler valueCompiler, DSCompiler derivativeCompiler) throws NumberIsTooLargeException {
        this.parameters = parameters2;
        this.order = order2;
        this.sizes = compileSizes(parameters2, order2, valueCompiler);
        this.derivativesIndirection = compileDerivativesIndirection(parameters2, order2, valueCompiler, derivativeCompiler);
        this.lowerIndirection = compileLowerIndirection(parameters2, order2, valueCompiler, derivativeCompiler);
        this.multIndirection = compileMultiplicationIndirection(parameters2, order2, valueCompiler, derivativeCompiler, this.lowerIndirection);
        this.compIndirection = compileCompositionIndirection(parameters2, order2, valueCompiler, derivativeCompiler, this.sizes, this.derivativesIndirection);
    }

    public static DSCompiler getCompiler(int parameters2, int order2) throws NumberIsTooLargeException {
        int length;
        DSCompiler derivativeCompiler;
        DSCompiler[][] cache = compilers.get();
        if (cache != null && cache.length > parameters2 && cache[parameters2].length > order2 && cache[parameters2][order2] != null) {
            return cache[parameters2][order2];
        }
        int maxParameters = FastMath.max(parameters2, cache == null ? 0 : cache.length);
        if (cache == null) {
            length = 0;
        } else {
            length = cache[0].length;
        }
        DSCompiler[][] newCache = (DSCompiler[][]) Array.newInstance(DSCompiler.class, maxParameters + 1, FastMath.max(order2, length) + 1);
        if (cache != null) {
            for (int i = 0; i < cache.length; i++) {
                System.arraycopy(cache[i], 0, newCache[i], 0, cache[i].length);
            }
        }
        for (int diag = 0; diag <= parameters2 + order2; diag++) {
            for (int o = FastMath.max(0, diag - parameters2); o <= FastMath.min(order2, diag); o++) {
                int p = diag - o;
                if (newCache[p][o] == null) {
                    DSCompiler valueCompiler = p == 0 ? null : newCache[p - 1][o];
                    if (o == 0) {
                        derivativeCompiler = null;
                    } else {
                        derivativeCompiler = newCache[p][o - 1];
                    }
                    newCache[p][o] = new DSCompiler(p, o, valueCompiler, derivativeCompiler);
                }
            }
        }
        compilers.compareAndSet(cache, newCache);
        return newCache[parameters2][order2];
    }

    private static int[][] compileSizes(int parameters2, int order2, DSCompiler valueCompiler) {
        int[][] sizes2 = (int[][]) Array.newInstance(Integer.TYPE, parameters2 + 1, order2 + 1);
        if (parameters2 == 0) {
            Arrays.fill(sizes2[0], 1);
        } else {
            System.arraycopy(valueCompiler.sizes, 0, sizes2, 0, parameters2);
            sizes2[parameters2][0] = 1;
            for (int i = 0; i < order2; i++) {
                sizes2[parameters2][i + 1] = sizes2[parameters2][i] + sizes2[parameters2 - 1][i + 1];
            }
        }
        return sizes2;
    }

    private static int[][] compileDerivativesIndirection(int parameters2, int order2, DSCompiler valueCompiler, DSCompiler derivativeCompiler) {
        if (parameters2 == 0 || order2 == 0) {
            return (int[][]) Array.newInstance(Integer.TYPE, 1, parameters2);
        }
        int vSize = valueCompiler.derivativesIndirection.length;
        int dSize = derivativeCompiler.derivativesIndirection.length;
        int[][] derivativesIndirection2 = (int[][]) Array.newInstance(Integer.TYPE, vSize + dSize, parameters2);
        for (int i = 0; i < vSize; i++) {
            System.arraycopy(valueCompiler.derivativesIndirection[i], 0, derivativesIndirection2[i], 0, parameters2 - 1);
        }
        for (int i2 = 0; i2 < dSize; i2++) {
            System.arraycopy(derivativeCompiler.derivativesIndirection[i2], 0, derivativesIndirection2[vSize + i2], 0, parameters2);
            int[] iArr = derivativesIndirection2[vSize + i2];
            int i3 = parameters2 - 1;
            iArr[i3] = iArr[i3] + 1;
        }
        return derivativesIndirection2;
    }

    private static int[] compileLowerIndirection(int parameters2, int order2, DSCompiler valueCompiler, DSCompiler derivativeCompiler) {
        if (parameters2 == 0 || order2 <= 1) {
            return new int[]{0};
        }
        int vSize = valueCompiler.lowerIndirection.length;
        int dSize = derivativeCompiler.lowerIndirection.length;
        int[] lowerIndirection2 = new int[(vSize + dSize)];
        System.arraycopy(valueCompiler.lowerIndirection, 0, lowerIndirection2, 0, vSize);
        for (int i = 0; i < dSize; i++) {
            lowerIndirection2[vSize + i] = valueCompiler.getSize() + derivativeCompiler.lowerIndirection[i];
        }
        return lowerIndirection2;
    }

    private static int[][][] compileMultiplicationIndirection(int parameters2, int order2, DSCompiler valueCompiler, DSCompiler derivativeCompiler, int[] lowerIndirection2) {
        if (parameters2 == 0 || order2 == 0) {
            return new int[][][]{new int[][]{new int[]{1, 0, 0}}};
        }
        int vSize = valueCompiler.multIndirection.length;
        int dSize = derivativeCompiler.multIndirection.length;
        int[][][] multIndirection2 = new int[(vSize + dSize)][][];
        System.arraycopy(valueCompiler.multIndirection, 0, multIndirection2, 0, vSize);
        for (int i = 0; i < dSize; i++) {
            int[][] dRow = derivativeCompiler.multIndirection[i];
            List<int[]> row = new ArrayList<>(dRow.length * 2);
            for (int j = 0; j < dRow.length; j++) {
                row.add(new int[]{dRow[j][0], lowerIndirection2[dRow[j][1]], dRow[j][2] + vSize});
                row.add(new int[]{dRow[j][0], dRow[j][1] + vSize, lowerIndirection2[dRow[j][2]]});
            }
            List<int[]> combined = new ArrayList<>(row.size());
            for (int j2 = 0; j2 < row.size(); j2++) {
                int[] termJ = row.get(j2);
                if (termJ[0] > 0) {
                    for (int k = j2 + 1; k < row.size(); k++) {
                        int[] termK = row.get(k);
                        if (termJ[1] == termK[1] && termJ[2] == termK[2]) {
                            termJ[0] = termJ[0] + termK[0];
                            termK[0] = 0;
                        }
                    }
                    combined.add(termJ);
                }
            }
            multIndirection2[vSize + i] = (int[][]) combined.toArray(new int[combined.size()][]);
        }
        return multIndirection2;
    }

    private static int[][][] compileCompositionIndirection(int parameters2, int order2, DSCompiler valueCompiler, DSCompiler derivativeCompiler, int[][] sizes2, int[][] derivativesIndirection2) throws NumberIsTooLargeException {
        if (parameters2 == 0 || order2 == 0) {
            return new int[][][]{new int[][]{new int[]{1, 0}}};
        }
        int vSize = valueCompiler.compIndirection.length;
        int dSize = derivativeCompiler.compIndirection.length;
        int[][][] compIndirection2 = new int[(vSize + dSize)][][];
        System.arraycopy(valueCompiler.compIndirection, 0, compIndirection2, 0, vSize);
        for (int i = 0; i < dSize; i++) {
            List<int[]> row = new ArrayList<>();
            int[][] arr$ = derivativeCompiler.compIndirection[i];
            int len$ = arr$.length;
            for (int i$ = 0; i$ < len$; i$++) {
                int[] term = arr$[i$];
                int[] derivedTermF = new int[(term.length + 1)];
                derivedTermF[0] = term[0];
                derivedTermF[1] = term[1] + 1;
                int[] orders = new int[parameters2];
                orders[parameters2 - 1] = 1;
                derivedTermF[term.length] = getPartialDerivativeIndex(parameters2, order2, sizes2, orders);
                for (int j = 2; j < term.length; j++) {
                    derivedTermF[j] = convertIndex(term[j], parameters2, derivativeCompiler.derivativesIndirection, parameters2, order2, sizes2);
                }
                Arrays.sort(derivedTermF, 2, derivedTermF.length);
                row.add(derivedTermF);
                for (int l = 2; l < term.length; l++) {
                    int[] derivedTermG = new int[term.length];
                    derivedTermG[0] = term[0];
                    derivedTermG[1] = term[1];
                    for (int j2 = 2; j2 < term.length; j2++) {
                        derivedTermG[j2] = convertIndex(term[j2], parameters2, derivativeCompiler.derivativesIndirection, parameters2, order2, sizes2);
                        if (j2 == l) {
                            System.arraycopy(derivativesIndirection2[derivedTermG[j2]], 0, orders, 0, parameters2);
                            int i2 = parameters2 - 1;
                            orders[i2] = orders[i2] + 1;
                            derivedTermG[j2] = getPartialDerivativeIndex(parameters2, order2, sizes2, orders);
                        }
                    }
                    Arrays.sort(derivedTermG, 2, derivedTermG.length);
                    row.add(derivedTermG);
                }
            }
            List<int[]> combined = new ArrayList<>(row.size());
            for (int j3 = 0; j3 < row.size(); j3++) {
                int[] termJ = row.get(j3);
                if (termJ[0] > 0) {
                    for (int k = j3 + 1; k < row.size(); k++) {
                        int[] termK = row.get(k);
                        boolean equals = termJ.length == termK.length;
                        int l2 = 1;
                        while (equals && l2 < termJ.length) {
                            equals &= termJ[l2] == termK[l2];
                            l2++;
                        }
                        if (equals) {
                            termJ[0] = termJ[0] + termK[0];
                            termK[0] = 0;
                        }
                    }
                    combined.add(termJ);
                }
            }
            compIndirection2[vSize + i] = (int[][]) combined.toArray(new int[combined.size()][]);
        }
        return compIndirection2;
    }

    public int getPartialDerivativeIndex(int... orders) throws DimensionMismatchException, NumberIsTooLargeException {
        if (orders.length == getFreeParameters()) {
            return getPartialDerivativeIndex(this.parameters, this.order, this.sizes, orders);
        }
        throw new DimensionMismatchException(orders.length, getFreeParameters());
    }

    private static int getPartialDerivativeIndex(int parameters2, int order2, int[][] sizes2, int... orders) throws NumberIsTooLargeException {
        int index = 0;
        int m = order2;
        int ordersSum = 0;
        int i = parameters2 - 1;
        while (i >= 0) {
            int derivativeOrder = orders[i];
            ordersSum += derivativeOrder;
            if (ordersSum > order2) {
                throw new NumberIsTooLargeException(Integer.valueOf(ordersSum), Integer.valueOf(order2), true);
            }
            while (true) {
                derivativeOrder--;
                if (derivativeOrder <= 0) {
                    break;
                }
                m--;
                index += sizes2[i][m];
            }
            i--;
            m = m;
        }
        return index;
    }

    private static int convertIndex(int index, int srcP, int[][] srcDerivativesIndirection, int destP, int destO, int[][] destSizes) throws NumberIsTooLargeException {
        int[] orders = new int[destP];
        System.arraycopy(srcDerivativesIndirection[index], 0, orders, 0, FastMath.min(srcP, destP));
        return getPartialDerivativeIndex(destP, destO, destSizes, orders);
    }

    public int[] getPartialDerivativeOrders(int index) {
        return this.derivativesIndirection[index];
    }

    public int getFreeParameters() {
        return this.parameters;
    }

    public int getOrder() {
        return this.order;
    }

    public int getSize() {
        return this.sizes[this.parameters][this.order];
    }

    public void linearCombination(double a1, double[] c1, int offset1, double a2, double[] c2, int offset2, double[] result, int resultOffset) {
        for (int i = 0; i < getSize(); i++) {
            result[resultOffset + i] = MathArrays.linearCombination(a1, c1[offset1 + i], a2, c2[offset2 + i]);
        }
    }

    public void linearCombination(double a1, double[] c1, int offset1, double a2, double[] c2, int offset2, double a3, double[] c3, int offset3, double[] result, int resultOffset) {
        for (int i = 0; i < getSize(); i++) {
            result[resultOffset + i] = MathArrays.linearCombination(a1, c1[offset1 + i], a2, c2[offset2 + i], a3, c3[offset3 + i]);
        }
    }

    public void linearCombination(double a1, double[] c1, int offset1, double a2, double[] c2, int offset2, double a3, double[] c3, int offset3, double a4, double[] c4, int offset4, double[] result, int resultOffset) {
        for (int i = 0; i < getSize(); i++) {
            result[resultOffset + i] = MathArrays.linearCombination(a1, c1[offset1 + i], a2, c2[offset2 + i], a3, c3[offset3 + i], a4, c4[offset4 + i]);
        }
    }

    public void add(double[] lhs, int lhsOffset, double[] rhs, int rhsOffset, double[] result, int resultOffset) {
        for (int i = 0; i < getSize(); i++) {
            result[resultOffset + i] = lhs[lhsOffset + i] + rhs[rhsOffset + i];
        }
    }

    public void subtract(double[] lhs, int lhsOffset, double[] rhs, int rhsOffset, double[] result, int resultOffset) {
        for (int i = 0; i < getSize(); i++) {
            result[resultOffset + i] = lhs[lhsOffset + i] - rhs[rhsOffset + i];
        }
    }

    public void multiply(double[] lhs, int lhsOffset, double[] rhs, int rhsOffset, double[] result, int resultOffset) {
        for (int i = 0; i < this.multIndirection.length; i++) {
            int[][] mappingI = this.multIndirection[i];
            double r = 0.0d;
            for (int j = 0; j < mappingI.length; j++) {
                r += ((double) mappingI[j][0]) * lhs[mappingI[j][1] + lhsOffset] * rhs[mappingI[j][2] + rhsOffset];
            }
            result[resultOffset + i] = r;
        }
    }

    public void divide(double[] lhs, int lhsOffset, double[] rhs, int rhsOffset, double[] result, int resultOffset) {
        double[] reciprocal = new double[getSize()];
        pow(rhs, lhsOffset, -1, reciprocal, 0);
        multiply(lhs, lhsOffset, reciprocal, 0, result, resultOffset);
    }

    public void remainder(double[] lhs, int lhsOffset, double[] rhs, int rhsOffset, double[] result, int resultOffset) {
        double rem = FastMath.IEEEremainder(lhs[lhsOffset], rhs[rhsOffset]);
        double k = FastMath.rint((lhs[lhsOffset] - rem) / rhs[rhsOffset]);
        result[resultOffset] = rem;
        for (int i = 1; i < getSize(); i++) {
            result[resultOffset + i] = lhs[lhsOffset + i] - (rhs[rhsOffset + i] * k);
        }
    }

    public void pow(double a, double[] operand, int operandOffset, double[] result, int resultOffset) {
        double[] function = new double[(this.order + 1)];
        if (a != 0.0d) {
            function[0] = FastMath.pow(a, operand[operandOffset]);
            double lnA = FastMath.log(a);
            for (int i = 1; i < function.length; i++) {
                function[i] = function[i - 1] * lnA;
            }
        } else if (operand[operandOffset] == 0.0d) {
            function[0] = 1.0d;
            double infinity = Double.POSITIVE_INFINITY;
            for (int i2 = 1; i2 < function.length; i2++) {
                infinity = -infinity;
                function[i2] = infinity;
            }
        } else if (operand[operandOffset] < 0.0d) {
            Arrays.fill(function, Double.NaN);
        }
        compose(operand, operandOffset, function, result, resultOffset);
    }

    public void pow(double[] operand, int operandOffset, double p, double[] result, int resultOffset) {
        double[] function = new double[(this.order + 1)];
        double xk = FastMath.pow(operand[operandOffset], p - ((double) this.order));
        for (int i = this.order; i > 0; i--) {
            function[i] = xk;
            xk *= operand[operandOffset];
        }
        function[0] = xk;
        double coefficient = p;
        for (int i2 = 1; i2 <= this.order; i2++) {
            function[i2] = function[i2] * coefficient;
            coefficient *= p - ((double) i2);
        }
        compose(operand, operandOffset, function, result, resultOffset);
    }

    public void pow(double[] operand, int operandOffset, int n, double[] result, int resultOffset) {
        if (n == 0) {
            result[resultOffset] = 1.0d;
            Arrays.fill(result, resultOffset + 1, getSize() + resultOffset, 0.0d);
            return;
        }
        double[] function = new double[(this.order + 1)];
        if (n > 0) {
            int maxOrder = FastMath.min(this.order, n);
            double xk = FastMath.pow(operand[operandOffset], n - maxOrder);
            for (int i = maxOrder; i > 0; i--) {
                function[i] = xk;
                xk *= operand[operandOffset];
            }
            function[0] = xk;
        } else {
            double inv = 1.0d / operand[operandOffset];
            double xk2 = FastMath.pow(inv, -n);
            for (int i2 = 0; i2 <= this.order; i2++) {
                function[i2] = xk2;
                xk2 *= inv;
            }
        }
        double coefficient = (double) n;
        for (int i3 = 1; i3 <= this.order; i3++) {
            function[i3] = function[i3] * coefficient;
            coefficient *= (double) (n - i3);
        }
        compose(operand, operandOffset, function, result, resultOffset);
    }

    public void pow(double[] x, int xOffset, double[] y, int yOffset, double[] result, int resultOffset) {
        double[] logX = new double[getSize()];
        log(x, xOffset, logX, 0);
        double[] yLogX = new double[getSize()];
        multiply(logX, 0, y, yOffset, yLogX, 0);
        exp(yLogX, 0, result, resultOffset);
    }

    public void rootN(double[] operand, int operandOffset, int n, double[] result, int resultOffset) {
        double xk;
        double[] function = new double[(this.order + 1)];
        if (n == 2) {
            function[0] = FastMath.sqrt(operand[operandOffset]);
            xk = 0.5d / function[0];
        } else if (n == 3) {
            function[0] = FastMath.cbrt(operand[operandOffset]);
            xk = 1.0d / ((3.0d * function[0]) * function[0]);
        } else {
            function[0] = FastMath.pow(operand[operandOffset], 1.0d / ((double) n));
            xk = 1.0d / (((double) n) * FastMath.pow(function[0], n - 1));
        }
        double nReciprocal = 1.0d / ((double) n);
        double xReciprocal = 1.0d / operand[operandOffset];
        for (int i = 1; i <= this.order; i++) {
            function[i] = xk;
            xk *= (nReciprocal - ((double) i)) * xReciprocal;
        }
        compose(operand, operandOffset, function, result, resultOffset);
    }

    public void exp(double[] operand, int operandOffset, double[] result, int resultOffset) {
        double[] function = new double[(this.order + 1)];
        Arrays.fill(function, FastMath.exp(operand[operandOffset]));
        compose(operand, operandOffset, function, result, resultOffset);
    }

    public void expm1(double[] operand, int operandOffset, double[] result, int resultOffset) {
        double[] function = new double[(this.order + 1)];
        function[0] = FastMath.expm1(operand[operandOffset]);
        Arrays.fill(function, 1, this.order + 1, FastMath.exp(operand[operandOffset]));
        compose(operand, operandOffset, function, result, resultOffset);
    }

    public void log(double[] operand, int operandOffset, double[] result, int resultOffset) {
        double[] function = new double[(this.order + 1)];
        function[0] = FastMath.log(operand[operandOffset]);
        if (this.order > 0) {
            double inv = 1.0d / operand[operandOffset];
            double xk = inv;
            for (int i = 1; i <= this.order; i++) {
                function[i] = xk;
                xk *= ((double) (-i)) * inv;
            }
        }
        compose(operand, operandOffset, function, result, resultOffset);
    }

    public void log1p(double[] operand, int operandOffset, double[] result, int resultOffset) {
        double[] function = new double[(this.order + 1)];
        function[0] = FastMath.log1p(operand[operandOffset]);
        if (this.order > 0) {
            double inv = 1.0d / (1.0d + operand[operandOffset]);
            double xk = inv;
            for (int i = 1; i <= this.order; i++) {
                function[i] = xk;
                xk *= ((double) (-i)) * inv;
            }
        }
        compose(operand, operandOffset, function, result, resultOffset);
    }

    public void log10(double[] operand, int operandOffset, double[] result, int resultOffset) {
        double[] function = new double[(this.order + 1)];
        function[0] = FastMath.log10(operand[operandOffset]);
        if (this.order > 0) {
            double inv = 1.0d / operand[operandOffset];
            double xk = inv / FastMath.log(10.0d);
            for (int i = 1; i <= this.order; i++) {
                function[i] = xk;
                xk *= ((double) (-i)) * inv;
            }
        }
        compose(operand, operandOffset, function, result, resultOffset);
    }

    public void cos(double[] operand, int operandOffset, double[] result, int resultOffset) {
        double[] function = new double[(this.order + 1)];
        function[0] = FastMath.cos(operand[operandOffset]);
        if (this.order > 0) {
            function[1] = -FastMath.sin(operand[operandOffset]);
            for (int i = 2; i <= this.order; i++) {
                function[i] = -function[i - 2];
            }
        }
        compose(operand, operandOffset, function, result, resultOffset);
    }

    public void sin(double[] operand, int operandOffset, double[] result, int resultOffset) {
        double[] function = new double[(this.order + 1)];
        function[0] = FastMath.sin(operand[operandOffset]);
        if (this.order > 0) {
            function[1] = FastMath.cos(operand[operandOffset]);
            for (int i = 2; i <= this.order; i++) {
                function[i] = -function[i - 2];
            }
        }
        compose(operand, operandOffset, function, result, resultOffset);
    }

    public void tan(double[] operand, int operandOffset, double[] result, int resultOffset) {
        double[] function = new double[(this.order + 1)];
        double t = FastMath.tan(operand[operandOffset]);
        function[0] = t;
        if (this.order > 0) {
            double[] p = new double[(this.order + 2)];
            p[1] = 1.0d;
            double t2 = t * t;
            for (int n = 1; n <= this.order; n++) {
                double v = 0.0d;
                p[n + 1] = ((double) n) * p[n];
                for (int k = n + 1; k >= 0; k -= 2) {
                    v = (v * t2) + p[k];
                    if (k > 2) {
                        p[k - 2] = (((double) (k - 1)) * p[k - 1]) + (((double) (k - 3)) * p[k - 3]);
                    } else if (k == 2) {
                        p[0] = p[1];
                    }
                }
                if ((n & 1) == 0) {
                    v *= t;
                }
                function[n] = v;
            }
        }
        compose(operand, operandOffset, function, result, resultOffset);
    }

    public void acos(double[] operand, int operandOffset, double[] result, int resultOffset) {
        double[] function = new double[(this.order + 1)];
        double x = operand[operandOffset];
        function[0] = FastMath.acos(x);
        if (this.order > 0) {
            double[] p = new double[this.order];
            p[0] = -1.0d;
            double x2 = x * x;
            double f = 1.0d / (1.0d - x2);
            double coeff = FastMath.sqrt(f);
            function[1] = p[0] * coeff;
            for (int n = 2; n <= this.order; n++) {
                double v = 0.0d;
                p[n - 1] = ((double) (n - 1)) * p[n - 2];
                for (int k = n - 1; k >= 0; k -= 2) {
                    v = (v * x2) + p[k];
                    if (k > 2) {
                        p[k - 2] = (((double) (k - 1)) * p[k - 1]) + (((double) ((n * 2) - k)) * p[k - 3]);
                    } else if (k == 2) {
                        p[0] = p[1];
                    }
                }
                if ((n & 1) == 0) {
                    v *= x;
                }
                coeff *= f;
                function[n] = coeff * v;
            }
        }
        compose(operand, operandOffset, function, result, resultOffset);
    }

    public void asin(double[] operand, int operandOffset, double[] result, int resultOffset) {
        double[] function = new double[(this.order + 1)];
        double x = operand[operandOffset];
        function[0] = FastMath.asin(x);
        if (this.order > 0) {
            double[] p = new double[this.order];
            p[0] = 1.0d;
            double x2 = x * x;
            double f = 1.0d / (1.0d - x2);
            double coeff = FastMath.sqrt(f);
            function[1] = p[0] * coeff;
            for (int n = 2; n <= this.order; n++) {
                double v = 0.0d;
                p[n - 1] = ((double) (n - 1)) * p[n - 2];
                for (int k = n - 1; k >= 0; k -= 2) {
                    v = (v * x2) + p[k];
                    if (k > 2) {
                        p[k - 2] = (((double) (k - 1)) * p[k - 1]) + (((double) ((n * 2) - k)) * p[k - 3]);
                    } else if (k == 2) {
                        p[0] = p[1];
                    }
                }
                if ((n & 1) == 0) {
                    v *= x;
                }
                coeff *= f;
                function[n] = coeff * v;
            }
        }
        compose(operand, operandOffset, function, result, resultOffset);
    }

    public void atan(double[] operand, int operandOffset, double[] result, int resultOffset) {
        double[] function = new double[(this.order + 1)];
        double x = operand[operandOffset];
        function[0] = FastMath.atan(x);
        if (this.order > 0) {
            double[] q = new double[this.order];
            q[0] = 1.0d;
            double x2 = x * x;
            double f = 1.0d / (1.0d + x2);
            double coeff = f;
            function[1] = q[0] * coeff;
            for (int n = 2; n <= this.order; n++) {
                double v = 0.0d;
                q[n - 1] = ((double) (-n)) * q[n - 2];
                for (int k = n - 1; k >= 0; k -= 2) {
                    v = (v * x2) + q[k];
                    if (k > 2) {
                        q[k - 2] = (((double) (k - 1)) * q[k - 1]) + (((double) ((k - 1) - (n * 2))) * q[k - 3]);
                    } else if (k == 2) {
                        q[0] = q[1];
                    }
                }
                if ((n & 1) == 0) {
                    v *= x;
                }
                coeff *= f;
                function[n] = coeff * v;
            }
        }
        compose(operand, operandOffset, function, result, resultOffset);
    }

    public void atan2(double[] y, int yOffset, double[] x, int xOffset, double[] result, int resultOffset) {
        double[] tmp1 = new double[getSize()];
        multiply(x, xOffset, x, xOffset, tmp1, 0);
        double[] tmp2 = new double[getSize()];
        multiply(y, yOffset, y, yOffset, tmp2, 0);
        add(tmp1, 0, tmp2, 0, tmp2, 0);
        rootN(tmp2, 0, 2, tmp1, 0);
        if (x[xOffset] >= 0.0d) {
            add(tmp1, 0, x, xOffset, tmp2, 0);
            divide(y, yOffset, tmp2, 0, tmp1, 0);
            atan(tmp1, 0, tmp2, 0);
            for (int i = 0; i < tmp2.length; i++) {
                result[resultOffset + i] = 2.0d * tmp2[i];
            }
        } else {
            subtract(tmp1, 0, x, xOffset, tmp2, 0);
            divide(y, yOffset, tmp2, 0, tmp1, 0);
            atan(tmp1, 0, tmp2, 0);
            result[resultOffset] = (tmp2[0] <= 0.0d ? -3.141592653589793d : 3.141592653589793d) - (2.0d * tmp2[0]);
            for (int i2 = 1; i2 < tmp2.length; i2++) {
                result[resultOffset + i2] = -2.0d * tmp2[i2];
            }
        }
        result[resultOffset] = FastMath.atan2(y[yOffset], x[xOffset]);
    }

    public void cosh(double[] operand, int operandOffset, double[] result, int resultOffset) {
        double[] function = new double[(this.order + 1)];
        function[0] = FastMath.cosh(operand[operandOffset]);
        if (this.order > 0) {
            function[1] = FastMath.sinh(operand[operandOffset]);
            for (int i = 2; i <= this.order; i++) {
                function[i] = function[i - 2];
            }
        }
        compose(operand, operandOffset, function, result, resultOffset);
    }

    public void sinh(double[] operand, int operandOffset, double[] result, int resultOffset) {
        double[] function = new double[(this.order + 1)];
        function[0] = FastMath.sinh(operand[operandOffset]);
        if (this.order > 0) {
            function[1] = FastMath.cosh(operand[operandOffset]);
            for (int i = 2; i <= this.order; i++) {
                function[i] = function[i - 2];
            }
        }
        compose(operand, operandOffset, function, result, resultOffset);
    }

    public void tanh(double[] operand, int operandOffset, double[] result, int resultOffset) {
        double[] function = new double[(this.order + 1)];
        double t = FastMath.tanh(operand[operandOffset]);
        function[0] = t;
        if (this.order > 0) {
            double[] p = new double[(this.order + 2)];
            p[1] = 1.0d;
            double t2 = t * t;
            for (int n = 1; n <= this.order; n++) {
                double v = 0.0d;
                p[n + 1] = ((double) (-n)) * p[n];
                for (int k = n + 1; k >= 0; k -= 2) {
                    v = (v * t2) + p[k];
                    if (k > 2) {
                        p[k - 2] = (((double) (k - 1)) * p[k - 1]) - (((double) (k - 3)) * p[k - 3]);
                    } else if (k == 2) {
                        p[0] = p[1];
                    }
                }
                if ((n & 1) == 0) {
                    v *= t;
                }
                function[n] = v;
            }
        }
        compose(operand, operandOffset, function, result, resultOffset);
    }

    public void acosh(double[] operand, int operandOffset, double[] result, int resultOffset) {
        double[] function = new double[(this.order + 1)];
        double x = operand[operandOffset];
        function[0] = FastMath.acosh(x);
        if (this.order > 0) {
            double[] p = new double[this.order];
            p[0] = 1.0d;
            double x2 = x * x;
            double f = 1.0d / (x2 - 1.0d);
            double coeff = FastMath.sqrt(f);
            function[1] = p[0] * coeff;
            for (int n = 2; n <= this.order; n++) {
                double v = 0.0d;
                p[n - 1] = ((double) (1 - n)) * p[n - 2];
                for (int k = n - 1; k >= 0; k -= 2) {
                    v = (v * x2) + p[k];
                    if (k > 2) {
                        p[k - 2] = (((double) (1 - k)) * p[k - 1]) + (((double) (k - (n * 2))) * p[k - 3]);
                    } else if (k == 2) {
                        p[0] = -p[1];
                    }
                }
                if ((n & 1) == 0) {
                    v *= x;
                }
                coeff *= f;
                function[n] = coeff * v;
            }
        }
        compose(operand, operandOffset, function, result, resultOffset);
    }

    public void asinh(double[] operand, int operandOffset, double[] result, int resultOffset) {
        double[] function = new double[(this.order + 1)];
        double x = operand[operandOffset];
        function[0] = FastMath.asinh(x);
        if (this.order > 0) {
            double[] p = new double[this.order];
            p[0] = 1.0d;
            double x2 = x * x;
            double f = 1.0d / (1.0d + x2);
            double coeff = FastMath.sqrt(f);
            function[1] = p[0] * coeff;
            for (int n = 2; n <= this.order; n++) {
                double v = 0.0d;
                p[n - 1] = ((double) (1 - n)) * p[n - 2];
                for (int k = n - 1; k >= 0; k -= 2) {
                    v = (v * x2) + p[k];
                    if (k > 2) {
                        p[k - 2] = (((double) (k - 1)) * p[k - 1]) + (((double) (k - (n * 2))) * p[k - 3]);
                    } else if (k == 2) {
                        p[0] = p[1];
                    }
                }
                if ((n & 1) == 0) {
                    v *= x;
                }
                coeff *= f;
                function[n] = coeff * v;
            }
        }
        compose(operand, operandOffset, function, result, resultOffset);
    }

    public void atanh(double[] operand, int operandOffset, double[] result, int resultOffset) {
        double[] function = new double[(this.order + 1)];
        double x = operand[operandOffset];
        function[0] = FastMath.atanh(x);
        if (this.order > 0) {
            double[] q = new double[this.order];
            q[0] = 1.0d;
            double x2 = x * x;
            double f = 1.0d / (1.0d - x2);
            double coeff = f;
            function[1] = q[0] * coeff;
            for (int n = 2; n <= this.order; n++) {
                double v = 0.0d;
                q[n - 1] = ((double) n) * q[n - 2];
                for (int k = n - 1; k >= 0; k -= 2) {
                    v = (v * x2) + q[k];
                    if (k > 2) {
                        q[k - 2] = (((double) (k - 1)) * q[k - 1]) + (((double) (((n * 2) - k) + 1)) * q[k - 3]);
                    } else if (k == 2) {
                        q[0] = q[1];
                    }
                }
                if ((n & 1) == 0) {
                    v *= x;
                }
                coeff *= f;
                function[n] = coeff * v;
            }
        }
        compose(operand, operandOffset, function, result, resultOffset);
    }

    public void compose(double[] operand, int operandOffset, double[] f, double[] result, int resultOffset) {
        for (int i = 0; i < this.compIndirection.length; i++) {
            int[][] mappingI = this.compIndirection[i];
            double r = 0.0d;
            for (int[] mappingIJ : mappingI) {
                double product = ((double) mappingIJ[0]) * f[mappingIJ[1]];
                for (int k = 2; k < mappingIJ.length; k++) {
                    product *= operand[mappingIJ[k] + operandOffset];
                }
                r += product;
            }
            result[resultOffset + i] = r;
        }
    }

    public double taylor(double[] ds, int dsOffset, double... delta) throws MathArithmeticException {
        double value = 0.0d;
        for (int i = getSize() - 1; i >= 0; i--) {
            int[] orders = getPartialDerivativeOrders(i);
            double term = ds[dsOffset + i];
            for (int k = 0; k < orders.length; k++) {
                if (orders[k] > 0) {
                    try {
                        term *= FastMath.pow(delta[k], orders[k]) / ((double) CombinatoricsUtils.factorial(orders[k]));
                    } catch (NotPositiveException e) {
                        throw new MathInternalError(e);
                    }
                }
            }
            value += term;
        }
        return value;
    }

    public void checkCompatibility(DSCompiler compiler) throws DimensionMismatchException {
        if (this.parameters != compiler.parameters) {
            throw new DimensionMismatchException(this.parameters, compiler.parameters);
        } else if (this.order != compiler.order) {
            throw new DimensionMismatchException(this.order, compiler.order);
        }
    }
}
