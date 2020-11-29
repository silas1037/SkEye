package org.apache.commons.math3.random;

import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.util.FastMath;

public class StableRandomGenerator implements NormalizedRandomGenerator {
    private final double alpha;
    private final double beta;
    private final RandomGenerator generator;
    private final double zeta;

    public StableRandomGenerator(RandomGenerator generator2, double alpha2, double beta2) throws NullArgumentException, OutOfRangeException {
        if (generator2 == null) {
            throw new NullArgumentException();
        } else if (alpha2 <= 0.0d || alpha2 > 2.0d) {
            throw new OutOfRangeException(LocalizedFormats.OUT_OF_RANGE_LEFT, Double.valueOf(alpha2), 0, 2);
        } else if (beta2 < -1.0d || beta2 > 1.0d) {
            throw new OutOfRangeException(LocalizedFormats.OUT_OF_RANGE_SIMPLE, Double.valueOf(beta2), -1, 1);
        } else {
            this.generator = generator2;
            this.alpha = alpha2;
            this.beta = beta2;
            if (alpha2 >= 2.0d || beta2 == 0.0d) {
                this.zeta = 0.0d;
            } else {
                this.zeta = FastMath.tan((3.141592653589793d * alpha2) / 2.0d) * beta2;
            }
        }
    }

    @Override // org.apache.commons.math3.random.NormalizedRandomGenerator
    public double nextNormalizedDouble() {
        double omega = -FastMath.log(this.generator.nextDouble());
        double phi = 3.141592653589793d * (this.generator.nextDouble() - 0.5d);
        if (this.alpha == 2.0d) {
            return FastMath.sqrt(2.0d * omega) * FastMath.sin(phi);
        }
        if (this.beta != 0.0d) {
            double cosPhi = FastMath.cos(phi);
            if (FastMath.abs(this.alpha - 1.0d) > 1.0E-8d) {
                double alphaPhi = this.alpha * phi;
                double invAlphaPhi = phi - alphaPhi;
                return (((FastMath.sin(alphaPhi) + (this.zeta * FastMath.cos(alphaPhi))) / cosPhi) * (FastMath.cos(invAlphaPhi) + (this.zeta * FastMath.sin(invAlphaPhi)))) / FastMath.pow(omega * cosPhi, (1.0d - this.alpha) / this.alpha);
            }
            double betaPhi = 1.5707963267948966d + (this.beta * phi);
            double x = 0.6366197723675814d * ((FastMath.tan(phi) * betaPhi) - (this.beta * FastMath.log(((1.5707963267948966d * omega) * cosPhi) / betaPhi)));
            if (this.alpha != 1.0d) {
                return x + (this.beta * FastMath.tan((3.141592653589793d * this.alpha) / 2.0d));
            }
            return x;
        } else if (this.alpha == 1.0d) {
            return FastMath.tan(phi);
        } else {
            return (FastMath.pow(FastMath.cos((1.0d - this.alpha) * phi) * omega, (1.0d / this.alpha) - 1.0d) * FastMath.sin(this.alpha * phi)) / FastMath.pow(FastMath.cos(phi), 1.0d / this.alpha);
        }
    }
}
