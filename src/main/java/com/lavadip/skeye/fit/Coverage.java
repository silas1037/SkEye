package com.lavadip.skeye.fit;

import com.lavadip.skeye.Vector3d;
import java.util.List;

public class Coverage {
    private static final Vector3d[] coveragePoints = {new Vector3d(f92t, 0.0d, 1.0d), new Vector3d(-f92t, 0.0d, 1.0d), new Vector3d(f92t, 0.0d, -1.0d), new Vector3d(-f92t, 0.0d, -1.0d), new Vector3d(0.0d, 1.0d, f92t), new Vector3d(0.0d, -1.0d, f92t), new Vector3d(0.0d, 1.0d, -f92t), new Vector3d(0.0d, -1.0d, -f92t), new Vector3d(1.0d, f92t, 0.0d), new Vector3d(-1.0d, f92t, 0.0d), new Vector3d(1.0d, -f92t, 0.0d), new Vector3d(-1.0d, -f92t, 0.0d)};

    /* renamed from: t */
    private static final double f92t = ((-1.0d + Math.sqrt(5.0d)) / 2.0d);

    public static double findCoverage(List<Vector3d> correctedMagPoints) {
        int numPoints = coveragePoints.length;
        int[] countHitsNear = new int[numPoints];
        int[] countHitsFar = new int[numPoints];
        for (Vector3d p : coveragePoints) {
            p.normalise();
        }
        for (Vector3d p2 : correctedMagPoints) {
            Vector3d normP = p2.normalised();
            for (int i = 0; i < numPoints; i++) {
                double angle = normP.angleBetweenMag(coveragePoints[i]);
                if (angle < 0.39269908169872414d) {
                    countHitsNear[i] = countHitsNear[i] + 1;
                } else if (angle < 0.6283185307179586d) {
                    countHitsFar[i] = countHitsFar[i] + 1;
                }
            }
        }
        int totalHits = 0;
        for (int c : countHitsNear) {
            totalHits += Math.min(6, c);
        }
        for (int c2 : countHitsFar) {
            totalHits += Math.min(6, c2);
        }
        return (((double) totalHits) * 100.0d) / ((double) (numPoints * 12));
    }
}
