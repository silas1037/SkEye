package org.apache.commons.math3.linear;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.Arrays;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.NoDataException;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.MathUtils;

public class BlockRealMatrix extends AbstractRealMatrix implements Serializable {
    public static final int BLOCK_SIZE = 52;
    private static final long serialVersionUID = 4991895511313664478L;
    private final int blockColumns;
    private final int blockRows;
    private final double[][] blocks;
    private final int columns;
    private final int rows;

    public BlockRealMatrix(int rows2, int columns2) throws NotStrictlyPositiveException {
        super(rows2, columns2);
        this.rows = rows2;
        this.columns = columns2;
        this.blockRows = ((rows2 + 52) - 1) / 52;
        this.blockColumns = ((columns2 + 52) - 1) / 52;
        this.blocks = createBlocksLayout(rows2, columns2);
    }

    public BlockRealMatrix(double[][] rawData) throws DimensionMismatchException, NotStrictlyPositiveException {
        this(rawData.length, rawData[0].length, toBlocksLayout(rawData), false);
    }

    public BlockRealMatrix(int rows2, int columns2, double[][] blockData, boolean copyArray) throws DimensionMismatchException, NotStrictlyPositiveException {
        super(rows2, columns2);
        this.rows = rows2;
        this.columns = columns2;
        this.blockRows = ((rows2 + 52) - 1) / 52;
        this.blockColumns = ((columns2 + 52) - 1) / 52;
        if (copyArray) {
            this.blocks = new double[(this.blockRows * this.blockColumns)][];
        } else {
            this.blocks = blockData;
        }
        int index = 0;
        for (int iBlock = 0; iBlock < this.blockRows; iBlock++) {
            int iHeight = blockHeight(iBlock);
            int jBlock = 0;
            while (jBlock < this.blockColumns) {
                if (blockData[index].length != blockWidth(jBlock) * iHeight) {
                    throw new DimensionMismatchException(blockData[index].length, blockWidth(jBlock) * iHeight);
                }
                if (copyArray) {
                    this.blocks[index] = (double[]) blockData[index].clone();
                }
                jBlock++;
                index++;
            }
        }
    }

    public static double[][] toBlocksLayout(double[][] rawData) throws DimensionMismatchException {
        int rows2 = rawData.length;
        int columns2 = rawData[0].length;
        int blockRows2 = ((rows2 + 52) - 1) / 52;
        int blockColumns2 = ((columns2 + 52) - 1) / 52;
        for (double[] dArr : rawData) {
            int length = dArr.length;
            if (length != columns2) {
                throw new DimensionMismatchException(columns2, length);
            }
        }
        double[][] blocks2 = new double[(blockRows2 * blockColumns2)][];
        int blockIndex = 0;
        for (int iBlock = 0; iBlock < blockRows2; iBlock++) {
            int pStart = iBlock * 52;
            int pEnd = FastMath.min(pStart + 52, rows2);
            int iHeight = pEnd - pStart;
            for (int jBlock = 0; jBlock < blockColumns2; jBlock++) {
                int qStart = jBlock * 52;
                int jWidth = FastMath.min(qStart + 52, columns2) - qStart;
                double[] block = new double[(iHeight * jWidth)];
                blocks2[blockIndex] = block;
                int index = 0;
                for (int p = pStart; p < pEnd; p++) {
                    System.arraycopy(rawData[p], qStart, block, index, jWidth);
                    index += jWidth;
                }
                blockIndex++;
            }
        }
        return blocks2;
    }

    public static double[][] createBlocksLayout(int rows2, int columns2) {
        int blockRows2 = ((rows2 + 52) - 1) / 52;
        int blockColumns2 = ((columns2 + 52) - 1) / 52;
        double[][] blocks2 = new double[(blockRows2 * blockColumns2)][];
        int blockIndex = 0;
        for (int iBlock = 0; iBlock < blockRows2; iBlock++) {
            int pStart = iBlock * 52;
            int iHeight = FastMath.min(pStart + 52, rows2) - pStart;
            for (int jBlock = 0; jBlock < blockColumns2; jBlock++) {
                int qStart = jBlock * 52;
                blocks2[blockIndex] = new double[(iHeight * (FastMath.min(qStart + 52, columns2) - qStart))];
                blockIndex++;
            }
        }
        return blocks2;
    }

    @Override // org.apache.commons.math3.linear.AbstractRealMatrix, org.apache.commons.math3.linear.RealMatrix
    public BlockRealMatrix createMatrix(int rowDimension, int columnDimension) throws NotStrictlyPositiveException {
        return new BlockRealMatrix(rowDimension, columnDimension);
    }

    @Override // org.apache.commons.math3.linear.AbstractRealMatrix, org.apache.commons.math3.linear.RealMatrix
    public BlockRealMatrix copy() {
        BlockRealMatrix copied = new BlockRealMatrix(this.rows, this.columns);
        for (int i = 0; i < this.blocks.length; i++) {
            System.arraycopy(this.blocks[i], 0, copied.blocks[i], 0, this.blocks[i].length);
        }
        return copied;
    }

    @Override // org.apache.commons.math3.linear.AbstractRealMatrix, org.apache.commons.math3.linear.RealMatrix
    public BlockRealMatrix add(RealMatrix m) throws MatrixDimensionMismatchException {
        try {
            return add((BlockRealMatrix) m);
        } catch (ClassCastException e) {
            MatrixUtils.checkAdditionCompatible(this, m);
            BlockRealMatrix out = new BlockRealMatrix(this.rows, this.columns);
            int blockIndex = 0;
            for (int iBlock = 0; iBlock < out.blockRows; iBlock++) {
                for (int jBlock = 0; jBlock < out.blockColumns; jBlock++) {
                    double[] outBlock = out.blocks[blockIndex];
                    double[] tBlock = this.blocks[blockIndex];
                    int pStart = iBlock * 52;
                    int pEnd = FastMath.min(pStart + 52, this.rows);
                    int qStart = jBlock * 52;
                    int qEnd = FastMath.min(qStart + 52, this.columns);
                    int k = 0;
                    for (int p = pStart; p < pEnd; p++) {
                        for (int q = qStart; q < qEnd; q++) {
                            outBlock[k] = tBlock[k] + m.getEntry(p, q);
                            k++;
                        }
                    }
                    blockIndex++;
                }
            }
            return out;
        }
    }

