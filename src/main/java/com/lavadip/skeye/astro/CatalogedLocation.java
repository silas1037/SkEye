package com.lavadip.skeye.astro;

import com.lavadip.skeye.CommonNames;
import com.lavadip.skeye.Vector3d;
import com.lavadip.skeye.catalog.Catalog;
import com.lavadip.skeye.catalog.CatalogManager;

public final class CatalogedLocation extends LocationInSky {
    public final int catalogId;

    /* renamed from: id */
    public final int f21id;
    private final String name;
    public final int objNum;
    public final float priority;

    public CatalogedLocation(int id, Catalog catalog, int catalogId2, int objNum2, Vector3d posVec) {
        super(posVec);
        this.catalogId = catalogId2;
        this.objNum = objNum2;
        this.name = catalog.getName(objNum2);
        this.f21id = id;
        double zenithAngle = 1.5707963267948966d - this.altitude;
        float mag = catalog.getMag(objNum2);
        this.priority = 100.0f * ((float) (Float.isNaN(mag) ? Math.exp(-zenithAngle) : Math.exp(((double) ((-mag) / 4.0f)) - zenithAngle)));
    }

    public int compareTo(CatalogedLocation another) {
        float diff = this.priority - another.priority;
        if (diff < 0.0f) {
            return -1;
        }
        return diff > 0.0f ? 1 : 0;
    }

    public static CatalogedLocation make(int id) {
        int catalogId2 = CatalogManager.getCatalog(id);
        int objNum2 = CatalogManager.getObjNum(id);
        Catalog catalog = CatalogManager.catalogs[catalogId2];
        Vector3d result = new Vector3d();
        result.setXYZ(catalog.getVecPositions(), objNum2 * 3);
        Vector3d posVec = result.rotateAboutYaxis((double) ((float) (-Sky.getRotationAngle()))).rotateAboutXaxis((double) Sky.getUserLatitude());
        posVec.normalise();
        return new CatalogedLocation(id, catalog, catalogId2, objNum2, posVec);
    }

    @Override // com.lavadip.skeye.astro.LocationInSky
    public String getName() {
        return this.name;
    }

    @Override // com.lavadip.skeye.astro.LocationInSky
    public String getFullName() {
        int commonNameIndex = CommonNames.idToIndex.get(this.f21id);
        return commonNameIndex >= 0 ? String.valueOf(this.name) + ", " + CommonNames.names[commonNameIndex] : this.name;
    }

    @Override // com.lavadip.skeye.astro.LocationInSky
    public float getVisMag() {
        return CatalogManager.catalogs[this.catalogId].getMag(this.objNum);
    }

    @Override // com.lavadip.skeye.astro.LocationInSky
    public String getDescr() {
        return CatalogManager.catalogs[this.catalogId].getTypeDescrForSearch(this.objNum);
    }

    public String toString() {
        return String.format("<b>%s</b><br/><small>( %s )</small>", getFullName(), CatalogManager.getTypeDescr(this.f21id));
    }

    @Override // com.lavadip.skeye.astro.LocationInSky
    public boolean matches(String filterStr) {
        return CatalogManager.catalogs[this.catalogId].matches(this.objNum, this.f21id, filterStr);
    }
}
