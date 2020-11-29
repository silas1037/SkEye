package org.apache.commons.math3.optimization.linear;

@Deprecated
public enum Relationship {
    EQ("="),
    LEQ("<="),
    GEQ(">=");
    
    private final String stringValue;

    private Relationship(String stringValue2) {
        this.stringValue = stringValue2;
    }

    public String toString() {
        return this.stringValue;
    }

    public Relationship oppositeRelationship() {
        switch (this) {
            case LEQ:
                return GEQ;
            case GEQ:
                return LEQ;
            default:
                return EQ;
        }
    }
}
