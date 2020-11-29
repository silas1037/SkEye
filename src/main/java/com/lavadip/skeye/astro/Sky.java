package com.lavadip.skeye.astro;

import android.content.res.AssetManager;
import com.lavadip.skeye.AstroUtil;
import com.lavadip.skeye.FarLabelManager;
import com.lavadip.skeye.Vector3d;
import com.lavadip.skeye.astro.Precession;
import com.lavadip.skeye.catalog.Catalog;
import com.lavadip.skeye.catalog.CatalogManager;
import com.lavadip.skeye.config.LocationOnEarth;
import com.lavadip.skeye.util.Util;
import java.util.ArrayList;
import java.util.Date;

public final class Sky {
    private static final int ANGLE_LIMIT = 0;
    public static int DEFAULT_INDEX_DEC_DIVS = 10;
    public static int DEFAULT_INDEX_RA_DIVS = 20;
    private static double ERA = 0.0d;
    private static double cosLat = 0.0d;
    private static Date currDate = new Date();
    private static Precession.UTCDate currUTCDate = null;
    private static final double dUT1 = -0.29115d;
    public static final FarLabelManager farLabelMgr = new FarLabelManager();
    private static double lmst = 0.0d;
    private static Vector3d polarVec = new Vector3d(yVec);
    private static final Precession precession = new Precession(new Precession.UTCDate(new Date()));
    private static float[] precessionMatrix = precession.getTransformMatrix();
    private static final double safeAngle = Math.toRadians(0.0d);
    private static double sinLat = 0.0d;
    public static SkyIndex skyIndex;
    private static float userAltitude;
    private static volatile float userLatitude;
    private static float userLongitude;
    public static final Vector3d yVec = new Vector3d(0.0f, 1.0f, 0.0f);
    private static final Vector3d zVec = new Vector3d(0.0f, 0.0f, 1.0f);

    public static void setLmst(Date date, double lmstArg) {
        synchronized (yVec) {
            lmst = lmstArg;
            currDate = date;
            currUTCDate = new Precession.UTCDate(date);
        }
    }

    private static double julianDate(Precession.UTCDate date) {
        int day = date.day;
        int month = date.month + 1;
        int year = date.year + 1900;
        return (((double) ((((((long) day) - 32075) + ((1461 * ((((long) year) + 4800) + ((((long) month) - 14) / 12))) / 4)) + ((367 * ((((long) month) - 2) - (((((long) month) - 14) / 12) * 12))) / 12)) - ((3 * (((((long) year) + 4900) + ((((long) month) - 14) / 12)) / 100)) / 4))) - 0.5d) + (((((double) date.hours) + (((double) date.mins) / 60.0d)) + (((double) date.secs) / 3600.0d)) / 24.0d);
    }

    public static double getLmst() {
        return lmst;
    }

    public static double getRotationAngle() {
        return ERA + ((double) userLongitude);
    }

    public static float[] getPrecessionMatrix() {
        return precessionMatrix;
    }

    public static void precess(float[] j2000Pos, float[] currentVecPos, int N, float depth) {
        for (int i = 0; i < N; i++) {
            float ra = j2000Pos[i * 2];
            float dec = j2000Pos[(i * 2) + 1];
            double cosDec = Math.cos((double) dec);
            Vector3d pVec = precession.applyRotationCIRS(new Vector3d(Math.cos((double) ra) * cosDec, Math.sin((double) ra) * cosDec, Math.sin((double) dec)));
            currentVecPos[i * 3] = (float) (pVec.f17y * ((double) depth));
            currentVecPos[(i * 3) + 1] = (float) (pVec.f18z * ((double) depth));
            currentVecPos[(i * 3) + 2] = (float) (pVec.f16x * ((double) depth));
        }
    }

    public static void init(LocationOnEarth location) {
        userLongitude = location.longitude;
        userLatitude = location.latitude;
        userAltitude = location.altitude;
        cosLat = Math.cos((double) userLatitude);
        sinLat = Math.sin((double) userLatitude);
        polarVec = yVec.rotateAboutXaxis((double) userLatitude);
    }

