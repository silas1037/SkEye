package com.lavadip.skeye.config;

import android.content.Context;
import android.preference.ListPreference;
import android.util.AttributeSet;

public class ListPreferenceWithSummary extends ListPreference {
    public ListPreferenceWithSummary(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ListPreferenceWithSummary(Context context) {
        super(context);
    }

    public void setValue(String value) {
        super.setValue(value);
        setSummary(value);
    }

    @Override // android.preference.ListPreference, android.preference.Preference
    public void setSummary(CharSequence summary) {
        super.setSummary(getEntry());
    }
}
