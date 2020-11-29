package com.lavadip.skeye.catalog;

import android.annotation.TargetApi;
import android.content.res.Resources;
import com.lavadip.skeye.AstroUtil;
import com.lavadip.skeye.C0031R;
import com.lavadip.skeye.LabelMaker;
import com.lavadip.skeye.LabelPaints;
import com.lavadip.skeye.MyShadyRenderer;
import com.lavadip.skeye.QuickSettingsManager;
import com.lavadip.skeye.SkEye;
import com.lavadip.skeye.Vector3d;
import com.lavadip.skeye.astro.C0059L1;
import com.lavadip.skeye.astro.IntList;
import com.lavadip.skeye.astro.Sky;
import com.lavadip.skeye.astro.ephemeris.Ephemeris;
import com.lavadip.skeye.astro.ephemeris.EphemerisImplementation;
import com.lavadip.skeye.astro.ephemeris.Utils;
import com.lavadip.skeye.util.Util;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.Date;

public class SolarSysCatalog extends Catalog {
    private static int FLOATS_PER_DISK = (POINTS_PER_DISK * 3);
    private static int JUP_DISK_INDEX = 1;
    public static final int JUP_INDEX = 5;
    public static final int MOON_INDEX = 1;
    static final int MOON_PHASE_FIRST_QUARTER = 2;
    static final int MOON_PHASE_FULL = 4;
    static final int MOON_PHASE_LAST_QUARTER = 6;
    static final int MOON_PHASE_NEW = 0;
    static final int MOON_PHASE_WANING_CRESCENT = 7;
    static final int MOON_PHASE_WANING_GIBBOUS = 5;
    static final int MOON_PHASE_WAXING_CRESCENT = 1;
    static final int MOON_PHASE_WAXING_GIBBOUS = 3;
    private static int NUM_DISKS = 2;
    private static final int NUM_JOVIAN_MOONS = 4;
    public static final int NUM_OBJS = 14;
    public static final int NUM_PLANETS = 9;
    private static final int NUM_SUN_POSITIONS = 12;
    private static int POINTS_PER_DISK = 34;
    private static final long REFRESH_PERIOD = 30000;
    private static int SUN_DISK_INDEX = 0;
    public static final int SUN_INDEX = 0;
    private static final Vector3d Y_VEC = new Vector3d(0.0f, 1.0f, 0.0f);
    private static String antiSolarPointDescr = null;
    private static final float[] colorArray = {1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.5f, 0.5f, 1.0f, 0.96078f, 0.949f, 0.8f, 1.0f, 0.5f, 0.5f, 1.0f, 1.0f, 0.5f, 0.25f, 0.5f, 1.0f, 0.48039f, 0.4745f, 0.8f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 0.5f, 1.0f, 0.0f, 0.0f, 0.8f};
    private static final String eclipticAlphaKey = "eclipticAlpha";
    private static final FloatBuffer futureSunBuffer = ByteBuffer.allocateDirect(144).order(ByteOrder.nativeOrder()).asFloatBuffer();
    static final ShortBuffer futureSunLineIndexBuffer;
    private static String illumDescr;
    private static String jupiterMoonDescr;
    private static String moonDescr;
    private static String[] moonPhaseDescr;
    private static String[] names;
    private static String planetDescr;
    private static final float[] positions = new float[28];
    private static String sunDescr;
    private int colorOffset = 0;
    private final FloatBuffer diskVertexPosBuffer = ByteBuffer.allocateDirect((NUM_DISKS * 12) * POINTS_PER_DISK).order(ByteOrder.nativeOrder()).asFloatBuffer();
    private float endMoonCrescentAngle = 3.1415927f;
    private int labelBaseId = 0;
    private long lastUpdate = 0;
    private final FloatBuffer moonVertexPosBuffer = ByteBuffer.allocateDirect(POINTS_PER_DISK * 12).order(ByteOrder.nativeOrder()).asFloatBuffer();
    private final float[] planetMag = {-26.74f, -12.74f, 4.0f, -4.0f, -1.0f, -2.0f, 0.0f, 5.5f, 7.7f, Float.NaN, 5.5f, 5.78f, 5.11f, 6.13f};

    static {
        short[] indices = new short[12];
        for (short i = 0; i < 12; i = (short) (i + 1)) {
            indices[i] = i;
        }
        futureSunLineIndexBuffer = Util.setLineIndex(indices);
    }

