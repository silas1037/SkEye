package org.apache.commons.math3.linear;

import java.io.Serializable;
import java.util.Iterator;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MathArithmeticException;
import org.apache.commons.math3.exception.NotPositiveException;
import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.OpenIntToDoubleHashMap;

public class OpenMapRealVector extends SparseRealVector implements Serializable {
    public static final double DEFAULT_ZERO_TOLERANCE = 1.0E-12d;
    private static final long serialVersionUID = 8772222695580707260L;
    private final OpenIntToDoubleHashMap entries;
    private final double epsilon;
    private final int virtualSize;

    public OpenMapRealVector() {
        this(0, 1.0E-12d);
    }

    public OpenMapRealVector(int dimension) {
        this(dimension, 1.0E-12d);
    }

    public OpenMapRealVector(int dimension, double epsilon2) {
        this.virtualSize = dimension;
        this.entries = new OpenIntToDoubleHashMap(0.0d);
        this.epsilon = epsilon2;
    }

    protected OpenMapRealVector(OpenMapRealVector v, int resize) {
        this.virtualSize = v.getDimension() + resize;
        this.entries = new OpenIntToDoubleHashMap(v.entries);
        this.epsilon = v.epsilon;
    }

    public OpenMapRealVector(int dimension, int expectedSize) {
        this(dimension, expectedSize, 1.0E-12d);
    }

    public OpenMapRealVector(int dimension, int expectedSize, double epsilon2) {
        this.virtualSize = dimension;
        this.entries = new OpenIntToDoubleHashMap(expectedSize, 0.0d);
        this.epsilon = epsilon2;
    }

    public OpenMapRealVector(double[] values) {
        this(values, 1.0E-12d);
    }

    public OpenMapRealVector(double[] values, double epsilon2) {
        this.virtualSize = values.length;
        this.entries = new OpenIntToDoubleHashMap(0.0d);
        this.epsilon = epsilon2;
        for (int key = 0; key < values.length; key++) {
            double value = values[key];
            if (!isDefaultValue(value)) {
                this.entries.put(key, value);
            }
        }
    }

    public OpenMapRealVector(Double[] values) {
        this(values, 1.0E-12d);
    }

    public OpenMapRealVector(Double[] values, double epsilon2) {
        this.virtualSize = values.length;
        this.entries = new OpenIntToDoubleHashMap(0.0d);
        this.epsilon = epsilon2;
        for (int key = 0; key < values.length; key++) {
            double value = values[key].doubleValue();
            if (!isDefaultValue(value)) {
                this.entries.put(key, value);
            }
        }
    }

    public OpenMapRealVector(OpenMapRealVector v) {
        this.virtualSize = v.getDimension();
        this.entries = new OpenIntToDoubleHashMap(v.getEntries());
        this.epsilon = v.epsilon;
    }

    public OpenMapRealVector(RealVector v) {
        this.virtualSize = v.getDimension();
        this.entries = new OpenIntToDoubleHashMap(0.0d);
        this.epsilon = 1.0E-12d;
        for (int key = 0; key < this.virtualSize; key++) {
            double value = v.getEntry(key);
            if (!isDefaultValue(value)) {
                this.entries.put(key, value);
            }
        }
    }

    private OpenIntToDoubleHashMap getEntries() {
        return this.entries;
    }

    /* access modifiers changed from: protected */
    public boolean isDefaultValue(double value) {
        return FastMath.abs(value) < this.epsilon;
    }

    @Override // org.apache.commons.math3.linear.RealVector
    public RealVector add(RealVector v) throws DimensionMismatchException {
        checkVectorDimensions(v.getDimension());
        if (v instanceof OpenMapRealVector) {
            return add((OpenMapRealVector) v);
        }
        return super.add(v);
    }

    public OpenMapRealVector add(OpenMapRealVector v) throws DimensionMismatchException {
        checkVectorDimensions(v.getDimension());
        boolean copyThis = this.entries.size() > v.entries.size();
        OpenMapRealVector res = copyThis ? copy() : v.copy();
        OpenIntToDoubleHashMap.Iterator iter = copyThis ? v.entries.iterator() : this.entries.iterator();
        OpenIntToDoubleHashMap randomAccess = copyThis ? this.entries : v.entries;
        while (iter.hasNext()) {
            iter.advance();
            int key = iter.key();
            if (randomAccess.containsKey(key)) {
                res.setEntry(key, randomAccess.get(key) + iter.value());
            } else {
                res.setEntry(key, iter.value());
            }
        }
        return res;
    }

