package org.apache.commons.math3.stat.regression;

public interface MultipleLinearRegression {
    double estimateRegressandVariance();

    double[] estimateRegressionParameters();

    double[] estimateRegressionParametersStandardErrors();

    double[][] estimateRegressionParametersVariance();

    double[] estimateResiduals();
}