    SolarSysCatalog(int catalogId) {
        super(catalogId, C0031R.string.solar_system, true, true, true, false, 75.0f, false, 9.0f, false, false);
    }

    @Override // com.lavadip.skeye.catalog.Catalog
    public int getNumObjs() {
        return 14;
    }

    @Override // com.lavadip.skeye.catalog.Catalog
    public IntList getSelObjs() {
        return getAllObjs();
    }

    @Override // com.lavadip.skeye.catalog.Catalog
    public String getName(int objNum) {
        return names[objNum];
    }

    @Override // com.lavadip.skeye.catalog.Catalog
    public float[] getPositions() {
        return positions;
    }

    @Override // com.lavadip.skeye.catalog.Catalog
    public float getMag(int objNum) {
        return this.planetMag[objNum];
    }

    @Override // com.lavadip.skeye.catalog.Catalog
    @TargetApi(8)
    public void drawES20(MyShadyRenderer myRenderer, IntList currBlocks, float currFov) {
        myRenderer.mVectorLineShader.draw(2.0f * this.displayScaleFactor, myRenderer.mvpMatrix, futureSunBuffer, futureSunLineIndexBuffer, 2, futureSunLineIndexBuffer.capacity(), colorArray, this.colorOffset + 4);
        for (int i = 0; i < NUM_DISKS; i++) {
            this.diskVertexPosBuffer.position(FLOATS_PER_DISK * i);
            myRenderer.mPlainShader.draw(this.diskVertexPosBuffer, POINTS_PER_DISK, colorArray, this.colorOffset + 8);
        }
        myRenderer.mMoonShader.draw(this.moonVertexPosBuffer, this.endMoonCrescentAngle, POINTS_PER_DISK);
        myRenderer.mPointShader.beginDrawing(MyShadyRenderer.mStarTextureId);
        myRenderer.mPointShader.draw(myRenderer.mvpMatrix, colorArray, this.colorOffset, 4.0f * this.displayScaleFactor, this.myVertexArray, 14);
    }

    @Override // com.lavadip.skeye.catalog.Catalog
    public void updateSky(long currTime) {
        if (Math.abs(currTime - this.lastUpdate) > REFRESH_PERIOD) {
            Date targetDate = new Date(currTime);
            Ephemeris eph = new EphemerisImplementation();
            Ephemeris.PlanetData[] newPlanetPos = eph.getPlanetPositions(targetDate, Math.toDegrees((double) Sky.getUserLongitude()), Math.toDegrees((double) Sky.getUserLatitude()));
            for (int i = 0; i < 9; i++) {
                positions[i * 2] = (float) Math.toRadians(newPlanetPos[i].f37RA);
                positions[(i * 2) + 1] = (float) Math.toRadians(newPlanetPos[i].Dec);
            }
            positions[18] = (float) (((double) positions[0]) + 3.141592653589793d);
            positions[19] = -positions[1];
            float[] jupVecPos = new float[3];
            Util.map3d(positions[10], positions[11], jupVecPos, 0, (float) newPlanetPos[5].f38r);
            double targetJD = Utils.JED(targetDate);
            double[] jovianElems = new double[6];
            double[] jovianMoonCart = new double[6];
            for (int moon = 0; moon < 4; moon++) {
                C0059L1.DL1_2FRC(targetJD, moon, true, jovianElems, jovianMoonCart);
                jovianMoonCart[0] = jovianMoonCart[0] + ((double) jupVecPos[2]);
                jovianMoonCart[1] = jovianMoonCart[1] + ((double) jupVecPos[0]);
                jovianMoonCart[2] = jovianMoonCart[2] + ((double) jupVecPos[1]);
                double jovianMoonRa = Math.atan2(jovianMoonCart[1], jovianMoonCart[0]);
                double jovianMoonDec = AstroUtil.computeAlt((double) ((float) jovianMoonCart[0]), (double) ((float) jovianMoonCart[1]), (double) ((float) jovianMoonCart[2]));
                positions[(moon + 10) * 2] = (float) jovianMoonRa;
                positions[((moon + 10) * 2) + 1] = (float) jovianMoonDec;
            }
            updateVecPositions();
            updateVertexArray(true);
            updateSunMoon(newPlanetPos[0].f38r, newPlanetPos[1].f38r);
            updateJupiter(newPlanetPos[5].f38r);
            float[] futureSolarPosJ2000 = new float[24];
            futureSolarPosJ2000[0] = positions[0];
            futureSolarPosJ2000[1] = positions[1];
            for (int i2 = 1; i2 < 12; i2++) {
                Ephemeris.PlanetData sunFuturePos = eph.getPlanetPosition(new Date((((long) i2) * 2592000000L) + currTime), Ephemeris.Planet.SUN, Math.toDegrees((double) Sky.getUserLongitude()), Math.toDegrees((double) Sky.getUserLatitude()));
                futureSolarPosJ2000[i2 * 2] = (float) Math.toRadians(sunFuturePos.f37RA);
                futureSolarPosJ2000[(i2 * 2) + 1] = (float) Math.toRadians(sunFuturePos.Dec);
            }
            float[] futureSunVecPositions = new float[36];
            Sky.precess(futureSolarPosJ2000, futureSunVecPositions, 12, 9.0f);
            futureSunBuffer.position(0);
            futureSunBuffer.put(futureSunVecPositions);
            futureSunBuffer.position(0);
            this.lastUpdate = currTime;
        }
    }

