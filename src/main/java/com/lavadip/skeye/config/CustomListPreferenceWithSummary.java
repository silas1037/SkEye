package com.lavadip.skeye.config;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.Preference;
import android.util.AttributeSet;
import android.util.Log;
import com.lavadip.skeye.C0031R;
import com.lavadip.skeye.CustomDialog;

public class CustomListPreferenceWithSummary extends DialogPreference {
    private int mClickedDialogEntryIndex;
    private CharSequence[] mEntries;
    private CharSequence[] mEntryValues;
    private String mValue;

    public CustomListPreferenceWithSummary(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, C0031R.styleable.com_lavadip_skeye_config_CustomListPreferenceWithSummary, 0, 0);
        this.mEntries = a.getTextArray(C0031R.styleable.com_lavadip_skeye_config_CustomListPreferenceWithSummary_entries);
        this.mEntryValues = a.getTextArray(C0031R.styleable.f7xdab703c6);
        a.recycle();
    }

    public CustomListPreferenceWithSummary(Context context) {
        this(context, null);
    }

    public void setEntries(CharSequence[] entries) {
        this.mEntries = entries;
    }

    public void setEntries(int entriesResId) {
        setEntries(getContext().getResources().getTextArray(entriesResId));
    }

    public CharSequence[] getEntries() {
        return this.mEntries;
    }

    public void setEntryValues(CharSequence[] entryValues) {
        this.mEntryValues = entryValues;
    }

    public void setEntryValues(int entryValuesResId) {
        setEntryValues(getContext().getResources().getTextArray(entryValuesResId));
    }

    public CharSequence[] getEntryValues() {
        return this.mEntryValues;
    }

    public void setValue(String value) {
        this.mValue = value;
        persistString(value);
        setSummary(getEntry());
    }

    public void setValueIndex(int index) {
        if (this.mEntryValues != null) {
            setValue(this.mEntryValues[index].toString());
        }
    }

    public String getValue() {
        return this.mValue;
    }

    public CharSequence getEntry() {
        int index = getValueIndex();
        if (index < 0 || this.mEntries == null) {
            return null;
        }
        return this.mEntries[index];
    }

    public int findIndexOfValue(String value) {
        if (!(value == null || this.mEntryValues == null)) {
            for (int i = this.mEntryValues.length - 1; i >= 0; i--) {
                if (this.mEntryValues[i].equals(value)) {
                    return i;
                }
            }
        }
        return -1;
    }

    private int getValueIndex() {
        return findIndexOfValue(this.mValue);
    }

    /* access modifiers changed from: protected */
    @Override // com.lavadip.skeye.config.DialogPreference
    public void onPrepareDialogBuilder(CustomDialog.Builder builder) {
        super.onPrepareDialogBuilder(builder);
        if (this.mEntries == null || this.mEntryValues == null) {
            throw new IllegalStateException("ListPreference requires an entries array and an entryValues array.");
        }
        this.mClickedDialogEntryIndex = getValueIndex();
        Log.d("SKEYE", "index: " + this.mClickedDialogEntryIndex);
        builder.setSingleChoiceItems(this.mEntries, this.mClickedDialogEntryIndex, new DialogInterface.OnClickListener() {
            /* class com.lavadip.skeye.config.CustomListPreferenceWithSummary.DialogInterface$OnClickListenerC00931 */

            public void onClick(DialogInterface dialog, int which) {
                CustomListPreferenceWithSummary.this.mClickedDialogEntryIndex = which;
                CustomListPreferenceWithSummary.this.setValueIndex(which);
                CustomListPreferenceWithSummary.this.onClick(dialog, -1);
                dialog.dismiss();
            }
        });
        builder.setPositiveButton((CharSequence) null, (DialogInterface.OnClickListener) null);
        builder.setNegativeButton(getContext().getString(17039360), (DialogInterface.OnClickListener) null);
    }

    /* access modifiers changed from: protected */
    @Override // com.lavadip.skeye.config.DialogPreference
    public void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);
        if (positiveResult && this.mClickedDialogEntryIndex >= 0 && this.mEntryValues != null) {
            String value = this.mEntryValues[this.mClickedDialogEntryIndex].toString();
            if (callChangeListener(value)) {
                setValue(value);
            }
        }
    }

    /* access modifiers changed from: protected */
    public Object onGetDefaultValue(TypedArray a, int index) {
        return a.getString(index);
    }

    /* access modifiers changed from: protected */
    public void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        setValue(restorePersistedValue ? getPersistedString(getKey()) : (String) defaultValue);
    }

    /* access modifiers changed from: protected */
    @Override // com.lavadip.skeye.config.DialogPreference
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        if (isPersistent()) {
            return superState;
        }
        SavedState myState = new SavedState(superState);
        myState.value = getValue();
        return myState;
    }

    /* access modifiers changed from: protected */
    @Override // com.lavadip.skeye.config.DialogPreference
    public void onRestoreInstanceState(Parcelable state) {
        if (state == null || !state.getClass().equals(SavedState.class)) {
            super.onRestoreInstanceState(state);
            return;
        }
        SavedState myState = (SavedState) state;
        super.onRestoreInstanceState(myState.getSuperState());
        setValue(myState.value);
    }

    private static class SavedState extends Preference.BaseSavedState {
        String value;

        public SavedState(Parcel source) {
            super(source);
            this.value = source.readString();
        }

        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeString(this.value);
        }

        public SavedState(Parcelable superState) {
            super(superState);
        }
    }

    public void onClick(DialogInterface dialog, int which) {
    }

    public void onDismiss(DialogInterface dialog) {
    }
}
