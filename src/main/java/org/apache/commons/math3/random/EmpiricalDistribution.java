package org.apache.commons.math3.random;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.math3.distribution.AbstractRealDistribution;
import org.apache.commons.math3.distribution.ConstantRealDistribution;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.distribution.RealDistribution;
import org.apache.commons.math3.exception.MathIllegalStateException;
import org.apache.commons.math3.exception.MathInternalError;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.exception.ZeroException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.stat.descriptive.StatisticalSummary;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.MathUtils;

public class EmpiricalDistribution extends AbstractRealDistribution {
    public static final int DEFAULT_BIN_COUNT = 1000;
    private static final String FILE_CHARSET = "US-ASCII";
    private static final long serialVersionUID = 5729073523949762654L;
    private final int binCount;
    private final List<SummaryStatistics> binStats;
    private double delta;
    private boolean loaded;
    private double max;
    private double min;
    protected final RandomDataGenerator randomData;
    private SummaryStatistics sampleStats;
    private double[] upperBounds;

    public EmpiricalDistribution() {
        this((int) DEFAULT_BIN_COUNT);
    }

    public EmpiricalDistribution(int binCount2) {
        this(binCount2, new RandomDataGenerator());
    }

    public EmpiricalDistribution(int binCount2, RandomGenerator generator) {
        this(binCount2, new RandomDataGenerator(generator));
    }

    public EmpiricalDistribution(RandomGenerator generator) {
        this((int) DEFAULT_BIN_COUNT, generator);
    }

    @Deprecated
    public EmpiricalDistribution(int binCount2, RandomDataImpl randomData2) {
        this(binCount2, randomData2.getDelegate());
    }

    @Deprecated
    public EmpiricalDistribution(RandomDataImpl randomData2) {
        this((int) DEFAULT_BIN_COUNT, randomData2);
    }

    private EmpiricalDistribution(int binCount2, RandomDataGenerator randomData2) {
        super(randomData2.getRandomGenerator());
        this.sampleStats = null;
        this.max = Double.NEGATIVE_INFINITY;
        this.min = Double.POSITIVE_INFINITY;
        this.delta = 0.0d;
        this.loaded = false;
        this.upperBounds = null;
        if (binCount2 <= 0) {
            throw new NotStrictlyPositiveException(Integer.valueOf(binCount2));
        }
        this.binCount = binCount2;
        this.randomData = randomData2;
        this.binStats = new ArrayList();
    }

    public void load(double[] in) throws NullArgumentException {
        try {
            new ArrayDataAdapter(in).computeStats();
            fillBinStats(new ArrayDataAdapter(in));
            this.loaded = true;
        } catch (IOException e) {
            throw new MathInternalError();
        }
    }

