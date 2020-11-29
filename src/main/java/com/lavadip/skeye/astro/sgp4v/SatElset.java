package com.lavadip.skeye.astro.sgp4v;

import java.io.Serializable;
import java.util.Calendar;
import java.util.StringTokenizer;
import java.util.TimeZone;
import org.apache.commons.math3.linear.BlockRealMatrix;

public final class SatElset implements Serializable {
    private static final String BLANKCARD = "                                                                     ";
    private static final double TODEGREES = 57.29577951308232d;
    private static final double TORADIANS = 0.017453292519943295d;
    private static final long serialVersionUID = 0;
    private double argPerigee;
    private double bstar;
    private double eccentricity;
    private int elsetNum;
    private int ephemerisType;
    private double epochDay;
    private int epochYr;
    private double inclination;
    private String intDesig;
    private boolean isValid;
    private double meanAnomaly;
    private double meanMotion;
    private String name;
    private int revNum;
    private double rightAscension;
    private String satClass;
    private int satID;

    public class ValueOutOfRangeException extends Exception {
        private static final long serialVersionUID = 7729000960933265858L;

        public ValueOutOfRangeException() {
        }

        public ValueOutOfRangeException(String s) {
            super(s);
        }
    }

    public SatElset() {
        this.name = " ";
        this.isValid = false;
    }

    public SatElset(SatElset elset) {
        this.name = new String(elset.getName());
        if (this.name == null) {
            this.name = " ";
        }
        this.argPerigee = elset.argPerigee;
        this.bstar = elset.bstar;
        this.eccentricity = elset.eccentricity;
        this.elsetNum = elset.elsetNum;
        this.ephemerisType = elset.ephemerisType;
        this.epochDay = elset.epochDay;
        this.epochYr = elset.epochYr;
        this.inclination = elset.inclination;
        this.intDesig = elset.intDesig;
        this.meanAnomaly = elset.meanAnomaly;
        this.meanMotion = elset.meanMotion;
        this.revNum = elset.revNum;
        this.rightAscension = elset.rightAscension;
        this.satClass = elset.satClass;
        this.satID = elset.satID;
    }

    public SatElset(String card1, String card2) throws SatElsetException {
        this(" ", card1, card2);
    }

    public SatElset(String name2, String card1, String card2) throws SatElsetException {
        boolean z = false;
        if (name2 == null) {
            this.name = " ";
        } else {
            this.name = new String(name2);
        }
        int slen = card1.length();
        card1 = slen <= 69 ? String.valueOf(card1) + BLANKCARD.substring(0, 69 - slen) : card1;
        String line1 = card1.substring(0, Math.min(card1.length(), 69));
        int slen2 = card2.length();
        card2 = slen2 <= 69 ? String.valueOf(card2) + BLANKCARD.substring(0, 69 - slen2) : card2;
        String line2 = card2.substring(0, Math.min(card2.length(), 69));
        try {
            if (card1IsValid(line1) && card2IsValid(line2) && line1.regionMatches(2, line2, 2, 5)) {
                z = true;
            }
            this.isValid = z;
        } catch (Exception exc) {
            throw new SatElsetException(String.valueOf(exc.toString()) + "\n" + "SatElset constructor Invalid elset:\n" + "Card1: [" + line1 + "]\n" + "Card2: [" + line2 + "]");
        }
    }

