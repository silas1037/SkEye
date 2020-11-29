package org.apache.commons.math3.optim.linear;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.math3.exception.TooManyIterationsException;
import org.apache.commons.math3.optim.OptimizationData;
import org.apache.commons.math3.optim.PointValuePair;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.Precision;

public class SimplexSolver extends LinearOptimizer {
    static final double DEFAULT_CUT_OFF = 1.0E-10d;
    private static final double DEFAULT_EPSILON = 1.0E-6d;
    static final int DEFAULT_ULPS = 10;
    private final double cutOff;
    private final double epsilon;
    private final int maxUlps;
    private PivotSelectionRule pivotSelection;
    private SolutionCallback solutionCallback;

    public SimplexSolver() {
        this(1.0E-6d, DEFAULT_ULPS, 1.0E-10d);
    }

    public SimplexSolver(double epsilon2) {
        this(epsilon2, DEFAULT_ULPS, 1.0E-10d);
    }

    public SimplexSolver(double epsilon2, int maxUlps2) {
        this(epsilon2, maxUlps2, 1.0E-10d);
    }

    public SimplexSolver(double epsilon2, int maxUlps2, double cutOff2) {
        this.epsilon = epsilon2;
        this.maxUlps = maxUlps2;
        this.cutOff = cutOff2;
        this.pivotSelection = PivotSelectionRule.DANTZIG;
    }

    @Override // org.apache.commons.math3.optim.nonlinear.scalar.MultivariateOptimizer, org.apache.commons.math3.optim.nonlinear.scalar.MultivariateOptimizer, org.apache.commons.math3.optim.linear.LinearOptimizer, org.apache.commons.math3.optim.linear.LinearOptimizer, org.apache.commons.math3.optim.BaseOptimizer, org.apache.commons.math3.optim.BaseMultivariateOptimizer
    public PointValuePair optimize(OptimizationData... optData) throws TooManyIterationsException {
        return super.optimize(optData);
    }

    /* access modifiers changed from: protected */
    @Override // org.apache.commons.math3.optim.nonlinear.scalar.MultivariateOptimizer, org.apache.commons.math3.optim.linear.LinearOptimizer, org.apache.commons.math3.optim.BaseOptimizer, org.apache.commons.math3.optim.BaseMultivariateOptimizer
    public void parseOptimizationData(OptimizationData... optData) {
        super.parseOptimizationData(optData);
        this.solutionCallback = null;
        for (OptimizationData data : optData) {
            if (data instanceof SolutionCallback) {
                this.solutionCallback = (SolutionCallback) data;
            } else if (data instanceof PivotSelectionRule) {
                this.pivotSelection = (PivotSelectionRule) data;
            }
        }
    }

    private Integer getPivotColumn(SimplexTableau tableau) {
        double minValue = 0.0d;
        Integer minPos = null;
        for (int i = tableau.getNumObjectiveFunctions(); i < tableau.getWidth() - 1; i++) {
            double entry = tableau.getEntry(0, i);
            if (entry < minValue) {
                minValue = entry;
                minPos = Integer.valueOf(i);
                if (this.pivotSelection == PivotSelectionRule.BLAND && isValidPivotColumn(tableau, i)) {
                    break;
                }
            }
        }
        return minPos;
    }

    private boolean isValidPivotColumn(SimplexTableau tableau, int col) {
        for (int i = tableau.getNumObjectiveFunctions(); i < tableau.getHeight(); i++) {
            if (Precision.compareTo(tableau.getEntry(i, col), 0.0d, this.cutOff) > 0) {
                return true;
            }
        }
        return false;
    }

    private Integer getPivotRow(SimplexTableau tableau, int col) {
        List<Integer> minRatioPositions = new ArrayList<>();
        double minRatio = Double.MAX_VALUE;
        for (int i = tableau.getNumObjectiveFunctions(); i < tableau.getHeight(); i++) {
            double rhs = tableau.getEntry(i, tableau.getWidth() - 1);
            double entry = tableau.getEntry(i, col);
            if (Precision.compareTo(entry, 0.0d, this.cutOff) > 0) {
                double ratio = FastMath.abs(rhs / entry);
                int cmp = Double.compare(ratio, minRatio);
                if (cmp == 0) {
                    minRatioPositions.add(Integer.valueOf(i));
                } else if (cmp < 0) {
                    minRatio = ratio;
                    minRatioPositions.clear();
                    minRatioPositions.add(Integer.valueOf(i));
                }
            }
        }
        if (minRatioPositions.size() == 0) {
            return null;
        }
        if (minRatioPositions.size() <= 1) {
            return minRatioPositions.get(0);
        }
        if (tableau.getNumArtificialVariables() > 0) {
            for (Integer row : minRatioPositions) {
                int i2 = 0;
                while (true) {
                    if (i2 < tableau.getNumArtificialVariables()) {
                        int column = i2 + tableau.getArtificialVariableOffset();
                        if (Precision.equals(tableau.getEntry(row.intValue(), column), 1.0d, this.maxUlps) && row.equals(tableau.getBasicRow(column))) {
                            return row;
                        }
                        i2++;
                    }
                }
            }
        }
        Integer minRow = null;
        int minIndex = tableau.getWidth();
        for (Integer row2 : minRatioPositions) {
            int basicVar = tableau.getBasicVariable(row2.intValue());
            if (basicVar < minIndex) {
                minIndex = basicVar;
                minRow = row2;
            }
        }
        return minRow;
    }

    /* access modifiers changed from: protected */
    public void doIteration(SimplexTableau tableau) throws TooManyIterationsException, UnboundedSolutionException {
        incrementIterationCount();
        Integer pivotCol = getPivotColumn(tableau);
        Integer pivotRow = getPivotRow(tableau, pivotCol.intValue());
        if (pivotRow == null) {
            throw new UnboundedSolutionException();
        }
        tableau.performRowOperations(pivotCol.intValue(), pivotRow.intValue());
    }

    /* access modifiers changed from: protected */
    public void solvePhase1(SimplexTableau tableau) throws TooManyIterationsException, UnboundedSolutionException, NoFeasibleSolutionException {
        if (tableau.getNumArtificialVariables() != 0) {
            while (!tableau.isOptimal()) {
                doIteration(tableau);
            }
            if (!Precision.equals(tableau.getEntry(0, tableau.getRhsOffset()), 0.0d, this.epsilon)) {
                throw new NoFeasibleSolutionException();
            }
        }
    }

    @Override // org.apache.commons.math3.optim.BaseOptimizer
    public PointValuePair doOptimize() throws TooManyIterationsException, UnboundedSolutionException, NoFeasibleSolutionException {
        double[] coeff;
        if (this.solutionCallback != null) {
            this.solutionCallback.setTableau(null);
        }
        SimplexTableau tableau = new SimplexTableau(getFunction(), getConstraints(), getGoalType(), isRestrictedToNonNegative(), this.epsilon, this.maxUlps);
        solvePhase1(tableau);
        tableau.dropPhase1Objective();
        if (this.solutionCallback != null) {
            this.solutionCallback.setTableau(tableau);
        }
        while (!tableau.isOptimal()) {
            doIteration(tableau);
        }
        PointValuePair solution = tableau.getSolution();
        if (isRestrictedToNonNegative()) {
            for (double d : solution.getPoint()) {
                if (Precision.compareTo(d, 0.0d, this.epsilon) < 0) {
                    throw new NoFeasibleSolutionException();
                }
            }
        }
        return solution;
    }
}