    public BlockRealMatrix add(BlockRealMatrix m) throws MatrixDimensionMismatchException {
        MatrixUtils.checkAdditionCompatible(this, m);
        BlockRealMatrix out = new BlockRealMatrix(this.rows, this.columns);
        for (int blockIndex = 0; blockIndex < out.blocks.length; blockIndex++) {
            double[] outBlock = out.blocks[blockIndex];
            double[] tBlock = this.blocks[blockIndex];
            double[] mBlock = m.blocks[blockIndex];
            for (int k = 0; k < outBlock.length; k++) {
                outBlock[k] = tBlock[k] + mBlock[k];
            }
        }
        return out;
    }

    @Override // org.apache.commons.math3.linear.AbstractRealMatrix, org.apache.commons.math3.linear.RealMatrix
    public BlockRealMatrix subtract(RealMatrix m) throws MatrixDimensionMismatchException {
        try {
            return subtract((BlockRealMatrix) m);
        } catch (ClassCastException e) {
            MatrixUtils.checkSubtractionCompatible(this, m);
            BlockRealMatrix out = new BlockRealMatrix(this.rows, this.columns);
            int blockIndex = 0;
            for (int iBlock = 0; iBlock < out.blockRows; iBlock++) {
                for (int jBlock = 0; jBlock < out.blockColumns; jBlock++) {
                    double[] outBlock = out.blocks[blockIndex];
                    double[] tBlock = this.blocks[blockIndex];
                    int pStart = iBlock * 52;
                    int pEnd = FastMath.min(pStart + 52, this.rows);
                    int qStart = jBlock * 52;
                    int qEnd = FastMath.min(qStart + 52, this.columns);
                    int k = 0;
                    for (int p = pStart; p < pEnd; p++) {
                        for (int q = qStart; q < qEnd; q++) {
                            outBlock[k] = tBlock[k] - m.getEntry(p, q);
                            k++;
                        }
                    }
                    blockIndex++;
                }
            }
            return out;
        }
    }

    public BlockRealMatrix subtract(BlockRealMatrix m) throws MatrixDimensionMismatchException {
        MatrixUtils.checkSubtractionCompatible(this, m);
        BlockRealMatrix out = new BlockRealMatrix(this.rows, this.columns);
        for (int blockIndex = 0; blockIndex < out.blocks.length; blockIndex++) {
            double[] outBlock = out.blocks[blockIndex];
            double[] tBlock = this.blocks[blockIndex];
            double[] mBlock = m.blocks[blockIndex];
            for (int k = 0; k < outBlock.length; k++) {
                outBlock[k] = tBlock[k] - mBlock[k];
            }
        }
        return out;
    }

    @Override // org.apache.commons.math3.linear.AbstractRealMatrix, org.apache.commons.math3.linear.RealMatrix
    public BlockRealMatrix scalarAdd(double d) {
        BlockRealMatrix out = new BlockRealMatrix(this.rows, this.columns);
        for (int blockIndex = 0; blockIndex < out.blocks.length; blockIndex++) {
            double[] outBlock = out.blocks[blockIndex];
            double[] tBlock = this.blocks[blockIndex];
            for (int k = 0; k < outBlock.length; k++) {
                outBlock[k] = tBlock[k] + d;
            }
        }
        return out;
    }

    @Override // org.apache.commons.math3.linear.AbstractRealMatrix, org.apache.commons.math3.linear.RealMatrix
    public RealMatrix scalarMultiply(double d) {
        BlockRealMatrix out = new BlockRealMatrix(this.rows, this.columns);
        for (int blockIndex = 0; blockIndex < out.blocks.length; blockIndex++) {
            double[] outBlock = out.blocks[blockIndex];
            double[] tBlock = this.blocks[blockIndex];
            for (int k = 0; k < outBlock.length; k++) {
                outBlock[k] = tBlock[k] * d;
            }
        }
        return out;
    }

    @Override // org.apache.commons.math3.linear.AbstractRealMatrix, org.apache.commons.math3.linear.RealMatrix
    public BlockRealMatrix multiply(RealMatrix m) throws DimensionMismatchException {
        try {
            return multiply((BlockRealMatrix) m);
        } catch (ClassCastException e) {
            MatrixUtils.checkMultiplicationCompatible(this, m);
            BlockRealMatrix out = new BlockRealMatrix(this.rows, m.getColumnDimension());
            int blockIndex = 0;
            for (int iBlock = 0; iBlock < out.blockRows; iBlock++) {
                int pStart = iBlock * 52;
                int pEnd = FastMath.min(pStart + 52, this.rows);
                for (int jBlock = 0; jBlock < out.blockColumns; jBlock++) {
                    int qStart = jBlock * 52;
                    int qEnd = FastMath.min(qStart + 52, m.getColumnDimension());
                    double[] outBlock = out.blocks[blockIndex];
                    for (int kBlock = 0; kBlock < this.blockColumns; kBlock++) {
                        int kWidth = blockWidth(kBlock);
                        double[] tBlock = this.blocks[(this.blockColumns * iBlock) + kBlock];
                        int rStart = kBlock * 52;
                        int k = 0;
                        for (int p = pStart; p < pEnd; p++) {
                            int lStart = (p - pStart) * kWidth;
                            int lEnd = lStart + kWidth;
                            for (int q = qStart; q < qEnd; q++) {
                                double sum = 0.0d;
                                int r = rStart;
                                for (int l = lStart; l < lEnd; l++) {
                                    sum += tBlock[l] * m.getEntry(r, q);
                                    r++;
                                }
                                outBlock[k] = outBlock[k] + sum;
                                k++;
                            }
                        }
                    }
                    blockIndex++;
                }
            }
            return out;
        }
    }

    public BlockRealMatrix multiply(BlockRealMatrix m) throws DimensionMismatchException {
        MatrixUtils.checkMultiplicationCompatible(this, m);
        BlockRealMatrix out = new BlockRealMatrix(this.rows, m.columns);
        int blockIndex = 0;
        for (int iBlock = 0; iBlock < out.blockRows; iBlock++) {
            int pStart = iBlock * 52;
            int pEnd = FastMath.min(pStart + 52, this.rows);
            for (int jBlock = 0; jBlock < out.blockColumns; jBlock++) {
                int jWidth = out.blockWidth(jBlock);
                int jWidth2 = jWidth + jWidth;
                int jWidth3 = jWidth2 + jWidth;
                int jWidth4 = jWidth3 + jWidth;
                double[] outBlock = out.blocks[blockIndex];
                for (int kBlock = 0; kBlock < this.blockColumns; kBlock++) {
                    int kWidth = blockWidth(kBlock);
                    double[] tBlock = this.blocks[(this.blockColumns * iBlock) + kBlock];
                    double[] mBlock = m.blocks[(m.blockColumns * kBlock) + jBlock];
                    int k = 0;
                    for (int p = pStart; p < pEnd; p++) {
                        int lStart = (p - pStart) * kWidth;
                        int lEnd = lStart + kWidth;
                        for (int nStart = 0; nStart < jWidth; nStart++) {
                            double sum = 0.0d;
                            int l = lStart;
                            int n = nStart;
                            while (l < lEnd - 3) {
                                sum += (tBlock[l] * mBlock[n]) + (tBlock[l + 1] * mBlock[n + jWidth]) + (tBlock[l + 2] * mBlock[n + jWidth2]) + (tBlock[l + 3] * mBlock[n + jWidth3]);
                                l += 4;
                                n += jWidth4;
                            }
                            while (l < lEnd) {
                                l++;
                                sum += tBlock[l] * mBlock[n];
                                n += jWidth;
                            }
                            outBlock[k] = outBlock[k] + sum;
                            k++;
                        }
                    }
                }
                blockIndex++;
            }
        }
        return out;
    }

