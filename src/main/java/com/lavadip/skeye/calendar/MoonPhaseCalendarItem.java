package com.lavadip.skeye.calendar;

import android.util.Log;
import android.view.View;
import com.lavadip.skeye.C0031R;
import com.lavadip.skeye.calendar.CalendarAdapter;

final class MoonPhaseCalendarItem extends CalendarAdapter.CalendarItem {
    private final double angleSunMoonDegrees;
    private final boolean waxing;

    public MoonPhaseCalendarItem(boolean waxing2, double angleSnMoonDegrees) {
        this.waxing = waxing2;
        this.angleSunMoonDegrees = angleSnMoonDegrees;
    }

    /* access modifiers changed from: package-private */
    @Override // com.lavadip.skeye.calendar.CalendarAdapter.CalendarItem
    public void updateView(View v) {
        try {
            ((CalendarMoonView) v.findViewById(C0031R.C0032id.date_moon)).setPhase(this.waxing, this.angleSunMoonDegrees);
        } catch (ArrayIndexOutOfBoundsException e) {
            Log.d("SKEYE", "Array index exception:" + e.getMessage() + ", " + e.getCause() + ", " + e.getStackTrace());
            Log.d("SKEYE", "stack trace: " + Log.getStackTraceString(e));
        }
    }
}
