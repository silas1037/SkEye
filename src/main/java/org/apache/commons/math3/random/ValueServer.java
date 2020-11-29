package org.apache.commons.math3.random;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.MathIllegalStateException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.exception.ZeroException;
import org.apache.commons.math3.exception.util.LocalizedFormats;

public class ValueServer {
    public static final int CONSTANT_MODE = 5;
    public static final int DIGEST_MODE = 0;
    public static final int EXPONENTIAL_MODE = 3;
    public static final int GAUSSIAN_MODE = 4;
    public static final int REPLAY_MODE = 1;
    public static final int UNIFORM_MODE = 2;
    private EmpiricalDistribution empiricalDistribution;
    private BufferedReader filePointer;
    private int mode;

    /* renamed from: mu */
    private double f355mu;
    private final RandomDataGenerator randomData;
    private double sigma;
    private URL valuesFileURL;

    public ValueServer() {
        this.mode = 5;
        this.valuesFileURL = null;
        this.f355mu = 0.0d;
        this.sigma = 0.0d;
        this.empiricalDistribution = null;
        this.filePointer = null;
        this.randomData = new RandomDataGenerator();
    }

    @Deprecated
    public ValueServer(RandomDataImpl randomData2) {
        this.mode = 5;
        this.valuesFileURL = null;
        this.f355mu = 0.0d;
        this.sigma = 0.0d;
        this.empiricalDistribution = null;
        this.filePointer = null;
        this.randomData = randomData2.getDelegate();
    }

    public ValueServer(RandomGenerator generator) {
        this.mode = 5;
        this.valuesFileURL = null;
        this.f355mu = 0.0d;
        this.sigma = 0.0d;
        this.empiricalDistribution = null;
        this.filePointer = null;
        this.randomData = new RandomDataGenerator(generator);
    }

    public double getNext() throws IOException, MathIllegalStateException, MathIllegalArgumentException {
        switch (this.mode) {
            case 0:
                return getNextDigest();
            case 1:
                return getNextReplay();
            case 2:
                return getNextUniform();
            case 3:
                return getNextExponential();
            case 4:
                return getNextGaussian();
            case 5:
                return this.f355mu;
            default:
                throw new MathIllegalStateException(LocalizedFormats.UNKNOWN_MODE, Integer.valueOf(this.mode), "DIGEST_MODE", 0, "REPLAY_MODE", 1, "UNIFORM_MODE", 2, "EXPONENTIAL_MODE", 3, "GAUSSIAN_MODE", 4, "CONSTANT_MODE", 5);
        }
    }

    public void fill(double[] values) throws IOException, MathIllegalStateException, MathIllegalArgumentException {
        for (int i = 0; i < values.length; i++) {
            values[i] = getNext();
        }
    }

    public double[] fill(int length) throws IOException, MathIllegalStateException, MathIllegalArgumentException {
        double[] out = new double[length];
        for (int i = 0; i < length; i++) {
            out[i] = getNext();
        }
        return out;
    }

    public void computeDistribution() throws IOException, ZeroException, NullArgumentException {
        computeDistribution(EmpiricalDistribution.DEFAULT_BIN_COUNT);
    }

    public void computeDistribution(int binCount) throws NullArgumentException, IOException, ZeroException {
        this.empiricalDistribution = new EmpiricalDistribution(binCount, this.randomData.getRandomGenerator());
        this.empiricalDistribution.load(this.valuesFileURL);
        this.f355mu = this.empiricalDistribution.getSampleStats().getMean();
        this.sigma = this.empiricalDistribution.getSampleStats().getStandardDeviation();
    }

    public int getMode() {
        return this.mode;
    }

    public void setMode(int mode2) {
        this.mode = mode2;
    }

    public URL getValuesFileURL() {
        return this.valuesFileURL;
    }

    public void setValuesFileURL(String url) throws MalformedURLException {
        this.valuesFileURL = new URL(url);
    }

    public void setValuesFileURL(URL url) {
        this.valuesFileURL = url;
    }

    public EmpiricalDistribution getEmpiricalDistribution() {
        return this.empiricalDistribution;
    }

    public void resetReplayFile() throws IOException {
        if (this.filePointer != null) {
            try {
                this.filePointer.close();
                this.filePointer = null;
            } catch (IOException e) {
            }
        }
        this.filePointer = new BufferedReader(new InputStreamReader(this.valuesFileURL.openStream(), "UTF-8"));
    }

    public void closeReplayFile() throws IOException {
        if (this.filePointer != null) {
            this.filePointer.close();
            this.filePointer = null;
        }
    }

    public double getMu() {
        return this.f355mu;
    }

    public void setMu(double mu) {
        this.f355mu = mu;
    }

    public double getSigma() {
        return this.sigma;
    }

    public void setSigma(double sigma2) {
        this.sigma = sigma2;
    }

    public void reSeed(long seed) {
        this.randomData.reSeed(seed);
    }

    private double getNextDigest() throws MathIllegalStateException {
        if (this.empiricalDistribution != null && this.empiricalDistribution.getBinStats().size() != 0) {
            return this.empiricalDistribution.getNextValue();
        }
        throw new MathIllegalStateException(LocalizedFormats.DIGEST_NOT_INITIALIZED, new Object[0]);
    }

    private double getNextReplay() throws IOException, MathIllegalStateException {
        if (this.filePointer == null) {
            resetReplayFile();
        }
        String str = this.filePointer.readLine();
        if (str == null) {
            closeReplayFile();
            resetReplayFile();
            str = this.filePointer.readLine();
            if (str == null) {
                throw new MathIllegalStateException(LocalizedFormats.URL_CONTAINS_NO_DATA, this.valuesFileURL);
            }
        }
        return Double.parseDouble(str);
    }

    private double getNextUniform() throws MathIllegalArgumentException {
        return this.randomData.nextUniform(0.0d, 2.0d * this.f355mu);
    }

    private double getNextExponential() throws MathIllegalArgumentException {
        return this.randomData.nextExponential(this.f355mu);
    }

    private double getNextGaussian() throws MathIllegalArgumentException {
        return this.randomData.nextGaussian(this.f355mu, this.sigma);
    }
}