    @Override // org.apache.commons.math3.linear.AbstractRealMatrix, org.apache.commons.math3.linear.RealMatrix
    public double[][] getData() {
        double[][] data = (double[][]) Array.newInstance(Double.TYPE, getRowDimension(), getColumnDimension());
        int lastColumns = this.columns - ((this.blockColumns - 1) * 52);
        for (int iBlock = 0; iBlock < this.blockRows; iBlock++) {
            int pStart = iBlock * 52;
            int pEnd = FastMath.min(pStart + 52, this.rows);
            int regularPos = 0;
            int lastPos = 0;
            for (int p = pStart; p < pEnd; p++) {
                double[] dataP = data[p];
                int blockIndex = iBlock * this.blockColumns;
                int dataPos = 0;
                int jBlock = 0;
                while (jBlock < this.blockColumns - 1) {
                    System.arraycopy(this.blocks[blockIndex], regularPos, dataP, dataPos, 52);
                    dataPos += 52;
                    jBlock++;
                    blockIndex++;
                }
                System.arraycopy(this.blocks[blockIndex], lastPos, dataP, dataPos, lastColumns);
                regularPos += 52;
                lastPos += lastColumns;
            }
        }
        return data;
    }

    @Override // org.apache.commons.math3.linear.AbstractRealMatrix, org.apache.commons.math3.linear.RealMatrix
    public double getNorm() {
        double[] colSums = new double[52];
        double maxColSum = 0.0d;
        for (int jBlock = 0; jBlock < this.blockColumns; jBlock++) {
            int jWidth = blockWidth(jBlock);
            Arrays.fill(colSums, 0, jWidth, 0.0d);
            for (int iBlock = 0; iBlock < this.blockRows; iBlock++) {
                int iHeight = blockHeight(iBlock);
                double[] block = this.blocks[(this.blockColumns * iBlock) + jBlock];
                for (int j = 0; j < jWidth; j++) {
                    double sum = 0.0d;
                    for (int i = 0; i < iHeight; i++) {
                        sum += FastMath.abs(block[(i * jWidth) + j]);
                    }
                    colSums[j] = colSums[j] + sum;
                }
            }
            for (int j2 = 0; j2 < jWidth; j2++) {
                maxColSum = FastMath.max(maxColSum, colSums[j2]);
            }
        }
        return maxColSum;
    }

    @Override // org.apache.commons.math3.linear.AbstractRealMatrix, org.apache.commons.math3.linear.RealMatrix
    public double getFrobeniusNorm() {
        double sum2 = 0.0d;
        for (int blockIndex = 0; blockIndex < this.blocks.length; blockIndex++) {
            double[] arr$ = this.blocks[blockIndex];
            for (double entry : arr$) {
                sum2 += entry * entry;
            }
        }
        return FastMath.sqrt(sum2);
    }

    @Override // org.apache.commons.math3.linear.AbstractRealMatrix, org.apache.commons.math3.linear.RealMatrix
    public BlockRealMatrix getSubMatrix(int startRow, int endRow, int startColumn, int endColumn) throws OutOfRangeException, NumberIsTooSmallException {
        MatrixUtils.checkSubMatrixIndex(this, startRow, endRow, startColumn, endColumn);
        BlockRealMatrix out = new BlockRealMatrix((endRow - startRow) + 1, (endColumn - startColumn) + 1);
        int rowsShift = startRow % 52;
        int blockStartColumn = startColumn / 52;
        int columnsShift = startColumn % 52;
        int pBlock = startRow / 52;
        for (int iBlock = 0; iBlock < out.blockRows; iBlock++) {
            int iHeight = out.blockHeight(iBlock);
            int qBlock = blockStartColumn;
            for (int jBlock = 0; jBlock < out.blockColumns; jBlock++) {
                int jWidth = out.blockWidth(jBlock);
                double[] outBlock = out.blocks[(out.blockColumns * iBlock) + jBlock];
                int index = (this.blockColumns * pBlock) + qBlock;
                int width = blockWidth(qBlock);
                int heightExcess = (iHeight + rowsShift) - 52;
                int widthExcess = (jWidth + columnsShift) - 52;
                if (heightExcess > 0) {
                    if (widthExcess > 0) {
                        int width2 = blockWidth(qBlock + 1);
                        copyBlockPart(this.blocks[index], width, rowsShift, 52, columnsShift, 52, outBlock, jWidth, 0, 0);
                        copyBlockPart(this.blocks[index + 1], width2, rowsShift, 52, 0, widthExcess, outBlock, jWidth, 0, jWidth - widthExcess);
                        copyBlockPart(this.blocks[this.blockColumns + index], width, 0, heightExcess, columnsShift, 52, outBlock, jWidth, iHeight - heightExcess, 0);
                        copyBlockPart(this.blocks[this.blockColumns + index + 1], width2, 0, heightExcess, 0, widthExcess, outBlock, jWidth, iHeight - heightExcess, jWidth - widthExcess);
                    } else {
                        copyBlockPart(this.blocks[index], width, rowsShift, 52, columnsShift, jWidth + columnsShift, outBlock, jWidth, 0, 0);
                        copyBlockPart(this.blocks[this.blockColumns + index], width, 0, heightExcess, columnsShift, jWidth + columnsShift, outBlock, jWidth, iHeight - heightExcess, 0);
                    }
                } else if (widthExcess > 0) {
                    int width22 = blockWidth(qBlock + 1);
                    copyBlockPart(this.blocks[index], width, rowsShift, iHeight + rowsShift, columnsShift, 52, outBlock, jWidth, 0, 0);
                    copyBlockPart(this.blocks[index + 1], width22, rowsShift, iHeight + rowsShift, 0, widthExcess, outBlock, jWidth, 0, jWidth - widthExcess);
                } else {
                    copyBlockPart(this.blocks[index], width, rowsShift, iHeight + rowsShift, columnsShift, jWidth + columnsShift, outBlock, jWidth, 0, 0);
                }
                qBlock++;
            }
            pBlock++;
        }
        return out;
    }