    private static void makePlainDisk(Vector3d posVec, FloatBuffer vertexPosBuffer, int bufferIndex, double distanceAU, double meanRadiusAU, double angleOffset) {
        Vector3d tVec = posVec.rotate((double) ((float) Math.atan2(meanRadiusAU, distanceAU)), posVec.crossMult(Y_VEC, true));
        vertexPosBuffer.position(FLOATS_PER_DISK * bufferIndex);
        posVec.putXYZ(vertexPosBuffer);
        for (int i = 0; i <= 32; i++) {
            tVec.rotate((double) ((float) (((((double) i) * 6.283185307179586d) / 32.0d) + angleOffset)), posVec).putXYZ(vertexPosBuffer);
        }
        vertexPosBuffer.position(0);
    }

    private void updateJupiter(double distanceAU) {
        Vector3d jupPos = new Vector3d();
        jupPos.setXYZ(this.vecPositions, 15);
        jupPos.normalise();
        makePlainDisk(jupPos, this.diskVertexPosBuffer, JUP_DISK_INDEX, distanceAU, 4.6732617030490934E-4d, 0.0d);
    }

    private void updateSunMoon(double distanceSunAU, double distanceMoonAU) {
        int moonDescrIndex;
        Vector3d sunPos = new Vector3d();
        sunPos.setXYZ(this.vecPositions, 0);
        sunPos.normalise();
        makePlainDisk(sunPos, this.diskVertexPosBuffer, SUN_DISK_INDEX, distanceSunAU, 0.0046491d, 0.0d);
        Vector3d moonPos = new Vector3d();
        moonPos.setXYZ(this.vecPositions, 3);
        moonPos.normalise();
        double angleSunMoon = moonPos.angleBetweenMag(sunPos);
        boolean waxing = sunPos.crossMult(moonPos, true).f17y > 0.0d;
        makePlainDisk(moonPos, this.moonVertexPosBuffer, 0, distanceMoonAU, 1.1614E-5d, -calculateAngleBrightLimb());
        this.endMoonCrescentAngle = (float) angleSunMoon;
        double percentIllumination = findMoonIllumination(angleSunMoon);
        if (percentIllumination < 1.0d) {
            moonDescrIndex = 0;
        } else if (percentIllumination < 49.0d) {
            moonDescrIndex = waxing ? 1 : 7;
        } else if (percentIllumination < 51.0d) {
            moonDescrIndex = waxing ? 2 : 6;
        } else if (percentIllumination < 99.0d) {
            moonDescrIndex = waxing ? 3 : 5;
        } else {
            moonDescrIndex = 4;
        }
        moonDescr = String.valueOf(moonPhaseDescr[moonDescrIndex]) + " " + String.format(illumDescr, Double.valueOf(percentIllumination));
    }

    private static double calculateAngleBrightLimb() {
        float raM = positions[2];
        float dM = positions[3];
        float raS = positions[0];
        float dS = positions[1];
        return Math.atan2(Math.cos((double) dS) * Math.sin((double) (raS - raM)), (Math.sin((double) dS) * Math.cos((double) dM)) - ((Math.cos((double) dS) * Math.sin((double) dM)) * Math.cos((double) (raS - raM))));
    }