    public boolean card1IsValid(String card) throws SatElsetException, ValueOutOfRangeException {
        String expString;
        if (card == null) {
            throw new SatElsetException("SatElset.card1IsValid Card 1 is null");
        } else if (card.length() < 68) {
            throw new SatElsetException("SatElset.card1IsValid Card 1 length < 68 length = " + card.length());
        } else if (!"1".equals(card.substring(0, 1))) {
            throw new SatElsetException("SatElset.card1IsValid Card 1 not 1: [" + card + "]");
        } else {
            try {
                this.satID = Integer.parseInt(card.substring(2, 7).replace(' ', '0'));
                if (this.satID < 1 || this.satID > 99999) {
                    throw new ValueOutOfRangeException("SatElset.card1IsValid Card 1 satID number out of range *" + card.substring(2, 7) + "*");
                }
                this.satClass = card.substring(7, 8);
                if ("U".equals(this.satClass) || "C".equals(this.satClass) || "S".equals(this.satClass) || "T".equals(this.satClass)) {
                    this.intDesig = card.substring(9, 17);
                    try {
                        this.epochYr = Integer.parseInt(card.substring(18, 20));
                        if (this.epochYr < 0 || this.epochYr > 99) {
                            throw new ValueOutOfRangeException("SatElset.card1IsValid Card 1 epochYr out of range: [" + card.substring(18, 20) + "]");
                        }
                        try {
                            this.epochDay = Double.valueOf(card.substring(20, 32)).doubleValue();
                            if (this.epochDay < 0.0d || this.epochDay > 367.0d) {
                                throw new ValueOutOfRangeException("SatElset.card1IsValid Card 1 epochDay out of range: [" + card.substring(20, 32) + "]");
                            }
                            Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
                            int day = (int) this.epochDay;
                            double tod = (this.epochDay - ((double) day)) * 86400.0d;
                            int hour = (int) (tod / 3600.0d);
                            int minute = (int) ((tod - (((double) hour) * 3600.0d)) / 60.0d);
                            cal.set(1, ((this.epochYr + 50) % 100) + 1950);
                            cal.set(6, day);
                            cal.set(11, hour);
                            cal.set(12, minute);
                            cal.set(13, (int) (tod - ((double) ((hour * 3600) + (minute * 60)))));
                            cal.set(14, (int) ((tod - ((double) ((int) tod))) * 1000.0d));
                            try {
                                if (card.charAt(50) == ' ') {
                                    String str = String.valueOf(card.substring(44, 45)) + "." + card.substring(45, 50) + "E+" + card.substring(51, 52);
                                } else {
                                    String str2 = String.valueOf(card.substring(44, 45)) + "." + card.substring(45, 50) + "E" + card.substring(50, 52);
                                }
                                try {
                                    String subStr = card.substring(59, 61);
                                    if (" 0".equalsIgnoreCase(subStr)) {
                                        expString = "00";
                                    } else {
                                        expString = subStr;
                                    }
                                    this.bstar = Double.valueOf(String.valueOf(card.substring(53, 54)) + "." + card.substring(54, 59) + "E" + expString).doubleValue();
                                    try {
                                        if (" ".equals(card.substring(62, 63))) {
                                            throw new SatElsetException("SatElset.card1IsValid No Ephemeris Type for Satellite number: " + this.satID + " [" + card.substring(62, 63) + "], Set to 0");
                                        }
                                        this.ephemerisType = Integer.parseInt(card.substring(62, 63));
                                        if (this.ephemerisType == 0 || this.ephemerisType == 2) {
                                            try {
                                                this.elsetNum = Integer.parseInt((String) new StringTokenizer(card.substring(65, 68), " ").nextElement());
                                                if (this.elsetNum < 0 || this.elsetNum > 999) {
                                                    throw new ValueOutOfRangeException("SatElset.card1IsValid Card 1 elsetNum number out of range: [" + card.substring(65, 68) + "]");
                                                } else if (card.length() <= 68 || " ".equals(card.substring(68, 69)) || checkSum(card) == Integer.parseInt(card.substring(68, 69))) {
                                                    return true;
                                                } else {
                                                    throw new SatElsetException("SatElset.card1IsValid Check Sum error on Card 1. Expecting: " + checkSum(card) + " Actual value on card: " + Integer.parseInt(card.substring(68, 69)) + " Satellite number: " + this.satID);
                                                }
                                            } catch (NumberFormatException ex) {
                                                String errString = "SatElset.card1IsValid Card 1 elsetNum number format exception: [" + card.substring(65, 68) + "]  Satellite number: " + this.satID;
                                                throw new SatElsetException(String.valueOf(errString) + "\\n" + ("SatElset.card1IsValid [" + ex + "]"));
                                            }
                                        } else {
                                            throw new ValueOutOfRangeException("SatElset.card1IsValid Card 1 ephemerisType out of range: [" + card.substring(62, 63) + "]  Satellite number: " + this.satID);
                                        }
                                    } catch (NumberFormatException ex2) {
                                        String errString2 = "SatElset.card1IsValid Card 1 ephemerisType number format exception: [" + card.substring(62, 63) + "]  Satellite number: " + this.satID;
                                        throw new SatElsetException(String.valueOf(errString2) + "\\n" + ("SatElset.card1IsValid [" + ex2 + "]"));
                                    }
                                } catch (NumberFormatException exc) {
                                    String errString3 = "SatElset.card1IsValid Invalid BSTAR for Satellite number: " + this.satID + " [" + card.substring(53, 61) + "]";
                                    throw new SatElsetException(String.valueOf(errString3) + "\\n" + ("SatElset.card1IsValid [" + exc + "]"));
                                }
                            } catch (NumberFormatException exc2) {
                                String errString4 = "SatElset.card1IsValid Invalid NdotDot for Satellite number: " + this.satID + " [" + card.substring(44, 52) + "]";
                                throw new SatElsetException(String.valueOf(errString4) + "\\n" + ("SatElset.card1IsValid [" + exc2 + "]"));
                            }
                        } catch (NumberFormatException ex3) {
                            String errString5 = "SatElset.card1IsValid Card 1 epochDay number format exception: [" + card.substring(20, 32) + "]  Satellite number: " + this.satID;
                            throw new SatElsetException(String.valueOf(errString5) + "\\n" + ("SatElset.card1IsValid [" + ex3 + "]"));
                        }
                    } catch (NumberFormatException ex4) {
                        String errString6 = "SatElset.card1IsValid Card 1 epochYr number format exception: [" + card.substring(18, 20) + "]  Satellite number: " + this.satID;
                        throw new SatElsetException(String.valueOf(errString6) + "\\n" + ("SatElset.card1IsValid [" + ex4 + "]"));
                    }
                } else {
                    throw new SatElsetException("SatElset.card1IsValid Card 1 classification error: [" + card.charAt(7) + "]");
                }
            } catch (NumberFormatException ex5) {
                String errString7 = "SatElset.card1IsValid Card 1 satID number format exception *" + card.substring(2, 7) + "*";
                throw new SatElsetException(String.valueOf(errString7) + "\\n" + ("SatElset.card1IsValid [" + ex5 + "]"));
            }
        }
    }