    private void copyBlockPart(double[] srcBlock, int srcWidth, int srcStartRow, int srcEndRow, int srcStartColumn, int srcEndColumn, double[] dstBlock, int dstWidth, int dstStartRow, int dstStartColumn) {
        int length = srcEndColumn - srcStartColumn;
        int srcPos = (srcStartRow * srcWidth) + srcStartColumn;
        int dstPos = (dstStartRow * dstWidth) + dstStartColumn;
        for (int srcRow = srcStartRow; srcRow < srcEndRow; srcRow++) {
            System.arraycopy(srcBlock, srcPos, dstBlock, dstPos, length);
            srcPos += srcWidth;
            dstPos += dstWidth;
        }
    }

    @Override // org.apache.commons.math3.linear.AbstractRealMatrix, org.apache.commons.math3.linear.RealMatrix
    public void setSubMatrix(double[][] subMatrix, int row, int column) throws OutOfRangeException, NoDataException, NullArgumentException, DimensionMismatchException {
        MathUtils.checkNotNull(subMatrix);
        int refLength = subMatrix[0].length;
        if (refLength == 0) {
            throw new NoDataException(LocalizedFormats.AT_LEAST_ONE_COLUMN);
        }
        int endRow = (subMatrix.length + row) - 1;
        int endColumn = (column + refLength) - 1;
        MatrixUtils.checkSubMatrixIndex(this, row, endRow, column, endColumn);
        for (double[] subRow : subMatrix) {
            if (subRow.length != refLength) {
                throw new DimensionMismatchException(refLength, subRow.length);
            }
        }
        int blockEndRow = (endRow + 52) / 52;
        int blockStartColumn = column / 52;
        int blockEndColumn = (endColumn + 52) / 52;
        for (int iBlock = row / 52; iBlock < blockEndRow; iBlock++) {
            int iHeight = blockHeight(iBlock);
            int firstRow = iBlock * 52;
            int iStart = FastMath.max(row, firstRow);
            int iEnd = FastMath.min(endRow + 1, firstRow + iHeight);
            for (int jBlock = blockStartColumn; jBlock < blockEndColumn; jBlock++) {
                int jWidth = blockWidth(jBlock);
                int firstColumn = jBlock * 52;
                int jStart = FastMath.max(column, firstColumn);
                int jLength = FastMath.min(endColumn + 1, firstColumn + jWidth) - jStart;
                double[] block = this.blocks[(this.blockColumns * iBlock) + jBlock];
                for (int i = iStart; i < iEnd; i++) {
                    System.arraycopy(subMatrix[i - row], jStart - column, block, ((i - firstRow) * jWidth) + (jStart - firstColumn), jLength);
                }
            }
        }
    }

    @Override // org.apache.commons.math3.linear.AbstractRealMatrix, org.apache.commons.math3.linear.RealMatrix
    public BlockRealMatrix getRowMatrix(int row) throws OutOfRangeException {
        MatrixUtils.checkRowIndex(this, row);
        BlockRealMatrix out = new BlockRealMatrix(1, this.columns);
        int iBlock = row / 52;
        int iRow = row - (iBlock * 52);
        int outBlockIndex = 0;
        int outIndex = 0;
        double[] outBlock = out.blocks[0];
        for (int jBlock = 0; jBlock < this.blockColumns; jBlock++) {
            int jWidth = blockWidth(jBlock);
            double[] block = this.blocks[(this.blockColumns * iBlock) + jBlock];
            int available = outBlock.length - outIndex;
            if (jWidth > available) {
                System.arraycopy(block, iRow * jWidth, outBlock, outIndex, available);
                outBlockIndex++;
                outBlock = out.blocks[outBlockIndex];
                System.arraycopy(block, iRow * jWidth, outBlock, 0, jWidth - available);
                outIndex = jWidth - available;
            } else {
                System.arraycopy(block, iRow * jWidth, outBlock, outIndex, jWidth);
                outIndex += jWidth;
            }
        }
        return out;
    }

    @Override // org.apache.commons.math3.linear.AbstractRealMatrix, org.apache.commons.math3.linear.RealMatrix
    public void setRowMatrix(int row, RealMatrix matrix) throws OutOfRangeException, MatrixDimensionMismatchException {
        try {
            setRowMatrix(row, (BlockRealMatrix) matrix);
        } catch (ClassCastException e) {
            super.setRowMatrix(row, matrix);
        }
    }

    public void setRowMatrix(int row, BlockRealMatrix matrix) throws OutOfRangeException, MatrixDimensionMismatchException {
        MatrixUtils.checkRowIndex(this, row);
        int nCols = getColumnDimension();
        if (matrix.getRowDimension() == 1 && matrix.getColumnDimension() == nCols) {
            int iBlock = row / 52;
            int iRow = row - (iBlock * 52);
            int mBlockIndex = 0;
            int mIndex = 0;
            double[] mBlock = matrix.blocks[0];
            for (int jBlock = 0; jBlock < this.blockColumns; jBlock++) {
                int jWidth = blockWidth(jBlock);
                double[] block = this.blocks[(this.blockColumns * iBlock) + jBlock];
                int available = mBlock.length - mIndex;
                if (jWidth > available) {
                    System.arraycopy(mBlock, mIndex, block, iRow * jWidth, available);
                    mBlockIndex++;
                    mBlock = matrix.blocks[mBlockIndex];
                    System.arraycopy(mBlock, 0, block, iRow * jWidth, jWidth - available);
                    mIndex = jWidth - available;
                } else {
                    System.arraycopy(mBlock, mIndex, block, iRow * jWidth, jWidth);
                    mIndex += jWidth;
                }
            }
            return;
        }
        throw new MatrixDimensionMismatchException(matrix.getRowDimension(), matrix.getColumnDimension(), 1, nCols);
    }

