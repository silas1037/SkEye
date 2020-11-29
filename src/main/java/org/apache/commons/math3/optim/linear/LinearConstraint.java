package org.apache.commons.math3.optim.linear;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealVector;

public class LinearConstraint implements Serializable {
    private static final long serialVersionUID = -764632794033034092L;
    private final transient RealVector coefficients;
    private final Relationship relationship;
    private final double value;

    public LinearConstraint(double[] coefficients2, Relationship relationship2, double value2) {
        this(new ArrayRealVector(coefficients2), relationship2, value2);
    }

    public LinearConstraint(RealVector coefficients2, Relationship relationship2, double value2) {
        this.coefficients = coefficients2;
        this.relationship = relationship2;
        this.value = value2;
    }

    public LinearConstraint(double[] lhsCoefficients, double lhsConstant, Relationship relationship2, double[] rhsCoefficients, double rhsConstant) {
        double[] sub = new double[lhsCoefficients.length];
        for (int i = 0; i < sub.length; i++) {
            sub[i] = lhsCoefficients[i] - rhsCoefficients[i];
        }
        this.coefficients = new ArrayRealVector(sub, false);
        this.relationship = relationship2;
        this.value = rhsConstant - lhsConstant;
    }

    public LinearConstraint(RealVector lhsCoefficients, double lhsConstant, Relationship relationship2, RealVector rhsCoefficients, double rhsConstant) {
        this.coefficients = lhsCoefficients.subtract(rhsCoefficients);
        this.relationship = relationship2;
        this.value = rhsConstant - lhsConstant;
    }

    public RealVector getCoefficients() {
        return this.coefficients;
    }

    public Relationship getRelationship() {
        return this.relationship;
    }

    public double getValue() {
        return this.value;
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof LinearConstraint)) {
            return false;
        }
        LinearConstraint rhs = (LinearConstraint) other;
        return this.relationship == rhs.relationship && this.value == rhs.value && this.coefficients.equals(rhs.coefficients);
    }

    public int hashCode() {
        return (this.relationship.hashCode() ^ Double.valueOf(this.value).hashCode()) ^ this.coefficients.hashCode();
    }

    private void writeObject(ObjectOutputStream oos) throws IOException {
        oos.defaultWriteObject();
        MatrixUtils.serializeRealVector(this.coefficients, oos);
    }

    private void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException {
        ois.defaultReadObject();
        MatrixUtils.deserializeRealVector(this, "coefficients", ois);
    }
}
