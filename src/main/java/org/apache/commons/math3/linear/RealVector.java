package org.apache.commons.math3.linear;

import java.util.Iterator;
import java.util.NoSuchElementException;
import org.apache.commons.math3.analysis.FunctionUtils;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.function.Add;
import org.apache.commons.math3.analysis.function.Divide;
import org.apache.commons.math3.analysis.function.Multiply;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MathArithmeticException;
import org.apache.commons.math3.exception.MathUnsupportedOperationException;
import org.apache.commons.math3.exception.NotPositiveException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.util.FastMath;

public abstract class RealVector {
    public abstract RealVector append(double d);

    public abstract RealVector append(RealVector realVector);

    public abstract RealVector copy();

    public abstract RealVector ebeDivide(RealVector realVector) throws DimensionMismatchException;

    public abstract RealVector ebeMultiply(RealVector realVector) throws DimensionMismatchException;

    public abstract int getDimension();

    public abstract double getEntry(int i) throws OutOfRangeException;

    public abstract RealVector getSubVector(int i, int i2) throws NotPositiveException, OutOfRangeException;

    public abstract boolean isInfinite();

    public abstract boolean isNaN();

    public abstract void setEntry(int i, double d) throws OutOfRangeException;

    public abstract void setSubVector(int i, RealVector realVector) throws OutOfRangeException;

    public void addToEntry(int index, double increment) throws OutOfRangeException {
        setEntry(index, getEntry(index) + increment);
    }

    /* access modifiers changed from: protected */
    public void checkVectorDimensions(RealVector v) throws DimensionMismatchException {
        checkVectorDimensions(v.getDimension());
    }

    /* access modifiers changed from: protected */
    public void checkVectorDimensions(int n) throws DimensionMismatchException {
        int d = getDimension();
        if (d != n) {
            throw new DimensionMismatchException(d, n);
        }
    }

    /* access modifiers changed from: protected */
    public void checkIndex(int index) throws OutOfRangeException {
        if (index < 0 || index >= getDimension()) {
            throw new OutOfRangeException(LocalizedFormats.INDEX, Integer.valueOf(index), 0, Integer.valueOf(getDimension() - 1));
        }
    }

    /* access modifiers changed from: protected */
    public void checkIndices(int start, int end) throws NumberIsTooSmallException, OutOfRangeException {
        int dim = getDimension();
        if (start < 0 || start >= dim) {
            throw new OutOfRangeException(LocalizedFormats.INDEX, Integer.valueOf(start), 0, Integer.valueOf(dim - 1));
        } else if (end < 0 || end >= dim) {
            throw new OutOfRangeException(LocalizedFormats.INDEX, Integer.valueOf(end), 0, Integer.valueOf(dim - 1));
        } else if (end < start) {
            throw new NumberIsTooSmallException(LocalizedFormats.INITIAL_ROW_AFTER_FINAL_ROW, Integer.valueOf(end), Integer.valueOf(start), false);
        }
    }

    public RealVector add(RealVector v) throws DimensionMismatchException {
        checkVectorDimensions(v);
        RealVector result = v.copy();
        Iterator<Entry> it = iterator();
        while (it.hasNext()) {
            Entry e = it.next();
            int index = e.getIndex();
            result.setEntry(index, e.getValue() + result.getEntry(index));
        }
        return result;
    }

    public RealVector subtract(RealVector v) throws DimensionMismatchException {
        checkVectorDimensions(v);
        RealVector result = v.mapMultiply(-1.0d);
        Iterator<Entry> it = iterator();
        while (it.hasNext()) {
            Entry e = it.next();
            int index = e.getIndex();
            result.setEntry(index, e.getValue() + result.getEntry(index));
        }
        return result;
    }

    public RealVector mapAdd(double d) {
        return copy().mapAddToSelf(d);
    }

    public RealVector mapAddToSelf(double d) {
        if (d != 0.0d) {
            return mapToSelf(FunctionUtils.fix2ndArgument(new Add(), d));
        }
        return this;
    }

    public double dotProduct(RealVector v) throws DimensionMismatchException {
        checkVectorDimensions(v);
        double d = 0.0d;
        int n = getDimension();
        for (int i = 0; i < n; i++) {
            d += getEntry(i) * v.getEntry(i);
        }
        return d;
    }

