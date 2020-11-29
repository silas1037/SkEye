package org.apache.commons.math3.linear;

import org.apache.commons.math3.exception.MultiDimensionMismatchException;
import org.apache.commons.math3.exception.util.LocalizedFormats;

public class MatrixDimensionMismatchException extends MultiDimensionMismatchException {
    private static final long serialVersionUID = -8415396756375798143L;

    public MatrixDimensionMismatchException(int wrongRowDim, int wrongColDim, int expectedRowDim, int expectedColDim) {
        super(LocalizedFormats.DIMENSIONS_MISMATCH_2x2, new Integer[]{Integer.valueOf(wrongRowDim), Integer.valueOf(wrongColDim)}, new Integer[]{Integer.valueOf(expectedRowDim), Integer.valueOf(expectedColDim)});
    }

    public int getWrongRowDimension() {
        return getWrongDimension(0);
    }

    public int getExpectedRowDimension() {
        return getExpectedDimension(0);
    }

    public int getWrongColumnDimension() {
        return getWrongDimension(1);
    }

    public int getExpectedColumnDimension() {
        return getExpectedDimension(1);
    }
}