    @Override // org.apache.commons.math3.linear.AbstractRealMatrix, org.apache.commons.math3.linear.RealMatrix
    public BlockRealMatrix getColumnMatrix(int column) throws OutOfRangeException {
        MatrixUtils.checkColumnIndex(this, column);
        BlockRealMatrix out = new BlockRealMatrix(this.rows, 1);
        int jBlock = column / 52;
        int jColumn = column - (jBlock * 52);
        int jWidth = blockWidth(jBlock);
        int outBlockIndex = 0;
        int outIndex = 0;
        double[] outBlock = out.blocks[0];
        for (int iBlock = 0; iBlock < this.blockRows; iBlock++) {
            int iHeight = blockHeight(iBlock);
            double[] block = this.blocks[(this.blockColumns * iBlock) + jBlock];
            int i = 0;
            while (i < iHeight) {
                if (outIndex >= outBlock.length) {
                    outBlockIndex++;
                    outBlock = out.blocks[outBlockIndex];
                    outIndex = 0;
                }
                outBlock[outIndex] = block[(i * jWidth) + jColumn];
                i++;
                outIndex++;
            }
        }
        return out;
    }

    @Override // org.apache.commons.math3.linear.AbstractRealMatrix, org.apache.commons.math3.linear.RealMatrix
    public void setColumnMatrix(int column, RealMatrix matrix) throws OutOfRangeException, MatrixDimensionMismatchException {
        try {
            setColumnMatrix(column, (BlockRealMatrix) matrix);
        } catch (ClassCastException e) {
            super.setColumnMatrix(column, matrix);
        }
    }

    /* access modifiers changed from: package-private */
    public void setColumnMatrix(int column, BlockRealMatrix matrix) throws OutOfRangeException, MatrixDimensionMismatchException {
        MatrixUtils.checkColumnIndex(this, column);
        int nRows = getRowDimension();
        if (matrix.getRowDimension() == nRows && matrix.getColumnDimension() == 1) {
            int jBlock = column / 52;
            int jColumn = column - (jBlock * 52);
            int jWidth = blockWidth(jBlock);
            int mBlockIndex = 0;
            int mIndex = 0;
            double[] mBlock = matrix.blocks[0];
            for (int iBlock = 0; iBlock < this.blockRows; iBlock++) {
                int iHeight = blockHeight(iBlock);
                double[] block = this.blocks[(this.blockColumns * iBlock) + jBlock];
                int i = 0;
                while (i < iHeight) {
                    if (mIndex >= mBlock.length) {
                        mBlockIndex++;
                        mBlock = matrix.blocks[mBlockIndex];
                        mIndex = 0;
                    }
                    block[(i * jWidth) + jColumn] = mBlock[mIndex];
                    i++;
                    mIndex++;
                }
            }
            return;
        }
        throw new MatrixDimensionMismatchException(matrix.getRowDimension(), matrix.getColumnDimension(), nRows, 1);
    }

    @Override // org.apache.commons.math3.linear.AbstractRealMatrix, org.apache.commons.math3.linear.RealMatrix
    public RealVector getRowVector(int row) throws OutOfRangeException {
        MatrixUtils.checkRowIndex(this, row);
        double[] outData = new double[this.columns];
        int iBlock = row / 52;
        int iRow = row - (iBlock * 52);
        int outIndex = 0;
        for (int jBlock = 0; jBlock < this.blockColumns; jBlock++) {
            int jWidth = blockWidth(jBlock);
            System.arraycopy(this.blocks[(this.blockColumns * iBlock) + jBlock], iRow * jWidth, outData, outIndex, jWidth);
            outIndex += jWidth;
        }
        return new ArrayRealVector(outData, false);
    }

    @Override // org.apache.commons.math3.linear.AbstractRealMatrix, org.apache.commons.math3.linear.RealMatrix
    public void setRowVector(int row, RealVector vector) throws OutOfRangeException, MatrixDimensionMismatchException {
        try {
            setRow(row, ((ArrayRealVector) vector).getDataRef());
        } catch (ClassCastException e) {
            super.setRowVector(row, vector);
        }
    }

    @Override // org.apache.commons.math3.linear.AbstractRealMatrix, org.apache.commons.math3.linear.RealMatrix
    public RealVector getColumnVector(int column) throws OutOfRangeException {
        MatrixUtils.checkColumnIndex(this, column);
        double[] outData = new double[this.rows];
        int jBlock = column / 52;
        int jColumn = column - (jBlock * 52);
        int jWidth = blockWidth(jBlock);
        int outIndex = 0;
        int iBlock = 0;
        while (iBlock < this.blockRows) {
            int iHeight = blockHeight(iBlock);
            double[] block = this.blocks[(this.blockColumns * iBlock) + jBlock];
            int i = 0;
            int outIndex2 = outIndex;
            while (i < iHeight) {
                outData[outIndex2] = block[(i * jWidth) + jColumn];
                i++;
                outIndex2++;
            }
            iBlock++;
            outIndex = outIndex2;
        }
        return new ArrayRealVector(outData, false);
    }

    @Override // org.apache.commons.math3.linear.AbstractRealMatrix, org.apache.commons.math3.linear.RealMatrix
    public void setColumnVector(int column, RealVector vector) throws OutOfRangeException, MatrixDimensionMismatchException {
        try {
            setColumn(column, ((ArrayRealVector) vector).getDataRef());
        } catch (ClassCastException e) {
            super.setColumnVector(column, vector);
        }
    }

    @Override // org.apache.commons.math3.linear.AbstractRealMatrix, org.apache.commons.math3.linear.RealMatrix
    public double[] getRow(int row) throws OutOfRangeException {
        MatrixUtils.checkRowIndex(this, row);
        double[] out = new double[this.columns];
        int iBlock = row / 52;
        int iRow = row - (iBlock * 52);
        int outIndex = 0;
        for (int jBlock = 0; jBlock < this.blockColumns; jBlock++) {
            int jWidth = blockWidth(jBlock);
            System.arraycopy(this.blocks[(this.blockColumns * iBlock) + jBlock], iRow * jWidth, out, outIndex, jWidth);
            outIndex += jWidth;
        }
        return out;
    }

    @Override // org.apache.commons.math3.linear.AbstractRealMatrix, org.apache.commons.math3.linear.RealMatrix
    public void setRow(int row, double[] array) throws OutOfRangeException, MatrixDimensionMismatchException {
        MatrixUtils.checkRowIndex(this, row);
        int nCols = getColumnDimension();
        if (array.length != nCols) {
            throw new MatrixDimensionMismatchException(1, array.length, 1, nCols);
        }
        int iBlock = row / 52;
        int iRow = row - (iBlock * 52);
        int outIndex = 0;
        for (int jBlock = 0; jBlock < this.blockColumns; jBlock++) {
            int jWidth = blockWidth(jBlock);
            System.arraycopy(array, outIndex, this.blocks[(this.blockColumns * iBlock) + jBlock], iRow * jWidth, jWidth);
            outIndex += jWidth;
        }
    }