    public double cosine(RealVector v) throws DimensionMismatchException, MathArithmeticException {
        double norm = getNorm();
        double vNorm = v.getNorm();
        if (norm != 0.0d && vNorm != 0.0d) {
            return dotProduct(v) / (norm * vNorm);
        }
        throw new MathArithmeticException(LocalizedFormats.ZERO_NORM, new Object[0]);
    }

    public double getDistance(RealVector v) throws DimensionMismatchException {
        checkVectorDimensions(v);
        double d = 0.0d;
        Iterator<Entry> it = iterator();
        while (it.hasNext()) {
            Entry e = it.next();
            double diff = e.getValue() - v.getEntry(e.getIndex());
            d += diff * diff;
        }
        return FastMath.sqrt(d);
    }

    public double getNorm() {
        double sum = 0.0d;
        Iterator<Entry> it = iterator();
        while (it.hasNext()) {
            double value = it.next().getValue();
            sum += value * value;
        }
        return FastMath.sqrt(sum);
    }

    public double getL1Norm() {
        double norm = 0.0d;
        Iterator<Entry> it = iterator();
        while (it.hasNext()) {
            norm += FastMath.abs(it.next().getValue());
        }
        return norm;
    }

    public double getLInfNorm() {
        double norm = 0.0d;
        Iterator<Entry> it = iterator();
        while (it.hasNext()) {
            norm = FastMath.max(norm, FastMath.abs(it.next().getValue()));
        }
        return norm;
    }

    public double getL1Distance(RealVector v) throws DimensionMismatchException {
        checkVectorDimensions(v);
        double d = 0.0d;
        Iterator<Entry> it = iterator();
        while (it.hasNext()) {
            Entry e = it.next();
            d += FastMath.abs(e.getValue() - v.getEntry(e.getIndex()));
        }
        return d;
    }

    public double getLInfDistance(RealVector v) throws DimensionMismatchException {
        checkVectorDimensions(v);
        double d = 0.0d;
        Iterator<Entry> it = iterator();
        while (it.hasNext()) {
            Entry e = it.next();
            d = FastMath.max(FastMath.abs(e.getValue() - v.getEntry(e.getIndex())), d);
        }
        return d;
    }

    public int getMinIndex() {
        int minIndex = -1;
        double minValue = Double.POSITIVE_INFINITY;
        Iterator<Entry> iterator = iterator();
        while (iterator.hasNext()) {
            Entry entry = iterator.next();
            if (entry.getValue() <= minValue) {
                minIndex = entry.getIndex();
                minValue = entry.getValue();
            }
        }
        return minIndex;
    }

    public double getMinValue() {
        int minIndex = getMinIndex();
        if (minIndex < 0) {
            return Double.NaN;
        }
        return getEntry(minIndex);
    }

    public int getMaxIndex() {
        int maxIndex = -1;
        double maxValue = Double.NEGATIVE_INFINITY;
        Iterator<Entry> iterator = iterator();
        while (iterator.hasNext()) {
            Entry entry = iterator.next();
            if (entry.getValue() >= maxValue) {
                maxIndex = entry.getIndex();
                maxValue = entry.getValue();
            }
        }
        return maxIndex;
    }

    public double getMaxValue() {
        int maxIndex = getMaxIndex();
        if (maxIndex < 0) {
            return Double.NaN;
        }
        return getEntry(maxIndex);
    }

    public RealVector mapMultiply(double d) {
        return copy().mapMultiplyToSelf(d);
    }

    public RealVector mapMultiplyToSelf(double d) {
        return mapToSelf(FunctionUtils.fix2ndArgument(new Multiply(), d));
    }

    public RealVector mapSubtract(double d) {
        return copy().mapSubtractToSelf(d);
    }

    public RealVector mapSubtractToSelf(double d) {
        return mapAddToSelf(-d);
    }

    public RealVector mapDivide(double d) {
        return copy().mapDivideToSelf(d);
    }

    public RealVector mapDivideToSelf(double d) {
        return mapToSelf(FunctionUtils.fix2ndArgument(new Divide(), d));
    }

