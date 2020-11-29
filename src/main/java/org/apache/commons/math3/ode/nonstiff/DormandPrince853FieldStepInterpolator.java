package org.apache.commons.math3.ode.nonstiff;

import org.apache.commons.math3.Field;
import org.apache.commons.math3.RealFieldElement;
import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.ode.FieldEquationsMapper;
import org.apache.commons.math3.ode.FieldODEStateAndDerivative;
import org.apache.commons.math3.util.MathArrays;

/* access modifiers changed from: package-private */
public class DormandPrince853FieldStepInterpolator<T extends RealFieldElement<T>> extends RungeKuttaFieldStepInterpolator<T> {

    /* renamed from: d */
    private final T[][] f291d;

    /* JADX DEBUG: Multi-variable search result rejected for r0v51, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[][] */
    /* JADX DEBUG: Multi-variable search result rejected for r0v59, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[][] */
    /* JADX DEBUG: Multi-variable search result rejected for r0v65, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[][] */
    /* JADX DEBUG: Multi-variable search result rejected for r0v71, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[][] */
    /* JADX DEBUG: Multi-variable search result rejected for r0v77, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[][] */
    /* JADX DEBUG: Multi-variable search result rejected for r0v83, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[][] */
    /* JADX DEBUG: Multi-variable search result rejected for r0v89, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[][] */
    /* JADX DEBUG: Multi-variable search result rejected for r0v95, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[][] */
    /* JADX DEBUG: Multi-variable search result rejected for r0v101, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[][] */
    /* JADX DEBUG: Multi-variable search result rejected for r0v107, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[][] */
    /* JADX DEBUG: Multi-variable search result rejected for r0v113, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[][] */
    /* JADX DEBUG: Multi-variable search result rejected for r0v119, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[][] */
    /* JADX DEBUG: Multi-variable search result rejected for r0v125, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[][] */
    /* JADX DEBUG: Multi-variable search result rejected for r0v131, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[][] */
    /* JADX DEBUG: Multi-variable search result rejected for r0v137, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[][] */
    /* JADX DEBUG: Multi-variable search result rejected for r0v143, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[][] */
    /* JADX DEBUG: Multi-variable search result rejected for r0v149, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[][] */
    /* JADX DEBUG: Multi-variable search result rejected for r0v157, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[][] */
    /* JADX DEBUG: Multi-variable search result rejected for r0v163, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[][] */
    /* JADX DEBUG: Multi-variable search result rejected for r0v169, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[][] */
    /* JADX DEBUG: Multi-variable search result rejected for r0v175, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[][] */
    /* JADX DEBUG: Multi-variable search result rejected for r0v181, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[][] */
    /* JADX DEBUG: Multi-variable search result rejected for r0v187, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[][] */
    /* JADX DEBUG: Multi-variable search result rejected for r0v193, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[][] */
    /* JADX DEBUG: Multi-variable search result rejected for r0v199, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[][] */
    /* JADX DEBUG: Multi-variable search result rejected for r0v205, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[][] */
    /* JADX DEBUG: Multi-variable search result rejected for r0v211, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[][] */
    /* JADX DEBUG: Multi-variable search result rejected for r0v217, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[][] */
    /* JADX DEBUG: Multi-variable search result rejected for r0v223, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[][] */
    /* JADX DEBUG: Multi-variable search result rejected for r0v231, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[][] */
    /* JADX DEBUG: Multi-variable search result rejected for r0v237, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[][] */
    /* JADX DEBUG: Multi-variable search result rejected for r0v243, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[][] */
    /* JADX WARN: Multi-variable type inference failed */
    DormandPrince853FieldStepInterpolator(Field<T> field, boolean forward, T[][] yDotK, FieldODEStateAndDerivative<T> globalPreviousState, FieldODEStateAndDerivative<T> globalCurrentState, FieldODEStateAndDerivative<T> softPreviousState, FieldODEStateAndDerivative<T> softCurrentState, FieldEquationsMapper<T> mapper) {
        super(field, forward, yDotK, globalPreviousState, globalCurrentState, softPreviousState, softCurrentState, mapper);
        this.f291d = (T[][]) ((RealFieldElement[][]) MathArrays.buildArray(field, 7, 16));
        this.f291d[0][0] = fraction(field, 104257.0d, 1920240.0d);
        this.f291d[0][1] = field.getZero();
        this.f291d[0][2] = field.getZero();
        this.f291d[0][3] = field.getZero();
        this.f291d[0][4] = field.getZero();
        this.f291d[0][5] = fraction(field, 3399327.0d, 763840.0d);
        this.f291d[0][6] = fraction(field, 6.6578432E7d, 3.5198415E7d);
        this.f291d[0][7] = fraction(field, -1.674902723E9d, 2.887164E8d);
        this.f291d[0][8] = fraction(field, 5.4980371265625E13d, 1.76692375811392E14d);
        this.f291d[0][9] = fraction(field, -734375.0d, 4826304.0d);
        this.f291d[0][10] = fraction(field, 1.71414593E8d, 8.512614E8d);
        this.f291d[0][11] = fraction(field, 137909.0d, 3084480.0d);
        this.f291d[0][12] = field.getZero();
        this.f291d[0][13] = field.getZero();
        this.f291d[0][14] = field.getZero();
        this.f291d[0][15] = field.getZero();
        ((T[][]) this.f291d)[1][0] = (RealFieldElement) ((RealFieldElement) this.f291d[0][0].negate()).add(1.0d);
        ((T[][]) this.f291d)[1][1] = (RealFieldElement) this.f291d[0][1].negate();
        ((T[][]) this.f291d)[1][2] = (RealFieldElement) this.f291d[0][2].negate();
        ((T[][]) this.f291d)[1][3] = (RealFieldElement) this.f291d[0][3].negate();
        ((T[][]) this.f291d)[1][4] = (RealFieldElement) this.f291d[0][4].negate();
        ((T[][]) this.f291d)[1][5] = (RealFieldElement) this.f291d[0][5].negate();
        ((T[][]) this.f291d)[1][6] = (RealFieldElement) this.f291d[0][6].negate();
        ((T[][]) this.f291d)[1][7] = (RealFieldElement) this.f291d[0][7].negate();
        ((T[][]) this.f291d)[1][8] = (RealFieldElement) this.f291d[0][8].negate();
        ((T[][]) this.f291d)[1][9] = (RealFieldElement) this.f291d[0][9].negate();
        ((T[][]) this.f291d)[1][10] = (RealFieldElement) this.f291d[0][10].negate();
        ((T[][]) this.f291d)[1][11] = (RealFieldElement) this.f291d[0][11].negate();
        ((T[][]) this.f291d)[1][12] = (RealFieldElement) this.f291d[0][12].negate();
        ((T[][]) this.f291d)[1][13] = (RealFieldElement) this.f291d[0][13].negate();
        ((T[][]) this.f291d)[1][14] = (RealFieldElement) this.f291d[0][14].negate();
        ((T[][]) this.f291d)[1][15] = (RealFieldElement) this.f291d[0][15].negate();
        ((T[][]) this.f291d)[2][0] = (RealFieldElement) ((RealFieldElement) this.f291d[0][0].multiply(2)).subtract(1.0d);
        ((T[][]) this.f291d)[2][1] = (RealFieldElement) this.f291d[0][1].multiply(2);
        ((T[][]) this.f291d)[2][2] = (RealFieldElement) this.f291d[0][2].multiply(2);
        ((T[][]) this.f291d)[2][3] = (RealFieldElement) this.f291d[0][3].multiply(2);
        ((T[][]) this.f291d)[2][4] = (RealFieldElement) this.f291d[0][4].multiply(2);
        ((T[][]) this.f291d)[2][5] = (RealFieldElement) this.f291d[0][5].multiply(2);
        ((T[][]) this.f291d)[2][6] = (RealFieldElement) this.f291d[0][6].multiply(2);
        ((T[][]) this.f291d)[2][7] = (RealFieldElement) this.f291d[0][7].multiply(2);
        ((T[][]) this.f291d)[2][8] = (RealFieldElement) this.f291d[0][8].multiply(2);
        ((T[][]) this.f291d)[2][9] = (RealFieldElement) this.f291d[0][9].multiply(2);
        ((T[][]) this.f291d)[2][10] = (RealFieldElement) this.f291d[0][10].multiply(2);
        ((T[][]) this.f291d)[2][11] = (RealFieldElement) this.f291d[0][11].multiply(2);
        ((T[][]) this.f291d)[2][12] = (RealFieldElement) ((RealFieldElement) this.f291d[0][12].multiply(2)).subtract(1.0d);
        ((T[][]) this.f291d)[2][13] = (RealFieldElement) this.f291d[0][13].multiply(2);
        ((T[][]) this.f291d)[2][14] = (RealFieldElement) this.f291d[0][14].multiply(2);
        ((T[][]) this.f291d)[2][15] = (RealFieldElement) this.f291d[0][15].multiply(2);
        this.f291d[3][0] = fraction(field, -1.7751989329E10d, 2.10607656E9d);
        this.f291d[3][1] = field.getZero();
        this.f291d[3][2] = field.getZero();
        this.f291d[3][3] = field.getZero();
        this.f291d[3][4] = field.getZero();
        this.f291d[3][5] = fraction(field, 4.272954039E9d, 7.53986464E9d);
        this.f291d[3][6] = fraction(field, -1.18476319744E11d, 3.8604839385E10d);
        this.f291d[3][7] = fraction(field, 7.55123450731E11d, 3.166577316E11d);
        this.f291d[3][8] = fraction(field, 3.6923844612348283E18d, 1.7441304416342505E18d);
        this.f291d[3][9] = fraction(field, -4.612609375E9d, 5.293382976E9d);
        this.f291d[3][10] = fraction(field, 2.091772278379E12d, 9.336445866E11d);
        this.f291d[3][11] = fraction(field, 2.136624137E9d, 3.38298912E9d);
        this.f291d[3][12] = fraction(field, -126493.0d, 1421424.0d);
        this.f291d[3][13] = fraction(field, 9.835E7d, 5419179.0d);
        this.f291d[3][14] = fraction(field, -1.8878125E7d, 2053168.0d);
        this.f291d[3][15] = fraction(field, -1.944542619E9d, 4.38351368E8d);
        this.f291d[4][0] = fraction(field, 3.2941697297E10d, 3.15911484E9d);
        this.f291d[4][1] = field.getZero();
        this.f291d[4][2] = field.getZero();
        this.f291d[4][3] = field.getZero();
        this.f291d[4][4] = field.getZero();
        this.f291d[4][5] = fraction(field, 4.56696183123E11d, 1.88496616E9d);
        this.f291d[4][6] = fraction(field, 1.9132610714624E13d, 1.15814518155E11d);
        this.f291d[4][7] = fraction(field, -1.77904688592943E14d, 4.749865974E11d);
        this.f291d[4][8] = fraction(field, -4.8211399418367652E18d, 2.18016305204281312E17d);
        this.f291d[4][9] = fraction(field, 3.0702015625E10d, 3.970037232E9d);
        this.f291d[4][10] = fraction(field, -8.5916079474274E13d, 2.8009337598E12d);
        this.f291d[4][11] = fraction(field, -5.919468007E9d, 6.3431046E8d);
        this.f291d[4][12] = fraction(field, 2479159.0d, 157936.0d);
        this.f291d[4][13] = fraction(field, -1.875E7d, 602131.0d);
        this.f291d[4][14] = fraction(field, -1.9203125E7d, 2053168.0d);
        this.f291d[4][15] = fraction(field, 1.5700361463E10d, 4.38351368E8d);
        this.f291d[5][0] = fraction(field, 1.2627015655E10d, 6.31822968E8d);
        this.f291d[5][1] = field.getZero();
        this.f291d[5][2] = field.getZero();
        this.f291d[5][3] = field.getZero();
        this.f291d[5][4] = field.getZero();
        this.f291d[5][5] = fraction(field, -7.2955222965E10d, 1.88496616E8d);
        this.f291d[5][6] = fraction(field, -1.314574495232E13d, 6.9488710893E10d);
        this.f291d[5][7] = fraction(field, 3.0084216194513E13d, 5.6998391688E10d);
        this.f291d[5][8] = fraction(field, -2.9685876100664064E17d, 2.5648977082856624E16d);
        this.f291d[5][9] = fraction(field, 5.69140625E8d, 8.2709109E7d);
        this.f291d[5][10] = fraction(field, -1.8684190637E10d, 1.8672891732E10d);
        this.f291d[5][11] = fraction(field, 6.9644045E7d, 8.9549712E7d);
        this.f291d[5][12] = fraction(field, -1.1847025E7d, 4264272.0d);
        this.f291d[5][13] = fraction(field, -9.7865E8d, 1.6257537E7d);
        this.f291d[5][14] = fraction(field, 5.19371875E8d, 6159504.0d);
        this.f291d[5][15] = fraction(field, 5.256837225E9d, 4.38351368E8d);
        this.f291d[6][0] = fraction(field, -4.50944925E8d, 1.7550638E7d);
        this.f291d[6][1] = field.getZero();
        this.f291d[6][2] = field.getZero();
        this.f291d[6][3] = field.getZero();
        this.f291d[6][4] = field.getZero();
        this.f291d[6][5] = fraction(field, -1.4532122925E10d, 9.4248308E7d);
        this.f291d[6][6] = fraction(field, -5.958769664E11d, 2.573655959E9d);
        this.f291d[6][7] = fraction(field, 1.88748653015E11d, 5.27762886E8d);
        this.f291d[6][8] = fraction(field, 2.5454854581152343E18d, 2.7252038150535164E16d);
        this.f291d[6][9] = fraction(field, -1.376953125E9d, 3.6759604E7d);
        this.f291d[6][10] = fraction(field, 5.3995596795E10d, 5.18691437E8d);
        this.f291d[6][11] = fraction(field, 2.10311225E8d, 7047894.0d);
        this.f291d[6][12] = fraction(field, -1718875.0d, 39484.0d);
        this.f291d[6][13] = fraction(field, 5.8E7d, 602131.0d);
        this.f291d[6][14] = fraction(field, -1546875.0d, 39484.0d);
        this.f291d[6][15] = fraction(field, -1.262172375E9d, 8429834.0d);
    }

