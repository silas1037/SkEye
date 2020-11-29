package org.apache.commons.math3.linear;

import java.io.Serializable;
import org.apache.commons.math3.Field;
import org.apache.commons.math3.FieldElement;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.NoDataException;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.MathArrays;
import org.apache.commons.math3.util.MathUtils;

public class BlockFieldMatrix<T extends FieldElement<T>> extends AbstractFieldMatrix<T> implements Serializable {
    public static final int BLOCK_SIZE = 36;
    private static final long serialVersionUID = -4602336630143123183L;
    private final int blockColumns;
    private final int blockRows;
    private final T[][] blocks;
    private final int columns;
    private final int rows;

    public BlockFieldMatrix(Field<T> field, int rows2, int columns2) throws NotStrictlyPositiveException {
        super(field, rows2, columns2);
        this.rows = rows2;
        this.columns = columns2;
        this.blockRows = ((rows2 + 36) - 1) / 36;
        this.blockColumns = ((columns2 + 36) - 1) / 36;
        this.blocks = (T[][]) createBlocksLayout(field, rows2, columns2);
    }

    public BlockFieldMatrix(T[][] rawData) throws DimensionMismatchException {
        this(rawData.length, rawData[0].length, toBlocksLayout(rawData), false);
    }

    /* JADX DEBUG: Multi-variable search result rejected for r5v4, resolved type: T extends org.apache.commons.math3.FieldElement<T>[][] */
    /* JADX WARN: Multi-variable type inference failed */
    public BlockFieldMatrix(int rows2, int columns2, T[][] blockData, boolean copyArray) throws DimensionMismatchException, NotStrictlyPositiveException {
        super(extractField(blockData), rows2, columns2);
        this.rows = rows2;
        this.columns = columns2;
        this.blockRows = ((rows2 + 36) - 1) / 36;
        this.blockColumns = ((columns2 + 36) - 1) / 36;
        if (copyArray) {
            this.blocks = (T[][]) ((FieldElement[][]) MathArrays.buildArray(getField(), this.blockRows * this.blockColumns, -1));
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
                    this.blocks[index] = (FieldElement[]) blockData[index].clone();
                }
                jBlock++;
                index++;
            }
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for r6v1, resolved type: T extends org.apache.commons.math3.FieldElement<T>[][] */
    /* JADX WARN: Multi-variable type inference failed */
    public static <T extends FieldElement<T>> T[][] toBlocksLayout(T[][] rawData) throws DimensionMismatchException {
        int rows2 = rawData.length;
        int columns2 = rawData[0].length;
        int blockRows2 = ((rows2 + 36) - 1) / 36;
        int blockColumns2 = ((columns2 + 36) - 1) / 36;
        for (T[] tArr : rawData) {
            int length = tArr.length;
            if (length != columns2) {
                throw new DimensionMismatchException(columns2, length);
            }
        }
        Field<T> field = extractField(rawData);
        T[][] blocks2 = (T[][]) ((FieldElement[][]) MathArrays.buildArray(field, blockRows2 * blockColumns2, -1));
        int blockIndex = 0;
        for (int iBlock = 0; iBlock < blockRows2; iBlock++) {
            int pStart = iBlock * 36;
            int pEnd = FastMath.min(pStart + 36, rows2);
            int iHeight = pEnd - pStart;
            for (int jBlock = 0; jBlock < blockColumns2; jBlock++) {
                int qStart = jBlock * 36;
                int jWidth = FastMath.min(qStart + 36, columns2) - qStart;
                FieldElement[] fieldElementArr = (FieldElement[]) MathArrays.buildArray(field, iHeight * jWidth);
                blocks2[blockIndex] = fieldElementArr;
                int index = 0;
                for (int p = pStart; p < pEnd; p++) {
                    System.arraycopy(rawData[p], qStart, fieldElementArr, index, jWidth);
                    index += jWidth;
                }
                blockIndex++;
            }
        }
        return blocks2;
    }

    /* JADX DEBUG: Multi-variable search result rejected for r4v1, resolved type: T extends org.apache.commons.math3.FieldElement<T>[][] */
    /* JADX WARN: Multi-variable type inference failed */
    public static <T extends FieldElement<T>> T[][] createBlocksLayout(Field<T> field, int rows2, int columns2) {
        int blockRows2 = ((rows2 + 36) - 1) / 36;
        int blockColumns2 = ((columns2 + 36) - 1) / 36;
        T[][] blocks2 = (T[][]) ((FieldElement[][]) MathArrays.buildArray(field, blockRows2 * blockColumns2, -1));
        int blockIndex = 0;
        for (int iBlock = 0; iBlock < blockRows2; iBlock++) {
            int pStart = iBlock * 36;
            int iHeight = FastMath.min(pStart + 36, rows2) - pStart;
            for (int jBlock = 0; jBlock < blockColumns2; jBlock++) {
                int qStart = jBlock * 36;
                blocks2[blockIndex] = (FieldElement[]) MathArrays.buildArray(field, iHeight * (FastMath.min(qStart + 36, columns2) - qStart));
                blockIndex++;
            }
        }
        return blocks2;
    }

    @Override // org.apache.commons.math3.linear.FieldMatrix, org.apache.commons.math3.linear.AbstractFieldMatrix
    public FieldMatrix<T> createMatrix(int rowDimension, int columnDimension) throws NotStrictlyPositiveException {
        return new BlockFieldMatrix(getField(), rowDimension, columnDimension);
    }

    @Override // org.apache.commons.math3.linear.FieldMatrix, org.apache.commons.math3.linear.AbstractFieldMatrix
    public FieldMatrix<T> copy() {
        BlockFieldMatrix<T> copied = new BlockFieldMatrix<>(getField(), this.rows, this.columns);
        for (int i = 0; i < this.blocks.length; i++) {
            System.arraycopy(this.blocks[i], 0, copied.blocks[i], 0, this.blocks[i].length);
        }
        return copied;
    }