    public RealMatrix outerProduct(RealVector v) {
        RealMatrix product;
        int m = getDimension();
        int n = v.getDimension();
        if ((v instanceof SparseRealVector) || (this instanceof SparseRealVector)) {
            product = new OpenMapRealMatrix(m, n);
        } else {
            product = new Array2DRowRealMatrix(m, n);
        }
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                product.setEntry(i, j, getEntry(i) * v.getEntry(j));
            }
        }
        return product;
    }

    public RealVector projection(RealVector v) throws DimensionMismatchException, MathArithmeticException {
        if (v.dotProduct(v) != 0.0d) {
            return v.mapMultiply(dotProduct(v) / v.dotProduct(v));
        }
        throw new MathArithmeticException(LocalizedFormats.ZERO_NORM, new Object[0]);
    }

    public void set(double value) {
        Iterator<Entry> it = iterator();
        while (it.hasNext()) {
            it.next().setValue(value);
        }
    }

    public double[] toArray() {
        int dim = getDimension();
        double[] values = new double[dim];
        for (int i = 0; i < dim; i++) {
            values[i] = getEntry(i);
        }
        return values;
    }

    public RealVector unitVector() throws MathArithmeticException {
        double norm = getNorm();
        if (norm != 0.0d) {
            return mapDivide(norm);
        }
        throw new MathArithmeticException(LocalizedFormats.ZERO_NORM, new Object[0]);
    }

    public void unitize() throws MathArithmeticException {
        if (getNorm() == 0.0d) {
            throw new MathArithmeticException(LocalizedFormats.ZERO_NORM, new Object[0]);
        }
        mapDivideToSelf(getNorm());
    }

    public Iterator<Entry> sparseIterator() {
        return new SparseEntryIterator();
    }

    public Iterator<Entry> iterator() {
        final int dim = getDimension();
        return new Iterator<Entry>() {
            /* class org.apache.commons.math3.linear.RealVector.C02741 */

            /* renamed from: e */
            private Entry f225e = new Entry();

            /* renamed from: i */
            private int f226i = 0;

            public boolean hasNext() {
                return this.f226i < dim;
            }

            @Override // java.util.Iterator
            public Entry next() {
                if (this.f226i < dim) {
                    Entry entry = this.f225e;
                    int i = this.f226i;
                    this.f226i = i + 1;
                    entry.setIndex(i);
                    return this.f225e;
                }
                throw new NoSuchElementException();
            }

            public void remove() throws MathUnsupportedOperationException {
                throw new MathUnsupportedOperationException();
            }
        };
    }

    public RealVector map(UnivariateFunction function) {
        return copy().mapToSelf(function);
    }

    public RealVector mapToSelf(UnivariateFunction function) {
        Iterator<Entry> it = iterator();
        while (it.hasNext()) {
            Entry e = it.next();
            e.setValue(function.value(e.getValue()));
        }
        return this;
    }

    public RealVector combine(double a, double b, RealVector y) throws DimensionMismatchException {
        return copy().combineToSelf(a, b, y);
    }

    public RealVector combineToSelf(double a, double b, RealVector y) throws DimensionMismatchException {
        checkVectorDimensions(y);
        for (int i = 0; i < getDimension(); i++) {
            setEntry(i, (a * getEntry(i)) + (b * y.getEntry(i)));
        }
        return this;
    }

    public double walkInDefaultOrder(RealVectorPreservingVisitor visitor) {
        int dim = getDimension();
        visitor.start(dim, 0, dim - 1);
        for (int i = 0; i < dim; i++) {
            visitor.visit(i, getEntry(i));
        }
        return visitor.end();
    }

    public double walkInDefaultOrder(RealVectorPreservingVisitor visitor, int start, int end) throws NumberIsTooSmallException, OutOfRangeException {
        checkIndices(start, end);
        visitor.start(getDimension(), start, end);
        for (int i = start; i <= end; i++) {
            visitor.visit(i, getEntry(i));
        }
        return visitor.end();
    }

    public double walkInOptimizedOrder(RealVectorPreservingVisitor visitor) {
        return walkInDefaultOrder(visitor);
    }

    public double walkInOptimizedOrder(RealVectorPreservingVisitor visitor, int start, int end) throws NumberIsTooSmallException, OutOfRangeException {
        return walkInDefaultOrder(visitor, start, end);
    }

    public double walkInDefaultOrder(RealVectorChangingVisitor visitor) {
        int dim = getDimension();
        visitor.start(dim, 0, dim - 1);
        for (int i = 0; i < dim; i++) {
            setEntry(i, visitor.visit(i, getEntry(i)));
        }
        return visitor.end();
    }

    public double walkInDefaultOrder(RealVectorChangingVisitor visitor, int start, int end) throws NumberIsTooSmallException, OutOfRangeException {
        checkIndices(start, end);
        visitor.start(getDimension(), start, end);
        for (int i = start; i <= end; i++) {
            setEntry(i, visitor.visit(i, getEntry(i)));
        }
        return visitor.end();
    }

    public double walkInOptimizedOrder(RealVectorChangingVisitor visitor) {
        return walkInDefaultOrder(visitor);
    }

    public double walkInOptimizedOrder(RealVectorChangingVisitor visitor, int start, int end) throws NumberIsTooSmallException, OutOfRangeException {
        return walkInDefaultOrder(visitor, start, end);
    }

    /* access modifiers changed from: protected */
    public class Entry {
        private int index;

        public Entry() {
            setIndex(0);
        }

        public double getValue() {
            return RealVector.this.getEntry(getIndex());
        }

        public void setValue(double value) {
            RealVector.this.setEntry(getIndex(), value);
        }

        public int getIndex() {
            return this.index;
        }

        public void setIndex(int index2) {
            this.index = index2;
        }
    }

    public boolean equals(Object other) throws MathUnsupportedOperationException {
        throw new MathUnsupportedOperationException();
    }

    public int hashCode() throws MathUnsupportedOperationException {
        throw new MathUnsupportedOperationException();
    }

    /* access modifiers changed from: protected */
    public class SparseEntryIterator implements Iterator<Entry> {
        private Entry current;
        private final int dim;
        private Entry next;

        protected SparseEntryIterator() {
            this.dim = RealVector.this.getDimension();
            this.current = new Entry();
            this.next = new Entry();
            if (this.next.getValue() == 0.0d) {
                advance(this.next);
            }
        }

        /* access modifiers changed from: protected */
        public void advance(Entry e) {
            if (e != null) {
                do {
                    e.setIndex(e.getIndex() + 1);
                    if (e.getIndex() >= this.dim) {
                        break;
                    }
                } while (e.getValue() == 0.0d);
                if (e.getIndex() >= this.dim) {
                    e.setIndex(-1);
                }
            }
        }

        public boolean hasNext() {
            return this.next.getIndex() >= 0;
        }

        @Override // java.util.Iterator
        public Entry next() {
            int index = this.next.getIndex();
            if (index < 0) {
                throw new NoSuchElementException();
            }
            this.current.setIndex(index);
            advance(this.next);
            return this.current;
        }

        public void remove() throws MathUnsupportedOperationException {
            throw new MathUnsupportedOperationException();
        }
    }

    public static RealVector unmodifiableRealVector(RealVector v) {
        return new RealVector(v) {
            /* class org.apache.commons.math3.linear.RealVector.C02752 */
            final /* synthetic */ RealVector val$v;

            {
                this.val$v = r1;
            }

            @Override // org.apache.commons.math3.linear.RealVector
            public RealVector mapToSelf(UnivariateFunction function) throws MathUnsupportedOperationException {
                throw new MathUnsupportedOperationException();
            }

            @Override // org.apache.commons.math3.linear.RealVector
            public RealVector map(UnivariateFunction function) {
                return this.val$v.map(function);
            }

            @Override // org.apache.commons.math3.linear.RealVector
            public Iterator<Entry> iterator() {
                final Iterator<Entry> i = this.val$v.iterator();
                return new Iterator<Entry>() {
                    /* class org.apache.commons.math3.linear.RealVector.C02752.C02761 */

                    /* renamed from: e */
                    private final UnmodifiableEntry f227e = new UnmodifiableEntry();

                    public boolean hasNext() {
                        return i.hasNext();
                    }

                    @Override // java.util.Iterator
                    public Entry next() {
                        this.f227e.setIndex(((Entry) i.next()).getIndex());
                        return this.f227e;
                    }

                    public void remove() throws MathUnsupportedOperationException {
                        throw new MathUnsupportedOperationException();
                    }
                };
            }

            @Override // org.apache.commons.math3.linear.RealVector
            public Iterator<Entry> sparseIterator() {
                final Iterator<Entry> i = this.val$v.sparseIterator();
                return new Iterator<Entry>() {
                    /* class org.apache.commons.math3.linear.RealVector.C02752.C02772 */

                    /* renamed from: e */
                    private final UnmodifiableEntry f228e = new UnmodifiableEntry();

                    public boolean hasNext() {
                        return i.hasNext();
                    }

                    @Override // java.util.Iterator
                    public Entry next() {
                        this.f228e.setIndex(((Entry) i.next()).getIndex());
                        return this.f228e;
                    }

                    public void remove() throws MathUnsupportedOperationException {
                        throw new MathUnsupportedOperationException();
                    }
                };
            }

            @Override // org.apache.commons.math3.linear.RealVector
            public RealVector copy() {
                return this.val$v.copy();
            }

            @Override // org.apache.commons.math3.linear.RealVector
            public RealVector add(RealVector w) throws DimensionMismatchException {
                return this.val$v.add(w);
            }

            @Override // org.apache.commons.math3.linear.RealVector
            public RealVector subtract(RealVector w) throws DimensionMismatchException {
                return this.val$v.subtract(w);
            }

            @Override // org.apache.commons.math3.linear.RealVector
            public RealVector mapAdd(double d) {
                return this.val$v.mapAdd(d);
            }

            @Override // org.apache.commons.math3.linear.RealVector
            public RealVector mapAddToSelf(double d) throws MathUnsupportedOperationException {
                throw new MathUnsupportedOperationException();
            }

            @Override // org.apache.commons.math3.linear.RealVector
            public RealVector mapSubtract(double d) {
                return this.val$v.mapSubtract(d);
            }

            @Override // org.apache.commons.math3.linear.RealVector
            public RealVector mapSubtractToSelf(double d) throws MathUnsupportedOperationException {
                throw new MathUnsupportedOperationException();
            }

            @Override // org.apache.commons.math3.linear.RealVector
            public RealVector mapMultiply(double d) {
                return this.val$v.mapMultiply(d);
            }

            @Override // org.apache.commons.math3.linear.RealVector
            public RealVector mapMultiplyToSelf(double d) throws MathUnsupportedOperationException {
                throw new MathUnsupportedOperationException();
            }

            @Override // org.apache.commons.math3.linear.RealVector
            public RealVector mapDivide(double d) {
                return this.val$v.mapDivide(d);
            }

            @Override // org.apache.commons.math3.linear.RealVector
            public RealVector mapDivideToSelf(double d) throws MathUnsupportedOperationException {
                throw new MathUnsupportedOperationException();
            }

            @Override // org.apache.commons.math3.linear.RealVector
            public RealVector ebeMultiply(RealVector w) throws DimensionMismatchException {
                return this.val$v.ebeMultiply(w);
            }

            @Override // org.apache.commons.math3.linear.RealVector
            public RealVector ebeDivide(RealVector w) throws DimensionMismatchException {
                return this.val$v.ebeDivide(w);
            }

            @Override // org.apache.commons.math3.linear.RealVector
            public double dotProduct(RealVector w) throws DimensionMismatchException {
                return this.val$v.dotProduct(w);
            }

            @Override // org.apache.commons.math3.linear.RealVector
            public double cosine(RealVector w) throws DimensionMismatchException, MathArithmeticException {
                return this.val$v.cosine(w);
            }

            @Override // org.apache.commons.math3.linear.RealVector
            public double getNorm() {
                return this.val$v.getNorm();
            }

            @Override // org.apache.commons.math3.linear.RealVector
            public double getL1Norm() {
                return this.val$v.getL1Norm();
            }

            @Override // org.apache.commons.math3.linear.RealVector
            public double getLInfNorm() {
                return this.val$v.getLInfNorm();
            }

            @Override // org.apache.commons.math3.linear.RealVector
            public double getDistance(RealVector w) throws DimensionMismatchException {
                return this.val$v.getDistance(w);
            }

            @Override // org.apache.commons.math3.linear.RealVector
            public double getL1Distance(RealVector w) throws DimensionMismatchException {
                return this.val$v.getL1Distance(w);
            }

            @Override // org.apache.commons.math3.linear.RealVector
            public double getLInfDistance(RealVector w) throws DimensionMismatchException {
                return this.val$v.getLInfDistance(w);
            }

            @Override // org.apache.commons.math3.linear.RealVector
            public RealVector unitVector() throws MathArithmeticException {
                return this.val$v.unitVector();
            }

            @Override // org.apache.commons.math3.linear.RealVector
            public void unitize() throws MathUnsupportedOperationException {
                throw new MathUnsupportedOperationException();
            }

            @Override // org.apache.commons.math3.linear.RealVector
            public RealMatrix outerProduct(RealVector w) {
                return this.val$v.outerProduct(w);
            }

            @Override // org.apache.commons.math3.linear.RealVector
            public double getEntry(int index) throws OutOfRangeException {
                return this.val$v.getEntry(index);
            }

            @Override // org.apache.commons.math3.linear.RealVector
            public void setEntry(int index, double value) throws MathUnsupportedOperationException {
                throw new MathUnsupportedOperationException();
            }

            @Override // org.apache.commons.math3.linear.RealVector
            public void addToEntry(int index, double value) throws MathUnsupportedOperationException {
                throw new MathUnsupportedOperationException();
            }

            @Override // org.apache.commons.math3.linear.RealVector
            public int getDimension() {
                return this.val$v.getDimension();
            }

            @Override // org.apache.commons.math3.linear.RealVector
            public RealVector append(RealVector w) {
                return this.val$v.append(w);
            }

            @Override // org.apache.commons.math3.linear.RealVector
            public RealVector append(double d) {
                return this.val$v.append(d);
            }

            @Override // org.apache.commons.math3.linear.RealVector
            public RealVector getSubVector(int index, int n) throws OutOfRangeException, NotPositiveException {
                return this.val$v.getSubVector(index, n);
            }

            @Override // org.apache.commons.math3.linear.RealVector
            public void setSubVector(int index, RealVector w) throws MathUnsupportedOperationException {
                throw new MathUnsupportedOperationException();
            }

            @Override // org.apache.commons.math3.linear.RealVector
            public void set(double value) throws MathUnsupportedOperationException {
                throw new MathUnsupportedOperationException();
            }

            @Override // org.apache.commons.math3.linear.RealVector
            public double[] toArray() {
                return this.val$v.toArray();
            }

            @Override // org.apache.commons.math3.linear.RealVector
            public boolean isNaN() {
                return this.val$v.isNaN();
            }

            @Override // org.apache.commons.math3.linear.RealVector
            public boolean isInfinite() {
                return this.val$v.isInfinite();
            }

            @Override // org.apache.commons.math3.linear.RealVector
            public RealVector combine(double a, double b, RealVector y) throws DimensionMismatchException {
                return this.val$v.combine(a, b, y);
            }

            @Override // org.apache.commons.math3.linear.RealVector
            public RealVector combineToSelf(double a, double b, RealVector y) throws MathUnsupportedOperationException {
                throw new MathUnsupportedOperationException();
            }

            /* access modifiers changed from: package-private */
            /* renamed from: org.apache.commons.math3.linear.RealVector$2$UnmodifiableEntry */
            public class UnmodifiableEntry extends Entry {
                UnmodifiableEntry() {
                    super();
                }

                @Override // org.apache.commons.math3.linear.RealVector.Entry
                public double getValue() {
                    return C02752.this.val$v.getEntry(getIndex());
                }

                @Override // org.apache.commons.math3.linear.RealVector.Entry
                public void setValue(double value) throws MathUnsupportedOperationException {
                    throw new MathUnsupportedOperationException();
                }
            }
        };
    }
}
