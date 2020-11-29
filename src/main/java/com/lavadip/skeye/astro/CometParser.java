package com.lavadip.skeye.astro;

import com.lavadip.skeye.AstroUtil;
import com.lavadip.skeye.astro.keplerian.Elements;
import com.lavadip.skeye.astro.keplerian.EllipticalOrbit;
import com.lavadip.skeye.astro.keplerian.HyperbolicOrbit;
import com.lavadip.skeye.astro.keplerian.Orbit;
import com.lavadip.skeye.astro.keplerian.ParabolicOrbit;
import fortranfmt.FortranFormat;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;

public final class CometParser {
    private static final String[] specParts = {"I4", "A1", "A7", "2X", "I4", "1X", "I2", "1X", "F7.4", "1X", "F9.6", "2X", "F8.6", "2X", "F8.4", "2X", "F8.4", "2X", "F8.4", "2X", "I4", "I2", "I2", "2X", "F4.1", "1X", "F4.0", "2X", "A56", "1X", "A9"};

    public static Comet parseComet(FortranFormat ffParser, String line) throws IOException {
        Orbit orbit;
        ArrayList<Object> fields = ffParser.parse(line);
        double eccentricity = ((Double) fields.get(7)).doubleValue();
        Elements elems = new Elements(new Instant(((Integer) fields.get(3)).intValue(), ((Integer) fields.get(4)).intValue(), ((Double) fields.get(5)).doubleValue()), ((Double) fields.get(6)).doubleValue(), 0.0d, eccentricity, Math.toRadians(((Double) fields.get(10)).doubleValue()), Math.toRadians(((Double) fields.get(9)).doubleValue()), Math.toRadians(((Double) fields.get(8)).doubleValue()));
        if (eccentricity < 1.0d) {
            orbit = new EllipticalOrbit(elems);
        } else if (eccentricity == 1.0d) {
            orbit = new ParabolicOrbit(elems);
        } else {
            orbit = new HyperbolicOrbit(elems);
        }
        return new Comet((String) fields.get(16), ((Double) fields.get(14)).doubleValue(), ((Double) fields.get(15)).doubleValue(), orbit);
    }

    public static Comet[] parseComets(String[] lines) {
        ArrayList<Comet> comets = new ArrayList<>(lines.length);
        try {
            FortranFormat ffParser = mkParser();
            for (String line : lines) {
                try {
                    comets.add(parseComet(ffParser, line));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return (Comet[]) comets.toArray(new Comet[comets.size()]);
        } catch (ParseException e2) {
            throw new UnknownError("Error while initialising");
        }
    }

    public static Comet[] parseComets(Iterable<String> lineIterator) {
        ArrayList<Comet> comets = new ArrayList<>();
        try {
            FortranFormat ffParser = mkParser();
            for (String line : lineIterator) {
                try {
                    comets.add(parseComet(ffParser, line));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return (Comet[]) comets.toArray(new Comet[comets.size()]);
        } catch (ParseException e2) {
            throw new UnknownError("Error while initialising");
        }
    }

    private static FortranFormat mkParser() throws ParseException {
        return new FortranFormat("(" + AstroUtil.mkString(specParts, ",") + ")");
    }
}