    private static double findMoonIllumination(double angleSunMoon) {
        double angleEarthSun;
        double angleEarthSunCrude = Math.asin(Math.sin(angleSunMoon) / Math.sqrt(1.00000576d - (0.0048d * Math.cos(angleSunMoon))));
        if (angleSunMoon < 1.5707963267948966d) {
            angleEarthSun = 3.141592653589793d - angleEarthSunCrude;
        } else {
            angleEarthSun = angleEarthSunCrude;
        }
        return 50.0d * (1.0d + Math.cos(angleEarthSun));
    }

    @Override // com.lavadip.skeye.catalog.Catalog
    public String getTypeDescr(int objNum) {
        if (objNum == 0) {
            return sunDescr;
        }
        if (objNum == 1) {
            return moonDescr;
        }
        if (objNum < 9) {
            return planetDescr;
        }
        if (objNum == 9) {
            return antiSolarPointDescr;
        }
        return jupiterMoonDescr;
    }

    @Override // com.lavadip.skeye.catalog.Catalog
    public void initLabels(LabelMaker labelMaker, LabelPaints paints, float displayScaleFactor) {
        this.displayScaleFactor = displayScaleFactor;
        for (int i = 0; i < 14; i++) {
            int labelId = labelMaker.add(getName(i), paints.planetPaints[i]);
            if (i == 0) {
                this.labelBaseId = labelId;
            }
        }
    }

    @Override // com.lavadip.skeye.catalog.Catalog
    public void drawLabelES(int objNum, float x, float y, MyShadyRenderer r, LabelMaker labelMaker, boolean centerLabelHoriz, boolean centerLabelVert) {
        labelMaker.drawES20(r, x, y, this.labelBaseId + objNum, centerLabelHoriz, centerLabelVert, 0.0f);
    }

    @Override // com.lavadip.skeye.catalog.Catalog
    public void setTheme(int themeOrdinal) {
        this.colorOffset = Util.chooseColorOffset(colorArray.length, themeOrdinal);
    }

    @Override // com.lavadip.skeye.catalog.Catalog
    public void init(SkEye skeye) throws IOException {
        Resources res = skeye.getResources();
        names = res.getStringArray(C0031R.array.solar_system);
        moonPhaseDescr = res.getStringArray(C0031R.array.moon_phase_descr);
        sunDescr = res.getString(C0031R.string.sun_descr);
        illumDescr = res.getString(C0031R.string.illum_descr);
        planetDescr = res.getString(C0031R.string.planet_descr);
        antiSolarPointDescr = res.getString(C0031R.string.anti_solar_descr);
        jupiterMoonDescr = res.getString(C0031R.string.jupiter_moon_descr);
    }

    @Override // com.lavadip.skeye.catalog.Catalog
    public boolean isObjVisible(int objNum, float fov) {
        return objNum <= 9 || ((double) fov) < 2.5d;
    }

    @Override // com.lavadip.skeye.catalog.Catalog
    public QuickSettingsManager.QuickSettingsGroup getQuickSettings(SkEye skeye, MyShadyRenderer renderer) {
        return new QuickSettingsManager.QuickSettingsGroup(new QuickSettingsManager.SettingDetails[]{new QuickSettingsManager.SettingDetails(new QuickSettingsManager.FloatRangeQuickSetting(eclipticAlphaKey, skeye.getString(C0031R.string.ecliptic_opacity), "", 0.0f, 1.0f, 0.02f), new QuickSettingsManager.TypicalSettingChangeHandler<Float>(skeye) {
            /* class com.lavadip.skeye.catalog.SolarSysCatalog.C00851 */

            @Override // com.lavadip.skeye.QuickSettingsManager.SettingChangeListener
            public /* bridge */ /* synthetic */ void onChange(String str, Object obj, boolean z) {
                onChange(str, (Float) obj, z);
            }

            @Override // com.lavadip.skeye.QuickSettingsManager.TypicalSettingChangeHandler
            public void onGLThread(String key, Float alpha, boolean trackingStopped) {
                int stride = SolarSysCatalog.colorArray.length / 3;
                for (int i = 0; i < SolarSysCatalog.colorArray.length; i += stride) {
                    SolarSysCatalog.colorArray[i + 4 + 3] = alpha.floatValue();
                }
            }
        }, getCurrEclipticAlpha(skeye))}, skeye.getString(C0031R.string.solar_system), "solarsystem");
    }

    private static Float getCurrEclipticAlpha(SkEye skeye) {
        return Float.valueOf(skeye.settingsManager.getQuickPref(eclipticAlphaKey, 0.5f));
    }
}
