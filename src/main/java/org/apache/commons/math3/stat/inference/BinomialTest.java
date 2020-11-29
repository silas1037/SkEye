package org.apache.commons.math3.stat.inference;

import org.apache.commons.math3.distribution.BinomialDistribution;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.MathInternalError;
import org.apache.commons.math3.exception.NotPositiveException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.exception.util.LocalizedFormats;

public class BinomialTest {
    public boolean binomialTest(int numberOfTrials, int numberOfSuccesses, double probability, AlternativeHypothesis alternativeHypothesis, double alpha) {
        return binomialTest(numberOfTrials, numberOfSuccesses, probability, alternativeHypothesis) < alpha;
    }

    public double binomialTest(int numberOfTrials, int numberOfSuccesses, double probability, AlternativeHypothesis alternativeHypothesis) {
        if (numberOfTrials < 0) {
            throw new NotPositiveException(Integer.valueOf(numberOfTrials));
        } else if (numberOfSuccesses < 0) {
            throw new NotPositiveException(Integer.valueOf(numberOfSuccesses));
        } else if (probability < 0.0d || probability > 1.0d) {
            throw new OutOfRangeException(Double.valueOf(probability), 0, 1);
        } else if (numberOfTrials < numberOfSuccesses) {
            throw new MathIllegalArgumentException(LocalizedFormats.BINOMIAL_INVALID_PARAMETERS_ORDER, Integer.valueOf(numberOfTrials), Integer.valueOf(numberOfSuccesses));
        } else if (alternativeHypothesis == null) {
            throw new NullArgumentException();
        } else {
            BinomialDistribution distribution = new BinomialDistribution(null, numberOfTrials, probability);
            switch (alternativeHypothesis) {
                case GREATER_THAN:
                    return 1.0d - distribution.cumulativeProbability(numberOfSuccesses - 1);
                case LESS_THAN:
                    return distribution.cumulativeProbability(numberOfSuccesses);
                case TWO_SIDED:
                    int criticalValueLow = 0;
                    int criticalValueHigh = numberOfTrials;
                    double pTotal = 0.0d;
                    do {
                        double pLow = distribution.probability(criticalValueLow);
                        double pHigh = distribution.probability(criticalValueHigh);
                        if (pLow == pHigh) {
                            pTotal += 2.0d * pLow;
                            criticalValueLow++;
                            criticalValueHigh--;
                        } else if (pLow < pHigh) {
                            pTotal += pLow;
                            criticalValueLow++;
                        } else {
                            pTotal += pHigh;
                            criticalValueHigh--;
                        }
                        if (criticalValueLow > numberOfSuccesses) {
                            return pTotal;
                        }
                    } while (criticalValueHigh >= numberOfSuccesses);
                    return pTotal;
                default:
                    throw new MathInternalError(LocalizedFormats.OUT_OF_RANGE_SIMPLE, alternativeHypothesis, AlternativeHypothesis.TWO_SIDED, AlternativeHypothesis.LESS_THAN);
            }
        }
    }
}
