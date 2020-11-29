package com.lavadip.skeye.astro;

public final class SkyIndex {
    private static final int INITIAL_LIST_SIZE = 32;
    final int MAX_DEC_INDEX;
    final int MAX_RA_INDEX;
    final int NUM_DEC_DIVS;
    final int NUM_RA_DIVS;
    private final IntList emptyList = new IntList(0);
    private final IntList[] index;

    public SkyIndex(int num_ra_divs, int num_dec_divs) {
        this.NUM_RA_DIVS = num_ra_divs;
        this.NUM_DEC_DIVS = num_dec_divs;
        this.MAX_RA_INDEX = this.NUM_RA_DIVS - 1;
        this.MAX_DEC_INDEX = this.NUM_DEC_DIVS - 1;
        this.index = new IntList[(this.NUM_RA_DIVS * this.NUM_DEC_DIVS)];
    }

    public int addObject(int id, float ra, float dec) {
        int indexNum;
        synchronized (this) {
            indexNum = getBlockIndex(ra, dec);
            if (this.index[indexNum] == null) {
                this.index[indexNum] = new IntList(32);
            }
            this.index[indexNum].add(id);
        }
        return indexNum;
    }

    /* access modifiers changed from: package-private */
    public void logStatistics() {
        for (int i = 0; i < this.NUM_RA_DIVS; i++) {
            String s = "";
            for (int j = 0; j < this.NUM_DEC_DIVS; j++) {
                IntList indexPtr = this.index[(this.NUM_DEC_DIVS * i) + j];
                s = indexPtr != null ? String.valueOf(s) + indexPtr.size + ", " : String.valueOf(s) + "0, ";
            }
        }
    }

    public IntList getNeighbours(float ra, float dec) {
        int[] index2 = new int[2];
        getIndices(ra, dec, index2);
        return getNeighbours(index2[0], index2[1]);
    }

    private static int wrapNext(int curr, int max) {
        if (curr < max) {
            return curr + 1;
        }
        return 0;
    }

    private static int wrapPrev(int curr, int max) {
        return curr > 0 ? curr - 1 : max;
    }

    private IntList getNeighbours(int raIndex, int decIndex) {
        IntList neighbours = new IntList(9);
        int prevRA = wrapPrev(raIndex, this.MAX_RA_INDEX);
        int prevDEC = wrapPrev(decIndex, this.MAX_DEC_INDEX);
        int nextRA = wrapNext(raIndex, this.MAX_RA_INDEX);
        int nextDEC = wrapNext(decIndex, this.MAX_DEC_INDEX);
        int prev2RA = wrapPrev(prevRA, this.MAX_RA_INDEX);
        int prev2DEC = wrapPrev(prevDEC, this.MAX_DEC_INDEX);
        int[] raCombs = {prev2RA, prevRA, raIndex, nextRA, wrapNext(nextRA, this.MAX_RA_INDEX)};
        int[] decCombs = {prev2DEC, prevDEC, decIndex, nextDEC, wrapNext(nextDEC, this.MAX_DEC_INDEX)};
        for (int ra : raCombs) {
            for (int dec : decCombs) {
                neighbours.add((this.NUM_DEC_DIVS * ra) + dec);
            }
        }
        return neighbours;
    }

    public int getBlockIndex(float ra, float dec) {
        int decIndex = (int) (((((double) dec) + 1.5707963267948966d) * ((double) this.NUM_DEC_DIVS)) / 3.141592653589793d);
        if (decIndex == 0 || decIndex == this.NUM_DEC_DIVS - 1) {
            return decIndex;
        }
        return decIndex + (this.NUM_DEC_DIVS * ((int) (((double) (((float) this.NUM_RA_DIVS) * ra)) / 6.283185307179586d)));
    }

    private void getIndices(float ra, float dec, int[] results) {
        int decIndex = (int) (((((double) dec) + 1.5707963267948966d) * ((double) this.NUM_DEC_DIVS)) / 3.141592653589793d);
        results[1] = decIndex;
        if (decIndex == 0 || decIndex == this.NUM_DEC_DIVS - 1) {
            results[0] = 0;
        } else {
            results[0] = (int) (((double) (((float) this.NUM_RA_DIVS) * ra)) / 6.283185307179586d);
        }
    }

    public boolean isEmpty(int blockIndex) {
        return this.index[blockIndex] == null;
    }

    public IntList getObjects(int blockIndex) {
        if (this.index[blockIndex] == null) {
            return this.emptyList;
        }
        return this.index[blockIndex];
    }
}