    @Override // org.apache.commons.math3.linear.AbstractRealMatrix, org.apache.commons.math3.linear.RealMatrix
    public double[] getColumn(int column) throws OutOfRangeException {
        MatrixUtils.checkColumnIndex(this, column);
        double[] out = new double[this.rows];
        int jBlock = column / 52;
        int jColumn = column - (jBlock * 52);
        int jWidth = blockWidth(jBlock);
        int outIndex = 0;
        int iBlock = 0;
        while (iBlock < this.blockRows) {
            int iHeight = blockHeight(iBlock);
            double[] block = this.blocks[(this.blockColumns * iBlock) + jBlock];
            int i = 0;
            int outIndex2 = outIndex;
            while (i < iHeight) {
                out[outIndex2] = block[(i * jWidth) + jColumn];
                i++;
                outIndex2++;
            }
            iBlock++;
            outIndex = outIndex2;
        }
        return out;
    }

    @Override // org.apache.commons.math3.linear.AbstractRealMatrix, org.apache.commons.math3.linear.RealMatrix
    public void setColumn(int column, double[] array) throws OutOfRangeException, MatrixDimensionMismatchException {
        MatrixUtils.checkColumnIndex(this, column);
        int nRows = getRowDimension();
        if (array.length != nRows) {
            throw new MatrixDimensionMismatchException(array.length, 1, nRows, 1);
        }
        int jBlock = column / 52;
        int jColumn = column - (jBlock * 52);
        int jWidth = blockWidth(jBlock);
        int outIndex = 0;
        int iBlock = 0;
        while (iBlock < this.blockRows) {
            int iHeight = blockHeight(iBlock);
            double[] block = this.blocks[(this.blockColumns * iBlock) + jBlock];
            int i = 0;
            int outIndex2 = outIndex;
            while (i < iHeight) {
                block[(i * jWidth) + jColumn] = array[outIndex2];
                i++;
                outIndex2++;
            }
            iBlock++;
            outIndex = outIndex2;
        }
    }

    @Override // org.apache.commons.math3.linear.AbstractRealMatrix, org.apache.commons.math3.linear.RealMatrix
    public double getEntry(int row, int column) throws OutOfRangeException {
        MatrixUtils.checkMatrixIndex(this, row, column);
        int iBlock = row / 52;
        int jBlock = column / 52;
        return this.blocks[(this.blockColumns * iBlock) + jBlock][((row - (iBlock * 52)) * blockWidth(jBlock)) + (column - (jBlock * 52))];
    }

    @Override // org.apache.commons.math3.linear.AbstractRealMatrix, org.apache.commons.math3.linear.RealMatrix
    public void setEntry(int row, int column, double value) throws OutOfRangeException {
        MatrixUtils.checkMatrixIndex(this, row, column);
        int iBlock = row / 52;
        int jBlock = column / 52;
        this.blocks[(this.blockColumns * iBlock) + jBlock][((row - (iBlock * 52)) * blockWidth(jBlock)) + (column - (jBlock * 52))] = value;
    }

    @Override // org.apache.commons.math3.linear.AbstractRealMatrix, org.apache.commons.math3.linear.RealMatrix
    public void addToEntry(int row, int column, double increment) throws OutOfRangeException {
        MatrixUtils.checkMatrixIndex(this, row, column);
        int iBlock = row / 52;
        int jBlock = column / 52;
        int k = ((row - (iBlock * 52)) * blockWidth(jBlock)) + (column - (jBlock * 52));
        double[] dArr = this.blocks[(this.blockColumns * iBlock) + jBlock];
        dArr[k] = dArr[k] + increment;
    }

    @Override // org.apache.commons.math3.linear.AbstractRealMatrix, org.apache.commons.math3.linear.RealMatrix
    public void multiplyEntry(int row, int column, double factor) throws OutOfRangeException {
        MatrixUtils.checkMatrixIndex(this, row, column);
        int iBlock = row / 52;
        int jBlock = column / 52;
        int k = ((row - (iBlock * 52)) * blockWidth(jBlock)) + (column - (jBlock * 52));
        double[] dArr = this.blocks[(this.blockColumns * iBlock) + jBlock];
        dArr[k] = dArr[k] * factor;
    }

    @Override // org.apache.commons.math3.linear.AbstractRealMatrix, org.apache.commons.math3.linear.RealMatrix
    public BlockRealMatrix transpose() {
        BlockRealMatrix out = new BlockRealMatrix(getColumnDimension(), getRowDimension());
        int blockIndex = 0;
        for (int iBlock = 0; iBlock < this.blockColumns; iBlock++) {
            for (int jBlock = 0; jBlock < this.blockRows; jBlock++) {
                double[] outBlock = out.blocks[blockIndex];
                double[] tBlock = this.blocks[(this.blockColumns * jBlock) + iBlock];
                int pStart = iBlock * 52;
                int pEnd = FastMath.min(pStart + 52, this.columns);
                int qStart = jBlock * 52;
                int qEnd = FastMath.min(qStart + 52, this.rows);
                int k = 0;
                for (int p = pStart; p < pEnd; p++) {
                    int lInc = pEnd - pStart;
                    int l = p - pStart;
                    for (int q = qStart; q < qEnd; q++) {
                        outBlock[k] = tBlock[l];
                        k++;
                        l += lInc;
                    }
                }
                blockIndex++;
            }
        }
        return out;
    }

    @Override // org.apache.commons.math3.linear.AbstractRealMatrix, org.apache.commons.math3.linear.AnyMatrix, org.apache.commons.math3.linear.RealLinearOperator
    public int getRowDimension() {
        return this.rows;
    }

    @Override // org.apache.commons.math3.linear.AbstractRealMatrix, org.apache.commons.math3.linear.AnyMatrix, org.apache.commons.math3.linear.RealLinearOperator
    public int getColumnDimension() {
        return this.columns;
    }

