package org.apache.commons.math3.p000ml.neuralnet.twod.util;

import java.util.HashMap;
import java.util.Map;
import org.apache.commons.math3.exception.MathIllegalStateException;
import org.apache.commons.math3.p000ml.neuralnet.Neuron;
import org.apache.commons.math3.p000ml.neuralnet.twod.NeuronSquareMesh2D;

/* renamed from: org.apache.commons.math3.ml.neuralnet.twod.util.LocationFinder */
public class LocationFinder {
    private final Map<Long, Location> locations = new HashMap();

    /* renamed from: org.apache.commons.math3.ml.neuralnet.twod.util.LocationFinder$Location */
    public static class Location {
        private final int column;
        private final int row;

        public Location(int row2, int column2) {
            this.row = row2;
            this.column = column2;
        }

        public int getRow() {
            return this.row;
        }

        public int getColumn() {
            return this.column;
        }
    }

    public LocationFinder(NeuronSquareMesh2D map) {
        int nR = map.getNumberOfRows();
        int nC = map.getNumberOfColumns();
        for (int r = 0; r < nR; r++) {
            for (int c = 0; c < nC; c++) {
                Long id = Long.valueOf(map.getNeuron(r, c).getIdentifier());
                if (this.locations.get(id) != null) {
                    throw new MathIllegalStateException();
                }
                this.locations.put(id, new Location(r, c));
            }
        }
    }

    public Location getLocation(Neuron n) {
        return this.locations.get(Long.valueOf(n.getIdentifier()));
    }
}
