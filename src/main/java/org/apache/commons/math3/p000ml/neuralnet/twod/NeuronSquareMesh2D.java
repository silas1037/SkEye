package org.apache.commons.math3.p000ml.neuralnet.twod;

import java.io.ObjectInputStream;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.math3.exception.MathInternalError;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.p000ml.neuralnet.FeatureInitializer;
import org.apache.commons.math3.p000ml.neuralnet.Network;
import org.apache.commons.math3.p000ml.neuralnet.Neuron;
import org.apache.commons.math3.p000ml.neuralnet.SquareNeighbourhood;

/* renamed from: org.apache.commons.math3.ml.neuralnet.twod.NeuronSquareMesh2D */
public class NeuronSquareMesh2D implements Iterable<Neuron>, Serializable {
    private static final long serialVersionUID = 1;
    private final long[][] identifiers;
    private final SquareNeighbourhood neighbourhood;
    private final Network network;
    private final int numberOfColumns;
    private final int numberOfRows;
    private final boolean wrapColumns;
    private final boolean wrapRows;

    /* renamed from: org.apache.commons.math3.ml.neuralnet.twod.NeuronSquareMesh2D$HorizontalDirection */
    public enum HorizontalDirection {
        RIGHT,
        CENTER,
        LEFT
    }

    /* renamed from: org.apache.commons.math3.ml.neuralnet.twod.NeuronSquareMesh2D$VerticalDirection */
    public enum VerticalDirection {
        UP,
        CENTER,
        DOWN
    }

    NeuronSquareMesh2D(boolean wrapRowDim, boolean wrapColDim, SquareNeighbourhood neighbourhoodType, double[][][] featuresList) {
        this.numberOfRows = featuresList.length;
        this.numberOfColumns = featuresList[0].length;
        if (this.numberOfRows < 2) {
            throw new NumberIsTooSmallException(Integer.valueOf(this.numberOfRows), 2, true);
        } else if (this.numberOfColumns < 2) {
            throw new NumberIsTooSmallException(Integer.valueOf(this.numberOfColumns), 2, true);
        } else {
            this.wrapRows = wrapRowDim;
            this.wrapColumns = wrapColDim;
            this.neighbourhood = neighbourhoodType;
            this.network = new Network(0, featuresList[0][0].length);
            this.identifiers = (long[][]) Array.newInstance(Long.TYPE, this.numberOfRows, this.numberOfColumns);
            for (int i = 0; i < this.numberOfRows; i++) {
                for (int j = 0; j < this.numberOfColumns; j++) {
                    this.identifiers[i][j] = this.network.createNeuron(featuresList[i][j]);
                }
            }
            createLinks();
        }
    }

    public NeuronSquareMesh2D(int numRows, boolean wrapRowDim, int numCols, boolean wrapColDim, SquareNeighbourhood neighbourhoodType, FeatureInitializer[] featureInit) {
        if (numRows < 2) {
            throw new NumberIsTooSmallException(Integer.valueOf(numRows), 2, true);
        } else if (numCols < 2) {
            throw new NumberIsTooSmallException(Integer.valueOf(numCols), 2, true);
        } else {
            this.numberOfRows = numRows;
            this.wrapRows = wrapRowDim;
            this.numberOfColumns = numCols;
            this.wrapColumns = wrapColDim;
            this.neighbourhood = neighbourhoodType;
            this.identifiers = (long[][]) Array.newInstance(Long.TYPE, this.numberOfRows, this.numberOfColumns);
            int fLen = featureInit.length;
            this.network = new Network(0, fLen);
            for (int i = 0; i < numRows; i++) {
                for (int j = 0; j < numCols; j++) {
                    double[] features = new double[fLen];
                    for (int fIndex = 0; fIndex < fLen; fIndex++) {
                        features[fIndex] = featureInit[fIndex].value();
                    }
                    this.identifiers[i][j] = this.network.createNeuron(features);
                }
            }
            createLinks();
        }
    }

    private NeuronSquareMesh2D(boolean wrapRowDim, boolean wrapColDim, SquareNeighbourhood neighbourhoodType, Network net, long[][] idGrid) {
        this.numberOfRows = idGrid.length;
        this.numberOfColumns = idGrid[0].length;
        this.wrapRows = wrapRowDim;
        this.wrapColumns = wrapColDim;
        this.neighbourhood = neighbourhoodType;
        this.network = net;
        this.identifiers = idGrid;
    }

