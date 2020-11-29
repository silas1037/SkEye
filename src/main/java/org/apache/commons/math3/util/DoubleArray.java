package org.apache.commons.math3.util;

public interface DoubleArray {
    void addElement(double d);

    double addElementRolling(double d);

    void addElements(double[] dArr);

    void clear();

    double getElement(int i);

    double[] getElements();

    int getNumElements();

    void setElement(int i, double d);
}