    public static void getEqCoords(Vector3d vec, double[] result) {
        double y = (vec.f17y * cosLat) + (vec.f18z * sinLat);
        double z = (vec.f18z * cosLat) - (vec.f17y * sinLat);
        double dec = AstroUtil.computeAlt(vec.f16x, (double) ((float) z), (double) ((float) y));
        double ha = Util.makeAnglePositive(Math.atan2(-vec.f16x, z));
        result[0] = ha;
        result[1] = Util.makeAnglePositive(getRotationAngle() - ha);
        result[2] = dec;
    }

    public static void eqToRotMatrix(double ra, double dec, float[] rotMatrix) {
        Util.vectorToRotMatrix(Util.map3d((double) ((float) (ra - getRotationAngle())), (double) ((float) dec)).rotateAboutXaxis((double) userLatitude), rotMatrix);
    }

    public static ArrayList<CatalogedLocation> getVisibleObjs(boolean anyDirection, Catalog catalog) {
        ArrayList<CatalogedLocation> visibles = new ArrayList<>();
        IntList selObjs = catalog.getSelObjs();
        for (int i = 0; i < selObjs.size; i++) {
            CatalogedLocation skyObj = getSkyObject(catalog.f84id, CatalogManager.getObjNum(selObjs.get(i)));
            if (anyDirection || skyObj.altitude > safeAngle) {
                visibles.add(skyObj);
            }
        }
        return visibles;
    }

    public static CatalogedLocation getSkyObject(int id) {
        return CatalogedLocation.make(id);
    }

    public static CatalogedLocation getSkyObject(int catalogId, int objNum) {
        return CatalogedLocation.make(CatalogManager.makeObjId(catalogId, objNum));
    }

    public static float getUserLongitude() {
        return userLongitude;
    }

    public static float getUserLatitude() {
        return userLatitude;
    }

    public static Vector3d getPolarVec() {
        return polarVec;
    }

    public static double getUserAltitudeKm() {
        return ((double) userAltitude) / 1000.0d;
    }

    public static void updateIndex() {
        SkyIndex newIndex = new SkyIndex(DEFAULT_INDEX_RA_DIVS, DEFAULT_INDEX_DEC_DIVS);
        Catalog[] catalogArr = CatalogManager.catalogs;
        for (Catalog c : catalogArr) {
            if (c.indexed) {
                float[] objPos = c.getPositions();
                IntList selObjs = c.getSelObjs();
                for (int i = 0; i < selObjs.size; i++) {
                    int objId = selObjs.get(i);
                    int objNum = CatalogManager.getObjNum(objId);
                    newIndex.addObject(objId, objPos[objNum * 2], objPos[(objNum * 2) + 1]);
                }
            }
        }
        skyIndex = newIndex;
    }

    public static void init() {
        updateIndex();
    }

    public static void setUserLocation(LocationOnEarth location) {
        synchronized (zVec) {
            init(location);
        }
    }

    public static void setTime(Date date) {
        synchronized (yVec) {
            currDate = date;
            setLmst(date, TimeHelper.lmst(date, (double) userLongitude));
            double Tu = (julianDate(currUTCDate) - -3.369791666666667E-6d) - 2451545.0d;
            ERA = ((Tu % 1.0d) + 0.779057273264d + (0.00273781191135448d * Tu)) * 6.283185307179586d;
            while (ERA > 6.283185307179586d) {
                ERA -= 6.283185307179586d;
            }
        }
    }

    public static void createFarLabelManager(AssetManager assets) {
        synchronized (zVec) {
            if (farLabelMgr != null) {
                farLabelMgr.onDestroy();
            }
            farLabelMgr.updateSettings(assets);
        }
    }

    public static void onDestroy() {
        synchronized (zVec) {
            if (farLabelMgr != null) {
                farLabelMgr.onDestroy();
            }
        }
    }

    public static long getCurrentTime() {
        return currDate.getTime();
    }
}