    public synchronized NeuronSquareMesh2D copy() {
        long[][] idGrid;
        idGrid = (long[][]) Array.newInstance(Long.TYPE, this.numberOfRows, this.numberOfColumns);
        for (int r = 0; r < this.numberOfRows; r++) {
            for (int c = 0; c < this.numberOfColumns; c++) {
                idGrid[r][c] = this.identifiers[r][c];
            }
        }
        return new NeuronSquareMesh2D(this.wrapRows, this.wrapColumns, this.neighbourhood, this.network.copy(), idGrid);
    }

    @Override // java.lang.Iterable
    public Iterator<Neuron> iterator() {
        return this.network.iterator();
    }

    public Network getNetwork() {
        return this.network;
    }

    public int getNumberOfRows() {
        return this.numberOfRows;
    }

    public int getNumberOfColumns() {
        return this.numberOfColumns;
    }

    public Neuron getNeuron(int i, int j) {
        if (i < 0 || i >= this.numberOfRows) {
            throw new OutOfRangeException(Integer.valueOf(i), 0, Integer.valueOf(this.numberOfRows - 1));
        } else if (j >= 0 && j < this.numberOfColumns) {
            return this.network.getNeuron(this.identifiers[i][j]);
        } else {
            throw new OutOfRangeException(Integer.valueOf(j), 0, Integer.valueOf(this.numberOfColumns - 1));
        }
    }

    public Neuron getNeuron(int row, int col, HorizontalDirection alongRowDir, VerticalDirection alongColDir) {
        int[] location = getLocation(row, col, alongRowDir, alongColDir);
        if (location == null) {
            return null;
        }
        return getNeuron(location[0], location[1]);
    }

    private int[] getLocation(int row, int col, HorizontalDirection alongRowDir, VerticalDirection alongColDir) {
        int colOffset;
        int rowOffset;
        switch (alongRowDir) {
            case LEFT:
                colOffset = -1;
                break;
            case RIGHT:
                colOffset = 1;
                break;
            case CENTER:
                colOffset = 0;
                break;
            default:
                throw new MathInternalError();
        }
        int colIndex = col + colOffset;
        if (this.wrapColumns) {
            if (colIndex < 0) {
                colIndex += this.numberOfColumns;
            } else {
                colIndex %= this.numberOfColumns;
            }
        }
        switch (alongColDir) {
            case UP:
                rowOffset = -1;
                break;
            case DOWN:
                rowOffset = 1;
                break;
            case CENTER:
                rowOffset = 0;
                break;
            default:
                throw new MathInternalError();
        }
        int rowIndex = row + rowOffset;
        if (this.wrapRows) {
            if (rowIndex < 0) {
                rowIndex += this.numberOfRows;
            } else {
                rowIndex %= this.numberOfRows;
            }
        }
        if (rowIndex < 0 || rowIndex >= this.numberOfRows || colIndex < 0 || colIndex >= this.numberOfColumns) {
            return null;
        }
        return new int[]{rowIndex, colIndex};
    }

