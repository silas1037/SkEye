package org.apache.commons.math3.ode.events;

import org.apache.commons.math3.exception.MathInternalError;

public enum FilterType {
    TRIGGER_ONLY_DECREASING_EVENTS {
        /* access modifiers changed from: protected */
        @Override // org.apache.commons.math3.ode.events.FilterType
        public boolean getTriggeredIncreasing() {
            return false;
        }

        /* access modifiers changed from: protected */
        @Override // org.apache.commons.math3.ode.events.FilterType
        public Transformer selectTransformer(Transformer previous, double g, boolean forward) {
            if (forward) {
                switch (C02973.$SwitchMap$org$apache$commons$math3$ode$events$Transformer[previous.ordinal()]) {
                    case 1:
                        if (g > 0.0d) {
                            return Transformer.MAX;
                        }
                        if (g < 0.0d) {
                            return Transformer.PLUS;
                        }
                        return Transformer.UNINITIALIZED;
                    case 2:
                        if (g >= 0.0d) {
                            return Transformer.MIN;
                        }
                        return previous;
                    case 3:
                        if (g >= 0.0d) {
                            return Transformer.MAX;
                        }
                        return previous;
                    case 4:
                        if (g <= 0.0d) {
                            return Transformer.MINUS;
                        }
                        return previous;
                    case 5:
                        if (g <= 0.0d) {
                            return Transformer.PLUS;
                        }
                        return previous;
                    default:
                        throw new MathInternalError();
                }
            } else {
                switch (C02973.$SwitchMap$org$apache$commons$math3$ode$events$Transformer[previous.ordinal()]) {
                    case 1:
                        if (g > 0.0d) {
                            return Transformer.MINUS;
                        }
                        if (g < 0.0d) {
                            return Transformer.MIN;
                        }
                        return Transformer.UNINITIALIZED;
                    case 2:
                        if (g <= 0.0d) {
                            return Transformer.MAX;
                        }
                        return previous;
                    case 3:
                        if (g <= 0.0d) {
                            return Transformer.MIN;
                        }
                        return previous;
                    case 4:
                        if (g >= 0.0d) {
                            return Transformer.PLUS;
                        }
                        return previous;
                    case 5:
                        if (g >= 0.0d) {
                            return Transformer.MINUS;
                        }
                        return previous;
                    default:
                        throw new MathInternalError();
                }
            }
        }
    },
    TRIGGER_ONLY_INCREASING_EVENTS {
        /* access modifiers changed from: protected */
        @Override // org.apache.commons.math3.ode.events.FilterType
        public boolean getTriggeredIncreasing() {
            return true;
        }

        /* access modifiers changed from: protected */
        @Override // org.apache.commons.math3.ode.events.FilterType
        public Transformer selectTransformer(Transformer previous, double g, boolean forward) {
            if (forward) {
                switch (C02973.$SwitchMap$org$apache$commons$math3$ode$events$Transformer[previous.ordinal()]) {
                    case 1:
                        if (g > 0.0d) {
                            return Transformer.PLUS;
                        }
                        if (g < 0.0d) {
                            return Transformer.MIN;
                        }
                        return Transformer.UNINITIALIZED;
                    case 2:
                        if (g <= 0.0d) {
                            return Transformer.MAX;
                        }
                        return previous;
                    case 3:
                        if (g <= 0.0d) {
                            return Transformer.MIN;
                        }
                        return previous;
                    case 4:
                        if (g >= 0.0d) {
                            return Transformer.PLUS;
                        }
                        return previous;
                    case 5:
                        if (g >= 0.0d) {
                            return Transformer.MINUS;
                        }
                        return previous;
                    default:
                        throw new MathInternalError();
                }
            } else {
                switch (C02973.$SwitchMap$org$apache$commons$math3$ode$events$Transformer[previous.ordinal()]) {
                    case 1:
                        if (g > 0.0d) {
                            return Transformer.MAX;
                        }
                        if (g < 0.0d) {
                            return Transformer.MINUS;
                        }
                        return Transformer.UNINITIALIZED;
                    case 2:
                        if (g >= 0.0d) {
                            return Transformer.MIN;
                        }
                        return previous;
                    case 3:
                        if (g >= 0.0d) {
                            return Transformer.MAX;
                        }
                        return previous;
                    case 4:
                        if (g <= 0.0d) {
                            return Transformer.MINUS;
                        }
                        return previous;
                    case 5:
                        if (g <= 0.0d) {
                            return Transformer.PLUS;
                        }
                        return previous;
                    default:
                        throw new MathInternalError();
                }
            }
        }
    };

    /* access modifiers changed from: protected */
    public abstract boolean getTriggeredIncreasing();

    /* access modifiers changed from: protected */
    public abstract Transformer selectTransformer(Transformer transformer, double d, boolean z);

    /* renamed from: org.apache.commons.math3.ode.events.FilterType$3 */
    static /* synthetic */ class C02973 {
        static final /* synthetic */ int[] $SwitchMap$org$apache$commons$math3$ode$events$Transformer = new int[Transformer.values().length];

        static {
            try {
                $SwitchMap$org$apache$commons$math3$ode$events$Transformer[Transformer.UNINITIALIZED.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$org$apache$commons$math3$ode$events$Transformer[Transformer.PLUS.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$org$apache$commons$math3$ode$events$Transformer[Transformer.MINUS.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$org$apache$commons$math3$ode$events$Transformer[Transformer.MIN.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$org$apache$commons$math3$ode$events$Transformer[Transformer.MAX.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
        }
    }
}
