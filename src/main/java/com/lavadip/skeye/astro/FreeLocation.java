package com.lavadip.skeye.astro;

import com.lavadip.skeye.Vector3d;

public final class FreeLocation extends LocationInSky {
    public FreeLocation(Vector3d positionVec) {
        super(positionVec);
    }

    @Override // com.lavadip.skeye.astro.LocationInSky
    public float getVisMag() {
        return 0.0f;
    }

    @Override // com.lavadip.skeye.astro.LocationInSky
    public String getName() {
        return "Insta-Aligned";
    }

    @Override // com.lavadip.skeye.astro.LocationInSky
    public String getFullName() {
        return getName();
    }

    @Override // com.lavadip.skeye.astro.LocationInSky
    public String getDescr() {
        return "Free point";
    }
}