    public OpenMapRealVector append(OpenMapRealVector v) {
        OpenMapRealVector res = new OpenMapRealVector(this, v.getDimension());
        OpenIntToDoubleHashMap.Iterator iter = v.entries.iterator();
        while (iter.hasNext()) {
            iter.advance();
            res.setEntry(iter.key() + this.virtualSize, iter.value());
        }
        return res;
    }

    @Override // org.apache.commons.math3.linear.RealVector
    public OpenMapRealVector append(RealVector v) {
        if (v instanceof OpenMapRealVector) {
            return append((OpenMapRealVector) v);
        }
        OpenMapRealVector res = new OpenMapRealVector(this, v.getDimension());
        for (int i = 0; i < v.getDimension(); i++) {
            res.setEntry(this.virtualSize + i, v.getEntry(i));
        }
        return res;
    }

    @Override // org.apache.commons.math3.linear.RealVector
    public OpenMapRealVector append(double d) {
        OpenMapRealVector res = new OpenMapRealVector(this, 1);
        res.setEntry(this.virtualSize, d);
        return res;
    }

    @Override // org.apache.commons.math3.linear.RealVector
    public OpenMapRealVector copy() {
        return new OpenMapRealVector(this);
    }

    @Deprecated
    public double dotProduct(OpenMapRealVector v) throws DimensionMismatchException {
        return dotProduct((RealVector) v);
    }

    @Override // org.apache.commons.math3.linear.RealVector
    public OpenMapRealVector ebeDivide(RealVector v) throws DimensionMismatchException {
        checkVectorDimensions(v.getDimension());
        OpenMapRealVector res = new OpenMapRealVector(this);
        int n = getDimension();
        for (int i = 0; i < n; i++) {
            res.setEntry(i, getEntry(i) / v.getEntry(i));
        }
        return res;
    }

    @Override // org.apache.commons.math3.linear.RealVector
    public OpenMapRealVector ebeMultiply(RealVector v) throws DimensionMismatchException {
        checkVectorDimensions(v.getDimension());
        OpenMapRealVector res = new OpenMapRealVector(this);
        OpenIntToDoubleHashMap.Iterator iter = this.entries.iterator();
        while (iter.hasNext()) {
            iter.advance();
            res.setEntry(iter.key(), iter.value() * v.getEntry(iter.key()));
        }
        return res;
    }

    @Override // org.apache.commons.math3.linear.RealVector
    public OpenMapRealVector getSubVector(int index, int n) throws NotPositiveException, OutOfRangeException {
        checkIndex(index);
        if (n < 0) {
            throw new NotPositiveException(LocalizedFormats.NUMBER_OF_ELEMENTS_SHOULD_BE_POSITIVE, Integer.valueOf(n));
        }
        checkIndex((index + n) - 1);
        OpenMapRealVector res = new OpenMapRealVector(n);
        int end = index + n;
        OpenIntToDoubleHashMap.Iterator iter = this.entries.iterator();
        while (iter.hasNext()) {
            iter.advance();
            int key = iter.key();
            if (key >= index && key < end) {
                res.setEntry(key - index, iter.value());
            }
        }
        return res;
    }

    @Override // org.apache.commons.math3.linear.RealVector
    public int getDimension() {
        return this.virtualSize;
    }

    public double getDistance(OpenMapRealVector v) throws DimensionMismatchException {
        checkVectorDimensions(v.getDimension());
        OpenIntToDoubleHashMap.Iterator iter = this.entries.iterator();
        double res = 0.0d;
        while (iter.hasNext()) {
            iter.advance();
            double delta = iter.value() - v.getEntry(iter.key());
            res += delta * delta;
        }
        OpenIntToDoubleHashMap.Iterator iter2 = v.getEntries().iterator();
        while (iter2.hasNext()) {
            iter2.advance();
            if (!this.entries.containsKey(iter2.key())) {
                double value = iter2.value();
                res += value * value;
            }
        }
        return FastMath.sqrt(res);
    }

    @Override // org.apache.commons.math3.linear.RealVector
    public double getDistance(RealVector v) throws DimensionMismatchException {
        checkVectorDimensions(v.getDimension());
        if (v instanceof OpenMapRealVector) {
            return getDistance((OpenMapRealVector) v);
        }
        return super.getDistance(v);
    }

    @Override // org.apache.commons.math3.linear.RealVector
    public double getEntry(int index) throws OutOfRangeException {
        checkIndex(index);
        return this.entries.get(index);
    }

