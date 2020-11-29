package org.apache.commons.math3.analysis.integration.gauss;

import java.lang.Number;
import java.util.Map;
import java.util.TreeMap;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.util.Pair;

public abstract class BaseRuleFactory<T extends Number> {
    private final Map<Integer, Pair<T[], T[]>> pointsAndWeights = new TreeMap();
    private final Map<Integer, Pair<double[], double[]>> pointsAndWeightsDouble = new TreeMap();

    /* access modifiers changed from: protected */
    public abstract Pair<T[], T[]> computeRule(int i) throws DimensionMismatchException;

    public Pair<double[], double[]> getRule(int numberOfPoints) throws NotStrictlyPositiveException, DimensionMismatchException {
        if (numberOfPoints <= 0) {
            throw new NotStrictlyPositiveException(LocalizedFormats.NUMBER_OF_POINTS, Integer.valueOf(numberOfPoints));
        }
        Pair<double[], double[]> cached = this.pointsAndWeightsDouble.get(Integer.valueOf(numberOfPoints));
        if (cached == null) {
            cached = convertToDouble(getRuleInternal(numberOfPoints));
            this.pointsAndWeightsDouble.put(Integer.valueOf(numberOfPoints), cached);
        }
        return new Pair<>(cached.getFirst().clone(), cached.getSecond().clone());
    }

    /* access modifiers changed from: protected */
    public synchronized Pair<T[], T[]> getRuleInternal(int numberOfPoints) throws DimensionMismatchException {
        Pair<T[], T[]> rule;
        rule = this.pointsAndWeights.get(Integer.valueOf(numberOfPoints));
        if (rule == null) {
            addRule(computeRule(numberOfPoints));
            rule = getRuleInternal(numberOfPoints);
        }
        return rule;
    }

    /* access modifiers changed from: protected */
    public void addRule(Pair<T[], T[]> rule) throws DimensionMismatchException {
        if (rule.getFirst().length != rule.getSecond().length) {
            throw new DimensionMismatchException(rule.getFirst().length, rule.getSecond().length);
        }
        this.pointsAndWeights.put(Integer.valueOf(rule.getFirst().length), rule);
    }

    private static <T extends Number> Pair<double[], double[]> convertToDouble(Pair<T[], T[]> rule) {
        T[] pT = rule.getFirst();
        T[] wT = rule.getSecond();
        int len = pT.length;
        double[] pD = new double[len];
        double[] wD = new double[len];
        for (int i = 0; i < len; i++) {
            pD[i] = pT[i].doubleValue();
            wD[i] = wT[i].doubleValue();
        }
        return new Pair<>(pD, wD);
    }
}
