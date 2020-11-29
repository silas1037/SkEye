package org.apache.commons.math3.analysis.interpolation;

import org.apache.commons.math3.analysis.MultivariateFunction;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.NoDataException;
import org.apache.commons.math3.exception.NotPositiveException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.random.UnitSphereRandomVectorGenerator;

public class MicrosphereProjectionInterpolator implements MultivariateInterpolator {
    private final double exponent;
    private final InterpolatingMicrosphere microsphere;
    private final double noInterpolationTolerance;
    private final boolean sharedSphere;

    public MicrosphereProjectionInterpolator(int dimension, int elements, double maxDarkFraction, double darkThreshold, double background, double exponent2, boolean sharedSphere2, double noInterpolationTolerance2) {
        this(new InterpolatingMicrosphere(dimension, elements, maxDarkFraction, darkThreshold, background, new UnitSphereRandomVectorGenerator(dimension)), exponent2, sharedSphere2, noInterpolationTolerance2);
    }

    public MicrosphereProjectionInterpolator(InterpolatingMicrosphere microsphere2, double exponent2, boolean sharedSphere2, double noInterpolationTolerance2) throws NotPositiveException {
        if (exponent2 < 0.0d) {
            throw new NotPositiveException(Double.valueOf(exponent2));
        }
        this.microsphere = microsphere2;
        this.exponent = exponent2;
        this.sharedSphere = sharedSphere2;
        this.noInterpolationTolerance = noInterpolationTolerance2;
    }

    @Override // org.apache.commons.math3.analysis.interpolation.MultivariateInterpolator
    public MultivariateFunction interpolate(final double[][] xval, final double[] yval) throws DimensionMismatchException, NoDataException, NullArgumentException {
        if (xval == null || yval == null) {
            throw new NullArgumentException();
        } else if (xval.length == 0) {
            throw new NoDataException();
        } else if (xval.length != yval.length) {
            throw new DimensionMismatchException(xval.length, yval.length);
        } else if (xval[0] == null) {
            throw new NullArgumentException();
        } else {
            int dimension = this.microsphere.getDimension();
            if (dimension != xval[0].length) {
                throw new DimensionMismatchException(xval[0].length, dimension);
            }
            final InterpolatingMicrosphere m = this.sharedSphere ? this.microsphere : this.microsphere.copy();
            return new MultivariateFunction() {
                /* class org.apache.commons.math3.analysis.interpolation.MicrosphereProjectionInterpolator.C02041 */

                @Override // org.apache.commons.math3.analysis.MultivariateFunction
                public double value(double[] point) {
                    return m.value(point, xval, yval, MicrosphereProjectionInterpolator.this.exponent, MicrosphereProjectionInterpolator.this.noInterpolationTolerance);
                }
            };
        }
    }
}