    public boolean card2IsValid(String card) throws SatElsetException, ValueOutOfRangeException {
        if (card == null || card.length() < 68 || !"2".equals(card.substring(0, 1))) {
            return false;
        }
        try {
            int satID2 = Integer.parseInt(card.substring(2, 7).replace(' ', '0'));
            if (satID2 < 1 || satID2 > 99999) {
                throw new ValueOutOfRangeException("SatElset.card2IsValid Card 2 satID number out of range: [" + card.substring(2, 7) + "]");
            }
            try {
                double testInclination = Double.valueOf(card.substring(8, 17)).doubleValue();
                if (testInclination < 0.0d || testInclination > 180.0d) {
                    throw new ValueOutOfRangeException("SatElset.card2IsValid Card 2 inclination out of range: [" + card.substring(8, 17) + "]  Satellite number: " + satID2);
                }
                this.inclination = TORADIANS * testInclination;
                try {
                    double rtAsc = Double.valueOf(card.substring(17, 26)).doubleValue();
                    if (rtAsc < 0.0d || rtAsc > 360.0d) {
                        throw new ValueOutOfRangeException("SatElset.card2IsValid Card 2 rightAcension out of range: [" + card.substring(17, 26) + "]  Satellite number: " + satID2);
                    }
                    this.rightAscension = TORADIANS * rtAsc;
                    try {
                        double testArgPerigee = Double.valueOf(card.substring(34, 42)).doubleValue();
                        if (testArgPerigee < 0.0d || testArgPerigee > 360.0d) {
                            throw new ValueOutOfRangeException("SatElset.card2IsValid Card 2 argPerigee out of range: [" + card.substring(34, 42) + "]  Satellite number: " + satID2);
                        }
                        this.argPerigee = TORADIANS * testArgPerigee;
                        try {
                            double testEccentricity = Double.valueOf("0." + card.substring(26, 33)).doubleValue();
                            if (testEccentricity < 0.0d || testEccentricity >= 1.0d) {
                                throw new ValueOutOfRangeException("SatElset.card2IsValid Card 2 eccentricity out of range: [0." + card.substring(26, 33) + "]  Satellite number: " + satID2);
                            }
                            this.eccentricity = testEccentricity;
                            try {
                                double testMeanAnomaly = Double.valueOf(card.substring(43, 51)).doubleValue();
                                if (testMeanAnomaly < 0.0d || testMeanAnomaly > 360.0d) {
                                    throw new ValueOutOfRangeException("SatElset.card2IsValid Card 2 meanAnomaly out of range: [" + card.substring(43, 51) + "]  Satellite number: " + satID2);
                                }
                                this.meanAnomaly = TORADIANS * testMeanAnomaly;
                                try {
                                    this.meanMotion = Double.valueOf(card.substring(52, 63)).doubleValue();
                                    if (this.meanMotion > 17.0d) {
                                        throw new ValueOutOfRangeException("SatElset.card2IsValid Card 2 meanMotion out of range: [" + card.substring(52, 63) + "]  Satellite number: " + satID2);
                                    }
                                    try {
                                        this.revNum = Integer.parseInt((String) new StringTokenizer(card.substring(63, 68), " ").nextElement());
                                        if (this.revNum < 0 || this.revNum > 99999) {
                                            throw new ValueOutOfRangeException("SatElset.card2IsValid Card 2 revNum out of range: [" + card.substring(52, 63) + "]  Satellite number: " + satID2);
                                        } else if (card.length() <= 68 || " ".equals(card.substring(68, 69)) || checkSum(card) == Integer.parseInt(card.substring(68, 69))) {
                                            return true;
                                        } else {
                                            throw new SatElsetException("SatElset.card2IsValid Check Sum error on Card 2. Expecting: " + checkSum(card) + " Actual value on card: " + Integer.parseInt(card.substring(68, 69)) + " Satellite number: " + satID2);
                                        }
                                    } catch (NumberFormatException ex) {
                                        String errString = "SatElset.card2IsValid Card 2 revNum number format exception: [" + card.substring(63, 68) + "]  Satellite number: " + satID2;
                                        throw new SatElsetException(String.valueOf(errString) + "\\n" + ("SatElset.card2IsValid [" + ex + "]"));
                                    }
                                } catch (NumberFormatException ex2) {
                                    String errString2 = "SatElset.card2IsValid Card 2 meanMotion number format exception: [" + card.substring(52, 63) + "]  Satellite number: " + satID2;
                                    throw new SatElsetException(String.valueOf(errString2) + "\\n" + ("SatElset.card2IsValid [" + ex2 + "]"));
                                }
                            } catch (NumberFormatException ex3) {
                                String errString3 = "SatElset.card2IsValid Card 2 meanAnomaly number format exception: [" + card.substring(43, 51) + "]  Satellite number: " + satID2;
                                throw new SatElsetException(String.valueOf(errString3) + "\\n" + ("SatElset.card2IsValid [" + ex3 + "]"));
                            }
                        } catch (NumberFormatException ex4) {
                            String errString4 = "SatElset.card2IsValid Card 2 eccentricity number format exception: [0." + card.substring(26, 33) + "]  Satellite number: " + satID2;
                            throw new SatElsetException(String.valueOf(errString4) + "\\n" + ("SatElset.card2IsValid [" + ex4 + "]"));
                        }
                    } catch (NumberFormatException ex5) {
                        String errString5 = "SatElset.card2IsValid Card 2 argPerigee number format exception: [" + card.substring(34, 42) + "]  Satellite number: " + satID2;
                        throw new SatElsetException(String.valueOf(errString5) + "\\n" + ("SatElset.card2IsValid [" + ex5 + "]"));
                    }
                } catch (NumberFormatException ex6) {
                    String errString6 = "SatElset.card2IsValid Card 2 rightAcension number format exception: [" + card.substring(17, 26) + "]  Satellite number: " + satID2;
                    throw new SatElsetException(String.valueOf(errString6) + "\\n" + ("SatElset.card2IsValid [" + ex6 + "]"));
                }
            } catch (NumberFormatException ex7) {
                String errString7 = "SatElset.card2IsValid Card 2 inclination number format exception: [" + card.substring(8, 17) + "]  Satellite number: " + satID2;
                throw new SatElsetException(String.valueOf(errString7) + "\\n" + ("SatElset.card2IsValid [" + ex7 + "]"));
            }
        } catch (NumberFormatException ex8) {
            String errString8 = "SatElset.card2IsValid Card 2 satID number format exception: [" + card.substring(2, 7) + "]";
            throw new SatElsetException(String.valueOf(errString8) + "\\n" + ("SatElset.card2IsValid [" + ex8 + "]"));
        }
    }