    public double getL1Distance(OpenMapRealVector v) throws DimensionMismatchException {
        checkVectorDimensions(v.getDimension());
        double max = 0.0d;
        OpenIntToDoubleHashMap.Iterator iter = this.entries.iterator();
        while (iter.hasNext()) {
            iter.advance();
            max += FastMath.abs(iter.value() - v.getEntry(iter.key()));
        }
        OpenIntToDoubleHashMap.Iterator iter2 = v.getEntries().iterator();
        while (iter2.hasNext()) {
            iter2.advance();
            if (!this.entries.containsKey(iter2.key())) {
                max += FastMath.abs(FastMath.abs(iter2.value()));
            }
        }
        return max;
    }

    @Override // org.apache.commons.math3.linear.RealVector
    public double getL1Distance(RealVector v) throws DimensionMismatchException {
        checkVectorDimensions(v.getDimension());
        if (v instanceof OpenMapRealVector) {
            return getL1Distance((OpenMapRealVector) v);
        }
        return super.getL1Distance(v);
    }

    private double getLInfDistance(OpenMapRealVector v) throws DimensionMismatchException {
        checkVectorDimensions(v.getDimension());
        double max = 0.0d;
        OpenIntToDoubleHashMap.Iterator iter = this.entries.iterator();
        while (iter.hasNext()) {
            iter.advance();
            double delta = FastMath.abs(iter.value() - v.getEntry(iter.key()));
            if (delta > max) {
                max = delta;
            }
        }
        OpenIntToDoubleHashMap.Iterator iter2 = v.getEntries().iterator();
        while (iter2.hasNext()) {
            iter2.advance();
            if (!this.entries.containsKey(iter2.key()) && iter2.value() > max) {
                max = iter2.value();
            }
        }
        return max;
    }

    @Override // org.apache.commons.math3.linear.RealVector
    public double getLInfDistance(RealVector v) throws DimensionMismatchException {
        checkVectorDimensions(v.getDimension());
        if (v instanceof OpenMapRealVector) {
            return getLInfDistance((OpenMapRealVector) v);
        }
        return super.getLInfDistance(v);
    }

    @Override // org.apache.commons.math3.linear.RealVector
    public boolean isInfinite() {
        boolean infiniteFound = false;
        OpenIntToDoubleHashMap.Iterator iter = this.entries.iterator();
        while (iter.hasNext()) {
            iter.advance();
            double value = iter.value();
            if (Double.isNaN(value)) {
                return false;
            }
            if (Double.isInfinite(value)) {
                infiniteFound = true;
            }
        }
        return infiniteFound;
    }

    @Override // org.apache.commons.math3.linear.RealVector
    public boolean isNaN() {
        OpenIntToDoubleHashMap.Iterator iter = this.entries.iterator();
        while (iter.hasNext()) {
            iter.advance();
            if (Double.isNaN(iter.value())) {
                return true;
            }
        }
        return false;
    }

    @Override // org.apache.commons.math3.linear.RealVector
    public OpenMapRealVector mapAdd(double d) {
        return copy().mapAddToSelf(d);
    }

    @Override // org.apache.commons.math3.linear.RealVector
    public OpenMapRealVector mapAddToSelf(double d) {
        for (int i = 0; i < this.virtualSize; i++) {
            setEntry(i, getEntry(i) + d);
        }
        return this;
    }

    @Override // org.apache.commons.math3.linear.RealVector
    public void setEntry(int index, double value) throws OutOfRangeException {
        checkIndex(index);
        if (!isDefaultValue(value)) {
            this.entries.put(index, value);
        } else if (this.entries.containsKey(index)) {
            this.entries.remove(index);
        }
    }

    @Override // org.apache.commons.math3.linear.RealVector
    public void setSubVector(int index, RealVector v) throws OutOfRangeException {
        checkIndex(index);
        checkIndex((v.getDimension() + index) - 1);
        for (int i = 0; i < v.getDimension(); i++) {
            setEntry(i + index, v.getEntry(i));
        }
    }

    @Override // org.apache.commons.math3.linear.RealVector
    public void set(double value) {
        for (int i = 0; i < this.virtualSize; i++) {
            setEntry(i, value);
        }
    }

    public OpenMapRealVector subtract(OpenMapRealVector v) throws DimensionMismatchException {
        checkVectorDimensions(v.getDimension());
        OpenMapRealVector res = copy();
        OpenIntToDoubleHashMap.Iterator iter = v.getEntries().iterator();
        while (iter.hasNext()) {
            iter.advance();
            int key = iter.key();
            if (this.entries.containsKey(key)) {
                res.setEntry(key, this.entries.get(key) - iter.value());
            } else {
                res.setEntry(key, -iter.value());
            }
        }
        return res;
    }

