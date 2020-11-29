package com.lavadip.skeye.catalog;

import android.net.Uri;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;

public final class CatalogManager {
    public static final int CATALOG_ID_BASIC_STAR = 2;
    public static final int CATALOG_ID_COMET = 9;
    public static final int CATALOG_ID_CONST = 1;
    public static final int CATALOG_ID_EXT_STAR = 6;
    public static final int CATALOG_ID_IC = 8;
    public static final int CATALOG_ID_MESSIER = 4;
    public static final int CATALOG_ID_NGC = 5;
    public static final int CATALOG_ID_SATELLITE = 7;
    public static final int CATALOG_ID_SOLAR = 3;
    static final int CATALOG_MASK = 2113929216;
    public static final float MAX_FOV_CATALOG_LABEL = 50.0f;
    static final char OBJ_BITS = 25;
    public static final int OBJ_MAJOR_TYPE_CLUSTER = 2;
    public static final int OBJ_MAJOR_TYPE_GALAXY = 3;
    public static final int OBJ_MAJOR_TYPE_NEBULA = 4;
    public static final int OBJ_MAJOR_TYPE_STAR = 1;
    static final int OBJ_MASK = 33554431;
    public static final int OBJ_MINOR_TYPE_GLOBULAR_CLUSTER = 1;
    public static final int OBJ_MINOR_TYPE_OPEN_CLUSTER = 2;
    public static final Catalog[] catalogs = {new SpecialCatalog(0), constCatalog, starCatalog, solarsysCatalog, messierCatalog, ngcCatalog, extStarCatalog, satCatalog, icCatalog, cometCatalog};
    private static final Catalog cometCatalog = new CometCatalog(9);
    private static final Catalog constCatalog = new ConstellationCatalog(1);
    public static final Catalog[] dynamicCatalogs;
    private static final Catalog extStarCatalog = new ExtStarCatalog(6);
    private static final Catalog icCatalog = new ICCatalog(8);
    private static final Catalog messierCatalog = new MessierCatalog(4);
    private static final Catalog ngcCatalog = new NGCCatalog(5);
    private static final Catalog satCatalog = new SatelliteCatalog(7);
    private static final Catalog solarsysCatalog = new SolarSysCatalog(3);
    private static final Catalog starCatalog = new BasicStarCatalog(2);

    static {
        int count = 0;
        for (Catalog c : catalogs) {
            if (c.isVeryDynamic) {
                count++;
            }
        }
        dynamicCatalogs = new Catalog[count];
        int count2 = 0;
        Catalog[] catalogArr = catalogs;
        for (Catalog c2 : catalogArr) {
            if (c2.isVeryDynamic) {
                dynamicCatalogs[count2] = c2;
                count2++;
            }
        }
    }

    public static int getCatalog(int objId) {
        return (CATALOG_MASK & objId) >>> 25;
    }

    public static int getObjNum(int objId) {
        return OBJ_MASK & objId;
    }

    public static int makeObjId(int catalog, int objNum) {
        return (catalog << 25) | objNum;
    }

    public static void updateSky(long currTime) {
        for (Catalog c : catalogs) {
            c.updateSky(currTime);
        }
    }

    public static String getTypeDescr(int objId) {
        return catalogs[getCatalog(objId)].getTypeDescr(getObjNum(objId));
    }

    public static int searchObjByUri(Uri uri) {
        List<String> paths = uri.getPathSegments();
        if (paths.size() == 3 && paths.get(0).equals("astro_object")) {
            String type = paths.get(1);
            String name = paths.get(2);
            Log.d("SKEYE", String.format("Looking for %s : %s", type, name));
            List<Catalog> shortListedCatalogs = new ArrayList<>();
            if (type.equals("constellation") || type.equals("any")) {
                shortListedCatalogs.add(constCatalog);
            }
            if (type.equals("star") || type.equals("any")) {
                shortListedCatalogs.add(starCatalog);
            }
            if (type.equals("solarsys") || type.equals("any")) {
                shortListedCatalogs.add(solarsysCatalog);
            }
            if (type.equals("messier") || type.equals("any")) {
                shortListedCatalogs.add(messierCatalog);
            }
            if (type.equals("ngc") || type.equals("any")) {
                shortListedCatalogs.add(ngcCatalog);
            }
            for (Catalog c : shortListedCatalogs) {
                int id = c.searchByName(name);
                if (id >= 0) {
                    return id;
                }
            }
        }
        return -1;
    }
}
