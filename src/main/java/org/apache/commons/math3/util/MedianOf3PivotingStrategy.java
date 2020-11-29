package org.apache.commons.math3.util;

import java.io.Serializable;
import org.apache.commons.math3.exception.MathIllegalArgumentException;

public class MedianOf3PivotingStrategy implements PivotingStrategyInterface, Serializable {
    private static final long serialVersionUID = 20140713;

    @Override // org.apache.commons.math3.util.PivotingStrategyInterface
    public int pivotIndex(double[] work, int begin, int end) throws MathIllegalArgumentException {
        MathArrays.verifyValues(work, begin, end - begin);
        int inclusiveEnd = end - 1;
        int middle = begin + ((inclusiveEnd - begin) / 2);
        double wBegin = work[begin];
        double wMiddle = work[middle];
        double wEnd = work[inclusiveEnd];
        if (wBegin < wMiddle) {
            if (wMiddle < wEnd) {
                return middle;
            }
            return wBegin >= wEnd ? begin : inclusiveEnd;
        } else if (wBegin < wEnd) {
            return begin;
        } else {
            return wMiddle >= wEnd ? middle : inclusiveEnd;
        }
    }
}
