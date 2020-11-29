package com.lavadip.skeye.audio;

import android.media.AudioTrack;

public class SoundWave {
    private static final int sampleRatePerMilli = 4;

    public static AudioTrack genTone(int freqOfTone, int durationMillis) {
        int numSamples = durationMillis * 4;
        byte[] generatedSnd = new byte[(numSamples * 2)];
        double factor = (6.283185307179586d * ((double) freqOfTone)) / 4000.0d;
        int rampSamples = numSamples / 10;
        int i = 0;
        int idx = 0;
        while (i < numSamples) {
            short val = (short) ((int) (32767.0d * (i < rampSamples ? ((double) i) / ((double) rampSamples) : i > numSamples - rampSamples ? (((double) numSamples) - ((double) i)) / ((double) rampSamples) : 1.0d) * Math.sin(((double) i) * factor)));
            int idx2 = idx + 1;
            generatedSnd[idx] = (byte) (val & 255);
            idx = idx2 + 1;
            generatedSnd[idx2] = (byte) ((65280 & val) >>> 8);
            i++;
        }
        AudioTrack track = new AudioTrack(3, getSamplerate(), 4, 2, generatedSnd.length, 0);
        track.write(generatedSnd, 0, generatedSnd.length);
        return track;
    }

    public static int getSamplerate() {
        return 4000;
    }
}
