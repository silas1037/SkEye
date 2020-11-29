package com.lavadip.skeye.device;

import com.lavadip.skeye.Vector3d;
import com.lavadip.skeye.fit.Coverage;
import com.lavadip.skeye.fit.FitPoints;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public final class SensorCalibrator implements DataHandler {
    private final int calibIndex;
    private final RemoteBluetoothDevice device;
    double fitness = 0.0d;
    private double fitnessTolerance;
    boolean isFresh = false;
    private boolean isStopped;
    private int lastCheck = 0;
    private final String lowFitnessMessage;
    private FitPoints magFit;
    private final ArrayList<Vector3d> magPoints = new ArrayList<>();
    private final String name;
    private String statusStr = null;
    private int suggestedSampleWindow;

    public SensorCalibrator(String name2, String lowFitnessMessage2, RemoteBluetoothDevice device2, int calibIndex2) {
        this.name = name2;
        this.lowFitnessMessage = lowFitnessMessage2;
        this.device = device2;
        this.calibIndex = calibIndex2;
    }

    @Override // com.lavadip.skeye.device.DataHandler
    public boolean hasStopped() {
        return this.isStopped;
    }

    /* access modifiers changed from: package-private */
    public void setup(int samplingWindowMultiple) {
        this.suggestedSampleWindow = this.device.getSuggestedSampleWindow() * samplingWindowMultiple;
        this.fitnessTolerance = this.device.getFitnessTolerance();
        this.lastCheck = 0;
        this.magPoints.clear();
        this.isStopped = false;
        this.fitness = 0.0d;
        this.isFresh = false;
    }

    /* access modifiers changed from: package-private */
    public void stop() {
        this.isStopped = true;
    }

    @Override // com.lavadip.skeye.device.DataHandler
    public void handleData(Vector3d newPoint) {
        if (!this.isStopped) {
            this.magPoints.add(newPoint);
        }
        int dataSize = this.magPoints.size();
        if (dataSize > this.suggestedSampleWindow) {
            this.magPoints.remove(0);
            dataSize--;
            this.lastCheck--;
        }
        int minSize = this.suggestedSampleWindow / 6;
        if (dataSize <= minSize) {
            this.statusStr = "Need more data to calibrate " + dataSize + " / " + minSize;
        } else if (!this.isStopped && dataSize - this.lastCheck > this.suggestedSampleWindow / 20) {
            this.lastCheck = dataSize;
            FitPoints newMagFit = new FitPoints();
            newMagFit.fitEllipsoid(this.magPoints);
            if (newMagFit.radii != null) {
                List<Vector3d> correctedMagPoints = newMagFit.correctAll(this.magPoints);
                double fitness2 = findFitness(correctedMagPoints);
                double coverage = Coverage.findCoverage(correctedMagPoints);
                if (coverage < 60.0d) {
                    this.statusStr = String.format("Coverage is low (%.1f%%). Spin the device some more.", Double.valueOf(coverage));
                } else if (fitness2 < 60.0d) {
                    this.statusStr = String.format("Fitness is low (%.1f%%). " + this.lowFitnessMessage, Double.valueOf(fitness2));
                } else {
                    String quality = (coverage <= 80.0d || fitness2 <= 90.0d) ? "good." : "great!";
                    if (fitness2 > this.fitness) {
                        this.isFresh = true;
                        this.magFit = newMagFit;
                        this.statusStr = "Calibration is complete! Quality: " + quality;
                        this.fitness = fitness2;
                    }
                }
            } else {
                this.statusStr = "Couldn't fit";
            }
        }
        updateStatus(this.statusStr);
    }

    private double findFitness(List<Vector3d> correctedMagPoints) {
        float countBad = 0.0f;
        for (Vector3d p : correctedMagPoints) {
            double deviation = Math.abs(p.length() - 1.0d);
            if (deviation > 2.0d * this.fitnessTolerance) {
                countBad += 1.0f;
            } else if (deviation > this.fitnessTolerance) {
                countBad = (float) (((double) countBad) + 0.5d);
            }
        }
        int total = correctedMagPoints.size();
        return (((double) (((float) total) - countBad)) * 100.0d) / ((double) total);
    }

    private void updateStatus(String msg) {
        this.device.setStatus(this.calibIndex, String.valueOf(this.name) + ": " + msg);
    }

    public Vector3d correct(Vector3d magPoint) {
        Vector3d cp = this.magFit.correct(magPoint);
        cp.scalarMultiplyInPlace(((this.magFit.radii.getEntry(0) + this.magFit.radii.getEntry(1)) + this.magFit.radii.getEntry(2)) / 3.0d);
        return cp;
    }

    public String serializeToString() {
        return System.currentTimeMillis() + "," + this.fitness + "," + this.magFit.serializeToString();
    }

    /* access modifiers changed from: package-private */
    public boolean initFromString(String str, long ageTolerance) {
        if (str == null) {
            return false;
        }
        StringTokenizer tokenizer = new StringTokenizer(str, ",");
        if (System.currentTimeMillis() - Long.parseLong(tokenizer.nextToken()) > ageTolerance) {
            return false;
        }
        this.fitness = Double.parseDouble(tokenizer.nextToken());
        this.magFit = new FitPoints();
        this.magFit.initFromString(tokenizer);
        this.isFresh = false;
        updateStatus(String.format("Loaded earlier calibration. Fitness: %.1f%%", Double.valueOf(this.fitness)));
        return true;
    }
}