    /* JADX DEBUG: Multi-variable search result rejected for r0v10, resolved type: T extends org.apache.commons.math3.FieldElement<T>[][] */
    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.apache.commons.math3.linear.FieldMatrix, org.apache.commons.math3.linear.AbstractFieldMatrix
    public FieldMatrix<T> add(FieldMatrix<T> m) throws MatrixDimensionMismatchException {
        try {
            return add((BlockFieldMatrix) ((BlockFieldMatrix) m));
        } catch (ClassCastException e) {
            checkAdditionCompatible(m);
            BlockFieldMatrix<T> out = new BlockFieldMatrix<>(getField(), this.rows, this.columns);
            int blockIndex = 0;
            for (int iBlock = 0; iBlock < out.blockRows; iBlock++) {
                for (int jBlock = 0; jBlock < out.blockColumns; jBlock++) {
                    FieldElement[] fieldElementArr = ((T[][]) out.blocks)[blockIndex];
                    T[] tBlock = this.blocks[blockIndex];
                    int pStart = iBlock * 36;
                    int pEnd = FastMath.min(pStart + 36, this.rows);
                    int qStart = jBlock * 36;
                    int qEnd = FastMath.min(qStart + 36, this.columns);
                    int k = 0;
                    for (int p = pStart; p < pEnd; p++) {
                        for (int q = qStart; q < qEnd; q++) {
                            fieldElementArr[k] = (FieldElement) tBlock[k].add(m.getEntry(p, q));
                            k++;
                        }
                    }
                    blockIndex++;
                }
            }
            return out;
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for r6v3, resolved type: T extends org.apache.commons.math3.FieldElement<T>[][] */
    /* JADX WARN: Multi-variable type inference failed */
    public BlockFieldMatrix<T> add(BlockFieldMatrix<T> m) throws MatrixDimensionMismatchException {
        checkAdditionCompatible(m);
        BlockFieldMatrix<T> out = new BlockFieldMatrix<>(getField(), this.rows, this.columns);
        for (int blockIndex = 0; blockIndex < out.blocks.length; blockIndex++) {
            FieldElement[] fieldElementArr = ((T[][]) out.blocks)[blockIndex];
            T[] tBlock = this.blocks[blockIndex];
            T[] mBlock = m.blocks[blockIndex];
            for (int k = 0; k < fieldElementArr.length; k++) {
                fieldElementArr[k] = (FieldElement) tBlock[k].add(mBlock[k]);
            }
        }
        return out;
    }

    /* JADX DEBUG: Multi-variable search result rejected for r0v10, resolved type: T extends org.apache.commons.math3.FieldElement<T>[][] */
    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.apache.commons.math3.linear.FieldMatrix, org.apache.commons.math3.linear.AbstractFieldMatrix
    public FieldMatrix<T> subtract(FieldMatrix<T> m) throws MatrixDimensionMismatchException {
        try {
            return subtract((BlockFieldMatrix) ((BlockFieldMatrix) m));
        } catch (ClassCastException e) {
            checkSubtractionCompatible(m);
            BlockFieldMatrix<T> out = new BlockFieldMatrix<>(getField(), this.rows, this.columns);
            int blockIndex = 0;
            for (int iBlock = 0; iBlock < out.blockRows; iBlock++) {
                for (int jBlock = 0; jBlock < out.blockColumns; jBlock++) {
                    FieldElement[] fieldElementArr = ((T[][]) out.blocks)[blockIndex];
                    T[] tBlock = this.blocks[blockIndex];
                    int pStart = iBlock * 36;
                    int pEnd = FastMath.min(pStart + 36, this.rows);
                    int qStart = jBlock * 36;
                    int qEnd = FastMath.min(qStart + 36, this.columns);
                    int k = 0;
                    for (int p = pStart; p < pEnd; p++) {
                        for (int q = qStart; q < qEnd; q++) {
                            fieldElementArr[k] = (FieldElement) tBlock[k].subtract(m.getEntry(p, q));
                            k++;
                        }
                    }
                    blockIndex++;
                }
            }
            return out;
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for r6v3, resolved type: T extends org.apache.commons.math3.FieldElement<T>[][] */
    /* JADX WARN: Multi-variable type inference failed */
    public BlockFieldMatrix<T> subtract(BlockFieldMatrix<T> m) throws MatrixDimensionMismatchException {
        checkSubtractionCompatible(m);
        BlockFieldMatrix<T> out = new BlockFieldMatrix<>(getField(), this.rows, this.columns);
        for (int blockIndex = 0; blockIndex < out.blocks.length; blockIndex++) {
            FieldElement[] fieldElementArr = ((T[][]) out.blocks)[blockIndex];
            T[] tBlock = this.blocks[blockIndex];
            T[] mBlock = m.blocks[blockIndex];
            for (int k = 0; k < fieldElementArr.length; k++) {
                fieldElementArr[k] = (FieldElement) tBlock[k].subtract(mBlock[k]);
            }
        }
        return out;
    }

    /* JADX DEBUG: Multi-variable search result rejected for r5v3, resolved type: T extends org.apache.commons.math3.FieldElement<T>[][] */
    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.apache.commons.math3.linear.FieldMatrix, org.apache.commons.math3.linear.AbstractFieldMatrix
    public FieldMatrix<T> scalarAdd(T d) {
        BlockFieldMatrix<T> out = new BlockFieldMatrix<>(getField(), this.rows, this.columns);
        for (int blockIndex = 0; blockIndex < out.blocks.length; blockIndex++) {
            FieldElement[] fieldElementArr = ((T[][]) out.blocks)[blockIndex];
            T[] tBlock = this.blocks[blockIndex];
            for (int k = 0; k < fieldElementArr.length; k++) {
                fieldElementArr[k] = (FieldElement) tBlock[k].add(d);
            }
        }
        return out;
    }

    /* JADX DEBUG: Multi-variable search result rejected for r5v3, resolved type: T extends org.apache.commons.math3.FieldElement<T>[][] */
    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.apache.commons.math3.linear.FieldMatrix, org.apache.commons.math3.linear.AbstractFieldMatrix
    public FieldMatrix<T> scalarMultiply(T d) {
        BlockFieldMatrix<T> out = new BlockFieldMatrix<>(getField(), this.rows, this.columns);
        for (int blockIndex = 0; blockIndex < out.blocks.length; blockIndex++) {
            FieldElement[] fieldElementArr = ((T[][]) out.blocks)[blockIndex];
            T[] tBlock = this.blocks[blockIndex];
            for (int k = 0; k < fieldElementArr.length; k++) {
                fieldElementArr[k] = (FieldElement) tBlock[k].multiply(d);
            }
        }
        return out;
    }

    @Override // org.apache.commons.math3.linear.FieldMatrix, org.apache.commons.math3.linear.AbstractFieldMatrix
    public FieldMatrix<T> multiply(FieldMatrix<T> m) throws DimensionMismatchException {
        try {
            return multiply((BlockFieldMatrix) ((BlockFieldMatrix) m));
        } catch (ClassCastException e) {
            checkMultiplicationCompatible(m);
            BlockFieldMatrix<T> out = new BlockFieldMatrix<>(getField(), this.rows, m.getColumnDimension());
            T zero = getField().getZero();
            int blockIndex = 0;
            for (int iBlock = 0; iBlock < out.blockRows; iBlock++) {
                int pStart = iBlock * 36;
                int pEnd = FastMath.min(pStart + 36, this.rows);
                for (int jBlock = 0; jBlock < out.blockColumns; jBlock++) {
                    int qStart = jBlock * 36;
                    int qEnd = FastMath.min(qStart + 36, m.getColumnDimension());
                    FieldElement[] fieldElementArr = out.blocks[blockIndex];
                    for (int kBlock = 0; kBlock < this.blockColumns; kBlock++) {
                        int kWidth = blockWidth(kBlock);
                        T[] tBlock = this.blocks[(this.blockColumns * iBlock) + kBlock];
                        int rStart = kBlock * 36;
                        int k = 0;
                        for (int p = pStart; p < pEnd; p++) {
                            int lStart = (p - pStart) * kWidth;
                            int lEnd = lStart + kWidth;
                            for (int q = qStart; q < qEnd; q++) {
                                FieldElement fieldElement = zero;
                                int r = rStart;
                                for (int l = lStart; l < lEnd; l++) {
                                    fieldElement = (FieldElement) fieldElement.add(tBlock[l].multiply(m.getEntry(r, q)));
                                    r++;
                                }
                                fieldElementArr[k] = (FieldElement) fieldElementArr[k].add(fieldElement);
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

    public BlockFieldMatrix<T> multiply(BlockFieldMatrix<T> m) throws DimensionMismatchException {
        checkMultiplicationCompatible(m);
        BlockFieldMatrix<T> out = new BlockFieldMatrix<>(getField(), this.rows, m.columns);
        T zero = getField().getZero();
        int blockIndex = 0;
        for (int iBlock = 0; iBlock < out.blockRows; iBlock++) {
            int pStart = iBlock * 36;
            int pEnd = FastMath.min(pStart + 36, this.rows);
            for (int jBlock = 0; jBlock < out.blockColumns; jBlock++) {
                int jWidth = out.blockWidth(jBlock);
                int jWidth2 = jWidth + jWidth;
                int jWidth3 = jWidth2 + jWidth;
                int jWidth4 = jWidth3 + jWidth;
                FieldElement[] fieldElementArr = out.blocks[blockIndex];
                for (int kBlock = 0; kBlock < this.blockColumns; kBlock++) {
                    int kWidth = blockWidth(kBlock);
                    T[] tBlock = this.blocks[(this.blockColumns * iBlock) + kBlock];
                    T[] mBlock = m.blocks[(m.blockColumns * kBlock) + jBlock];
                    int k = 0;
                    for (int p = pStart; p < pEnd; p++) {
                        int lStart = (p - pStart) * kWidth;
                        int lEnd = lStart + kWidth;
                        for (int nStart = 0; nStart < jWidth; nStart++) {
                            FieldElement fieldElement = zero;
                            int l = lStart;
                            int n = nStart;
                            while (l < lEnd - 3) {
                                fieldElement = (FieldElement) ((FieldElement) ((FieldElement) ((FieldElement) fieldElement.add(tBlock[l].multiply(mBlock[n]))).add(tBlock[l + 1].multiply(mBlock[n + jWidth]))).add(tBlock[l + 2].multiply(mBlock[n + jWidth2]))).add(tBlock[l + 3].multiply(mBlock[n + jWidth3]));
                                l += 4;
                                n += jWidth4;
                            }
                            while (l < lEnd) {
                                l++;
                                fieldElement = (FieldElement) fieldElement.add(tBlock[l].multiply(mBlock[n]));
                                n += jWidth;
                            }
                            fieldElementArr[k] = (FieldElement) fieldElementArr[k].add(fieldElement);
                            k++;
                        }
                    }
                }
                blockIndex++;
            }
        }
        return out;
    }

    @Override // org.apache.commons.math3.linear.FieldMatrix, org.apache.commons.math3.linear.AbstractFieldMatrix
    public T[][] getData() {
        T[][] data = (T[][]) ((FieldElement[][]) MathArrays.buildArray(getField(), getRowDimension(), getColumnDimension()));
        int lastColumns = this.columns - ((this.blockColumns - 1) * 36);
        for (int iBlock = 0; iBlock < this.blockRows; iBlock++) {
            int pStart = iBlock * 36;
            int pEnd = FastMath.min(pStart + 36, this.rows);
            int regularPos = 0;
            int lastPos = 0;
            for (int p = pStart; p < pEnd; p++) {
                T[] dataP = data[p];
                int blockIndex = iBlock * this.blockColumns;
                int dataPos = 0;
                int jBlock = 0;
                while (jBlock < this.blockColumns - 1) {
                    System.arraycopy(this.blocks[blockIndex], regularPos, dataP, dataPos, 36);
                    dataPos += 36;
                    jBlock++;
                    blockIndex++;
                }
                System.arraycopy(this.blocks[blockIndex], lastPos, dataP, dataPos, lastColumns);
                regularPos += 36;
                lastPos += lastColumns;
            }
        }
        return data;
    }

    @Override // org.apache.commons.math3.linear.FieldMatrix, org.apache.commons.math3.linear.AbstractFieldMatrix
    public FieldMatrix<T> getSubMatrix(int startRow, int endRow, int startColumn, int endColumn) throws OutOfRangeException, NumberIsTooSmallException {
        checkSubMatrixIndex(startRow, endRow, startColumn, endColumn);
        BlockFieldMatrix<T> out = new BlockFieldMatrix<>(getField(), (endRow - startRow) + 1, (endColumn - startColumn) + 1);
        int rowsShift = startRow % 36;
        int blockStartColumn = startColumn / 36;
        int columnsShift = startColumn % 36;
        int pBlock = startRow / 36;
        for (int iBlock = 0; iBlock < out.blockRows; iBlock++) {
            int iHeight = out.blockHeight(iBlock);
            int qBlock = blockStartColumn;
            for (int jBlock = 0; jBlock < out.blockColumns; jBlock++) {
                int jWidth = out.blockWidth(jBlock);
                T[] outBlock = out.blocks[(out.blockColumns * iBlock) + jBlock];
                int index = (this.blockColumns * pBlock) + qBlock;
                int width = blockWidth(qBlock);
                int heightExcess = (iHeight + rowsShift) - 36;
                int widthExcess = (jWidth + columnsShift) - 36;
                if (heightExcess > 0) {
                    if (widthExcess > 0) {
                        int width2 = blockWidth(qBlock + 1);
                        copyBlockPart(this.blocks[index], width, rowsShift, 36, columnsShift, 36, outBlock, jWidth, 0, 0);
                        copyBlockPart(this.blocks[index + 1], width2, rowsShift, 36, 0, widthExcess, outBlock, jWidth, 0, jWidth - widthExcess);
                        copyBlockPart(this.blocks[this.blockColumns + index], width, 0, heightExcess, columnsShift, 36, outBlock, jWidth, iHeight - heightExcess, 0);
                        copyBlockPart(this.blocks[this.blockColumns + index + 1], width2, 0, heightExcess, 0, widthExcess, outBlock, jWidth, iHeight - heightExcess, jWidth - widthExcess);
                    } else {
                        copyBlockPart(this.blocks[index], width, rowsShift, 36, columnsShift, jWidth + columnsShift, outBlock, jWidth, 0, 0);
                        copyBlockPart(this.blocks[this.blockColumns + index], width, 0, heightExcess, columnsShift, jWidth + columnsShift, outBlock, jWidth, iHeight - heightExcess, 0);
                    }
                } else if (widthExcess > 0) {
                    int width22 = blockWidth(qBlock + 1);
                    copyBlockPart(this.blocks[index], width, rowsShift, iHeight + rowsShift, columnsShift, 36, outBlock, jWidth, 0, 0);
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

    private void copyBlockPart(T[] srcBlock, int srcWidth, int srcStartRow, int srcEndRow, int srcStartColumn, int srcEndColumn, T[] dstBlock, int dstWidth, int dstStartRow, int dstStartColumn) {
        int length = srcEndColumn - srcStartColumn;
        int srcPos = (srcStartRow * srcWidth) + srcStartColumn;
        int dstPos = (dstStartRow * dstWidth) + dstStartColumn;
        for (int srcRow = srcStartRow; srcRow < srcEndRow; srcRow++) {
            System.arraycopy(srcBlock, srcPos, dstBlock, dstPos, length);
            srcPos += srcWidth;
            dstPos += dstWidth;
        }
    }

    @Override // org.apache.commons.math3.linear.FieldMatrix, org.apache.commons.math3.linear.AbstractFieldMatrix
    public void setSubMatrix(T[][] subMatrix, int row, int column) throws DimensionMismatchException, OutOfRangeException, NoDataException, NullArgumentException {
        MathUtils.checkNotNull(subMatrix);
        int refLength = subMatrix[0].length;
        if (refLength == 0) {
            throw new NoDataException(LocalizedFormats.AT_LEAST_ONE_COLUMN);
        }
        int endRow = (subMatrix.length + row) - 1;
        int endColumn = (column + refLength) - 1;
        checkSubMatrixIndex(row, endRow, column, endColumn);
        for (T[] subRow : subMatrix) {
            if (subRow.length != refLength) {
                throw new DimensionMismatchException(refLength, subRow.length);
            }
        }
        int blockEndRow = (endRow + 36) / 36;
        int blockStartColumn = column / 36;
        int blockEndColumn = (endColumn + 36) / 36;
        for (int iBlock = row / 36; iBlock < blockEndRow; iBlock++) {
            int iHeight = blockHeight(iBlock);
            int firstRow = iBlock * 36;
            int iStart = FastMath.max(row, firstRow);
            int iEnd = FastMath.min(endRow + 1, firstRow + iHeight);
            for (int jBlock = blockStartColumn; jBlock < blockEndColumn; jBlock++) {
                int jWidth = blockWidth(jBlock);
                int firstColumn = jBlock * 36;
                int jStart = FastMath.max(column, firstColumn);
                int jLength = FastMath.min(endColumn + 1, firstColumn + jWidth) - jStart;
                T[] block = this.blocks[(this.blockColumns * iBlock) + jBlock];
                for (int i = iStart; i < iEnd; i++) {
                    System.arraycopy(subMatrix[i - row], jStart - column, block, ((i - firstRow) * jWidth) + (jStart - firstColumn), jLength);
                }
            }
        }
    }

    @Override // org.apache.commons.math3.linear.FieldMatrix, org.apache.commons.math3.linear.AbstractFieldMatrix
    public FieldMatrix<T> getRowMatrix(int row) throws OutOfRangeException {
        checkRowIndex(row);
        BlockFieldMatrix<T> out = new BlockFieldMatrix<>(getField(), 1, this.columns);
        int iBlock = row / 36;
        int iRow = row - (iBlock * 36);
        int outBlockIndex = 0;
        int outIndex = 0;
        T[] outBlock = out.blocks[0];
        for (int jBlock = 0; jBlock < this.blockColumns; jBlock++) {
            int jWidth = blockWidth(jBlock);
            T[] block = this.blocks[(this.blockColumns * iBlock) + jBlock];
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

    @Override // org.apache.commons.math3.linear.FieldMatrix, org.apache.commons.math3.linear.AbstractFieldMatrix
    public void setRowMatrix(int row, FieldMatrix<T> matrix) throws MatrixDimensionMismatchException, OutOfRangeException {
        try {
            setRowMatrix(row, (BlockFieldMatrix) ((BlockFieldMatrix) matrix));
        } catch (ClassCastException e) {
            super.setRowMatrix(row, matrix);
        }
    }

    public void setRowMatrix(int row, BlockFieldMatrix<T> matrix) throws MatrixDimensionMismatchException, OutOfRangeException {
        checkRowIndex(row);
        int nCols = getColumnDimension();
        if (matrix.getRowDimension() == 1 && matrix.getColumnDimension() == nCols) {
            int iBlock = row / 36;
            int iRow = row - (iBlock * 36);
            int mBlockIndex = 0;
            int mIndex = 0;
            T[] mBlock = matrix.blocks[0];
            for (int jBlock = 0; jBlock < this.blockColumns; jBlock++) {
                int jWidth = blockWidth(jBlock);
                T[] block = this.blocks[(this.blockColumns * iBlock) + jBlock];
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

    @Override // org.apache.commons.math3.linear.FieldMatrix, org.apache.commons.math3.linear.AbstractFieldMatrix
    public FieldMatrix<T> getColumnMatrix(int column) throws OutOfRangeException {
        checkColumnIndex(column);
        BlockFieldMatrix<T> out = new BlockFieldMatrix<>(getField(), this.rows, 1);
        int jBlock = column / 36;
        int jColumn = column - (jBlock * 36);
        int jWidth = blockWidth(jBlock);
        int outBlockIndex = 0;
        int outIndex = 0;
        T[] outBlock = out.blocks[0];
        for (int iBlock = 0; iBlock < this.blockRows; iBlock++) {
            int iHeight = blockHeight(iBlock);
            T[] block = this.blocks[(this.blockColumns * iBlock) + jBlock];
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

    @Override // org.apache.commons.math3.linear.FieldMatrix, org.apache.commons.math3.linear.AbstractFieldMatrix
    public void setColumnMatrix(int column, FieldMatrix<T> matrix) throws MatrixDimensionMismatchException, OutOfRangeException {
        try {
            setColumnMatrix(column, (BlockFieldMatrix) ((BlockFieldMatrix) matrix));
        } catch (ClassCastException e) {
            super.setColumnMatrix(column, matrix);
        }
    }

    /* access modifiers changed from: package-private */
    public void setColumnMatrix(int column, BlockFieldMatrix<T> matrix) throws MatrixDimensionMismatchException, OutOfRangeException {
        checkColumnIndex(column);
        int nRows = getRowDimension();
        if (matrix.getRowDimension() == nRows && matrix.getColumnDimension() == 1) {
            int jBlock = column / 36;
            int jColumn = column - (jBlock * 36);
            int jWidth = blockWidth(jBlock);
            int mBlockIndex = 0;
            int mIndex = 0;
            T[] mBlock = matrix.blocks[0];
            for (int iBlock = 0; iBlock < this.blockRows; iBlock++) {
                int iHeight = blockHeight(iBlock);
                T[] block = this.blocks[(this.blockColumns * iBlock) + jBlock];
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

    @Override // org.apache.commons.math3.linear.FieldMatrix, org.apache.commons.math3.linear.AbstractFieldMatrix
    public FieldVector<T> getRowVector(int row) throws OutOfRangeException {
        checkRowIndex(row);
        FieldElement[] fieldElementArr = (FieldElement[]) MathArrays.buildArray(getField(), this.columns);
        int iBlock = row / 36;
        int iRow = row - (iBlock * 36);
        int outIndex = 0;
        for (int jBlock = 0; jBlock < this.blockColumns; jBlock++) {
            int jWidth = blockWidth(jBlock);
            System.arraycopy(this.blocks[(this.blockColumns * iBlock) + jBlock], iRow * jWidth, fieldElementArr, outIndex, jWidth);
            outIndex += jWidth;
        }
        return new ArrayFieldVector((Field) getField(), fieldElementArr, false);
    }

    /* JADX DEBUG: Multi-variable search result rejected for r3v0, resolved type: org.apache.commons.math3.linear.BlockFieldMatrix<T extends org.apache.commons.math3.FieldElement<T>> */
    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.apache.commons.math3.linear.FieldMatrix, org.apache.commons.math3.linear.AbstractFieldMatrix
    public void setRowVector(int row, FieldVector<T> vector) throws MatrixDimensionMismatchException, OutOfRangeException {
        try {
            setRow(row, ((ArrayFieldVector) vector).getDataRef());
        } catch (ClassCastException e) {
            super.setRowVector(row, vector);
        }
    }

    @Override // org.apache.commons.math3.linear.FieldMatrix, org.apache.commons.math3.linear.AbstractFieldMatrix
    public FieldVector<T> getColumnVector(int column) throws OutOfRangeException {
        checkColumnIndex(column);
        FieldElement[] fieldElementArr = (FieldElement[]) MathArrays.buildArray(getField(), this.rows);
        int jBlock = column / 36;
        int jColumn = column - (jBlock * 36);
        int jWidth = blockWidth(jBlock);
        int outIndex = 0;
        int iBlock = 0;
        while (iBlock < this.blockRows) {
            int iHeight = blockHeight(iBlock);
            T[] block = this.blocks[(this.blockColumns * iBlock) + jBlock];
            int i = 0;
            int outIndex2 = outIndex;
            while (i < iHeight) {
                fieldElementArr[outIndex2] = block[(i * jWidth) + jColumn];
                i++;
                outIndex2++;
            }
            iBlock++;
            outIndex = outIndex2;
        }
        return new ArrayFieldVector((Field) getField(), fieldElementArr, false);
    }

    /* JADX DEBUG: Multi-variable search result rejected for r3v0, resolved type: org.apache.commons.math3.linear.BlockFieldMatrix<T extends org.apache.commons.math3.FieldElement<T>> */
    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.apache.commons.math3.linear.FieldMatrix, org.apache.commons.math3.linear.AbstractFieldMatrix
    public void setColumnVector(int column, FieldVector<T> vector) throws OutOfRangeException, MatrixDimensionMismatchException {
        try {
            setColumn(column, ((ArrayFieldVector) vector).getDataRef());
        } catch (ClassCastException e) {
            super.setColumnVector(column, vector);
        }
    }

    @Override // org.apache.commons.math3.linear.FieldMatrix, org.apache.commons.math3.linear.AbstractFieldMatrix
    public T[] getRow(int row) throws OutOfRangeException {
        checkRowIndex(row);
        T[] out = (T[]) ((FieldElement[]) MathArrays.buildArray(getField(), this.columns));
        int iBlock = row / 36;
        int iRow = row - (iBlock * 36);
        int outIndex = 0;
        for (int jBlock = 0; jBlock < this.blockColumns; jBlock++) {
            int jWidth = blockWidth(jBlock);
            System.arraycopy(this.blocks[(this.blockColumns * iBlock) + jBlock], iRow * jWidth, out, outIndex, jWidth);
            outIndex += jWidth;
        }
        return out;
    }

    @Override // org.apache.commons.math3.linear.FieldMatrix, org.apache.commons.math3.linear.AbstractFieldMatrix
    public void setRow(int row, T[] array) throws OutOfRangeException, MatrixDimensionMismatchException {
        checkRowIndex(row);
        int nCols = getColumnDimension();
        if (array.length != nCols) {
            throw new MatrixDimensionMismatchException(1, array.length, 1, nCols);
        }
        int iBlock = row / 36;
        int iRow = row - (iBlock * 36);
        int outIndex = 0;
        for (int jBlock = 0; jBlock < this.blockColumns; jBlock++) {
            int jWidth = blockWidth(jBlock);
            System.arraycopy(array, outIndex, this.blocks[(this.blockColumns * iBlock) + jBlock], iRow * jWidth, jWidth);
            outIndex += jWidth;
        }
    }

    @Override // org.apache.commons.math3.linear.FieldMatrix, org.apache.commons.math3.linear.AbstractFieldMatrix
    public T[] getColumn(int column) throws OutOfRangeException {
        checkColumnIndex(column);
        T[] out = (T[]) ((FieldElement[]) MathArrays.buildArray(getField(), this.rows));
        int jBlock = column / 36;
        int jColumn = column - (jBlock * 36);
        int jWidth = blockWidth(jBlock);
        int outIndex = 0;
        int iBlock = 0;
        while (iBlock < this.blockRows) {
            int iHeight = blockHeight(iBlock);
            T[] block = this.blocks[(this.blockColumns * iBlock) + jBlock];
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

    @Override // org.apache.commons.math3.linear.FieldMatrix, org.apache.commons.math3.linear.AbstractFieldMatrix
    public void setColumn(int column, T[] array) throws MatrixDimensionMismatchException, OutOfRangeException {
        checkColumnIndex(column);
        int nRows = getRowDimension();
        if (array.length != nRows) {
            throw new MatrixDimensionMismatchException(array.length, 1, nRows, 1);
        }
        int jBlock = column / 36;
        int jColumn = column - (jBlock * 36);
        int jWidth = blockWidth(jBlock);
        int outIndex = 0;
        int iBlock = 0;
        while (iBlock < this.blockRows) {
            int iHeight = blockHeight(iBlock);
            T[] block = this.blocks[(this.blockColumns * iBlock) + jBlock];
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

    @Override // org.apache.commons.math3.linear.FieldMatrix, org.apache.commons.math3.linear.AbstractFieldMatrix
    public T getEntry(int row, int column) throws OutOfRangeException {
        checkRowIndex(row);
        checkColumnIndex(column);
        int iBlock = row / 36;
        int jBlock = column / 36;
        return this.blocks[(this.blockColumns * iBlock) + jBlock][((row - (iBlock * 36)) * blockWidth(jBlock)) + (column - (jBlock * 36))];
    }

    @Override // org.apache.commons.math3.linear.FieldMatrix, org.apache.commons.math3.linear.AbstractFieldMatrix
    public void setEntry(int row, int column, T value) throws OutOfRangeException {
        checkRowIndex(row);
        checkColumnIndex(column);
        int iBlock = row / 36;
        int jBlock = column / 36;
        this.blocks[(this.blockColumns * iBlock) + jBlock][((row - (iBlock * 36)) * blockWidth(jBlock)) + (column - (jBlock * 36))] = value;
    }

    @Override // org.apache.commons.math3.linear.FieldMatrix, org.apache.commons.math3.linear.AbstractFieldMatrix
    public void addToEntry(int row, int column, T increment) throws OutOfRangeException {
        checkRowIndex(row);
        checkColumnIndex(column);
        int iBlock = row / 36;
        int jBlock = column / 36;
        int k = ((row - (iBlock * 36)) * blockWidth(jBlock)) + (column - (jBlock * 36));
        FieldElement[] fieldElementArr = this.blocks[(this.blockColumns * iBlock) + jBlock];
        fieldElementArr[k] = (FieldElement) fieldElementArr[k].add(increment);
    }

    @Override // org.apache.commons.math3.linear.FieldMatrix, org.apache.commons.math3.linear.AbstractFieldMatrix
    public void multiplyEntry(int row, int column, T factor) throws OutOfRangeException {
        checkRowIndex(row);
        checkColumnIndex(column);
        int iBlock = row / 36;
        int jBlock = column / 36;
        int k = ((row - (iBlock * 36)) * blockWidth(jBlock)) + (column - (jBlock * 36));
        FieldElement[] fieldElementArr = this.blocks[(this.blockColumns * iBlock) + jBlock];
        fieldElementArr[k] = (FieldElement) fieldElementArr[k].multiply(factor);
    }

    @Override // org.apache.commons.math3.linear.FieldMatrix, org.apache.commons.math3.linear.AbstractFieldMatrix
    public FieldMatrix<T> transpose() {
        int nRows = getRowDimension();
        BlockFieldMatrix<T> out = new BlockFieldMatrix<>(getField(), getColumnDimension(), nRows);
        int blockIndex = 0;
        for (int iBlock = 0; iBlock < this.blockColumns; iBlock++) {
            for (int jBlock = 0; jBlock < this.blockRows; jBlock++) {
                T[] outBlock = out.blocks[blockIndex];
                T[] tBlock = this.blocks[(this.blockColumns * jBlock) + iBlock];
                int pStart = iBlock * 36;
                int pEnd = FastMath.min(pStart + 36, this.columns);
                int qStart = jBlock * 36;
                int qEnd = FastMath.min(qStart + 36, this.rows);
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

    @Override // org.apache.commons.math3.linear.AnyMatrix, org.apache.commons.math3.linear.AbstractFieldMatrix
    public int getRowDimension() {
        return this.rows;
    }

    @Override // org.apache.commons.math3.linear.AnyMatrix, org.apache.commons.math3.linear.AbstractFieldMatrix
    public int getColumnDimension() {
        return this.columns;
    }

    /* JADX DEBUG: Multi-variable search result rejected for r7v1, resolved type: T extends org.apache.commons.math3.FieldElement<T>[] */
    /* JADX DEBUG: Multi-variable search result rejected for r17v9, resolved type: org.apache.commons.math3.dfp.DfpDec */
    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.apache.commons.math3.linear.FieldMatrix, org.apache.commons.math3.linear.AbstractFieldMatrix
    public T[] operate(T[] v) throws DimensionMismatchException {
        if (v.length != this.columns) {
            throw new DimensionMismatchException(v.length, this.columns);
        }
        T[] out = (T[]) ((FieldElement[]) MathArrays.buildArray(getField(), this.rows));
        T zero = getField().getZero();
        for (int iBlock = 0; iBlock < this.blockRows; iBlock++) {
            int pStart = iBlock * 36;
            int pEnd = FastMath.min(pStart + 36, this.rows);
            for (int jBlock = 0; jBlock < this.blockColumns; jBlock++) {
                T[] block = this.blocks[(this.blockColumns * iBlock) + jBlock];
                int qStart = jBlock * 36;
                int qEnd = FastMath.min(qStart + 36, this.columns);
                int k = 0;
                int p = pStart;
                while (p < pEnd) {
                    FieldElement fieldElement = zero;
                    int q = qStart;
                    while (q < qEnd - 3) {
                        fieldElement = (FieldElement) ((FieldElement) ((FieldElement) ((FieldElement) fieldElement.add(block[k].multiply(v[q]))).add(block[k + 1].multiply(v[q + 1]))).add(block[k + 2].multiply(v[q + 2]))).add(block[k + 3].multiply(v[q + 3]));
                        k += 4;
                        q += 4;
                    }
                    int q2 = q;
                    int k2 = k;
                    while (q2 < qEnd) {
                        fieldElement = (FieldElement) fieldElement.add(block[k2].multiply(v[q2]));
                        q2++;
                        k2++;
                    }
                    out[p] = (FieldElement) out[p].add(fieldElement);
                    p++;
                    k = k2;
                }
            }
        }
        return out;
    }

    /* JADX DEBUG: Multi-variable search result rejected for r10v1, resolved type: T extends org.apache.commons.math3.FieldElement<T>[] */
    /* JADX DEBUG: Multi-variable search result rejected for r20v9, resolved type: org.apache.commons.math3.dfp.DfpDec */
    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.apache.commons.math3.linear.FieldMatrix, org.apache.commons.math3.linear.AbstractFieldMatrix
    public T[] preMultiply(T[] v) throws DimensionMismatchException {
        if (v.length != this.rows) {
            throw new DimensionMismatchException(v.length, this.rows);
        }
        T[] out = (T[]) ((FieldElement[]) MathArrays.buildArray(getField(), this.columns));
        T zero = getField().getZero();
        for (int jBlock = 0; jBlock < this.blockColumns; jBlock++) {
            int jWidth = blockWidth(jBlock);
            int jWidth2 = jWidth + jWidth;
            int jWidth3 = jWidth2 + jWidth;
            int jWidth4 = jWidth3 + jWidth;
            int qStart = jBlock * 36;
            int qEnd = FastMath.min(qStart + 36, this.columns);
            for (int iBlock = 0; iBlock < this.blockRows; iBlock++) {
                T[] block = this.blocks[(this.blockColumns * iBlock) + jBlock];
                int pStart = iBlock * 36;
                int pEnd = FastMath.min(pStart + 36, this.rows);
                for (int q = qStart; q < qEnd; q++) {
                    int k = q - qStart;
                    FieldElement fieldElement = zero;
                    int p = pStart;
                    while (p < pEnd - 3) {
                        fieldElement = (FieldElement) ((FieldElement) ((FieldElement) ((FieldElement) fieldElement.add(block[k].multiply(v[p]))).add(block[k + jWidth].multiply(v[p + 1]))).add(block[k + jWidth2].multiply(v[p + 2]))).add(block[k + jWidth3].multiply(v[p + 3]));
                        k += jWidth4;
                        p += 4;
                    }
                    for (int p2 = p; p2 < pEnd; p2++) {
                        fieldElement = (FieldElement) fieldElement.add(block[k].multiply(v[p2]));
                        k += jWidth;
                    }
                    out[q] = (FieldElement) out[q].add(fieldElement);
                }
            }
        }
        return out;
    }

    @Override // org.apache.commons.math3.linear.FieldMatrix, org.apache.commons.math3.linear.AbstractFieldMatrix
    public T walkInRowOrder(FieldMatrixChangingVisitor<T> visitor) {
        visitor.start(this.rows, this.columns, 0, this.rows - 1, 0, this.columns - 1);
        for (int iBlock = 0; iBlock < this.blockRows; iBlock++) {
            int pStart = iBlock * 36;
            int pEnd = FastMath.min(pStart + 36, this.rows);
            for (int p = pStart; p < pEnd; p++) {
                for (int jBlock = 0; jBlock < this.blockColumns; jBlock++) {
                    int jWidth = blockWidth(jBlock);
                    int qStart = jBlock * 36;
                    int qEnd = FastMath.min(qStart + 36, this.columns);
                    T[] block = this.blocks[(this.blockColumns * iBlock) + jBlock];
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

    @Override // org.apache.commons.math3.linear.FieldMatrix, org.apache.commons.math3.linear.AbstractFieldMatrix
    public T walkInRowOrder(FieldMatrixPreservingVisitor<T> visitor) {
        visitor.start(this.rows, this.columns, 0, this.rows - 1, 0, this.columns - 1);
        for (int iBlock = 0; iBlock < this.blockRows; iBlock++) {
            int pStart = iBlock * 36;
            int pEnd = FastMath.min(pStart + 36, this.rows);
            for (int p = pStart; p < pEnd; p++) {
                for (int jBlock = 0; jBlock < this.blockColumns; jBlock++) {
                    int jWidth = blockWidth(jBlock);
                    int qStart = jBlock * 36;
                    int qEnd = FastMath.min(qStart + 36, this.columns);
                    T[] block = this.blocks[(this.blockColumns * iBlock) + jBlock];
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

    @Override // org.apache.commons.math3.linear.FieldMatrix, org.apache.commons.math3.linear.AbstractFieldMatrix
    public T walkInRowOrder(FieldMatrixChangingVisitor<T> visitor, int startRow, int endRow, int startColumn, int endColumn) throws OutOfRangeException, NumberIsTooSmallException {
        checkSubMatrixIndex(startRow, endRow, startColumn, endColumn);
        visitor.start(this.rows, this.columns, startRow, endRow, startColumn, endColumn);
        for (int iBlock = startRow / 36; iBlock < (endRow / 36) + 1; iBlock++) {
            int p0 = iBlock * 36;
            int pStart = FastMath.max(startRow, p0);
            int pEnd = FastMath.min((iBlock + 1) * 36, endRow + 1);
            for (int p = pStart; p < pEnd; p++) {
                for (int jBlock = startColumn / 36; jBlock < (endColumn / 36) + 1; jBlock++) {
                    int jWidth = blockWidth(jBlock);
                    int q0 = jBlock * 36;
                    int qStart = FastMath.max(startColumn, q0);
                    int qEnd = FastMath.min((jBlock + 1) * 36, endColumn + 1);
                    T[] block = this.blocks[(this.blockColumns * iBlock) + jBlock];
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

    @Override // org.apache.commons.math3.linear.FieldMatrix, org.apache.commons.math3.linear.AbstractFieldMatrix
    public T walkInRowOrder(FieldMatrixPreservingVisitor<T> visitor, int startRow, int endRow, int startColumn, int endColumn) throws OutOfRangeException, NumberIsTooSmallException {
        checkSubMatrixIndex(startRow, endRow, startColumn, endColumn);
        visitor.start(this.rows, this.columns, startRow, endRow, startColumn, endColumn);
        for (int iBlock = startRow / 36; iBlock < (endRow / 36) + 1; iBlock++) {
            int p0 = iBlock * 36;
            int pStart = FastMath.max(startRow, p0);
            int pEnd = FastMath.min((iBlock + 1) * 36, endRow + 1);
            for (int p = pStart; p < pEnd; p++) {
                for (int jBlock = startColumn / 36; jBlock < (endColumn / 36) + 1; jBlock++) {
                    int jWidth = blockWidth(jBlock);
                    int q0 = jBlock * 36;
                    int qStart = FastMath.max(startColumn, q0);
                    int qEnd = FastMath.min((jBlock + 1) * 36, endColumn + 1);
                    T[] block = this.blocks[(this.blockColumns * iBlock) + jBlock];
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

    @Override // org.apache.commons.math3.linear.FieldMatrix, org.apache.commons.math3.linear.AbstractFieldMatrix
    public T walkInOptimizedOrder(FieldMatrixChangingVisitor<T> visitor) {
        visitor.start(this.rows, this.columns, 0, this.rows - 1, 0, this.columns - 1);
        int blockIndex = 0;
        for (int iBlock = 0; iBlock < this.blockRows; iBlock++) {
            int pStart = iBlock * 36;
            int pEnd = FastMath.min(pStart + 36, this.rows);
            for (int jBlock = 0; jBlock < this.blockColumns; jBlock++) {
                int qStart = jBlock * 36;
                int qEnd = FastMath.min(qStart + 36, this.columns);
                T[] block = this.blocks[blockIndex];
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

    @Override // org.apache.commons.math3.linear.FieldMatrix, org.apache.commons.math3.linear.AbstractFieldMatrix
    public T walkInOptimizedOrder(FieldMatrixPreservingVisitor<T> visitor) {
        visitor.start(this.rows, this.columns, 0, this.rows - 1, 0, this.columns - 1);
        int blockIndex = 0;
        for (int iBlock = 0; iBlock < this.blockRows; iBlock++) {
            int pStart = iBlock * 36;
            int pEnd = FastMath.min(pStart + 36, this.rows);
            for (int jBlock = 0; jBlock < this.blockColumns; jBlock++) {
                int qStart = jBlock * 36;
                int qEnd = FastMath.min(qStart + 36, this.columns);
                T[] block = this.blocks[blockIndex];
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

    @Override // org.apache.commons.math3.linear.FieldMatrix, org.apache.commons.math3.linear.AbstractFieldMatrix
    public T walkInOptimizedOrder(FieldMatrixChangingVisitor<T> visitor, int startRow, int endRow, int startColumn, int endColumn) throws OutOfRangeException, NumberIsTooSmallException {
        checkSubMatrixIndex(startRow, endRow, startColumn, endColumn);
        visitor.start(this.rows, this.columns, startRow, endRow, startColumn, endColumn);
        for (int iBlock = startRow / 36; iBlock < (endRow / 36) + 1; iBlock++) {
            int p0 = iBlock * 36;
            int pStart = FastMath.max(startRow, p0);
            int pEnd = FastMath.min((iBlock + 1) * 36, endRow + 1);
            for (int jBlock = startColumn / 36; jBlock < (endColumn / 36) + 1; jBlock++) {
                int jWidth = blockWidth(jBlock);
                int q0 = jBlock * 36;
                int qStart = FastMath.max(startColumn, q0);
                int qEnd = FastMath.min((jBlock + 1) * 36, endColumn + 1);
                T[] block = this.blocks[(this.blockColumns * iBlock) + jBlock];
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

    @Override // org.apache.commons.math3.linear.FieldMatrix, org.apache.commons.math3.linear.AbstractFieldMatrix
    public T walkInOptimizedOrder(FieldMatrixPreservingVisitor<T> visitor, int startRow, int endRow, int startColumn, int endColumn) throws OutOfRangeException, NumberIsTooSmallException {
        checkSubMatrixIndex(startRow, endRow, startColumn, endColumn);
        visitor.start(this.rows, this.columns, startRow, endRow, startColumn, endColumn);
        for (int iBlock = startRow / 36; iBlock < (endRow / 36) + 1; iBlock++) {
            int p0 = iBlock * 36;
            int pStart = FastMath.max(startRow, p0);
            int pEnd = FastMath.min((iBlock + 1) * 36, endRow + 1);
            for (int jBlock = startColumn / 36; jBlock < (endColumn / 36) + 1; jBlock++) {
                int jWidth = blockWidth(jBlock);
                int q0 = jBlock * 36;
                int qStart = FastMath.max(startColumn, q0);
                int qEnd = FastMath.min((jBlock + 1) * 36, endColumn + 1);
                T[] block = this.blocks[(this.blockColumns * iBlock) + jBlock];
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
            return this.rows - (blockRow * 36);
        }
        return 36;
    }

    private int blockWidth(int blockColumn) {
        if (blockColumn == this.blockColumns - 1) {
            return this.columns - (blockColumn * 36);
        }
        return 36;
    }
}