    @Override // org.apache.commons.math3.linear.RealVector
    public RealVector subtract(RealVector v) throws DimensionMismatchException {
        checkVectorDimensions(v.getDimension());
        if (v instanceof OpenMapRealVector) {
            return subtract((OpenMapRealVector) v);
        }
        return super.subtract(v);
    }

    @Override // org.apache.commons.math3.linear.RealVector
    public OpenMapRealVector unitVector() throws MathArithmeticException {
        OpenMapRealVector res = copy();
        res.unitize();
        return res;
    }

    @Override // org.apache.commons.math3.linear.RealVector
    public void unitize() throws MathArithmeticException {
        double norm = getNorm();
        if (isDefaultValue(norm)) {
            throw new MathArithmeticException(LocalizedFormats.ZERO_NORM, new Object[0]);
        }
        OpenIntToDoubleHashMap.Iterator iter = this.entries.iterator();
        while (iter.hasNext()) {
            iter.advance();
            this.entries.put(iter.key(), iter.value() / norm);
        }
    }

    @Override // org.apache.commons.math3.linear.RealVector
    public double[] toArray() {
        double[] res = new double[this.virtualSize];
        OpenIntToDoubleHashMap.Iterator iter = this.entries.iterator();
        while (iter.hasNext()) {
            iter.advance();
            res[iter.key()] = iter.value();
        }
        return res;
    }

    @Override // org.apache.commons.math3.linear.RealVector
    public int hashCode() {
        long temp = Double.doubleToLongBits(this.epsilon);
        int result = ((((int) ((temp >>> 32) ^ temp)) + 31) * 31) + this.virtualSize;
        OpenIntToDoubleHashMap.Iterator iter = this.entries.iterator();
        while (iter.hasNext()) {
            iter.advance();
            long temp2 = Double.doubleToLongBits(iter.value());
            result = (result * 31) + ((int) ((temp2 >> 32) ^ temp2));
        }
        return result;
    }

    @Override // org.apache.commons.math3.linear.RealVector
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof OpenMapRealVector)) {
            return false;
        }
        OpenMapRealVector other = (OpenMapRealVector) obj;
        if (this.virtualSize != other.virtualSize) {
            return false;
        }
        if (Double.doubleToLongBits(this.epsilon) != Double.doubleToLongBits(other.epsilon)) {
            return false;
        }
        OpenIntToDoubleHashMap.Iterator iter = this.entries.iterator();
        while (iter.hasNext()) {
            iter.advance();
            if (Double.doubleToLongBits(other.getEntry(iter.key())) != Double.doubleToLongBits(iter.value())) {
                return false;
            }
        }
        OpenIntToDoubleHashMap.Iterator iter2 = other.getEntries().iterator();
        while (iter2.hasNext()) {
            iter2.advance();
            if (Double.doubleToLongBits(iter2.value()) != Double.doubleToLongBits(getEntry(iter2.key()))) {
                return false;
            }
        }
        return true;
    }

    public double getSparsity() {
        return ((double) this.entries.size()) / ((double) getDimension());
    }

    @Override // org.apache.commons.math3.linear.RealVector
    public Iterator<RealVector.Entry> sparseIterator() {
        return new OpenMapSparseIterator();
    }

    protected class OpenMapEntry extends RealVector.Entry {
        private final OpenIntToDoubleHashMap.Iterator iter;

        protected OpenMapEntry(OpenIntToDoubleHashMap.Iterator iter2) {
            super();
            this.iter = iter2;
        }

        @Override // org.apache.commons.math3.linear.RealVector.Entry
        public double getValue() {
            return this.iter.value();
        }

        @Override // org.apache.commons.math3.linear.RealVector.Entry
        public void setValue(double value) {
            OpenMapRealVector.this.entries.put(this.iter.key(), value);
        }

        @Override // org.apache.commons.math3.linear.RealVector.Entry
        public int getIndex() {
            return this.iter.key();
        }
    }

    protected class OpenMapSparseIterator implements Iterator<RealVector.Entry> {
        private final RealVector.Entry current;
        private final OpenIntToDoubleHashMap.Iterator iter;

        protected OpenMapSparseIterator() {
            this.iter = OpenMapRealVector.this.entries.iterator();
            this.current = new OpenMapEntry(this.iter);
        }

        public boolean hasNext() {
            return this.iter.hasNext();
        }

        @Override // java.util.Iterator
        public RealVector.Entry next() {
            this.iter.advance();
            return this.current;
        }

        public void remove() {
            throw new UnsupportedOperationException("Not supported");
        }
    }
}