    private static int checkSum(String card) {
        int checksum = 0;
        for (int i = 0; i < 68; i++) {
            switch (card.charAt(i)) {
                case '-':
                case '1':
                    checksum++;
                    break;
                case '2':
                    checksum += 2;
                    break;
                case '3':
                    checksum += 3;
                    break;
                case BlockRealMatrix.BLOCK_SIZE /*{ENCODED_INT: 52}*/:
                    checksum += 4;
                    break;
                case '5':
                    checksum += 5;
                    break;
                case '6':
                    checksum += 6;
                    break;
                case '7':
                    checksum += 7;
                    break;
                case '8':
                    checksum += 8;
                    break;
                case '9':
                    checksum += 9;
                    break;
            }
        }
        return checksum % 10;
    }

    public static boolean equals(SatElset elset) {
        return false;
    }

    public double getArgPerigee() {
        return this.argPerigee;
    }

    public double getArgPerigeeDeg() {
        return this.argPerigee * TODEGREES;
    }

    public double getEccentricity() {
        return this.eccentricity;
    }

    public int getElsetNum() {
        return this.elsetNum;
    }

    public int getEphemerisType() {
        return this.ephemerisType;
    }

    public double getBstar() {
        return this.bstar;
    }

    public double getEpochDay() {
        return this.epochDay;
    }

    public int getEpochYr() {
        return this.epochYr;
    }

    public double getInclination() {
        return this.inclination;
    }

    public double getInclinationDeg() {
        return this.inclination * TODEGREES;
    }

    public String getIntDesig() {
        return this.intDesig;
    }

    public double getMeanAnomaly() {
        return this.meanAnomaly;
    }

    public double getMeanAnomalyDeg() {
        return this.meanAnomaly * TODEGREES;
    }

    public double getMeanMotion() {
        return this.meanMotion;
    }

    public String getName() {
        return this.name;
    }

    public double getPeriod() {
        double mm = getMeanMotion();
        if (mm > 0.0d) {
            return 1440.0d / mm;
        }
        return 0.0d;
    }

    public int getRevNum() {
        return this.revNum;
    }

    public double getRightAscension() {
        return this.rightAscension;
    }

    public double getRightAscensionDeg() {
        return this.rightAscension * TODEGREES;
    }

    public int getSatID() {
        return this.satID;
    }

    public boolean isValid() {
        return this.isValid;
    }
}
