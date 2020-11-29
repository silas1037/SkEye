package com.lavadip.skeye;

import android.content.Context;

public final class Directions {
    public static String[] dirAbbrvs;
    public static String[] dirStr;
    public static String[] mainDirStrs;

    public static void init(Context cntxt) {
        mainDirStrs = cntxt.getResources().getStringArray(C0031R.array.directions);
        String[] subDirStrs = cntxt.getResources().getStringArray(C0031R.array.sub_directions);
        dirStr = new String[]{mainDirStrs[0], subDirStrs[0], mainDirStrs[2], subDirStrs[1], mainDirStrs[1], subDirStrs[2], mainDirStrs[3], subDirStrs[3], mainDirStrs[0]};
        dirAbbrvs = cntxt.getResources().getStringArray(C0031R.array.direction_abbreviation);
    }
}