    /* JADX INFO: Can't fix incorrect switch cases order, some code will duplicate */
    private void createLinks() {
        List<Long> linkEnd = new ArrayList<>();
        int iLast = this.numberOfRows - 1;
        int jLast = this.numberOfColumns - 1;
        for (int i = 0; i < this.numberOfRows; i++) {
            for (int j = 0; j < this.numberOfColumns; j++) {
                linkEnd.clear();
                switch (this.neighbourhood) {
                    case MOORE:
                        if (i > 0) {
                            if (j > 0) {
                                linkEnd.add(Long.valueOf(this.identifiers[i - 1][j - 1]));
                            }
                            if (j < jLast) {
                                linkEnd.add(Long.valueOf(this.identifiers[i - 1][j + 1]));
                            }
                        }
                        if (i < iLast) {
                            if (j > 0) {
                                linkEnd.add(Long.valueOf(this.identifiers[i + 1][j - 1]));
                            }
                            if (j < jLast) {
                                linkEnd.add(Long.valueOf(this.identifiers[i + 1][j + 1]));
                            }
                        }
                        if (this.wrapRows) {
                            if (i == 0) {
                                if (j > 0) {
                                    linkEnd.add(Long.valueOf(this.identifiers[iLast][j - 1]));
                                }
                                if (j < jLast) {
                                    linkEnd.add(Long.valueOf(this.identifiers[iLast][j + 1]));
                                }
                            } else if (i == iLast) {
                                if (j > 0) {
                                    linkEnd.add(Long.valueOf(this.identifiers[0][j - 1]));
                                }
                                if (j < jLast) {
                                    linkEnd.add(Long.valueOf(this.identifiers[0][j + 1]));
                                }
                            }
                        }
                        if (this.wrapColumns) {
                            if (j == 0) {
                                if (i > 0) {
                                    linkEnd.add(Long.valueOf(this.identifiers[i - 1][jLast]));
                                }
                                if (i < iLast) {
                                    linkEnd.add(Long.valueOf(this.identifiers[i + 1][jLast]));
                                }
                            } else if (j == jLast) {
                                if (i > 0) {
                                    linkEnd.add(Long.valueOf(this.identifiers[i - 1][0]));
                                }
                                if (i < iLast) {
                                    linkEnd.add(Long.valueOf(this.identifiers[i + 1][0]));
                                }
                            }
                        }
                        if (this.wrapRows && this.wrapColumns) {
                            if (i != 0 || j != 0) {
                                if (i != 0 || j != jLast) {
                                    if (i != iLast || j != 0) {
                                        if (i == iLast && j == jLast) {
                                            linkEnd.add(Long.valueOf(this.identifiers[0][0]));
                                            break;
                                        }
                                    } else {
                                        linkEnd.add(Long.valueOf(this.identifiers[0][jLast]));
                                        break;
                                    }
                                } else {
                                    linkEnd.add(Long.valueOf(this.identifiers[iLast][0]));
                                    break;
                                }
                            } else {
                                linkEnd.add(Long.valueOf(this.identifiers[iLast][jLast]));
                                break;
                            }
                        }
                    case VON_NEUMANN:
                        break;
                    default:
                        throw new MathInternalError();
                }
                if (i > 0) {
                    linkEnd.add(Long.valueOf(this.identifiers[i - 1][j]));
                }
                if (i < iLast) {
                    linkEnd.add(Long.valueOf(this.identifiers[i + 1][j]));
                }
                if (this.wrapRows) {
                    if (i == 0) {
                        linkEnd.add(Long.valueOf(this.identifiers[iLast][j]));
                    } else if (i == iLast) {
                        linkEnd.add(Long.valueOf(this.identifiers[0][j]));
                    }
                }
                if (j > 0) {
                    linkEnd.add(Long.valueOf(this.identifiers[i][j - 1]));
                }
                if (j < jLast) {
                    linkEnd.add(Long.valueOf(this.identifiers[i][j + 1]));
                }
                if (this.wrapColumns) {
                    if (j == 0) {
                        linkEnd.add(Long.valueOf(this.identifiers[i][jLast]));
                    } else if (j == jLast) {
                        linkEnd.add(Long.valueOf(this.identifiers[i][0]));
                    }
                }
                Neuron aNeuron = this.network.getNeuron(this.identifiers[i][j]);
                for (Long l : linkEnd) {
                    this.network.addLink(aNeuron, this.network.getNeuron(l.longValue()));
                }
            }
        }
    }

    private void readObject(ObjectInputStream in) {
        throw new IllegalStateException();
    }

    private Object writeReplace() {
        double[][][] featuresList = (double[][][]) Array.newInstance(double[].class, this.numberOfRows, this.numberOfColumns);
        for (int i = 0; i < this.numberOfRows; i++) {
            for (int j = 0; j < this.numberOfColumns; j++) {
                featuresList[i][j] = getNeuron(i, j).getFeatures();
            }
        }
        return new SerializationProxy(this.wrapRows, this.wrapColumns, this.neighbourhood, featuresList);
    }

    /* renamed from: org.apache.commons.math3.ml.neuralnet.twod.NeuronSquareMesh2D$SerializationProxy */
    private static class SerializationProxy implements Serializable {
        private static final long serialVersionUID = 20130226;
        private final double[][][] featuresList;
        private final SquareNeighbourhood neighbourhood;
        private final boolean wrapColumns;
        private final boolean wrapRows;

        SerializationProxy(boolean wrapRows2, boolean wrapColumns2, SquareNeighbourhood neighbourhood2, double[][][] featuresList2) {
            this.wrapRows = wrapRows2;
            this.wrapColumns = wrapColumns2;
            this.neighbourhood = neighbourhood2;
            this.featuresList = featuresList2;
        }

        private Object readResolve() {
            return new NeuronSquareMesh2D(this.wrapRows, this.wrapColumns, this.neighbourhood, this.featuresList);
        }
    }
}