    public void load(URL url) throws IOException, NullArgumentException, ZeroException {
        Throwable th;
        MathUtils.checkNotNull(url);
        Charset charset = Charset.forName(FILE_CHARSET);
        BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream(), charset));
        try {
            new StreamDataAdapter(in).computeStats();
            if (this.sampleStats.getN() == 0) {
                throw new ZeroException(LocalizedFormats.URL_CONTAINS_NO_DATA, url);
            }
            BufferedReader in2 = new BufferedReader(new InputStreamReader(url.openStream(), charset));
            try {
                fillBinStats(new StreamDataAdapter(in2));
                this.loaded = true;
                try {
                    in2.close();
                } catch (IOException e) {
                }
            } catch (Throwable th2) {
                th = th2;
                in = in2;
                try {
                    in.close();
                } catch (IOException e2) {
                }
                throw th;
            }
        } catch (Throwable th3) {
            th = th3;
            in.close();
            throw th;
        }
    }

    public void load(File file) throws IOException, NullArgumentException {
        Throwable th;
        MathUtils.checkNotNull(file);
        Charset charset = Charset.forName(FILE_CHARSET);
        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(file), charset));
        try {
            new StreamDataAdapter(in).computeStats();
            try {
                BufferedReader in2 = new BufferedReader(new InputStreamReader(new FileInputStream(file), charset));
                try {
                    fillBinStats(new StreamDataAdapter(in2));
                    this.loaded = true;
                    try {
                        in2.close();
                    } catch (IOException e) {
                    }
                } catch (Throwable th2) {
                    th = th2;
                    in = in2;
                    try {
                        in.close();
                    } catch (IOException e2) {
                    }
                    throw th;
                }
            } catch (Throwable th3) {
                th = th3;
                in.close();
                throw th;
            }
        } catch (Throwable th4) {
            th = th4;
            in.close();
            throw th;
        }
    }

    /* access modifiers changed from: private */
    public abstract class DataAdapter {
        public abstract void computeBinStats() throws IOException;

        public abstract void computeStats() throws IOException;

        private DataAdapter() {
        }
    }

    private class StreamDataAdapter extends DataAdapter {
        private BufferedReader inputStream;

        StreamDataAdapter(BufferedReader in) {
            super();
            this.inputStream = in;
        }

        @Override // org.apache.commons.math3.random.EmpiricalDistribution.DataAdapter
        public void computeBinStats() throws IOException {
            while (true) {
                String str = this.inputStream.readLine();
                if (str != null) {
                    double val = Double.parseDouble(str);
                    ((SummaryStatistics) EmpiricalDistribution.this.binStats.get(EmpiricalDistribution.this.findBin(val))).addValue(val);
                } else {
                    this.inputStream.close();
                    this.inputStream = null;
                    return;
                }
            }
        }

        @Override // org.apache.commons.math3.random.EmpiricalDistribution.DataAdapter
        public void computeStats() throws IOException {
            EmpiricalDistribution.this.sampleStats = new SummaryStatistics();
            while (true) {
                String str = this.inputStream.readLine();
                if (str != null) {
                    EmpiricalDistribution.this.sampleStats.addValue(Double.parseDouble(str));
                } else {
                    this.inputStream.close();
                    this.inputStream = null;
                    return;
                }
            }
        }
    }

    private class ArrayDataAdapter extends DataAdapter {
        private double[] inputArray;

        ArrayDataAdapter(double[] in) throws NullArgumentException {
            super();
            MathUtils.checkNotNull(in);
            this.inputArray = in;
        }

        @Override // org.apache.commons.math3.random.EmpiricalDistribution.DataAdapter
        public void computeStats() throws IOException {
            EmpiricalDistribution.this.sampleStats = new SummaryStatistics();
            for (int i = 0; i < this.inputArray.length; i++) {
                EmpiricalDistribution.this.sampleStats.addValue(this.inputArray[i]);
            }
        }

        @Override // org.apache.commons.math3.random.EmpiricalDistribution.DataAdapter
        public void computeBinStats() throws IOException {
            for (int i = 0; i < this.inputArray.length; i++) {
                ((SummaryStatistics) EmpiricalDistribution.this.binStats.get(EmpiricalDistribution.this.findBin(this.inputArray[i]))).addValue(this.inputArray[i]);
            }
        }
    }

    private void fillBinStats(DataAdapter da) throws IOException {
        this.min = this.sampleStats.getMin();
        this.max = this.sampleStats.getMax();
        this.delta = (this.max - this.min) / ((double) this.binCount);
        if (!this.binStats.isEmpty()) {
            this.binStats.clear();
        }
        for (int i = 0; i < this.binCount; i++) {
            this.binStats.add(i, new SummaryStatistics());
        }
        da.computeBinStats();
        this.upperBounds = new double[this.binCount];
        this.upperBounds[0] = ((double) this.binStats.get(0).getN()) / ((double) this.sampleStats.getN());
        for (int i2 = 1; i2 < this.binCount - 1; i2++) {
            this.upperBounds[i2] = this.upperBounds[i2 - 1] + (((double) this.binStats.get(i2).getN()) / ((double) this.sampleStats.getN()));
        }
        this.upperBounds[this.binCount - 1] = 1.0d;
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private int findBin(double value) {
        return FastMath.min(FastMath.max(((int) FastMath.ceil((value - this.min) / this.delta)) - 1, 0), this.binCount - 1);
    }

    public double getNextValue() throws MathIllegalStateException {
        if (this.loaded) {
            return sample();
        }
        throw new MathIllegalStateException(LocalizedFormats.DISTRIBUTION_NOT_LOADED, new Object[0]);
    }

    public StatisticalSummary getSampleStats() {
        return this.sampleStats;
    }

    public int getBinCount() {
        return this.binCount;
    }

    public List<SummaryStatistics> getBinStats() {
        return this.binStats;
    }

    public double[] getUpperBounds() {
        double[] binUpperBounds = new double[this.binCount];
        for (int i = 0; i < this.binCount - 1; i++) {
            binUpperBounds[i] = this.min + (this.delta * ((double) (i + 1)));
        }
        binUpperBounds[this.binCount - 1] = this.max;
        return binUpperBounds;
    }

    public double[] getGeneratorUpperBounds() {
        int len = this.upperBounds.length;
        double[] out = new double[len];
        System.arraycopy(this.upperBounds, 0, out, 0, len);
        return out;
    }

    public boolean isLoaded() {
        return this.loaded;
    }

    public void reSeed(long seed) {
        this.randomData.reSeed(seed);
    }

    @Override // org.apache.commons.math3.distribution.AbstractRealDistribution, org.apache.commons.math3.distribution.RealDistribution
    public double probability(double x) {
        return 0.0d;
    }

    @Override // org.apache.commons.math3.distribution.RealDistribution
    public double density(double x) {
        if (x < this.min || x > this.max) {
            return 0.0d;
        }
        int binIndex = findBin(x);
        return (getKernel(this.binStats.get(binIndex)).density(x) * m9pB(binIndex)) / m8kB(binIndex);
    }

    @Override // org.apache.commons.math3.distribution.RealDistribution
    public double cumulativeProbability(double x) {
        if (x < this.min) {
            return 0.0d;
        }
        if (x >= this.max) {
            return 1.0d;
        }
        int binIndex = findBin(x);
        double pBminus = pBminus(binIndex);
        double pB = m9pB(binIndex);
        RealDistribution kernel = m7k(x);
        if (!(kernel instanceof ConstantRealDistribution)) {
            return pBminus + (pB * ((kernel.cumulativeProbability(x) - kernel.cumulativeProbability(binIndex == 0 ? this.min : getUpperBounds()[binIndex - 1])) / m8kB(binIndex)));
        } else if (x >= kernel.getNumericalMean()) {
            return pBminus + pB;
        } else {
            return pBminus;
        }
    }

    @Override // org.apache.commons.math3.distribution.AbstractRealDistribution, org.apache.commons.math3.distribution.RealDistribution
    public double inverseCumulativeProbability(double p) throws OutOfRangeException {
        if (p < 0.0d || p > 1.0d) {
            throw new OutOfRangeException(Double.valueOf(p), 0, 1);
        } else if (p == 0.0d) {
            return getSupportLowerBound();
        } else {
            if (p == 1.0d) {
                return getSupportUpperBound();
            }
            int i = 0;
            while (cumBinP(i) < p) {
                i++;
            }
            RealDistribution kernel = getKernel(this.binStats.get(i));
            double kB = m8kB(i);
            double lower = i == 0 ? this.min : getUpperBounds()[i - 1];
            double kBminus = kernel.cumulativeProbability(lower);
            double pB = m9pB(i);
            double pCrit = p - pBminus(i);
            if (pCrit > 0.0d) {
                return kernel.inverseCumulativeProbability(((pCrit * kB) / pB) + kBminus);
            }
            return lower;
        }
    }

    @Override // org.apache.commons.math3.distribution.RealDistribution
    public double getNumericalMean() {
        return this.sampleStats.getMean();
    }

    @Override // org.apache.commons.math3.distribution.RealDistribution
    public double getNumericalVariance() {
        return this.sampleStats.getVariance();
    }

    @Override // org.apache.commons.math3.distribution.RealDistribution
    public double getSupportLowerBound() {
        return this.min;
    }

    @Override // org.apache.commons.math3.distribution.RealDistribution
    public double getSupportUpperBound() {
        return this.max;
    }

    @Override // org.apache.commons.math3.distribution.RealDistribution
    public boolean isSupportLowerBoundInclusive() {
        return true;
    }

    @Override // org.apache.commons.math3.distribution.RealDistribution
    public boolean isSupportUpperBoundInclusive() {
        return true;
    }

    @Override // org.apache.commons.math3.distribution.RealDistribution
    public boolean isSupportConnected() {
        return true;
    }

    @Override // org.apache.commons.math3.distribution.AbstractRealDistribution, org.apache.commons.math3.distribution.RealDistribution
    public void reseedRandomGenerator(long seed) {
        this.randomData.reSeed(seed);
    }

    /* renamed from: pB */
    private double m9pB(int i) {
        return i == 0 ? this.upperBounds[0] : this.upperBounds[i] - this.upperBounds[i - 1];
    }

    private double pBminus(int i) {
        if (i == 0) {
            return 0.0d;
        }
        return this.upperBounds[i - 1];
    }

    /* renamed from: kB */
    private double m8kB(int i) {
        double[] binBounds = getUpperBounds();
        RealDistribution kernel = getKernel(this.binStats.get(i));
        return i == 0 ? kernel.cumulativeProbability(this.min, binBounds[0]) : kernel.cumulativeProbability(binBounds[i - 1], binBounds[i]);
    }

    /* renamed from: k */
    private RealDistribution m7k(double x) {
        return getKernel(this.binStats.get(findBin(x)));
    }

    private double cumBinP(int binIndex) {
        return this.upperBounds[binIndex];
    }

    /* access modifiers changed from: protected */
    public RealDistribution getKernel(SummaryStatistics bStats) {
        if (bStats.getN() == 1 || bStats.getVariance() == 0.0d) {
            return new ConstantRealDistribution(bStats.getMean());
        }
        return new NormalDistribution(this.randomData.getRandomGenerator(), bStats.getMean(), bStats.getStandardDeviation(), 1.0E-9d);
    }
}
