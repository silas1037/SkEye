package org.apache.commons.math3.analysis.interpolation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.math3.analysis.MultivariateFunction;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.NoDataException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.random.UnitSphereRandomVectorGenerator;
import org.apache.commons.math3.util.FastMath;

@Deprecated
public class MicrosphereInterpolatingFunction implements MultivariateFunction {
    private final double brightnessExponent;
    private final int dimension;
    private final List<MicrosphereSurfaceElement> microsphere;
    private final Map<RealVector, Double> samples;

    private static class MicrosphereSurfaceElement {
        private double brightestIllumination;
        private Map.Entry<RealVector, Double> brightestSample;
        private final RealVector normal;

        MicrosphereSurfaceElement(double[] n) {
            this.normal = new ArrayRealVector(n);
        }

        /* access modifiers changed from: package-private */
        public RealVector normal() {
            return this.normal;
        }

        /* access modifiers changed from: package-private */
        public void reset() {
            this.brightestIllumination = 0.0d;
            this.brightestSample = null;
        }

        /* access modifiers changed from: package-private */
        public void store(double illuminationFromSample, Map.Entry<RealVector, Double> sample) {
            if (illuminationFromSample > this.brightestIllumination) {
                this.brightestIllumination = illuminationFromSample;
                this.brightestSample = sample;
            }
        }

        /* access modifiers changed from: package-private */
        public double illumination() {
            return this.brightestIllumination;
        }

        /* access modifiers changed from: package-private */
        public Map.Entry<RealVector, Double> sample() {
            return this.brightestSample;
        }
    }

    public MicrosphereInterpolatingFunction(double[][] xval, double[] yval, int brightnessExponent2, int microsphereElements, UnitSphereRandomVectorGenerator rand) throws DimensionMismatchException, NoDataException, NullArgumentException {
        if (xval == null || yval == null) {
            throw new NullArgumentException();
        } else if (xval.length == 0) {
            throw new NoDataException();
        } else if (xval.length != yval.length) {
            throw new DimensionMismatchException(xval.length, yval.length);
        } else if (xval[0] == null) {
            throw new NullArgumentException();
        } else {
            this.dimension = xval[0].length;
            this.brightnessExponent = (double) brightnessExponent2;
            this.samples = new HashMap(yval.length);
            for (int i = 0; i < xval.length; i++) {
                double[] xvalI = xval[i];
                if (xvalI == null) {
                    throw new NullArgumentException();
                } else if (xvalI.length != this.dimension) {
                    throw new DimensionMismatchException(xvalI.length, this.dimension);
                } else {
                    this.samples.put(new ArrayRealVector(xvalI), Double.valueOf(yval[i]));
                }
            }
            this.microsphere = new ArrayList(microsphereElements);
            for (int i2 = 0; i2 < microsphereElements; i2++) {
                this.microsphere.add(new MicrosphereSurfaceElement(rand.nextVector()));
            }
        }
    }

    @Override // org.apache.commons.math3.analysis.MultivariateFunction
    public double value(double[] point) throws DimensionMismatchException {
        RealVector p = new ArrayRealVector(point);
        for (MicrosphereSurfaceElement md : this.microsphere) {
            md.reset();
        }
        for (Map.Entry<RealVector, Double> sd : this.samples.entrySet()) {
            RealVector diff = sd.getKey().subtract(p);
            double diffNorm = diff.getNorm();
            if (FastMath.abs(diffNorm) < FastMath.ulp(1.0d)) {
                return sd.getValue().doubleValue();
            }
            for (MicrosphereSurfaceElement md2 : this.microsphere) {
                md2.store(cosAngle(diff, md2.normal()) * FastMath.pow(diffNorm, -this.brightnessExponent), sd);
            }
        }
        double value = 0.0d;
        double totalWeight = 0.0d;
        for (MicrosphereSurfaceElement md3 : this.microsphere) {
            double iV = md3.illumination();
            Map.Entry<RealVector, Double> sd2 = md3.sample();
            if (sd2 != null) {
                value += sd2.getValue().doubleValue() * iV;
                totalWeight += iV;
            }
        }
        return value / totalWeight;
    }

    private double cosAngle(RealVector v, RealVector w) {
        return v.dotProduct(w) / (v.getNorm() * w.getNorm());
    }
}