    @Override // org.apache.commons.math3.linear.AbstractRealMatrix, org.apache.commons.math3.linear.RealMatrix
    public double[] operate(double[] v) throws DimensionMismatchException {
        if (v.length != this.columns) {
            throw new DimensionMismatchException(v.length, this.columns);
        }
        double[] out = new double[this.rows];
        for (int iBlock = 0; iBlock < this.blockRows; iBlock++) {
            int pStart = iBlock * 52;
            int pEnd = FastMath.min(pStart + 52, this.rows);
            for (int jBlock = 0; jBlock < this.blockColumns; jBlock++) {
                double[] block = this.blocks[(this.blockColumns * iBlock) + jBlock];
                int qStart = jBlock * 52;
                int qEnd = FastMath.min(qStart + 52, this.columns);
                int k = 0;
                int p = pStart;
                while (p < pEnd) {
                    double sum = 0.0d;
                    int q = qStart;
                    while (q < qEnd - 3) {
                        sum += (block[k] * v[q]) + (block[k + 1] * v[q + 1]) + (block[k + 2] * v[q + 2]) + (block[k + 3] * v[q + 3]);
                        k += 4;
                        q += 4;
                    }
                    int q2 = q;
                    int k2 = k;
                    while (q2 < qEnd) {
                        sum += block[k2] * v[q2];
                        q2++;
                        k2++;
                    }
                    out[p] = out[p] + sum;
                    p++;
                    k = k2;
                }
            }
        }
        return out;
    }

    @Override // org.apache.commons.math3.linear.AbstractRealMatrix, org.apache.commons.math3.linear.RealMatrix
    public double[] preMultiply(double[] v) throws DimensionMismatchException {
        if (v.length != this.rows) {
            throw new DimensionMismatchException(v.length, this.rows);
        }
        double[] out = new double[this.columns];
        for (int jBlock = 0; jBlock < this.blockColumns; jBlock++) {
            int jWidth = blockWidth(jBlock);
            int jWidth2 = jWidth + jWidth;
            int jWidth3 = jWidth2 + jWidth;
            int jWidth4 = jWidth3 + jWidth;
            int qStart = jBlock * 52;
            int qEnd = FastMath.min(qStart + 52, this.columns);
            for (int iBlock = 0; iBlock < this.blockRows; iBlock++) {
                double[] block = this.blocks[(this.blockColumns * iBlock) + jBlock];
                int pStart = iBlock * 52;
                int pEnd = FastMath.min(pStart + 52, this.rows);
                for (int q = qStart; q < qEnd; q++) {
                    int k = q - qStart;
                    double sum = 0.0d;
                    int p = pStart;
                    while (p < pEnd - 3) {
                        sum += (block[k] * v[p]) + (block[k + jWidth] * v[p + 1]) + (block[k + jWidth2] * v[p + 2]) + (block[k + jWidth3] * v[p + 3]);
                        k += jWidth4;
                        p += 4;
                    }
                    for (int p2 = p; p2 < pEnd; p2++) {
                        sum += block[k] * v[p2];
                        k += jWidth;
                    }
                    out[q] = out[q] + sum;
                }
            }
        }
        return out;
    }

    @Override // org.apache.commons.math3.linear.AbstractRealMatrix, org.apache.commons.math3.linear.RealMatrix
    public double walkInRowOrder(RealMatrixChangingVisitor visitor) {
        visitor.start(this.rows, this.columns, 0, this.rows - 1, 0, this.columns - 1);
        for (int iBlock = 0; iBlock < this.blockRows; iBlock++) {
            int pStart = iBlock * 52;
            int pEnd = FastMath.min(pStart + 52, this.rows);
            for (int p = pStart; p < pEnd; p++) {
                for (int jBlock = 0; jBlock < this.blockColumns; jBlock++) {
                    int jWidth = blockWidth(jBlock);
                    int qStart = jBlock * 52;
                    int qEnd = FastMath.min(qStart + 52, this.columns);
                    double[] block = this.blocks[(this.blockColumns * iBlock) + jBlock];
                    int k = (p - pStart) * jWidth;
                    for (int q = qStart; q < qEnd; q++) {
                        block[k] = visitor.visit(p, q, block[k]);
                        k++;
                    }
                }
            }
        }
        return visitor.end();
    }

    @Override // org.apache.commons.math3.linear.AbstractRealMatrix, org.apache.commons.math3.linear.RealMatrix
    public double walkInRowOrder(RealMatrixPreservingVisitor visitor) {
        visitor.start(this.rows, this.columns, 0, this.rows - 1, 0, this.columns - 1);
        for (int iBlock = 0; iBlock < this.blockRows; iBlock++) {
            int pStart = iBlock * 52;
            int pEnd = FastMath.min(pStart + 52, this.rows);
            for (int p = pStart; p < pEnd; p++) {
                for (int jBlock = 0; jBlock < this.blockColumns; jBlock++) {
                    int jWidth = blockWidth(jBlock);
                    int qStart = jBlock * 52;
                    int qEnd = FastMath.min(qStart + 52, this.columns);
                    double[] block = this.blocks[(this.blockColumns * iBlock) + jBlock];
                    int k = (p - pStart) * jWidth;
                    for (int q = qStart; q < qEnd; q++) {
                        visitor.visit(p, q, block[k]);
                        k++;
                    }
                }
            }
        }
        return visitor.end();
    }

    @Override // org.apache.commons.math3.linear.AbstractRealMatrix, org.apache.commons.math3.linear.RealMatrix
    public double walkInRowOrder(RealMatrixChangingVisitor visitor, int startRow, int endRow, int startColumn, int endColumn) throws OutOfRangeException, NumberIsTooSmallException {
        MatrixUtils.checkSubMatrixIndex(this, startRow, endRow, startColumn, endColumn);
        visitor.start(this.rows, this.columns, startRow, endRow, startColumn, endColumn);
        for (int iBlock = startRow / 52; iBlock < (endRow / 52) + 1; iBlock++) {
            int p0 = iBlock * 52;
            int pStart = FastMath.max(startRow, p0);
            int pEnd = FastMath.min((iBlock + 1) * 52, endRow + 1);
            for (int p = pStart; p < pEnd; p++) {
                for (int jBlock = startColumn / 52; jBlock < (endColumn / 52) + 1; jBlock++) {
                    int jWidth = blockWidth(jBlock);
                    int q0 = jBlock * 52;
                    int qStart = FastMath.max(startColumn, q0);
                    int qEnd = FastMath.min((jBlock + 1) * 52, endColumn + 1);
                    double[] block = this.blocks[(this.blockColumns * iBlock) + jBlock];
                    int k = (((p - p0) * jWidth) + qStart) - q0;
                    for (int q = qStart; q < qEnd; q++) {
                        block[k] = visitor.visit(p, q, block[k]);
                        k++;
                    }
                }
            }
        }
        return visitor.end();
    }