    /* access modifiers changed from: protected */
    @Override // org.apache.commons.math3.ode.nonstiff.RungeKuttaFieldStepInterpolator
    public DormandPrince853FieldStepInterpolator<T> create(Field<T> newField, boolean newForward, T[][] newYDotK, FieldODEStateAndDerivative<T> newGlobalPreviousState, FieldODEStateAndDerivative<T> newGlobalCurrentState, FieldODEStateAndDerivative<T> newSoftPreviousState, FieldODEStateAndDerivative<T> newSoftCurrentState, FieldEquationsMapper<T> newMapper) {
        return new DormandPrince853FieldStepInterpolator<>(newField, newForward, newYDotK, newGlobalPreviousState, newGlobalCurrentState, newSoftPreviousState, newSoftCurrentState, newMapper);
    }

    private T fraction(Field<T> field, double p, double q) {
        return (T) ((RealFieldElement) ((RealFieldElement) field.getZero().add(p)).divide(q));
    }

    /* JADX DEBUG: Multi-variable search result rejected for r30v0, resolved type: org.apache.commons.math3.ode.nonstiff.DormandPrince853FieldStepInterpolator<T extends org.apache.commons.math3.RealFieldElement<T>> */
    /* JADX WARN: Multi-variable type inference failed */
    /* access modifiers changed from: protected */
    @Override // org.apache.commons.math3.ode.sampling.AbstractFieldStepInterpolator
    public FieldODEStateAndDerivative<T> computeInterpolatedStateAndDerivatives(FieldEquationsMapper<T> fieldEquationsMapper, T time, T theta, T thetaH, T oneMinusThetaH) throws MaxCountExceededException {
        T[] interpolatedState;
        T[] interpolatedDerivatives;
        RealFieldElement realFieldElement = (RealFieldElement) time.getField().getOne();
        RealFieldElement realFieldElement2 = (RealFieldElement) realFieldElement.subtract(theta);
        RealFieldElement realFieldElement3 = (RealFieldElement) theta.multiply(2);
        RealFieldElement realFieldElement4 = (RealFieldElement) theta.multiply(theta);
        RealFieldElement realFieldElement5 = (RealFieldElement) realFieldElement.subtract(realFieldElement3);
        RealFieldElement realFieldElement6 = (RealFieldElement) theta.multiply(((RealFieldElement) theta.multiply(-3)).add(2.0d));
        RealFieldElement realFieldElement7 = (RealFieldElement) realFieldElement3.multiply(((RealFieldElement) theta.multiply(realFieldElement3.subtract(3.0d))).add(1.0d));
        RealFieldElement realFieldElement8 = (RealFieldElement) realFieldElement4.multiply(((RealFieldElement) theta.multiply(((RealFieldElement) theta.multiply(5)).subtract(8.0d))).add(3.0d));
        RealFieldElement realFieldElement9 = (RealFieldElement) realFieldElement4.multiply(((RealFieldElement) theta.multiply(((RealFieldElement) theta.multiply(((RealFieldElement) theta.multiply(-6)).add(15.0d))).subtract(12.0d))).add(3.0d));
        RealFieldElement realFieldElement10 = (RealFieldElement) realFieldElement4.multiply(theta.multiply(((RealFieldElement) theta.multiply(((RealFieldElement) theta.multiply(((RealFieldElement) theta.multiply(-7)).add(18.0d))).subtract(15.0d))).add(4.0d)));
        if (getGlobalPreviousState() == null || theta.getReal() > 0.5d) {
            RealFieldElement realFieldElement11 = (RealFieldElement) oneMinusThetaH.negate();
            RealFieldElement realFieldElement12 = (RealFieldElement) ((RealFieldElement) realFieldElement11.multiply(theta)).negate();
            RealFieldElement realFieldElement13 = (RealFieldElement) realFieldElement12.multiply(theta);
            RealFieldElement realFieldElement14 = (RealFieldElement) realFieldElement13.multiply(realFieldElement2);
            RealFieldElement realFieldElement15 = (RealFieldElement) realFieldElement14.multiply(theta);
            RealFieldElement realFieldElement16 = (RealFieldElement) realFieldElement15.multiply(realFieldElement2);
            RealFieldElement realFieldElement17 = (RealFieldElement) realFieldElement16.multiply(theta);
            RealFieldElement[] realFieldElementArr = (RealFieldElement[]) MathArrays.buildArray(time.getField(), 16);
            RealFieldElement[] realFieldElementArr2 = (RealFieldElement[]) MathArrays.buildArray(time.getField(), 16);
            for (int i = 0; i < realFieldElementArr.length; i++) {
                realFieldElementArr[i] = (RealFieldElement) ((RealFieldElement) ((RealFieldElement) ((RealFieldElement) ((RealFieldElement) ((RealFieldElement) ((RealFieldElement) realFieldElement11.multiply(this.f291d[0][i])).add(realFieldElement12.multiply(this.f291d[1][i]))).add(realFieldElement13.multiply(this.f291d[2][i]))).add(realFieldElement14.multiply(this.f291d[3][i]))).add(realFieldElement15.multiply(this.f291d[4][i]))).add(realFieldElement16.multiply(this.f291d[5][i]))).add(realFieldElement17.multiply(this.f291d[6][i]));
                realFieldElementArr2[i] = (RealFieldElement) ((RealFieldElement) ((RealFieldElement) ((RealFieldElement) ((RealFieldElement) ((RealFieldElement) this.f291d[0][i].add(realFieldElement5.multiply(this.f291d[1][i]))).add(realFieldElement6.multiply(this.f291d[2][i]))).add(realFieldElement7.multiply(this.f291d[3][i]))).add(realFieldElement8.multiply(this.f291d[4][i]))).add(realFieldElement9.multiply(this.f291d[5][i]))).add(realFieldElement10.multiply(this.f291d[6][i]));
            }
            interpolatedState = currentStateLinearCombination(realFieldElementArr[0], realFieldElementArr[1], realFieldElementArr[2], realFieldElementArr[3], realFieldElementArr[4], realFieldElementArr[5], realFieldElementArr[6], realFieldElementArr[7], realFieldElementArr[8], realFieldElementArr[9], realFieldElementArr[10], realFieldElementArr[11], realFieldElementArr[12], realFieldElementArr[13], realFieldElementArr[14], realFieldElementArr[15]);
            interpolatedDerivatives = derivativeLinearCombination(realFieldElementArr2[0], realFieldElementArr2[1], realFieldElementArr2[2], realFieldElementArr2[3], realFieldElementArr2[4], realFieldElementArr2[5], realFieldElementArr2[6], realFieldElementArr2[7], realFieldElementArr2[8], realFieldElementArr2[9], realFieldElementArr2[10], realFieldElementArr2[11], realFieldElementArr2[12], realFieldElementArr2[13], realFieldElementArr2[14], realFieldElementArr2[15]);
        } else {
            RealFieldElement realFieldElement18 = (RealFieldElement) thetaH.multiply(realFieldElement2);
            RealFieldElement realFieldElement19 = (RealFieldElement) realFieldElement18.multiply(theta);
            RealFieldElement realFieldElement20 = (RealFieldElement) realFieldElement19.multiply(realFieldElement2);
            RealFieldElement realFieldElement21 = (RealFieldElement) realFieldElement20.multiply(theta);
            RealFieldElement realFieldElement22 = (RealFieldElement) realFieldElement21.multiply(realFieldElement2);
            RealFieldElement realFieldElement23 = (RealFieldElement) realFieldElement22.multiply(theta);
            RealFieldElement[] realFieldElementArr3 = (RealFieldElement[]) MathArrays.buildArray(time.getField(), 16);
            RealFieldElement[] realFieldElementArr4 = (RealFieldElement[]) MathArrays.buildArray(time.getField(), 16);
            for (int i2 = 0; i2 < realFieldElementArr3.length; i2++) {
                realFieldElementArr3[i2] = (RealFieldElement) ((RealFieldElement) ((RealFieldElement) ((RealFieldElement) ((RealFieldElement) ((RealFieldElement) ((RealFieldElement) thetaH.multiply(this.f291d[0][i2])).add(realFieldElement18.multiply(this.f291d[1][i2]))).add(realFieldElement19.multiply(this.f291d[2][i2]))).add(realFieldElement20.multiply(this.f291d[3][i2]))).add(realFieldElement21.multiply(this.f291d[4][i2]))).add(realFieldElement22.multiply(this.f291d[5][i2]))).add(realFieldElement23.multiply(this.f291d[6][i2]));
                realFieldElementArr4[i2] = (RealFieldElement) ((RealFieldElement) ((RealFieldElement) ((RealFieldElement) ((RealFieldElement) ((RealFieldElement) this.f291d[0][i2].add(realFieldElement5.multiply(this.f291d[1][i2]))).add(realFieldElement6.multiply(this.f291d[2][i2]))).add(realFieldElement7.multiply(this.f291d[3][i2]))).add(realFieldElement8.multiply(this.f291d[4][i2]))).add(realFieldElement9.multiply(this.f291d[5][i2]))).add(realFieldElement10.multiply(this.f291d[6][i2]));
            }
            interpolatedState = previousStateLinearCombination(realFieldElementArr3[0], realFieldElementArr3[1], realFieldElementArr3[2], realFieldElementArr3[3], realFieldElementArr3[4], realFieldElementArr3[5], realFieldElementArr3[6], realFieldElementArr3[7], realFieldElementArr3[8], realFieldElementArr3[9], realFieldElementArr3[10], realFieldElementArr3[11], realFieldElementArr3[12], realFieldElementArr3[13], realFieldElementArr3[14], realFieldElementArr3[15]);
            interpolatedDerivatives = derivativeLinearCombination(realFieldElementArr4[0], realFieldElementArr4[1], realFieldElementArr4[2], realFieldElementArr4[3], realFieldElementArr4[4], realFieldElementArr4[5], realFieldElementArr4[6], realFieldElementArr4[7], realFieldElementArr4[8], realFieldElementArr4[9], realFieldElementArr4[10], realFieldElementArr4[11], realFieldElementArr4[12], realFieldElementArr4[13], realFieldElementArr4[14], realFieldElementArr4[15]);
        }
        return new FieldODEStateAndDerivative<>(time, interpolatedState, interpolatedDerivatives);
    }
}
