package org.apache.commons.math3.ode.events;

import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.Precision;

enum Transformer {
    UNINITIALIZED {
        /* access modifiers changed from: protected */
        @Override // org.apache.commons.math3.ode.events.Transformer
        public double transformed(double g) {
            return 0.0d;
        }
    },
    PLUS {
        /* access modifiers changed from: protected */
        @Override // org.apache.commons.math3.ode.events.Transformer
        public double transformed(double g) {
            return g;
        }
    },
    MINUS {
        /* access modifiers changed from: protected */
        @Override // org.apache.commons.math3.ode.events.Transformer
        public double transformed(double g) {
            return -g;
        }
    },
    MIN {
        /* access modifiers changed from: protected */
        @Override // org.apache.commons.math3.ode.events.Transformer
        public double transformed(double g) {
            return FastMath.min(-Precision.SAFE_MIN, FastMath.min(-g, g));
        }
    },
    MAX {
        /* access modifiers changed from: protected */
        @Override // org.apache.commons.math3.ode.events.Transformer
        public double transformed(double g) {
            return FastMath.max(Precision.SAFE_MIN, FastMath.max(-g, g));
        }
    };

    /* access modifiers changed from: protected */
    public abstract double transformed(double d);
}