    @Override // org.apache.commons.math3.linear.AbstractRealMatrix, org.apache.commons.math3.linear.RealMatrix
    public double walkInRowOrder(RealMatrixPreservingVisitor visitor, int startRow, int endRow, int startColumn, int endColumn) throws OutOfRangeException, NumberIsTooSmallException {
        MatrixUtils.checkSubMatrixIndex(this, startRow, endRow, startColumn, endColumn);
        visitor.start(this.rows, this.columns, startRow, endRow, startColumn, endColumn);
        for (int iBlock = startRow / 52; iBlock < (endRow / 52) + 1; iBlock++) {
            int p0 = iBlock * 52;
            int pStart = FastMath.max(startRow, p0);
            int pEnd = FastMath.min((iBlock + 1) * 52, endRow + 1);
            for (int p = pStart; p < pEnd; p++) {
                for (int jBlock = startColumn / 52; jBlock < (endColumn / 52) + 1; jBlock++) {
                    int jWidth = blockWidth(jBlock);
                    int q0 = jBlock * 52;
                    int qStart = FastMath.max(startColumn, q0);
                    int qEnd = FastMath.min((jBlock + 1) * 52, endColumn + 1);
                    double[] block = this.blocks[(this.blockColumns * iBlock) + jBlock];
                    int k = (((p - p0) * jWidth) + qStart) - q0;
                    for (int q = qStart; q < qEnd; q++) {
                        visitor.visit(p, q, block[k]);
                        k++;
                    }
                }
            }
        }
        return visitor.end();
    }

    @Override // org.apache.commons.math3.linear.AbstractRealMatrix, org.apache.commons.math3.linear.RealMatrix
    public double walkInOptimizedOrder(RealMatrixChangingVisitor visitor) {
        visitor.start(this.rows, this.columns, 0, this.rows - 1, 0, this.columns - 1);
        int blockIndex = 0;
        for (int iBlock = 0; iBlock < this.blockRows; iBlock++) {
            int pStart = iBlock * 52;
            int pEnd = FastMath.min(pStart + 52, this.rows);
            for (int jBlock = 0; jBlock < this.blockColumns; jBlock++) {
                int qStart = jBlock * 52;
                int qEnd = FastMath.min(qStart + 52, this.columns);
                double[] block = this.blocks[blockIndex];
                int k = 0;
                for (int p = pStart; p < pEnd; p++) {
                    for (int q = qStart; q < qEnd; q++) {
                        block[k] = visitor.visit(p, q, block[k]);
                        k++;
                    }
                }
                blockIndex++;
            }
        }
        return visitor.end();
    }

    @Override // org.apache.commons.math3.linear.AbstractRealMatrix, org.apache.commons.math3.linear.RealMatrix
    public double walkInOptimizedOrder(RealMatrixPreservingVisitor visitor) {
        visitor.start(this.rows, this.columns, 0, this.rows - 1, 0, this.columns - 1);
        int blockIndex = 0;
        for (int iBlock = 0; iBlock < this.blockRows; iBlock++) {
            int pStart = iBlock * 52;
            int pEnd = FastMath.min(pStart + 52, this.rows);
            for (int jBlock = 0; jBlock < this.blockColumns; jBlock++) {
                int qStart = jBlock * 52;
                int qEnd = FastMath.min(qStart + 52, this.columns);
                double[] block = this.blocks[blockIndex];
                int k = 0;
                for (int p = pStart; p < pEnd; p++) {
                    for (int q = qStart; q < qEnd; q++) {
                        visitor.visit(p, q, block[k]);
                        k++;
                    }
                }
                blockIndex++;
            }
        }
        return visitor.end();
    }

    @Override // org.apache.commons.math3.linear.AbstractRealMatrix, org.apache.commons.math3.linear.RealMatrix
    public double walkInOptimizedOrder(RealMatrixChangingVisitor visitor, int startRow, int endRow, int startColumn, int endColumn) throws OutOfRangeException, NumberIsTooSmallException {
        MatrixUtils.checkSubMatrixIndex(this, startRow, endRow, startColumn, endColumn);
        visitor.start(this.rows, this.columns, startRow, endRow, startColumn, endColumn);
        for (int iBlock = startRow / 52; iBlock < (endRow / 52) + 1; iBlock++) {
            int p0 = iBlock * 52;
            int pStart = FastMath.max(startRow, p0);
            int pEnd = FastMath.min((iBlock + 1) * 52, endRow + 1);
            for (int jBlock = startColumn / 52; jBlock < (endColumn / 52) + 1; jBlock++) {
                int jWidth = blockWidth(jBlock);
                int q0 = jBlock * 52;
                int qStart = FastMath.max(startColumn, q0);
                int qEnd = FastMath.min((jBlock + 1) * 52, endColumn + 1);
                double[] block = this.blocks[(this.blockColumns * iBlock) + jBlock];
                for (int p = pStart; p < pEnd; p++) {
                    int k = (((p - p0) * jWidth) + qStart) - q0;
                    for (int q = qStart; q < qEnd; q++) {
                        block[k] = visitor.visit(p, q, block[k]);
                        k++;
                    }
                }
            }
        }
        return visitor.end();
    }

    @Override // org.apache.commons.math3.linear.AbstractRealMatrix, org.apache.commons.math3.linear.RealMatrix
    public double walkInOptimizedOrder(RealMatrixPreservingVisitor visitor, int startRow, int endRow, int startColumn, int endColumn) throws OutOfRangeException, NumberIsTooSmallException {
        MatrixUtils.checkSubMatrixIndex(this, startRow, endRow, startColumn, endColumn);
        visitor.start(this.rows, this.columns, startRow, endRow, startColumn, endColumn);
        for (int iBlock = startRow / 52; iBlock < (endRow / 52) + 1; iBlock++) {
            int p0 = iBlock * 52;
            int pStart = FastMath.max(startRow, p0);
            int pEnd = FastMath.min((iBlock + 1) * 52, endRow + 1);
            for (int jBlock = startColumn / 52; jBlock < (endColumn / 52) + 1; jBlock++) {
                int jWidth = blockWidth(jBlock);
                int q0 = jBlock * 52;
                int qStart = FastMath.max(startColumn, q0);
                int qEnd = FastMath.min((jBlock + 1) * 52, endColumn + 1);
                double[] block = this.blocks[(this.blockColumns * iBlock) + jBlock];
                for (int p = pStart; p < pEnd; p++) {
                    int k = (((p - p0) * jWidth) + qStart) - q0;
                    for (int q = qStart; q < qEnd; q++) {
                        visitor.visit(p, q, block[k]);
                        k++;
                    }
                }
            }
        }
        return visitor.end();
    }

    private int blockHeight(int blockRow) {
        if (blockRow == this.blockRows - 1) {
            return this.rows - (blockRow * 52);
        }
        return 52;
    }

    private int blockWidth(int blockColumn) {
        if (blockColumn == this.blockColumns - 1) {
            return this.columns - (blockColumn * 52);
        }
        return 52;
    }
}
