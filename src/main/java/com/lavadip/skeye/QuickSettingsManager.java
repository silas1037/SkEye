package com.lavadip.skeye;

import android.content.Context;
import android.content.DialogInterface;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import com.lavadip.skeye.CustomDialog;
import com.lavadip.skeye.catalog.Catalog;
import com.lavadip.skeye.catalog.CatalogManager;
import java.util.ArrayList;
import java.util.List;

public final class QuickSettingsManager {
    public state currState = state.HIDDEN;
    private final MyShadyRenderer renderer;
    private final SkEye skeye;

    public interface SettingChangeListener<T> {
        void onChange(String str, T t, boolean z);
    }

    /* access modifiers changed from: package-private */
    public enum state {
        HIDDEN,
        SHOWING
    }

    public QuickSettingsManager(SkEye skeye2, MyShadyRenderer renderer2) {
        this.skeye = skeye2;
        this.renderer = renderer2;
    }

    public static final class QuickSettingsGroup {
        final String key;
        final SettingDetails<?>[] settings;
        final String title;

        public QuickSettingsGroup(SettingDetails<?>[] settingDetailsArr, String title2, String key2) {
            this.settings = settingDetailsArr;
            this.title = title2;
            this.key = key2;
        }

        public QuickSettingsGroup mergeWith(QuickSettingsGroup other) {
            SettingDetails[] mergedSettings = new SettingDetails[(this.settings.length + other.settings.length)];
            System.arraycopy(this.settings, 0, mergedSettings, 0, this.settings.length);
            System.arraycopy(other.settings, 0, mergedSettings, this.settings.length, other.settings.length);
            return new QuickSettingsGroup(mergedSettings, this.title, this.key);
        }
    }

    public static abstract class QuickSetting<T> {
        final String description;
        final String key;
        final String label;

        /* access modifiers changed from: package-private */
        public abstract View createView(Context context, T t, SettingChangeListener<T> settingChangeListener);

        public QuickSetting(String key2, String label2, String description2) {
            this.key = key2;
            this.label = label2;
            this.description = description2;
        }
    }

    public static class IntegerRangeQuickSetting extends QuickSetting<Integer> {
        static final /* synthetic */ boolean $assertionsDisabled = (!QuickSettingsManager.class.desiredAssertionStatus());
        private final int max;
        private final int min;
        private final int range;

        public IntegerRangeQuickSetting(String key, String label, String description, int min2, int max2) {
            super(key, label, description);
            if ($assertionsDisabled || min2 <= max2) {
                this.min = min2;
                this.max = max2;
                this.range = max2 - min2;
                return;
            }
            throw new AssertionError();
        }

        /* access modifiers changed from: package-private */
        public View createView(Context context, Integer initialValue, final SettingChangeListener<Integer> changeListener) {
            if ($assertionsDisabled || (initialValue.intValue() >= this.min && initialValue.intValue() <= this.max)) {
                SeekBar v = new SeekBar(context);
                v.setMax(this.range);
                v.setProgress(initialValue.intValue() - this.min);
                v.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    /* class com.lavadip.skeye.QuickSettingsManager.IntegerRangeQuickSetting.C00291 */

                    public void onStopTrackingTouch(SeekBar seekBar) {
                        if (changeListener != null) {
                            changeListener.onChange(IntegerRangeQuickSetting.this.key, Integer.valueOf(IntegerRangeQuickSetting.this.min + seekBar.getProgress()), true);
                        }
                    }

                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        if (changeListener != null) {
                            changeListener.onChange(IntegerRangeQuickSetting.this.key, Integer.valueOf(IntegerRangeQuickSetting.this.min + progress), false);
                        }
                    }
                });
                return v;
            }
            throw new AssertionError();
        }
    }

    public static class FloatRangeQuickSetting extends QuickSetting<Float> {
        static final /* synthetic */ boolean $assertionsDisabled = (!QuickSettingsManager.class.desiredAssertionStatus());
        private final float max;
        private final float min;
        private final float multiplier;
        private final int range;

        public FloatRangeQuickSetting(String key, String label, String description, float min2, float max2) {
            super(key, label, description);
            if ($assertionsDisabled || min2 <= max2) {
                this.min = min2;
                this.max = max2;
                float rangeF = max2 - min2;
                int rangeI = (int) rangeF;
                float multiplierTmp = 1.0f;
                while (multiplierTmp < 1000000.0f && (rangeF * multiplierTmp) - ((float) rangeI) > 0.0f) {
                    rangeI = (int) (rangeF * multiplierTmp);
                    multiplierTmp *= 10.0f;
                }
                this.range = rangeI;
                this.multiplier = multiplierTmp;
                return;
            }
            throw new AssertionError();
        }

        public FloatRangeQuickSetting(String key, String label, String description, float min2, float max2, float increment) {
            super(key, label, description);
            if ($assertionsDisabled || min2 <= max2) {
                this.min = min2;
                this.max = max2;
                float rangeF = max2 - min2;
                int i = (int) rangeF;
                this.range = (int) (rangeF / increment);
                this.multiplier = 1.0f / increment;
                return;
            }
            throw new AssertionError();
        }

        private int float2int(float f) {
            return (int) (this.multiplier * f);
        }

        /* access modifiers changed from: private */
        /* access modifiers changed from: public */
        private float int2float(int i) {
            return ((float) i) / this.multiplier;
        }

        /* access modifiers changed from: package-private */
        public View createView(Context context, Float initialValue, final SettingChangeListener<Float> changeListener) {
            if ($assertionsDisabled || (initialValue.floatValue() >= this.min && initialValue.floatValue() <= this.max)) {
                SeekBar v = new SeekBar(context);
                v.setMax(this.range);
                v.setProgress(float2int(initialValue.floatValue() - this.min));
                v.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    /* class com.lavadip.skeye.QuickSettingsManager.FloatRangeQuickSetting.C00281 */

                    public void onStopTrackingTouch(SeekBar seekBar) {
                        if (changeListener != null) {
                            changeListener.onChange(FloatRangeQuickSetting.this.key, Float.valueOf(FloatRangeQuickSetting.this.int2float(seekBar.getProgress()) + FloatRangeQuickSetting.this.min), true);
                        }
                    }

                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        if (changeListener != null) {
                            changeListener.onChange(FloatRangeQuickSetting.this.key, Float.valueOf(FloatRangeQuickSetting.this.int2float(progress) + FloatRangeQuickSetting.this.min), false);
                        }
                    }
                });
                return v;
            }
            throw new AssertionError();
        }
    }

    public static final class PreferenceSaver {
        public static <T> void save(String key, T t) {
        }
    }

    public static final class SettingDetails<T> {
        final T initialValue;
        final SettingChangeListener<T> listener;
        final QuickSetting<T> setting;

        public SettingDetails(QuickSetting<T> setting2, SettingChangeListener<T> listener2, T initialValue2) {
            this.setting = setting2;
            this.listener = listener2;
            this.initialValue = initialValue2;
        }

        public View createView(Context context) {
            return this.setting.createView(context, this.initialValue, this.listener);
        }
    }

    private static QuickSettingsGroup[] createSettings(SkEye skeye2, MyShadyRenderer renderer2) {
        List<QuickSettingsGroup> settingGroups = new ArrayList<>(0);
        mergeAndAppend(settingGroups, skeye2.mkQuickSettingGroup());
        mergeAndAppend(settingGroups, renderer2.mkQuickSettingGroup(skeye2));
        for (Catalog catalog : CatalogManager.catalogs) {
            mergeAndAppend(settingGroups, catalog.getQuickSettings(skeye2, renderer2));
        }
        return (QuickSettingsGroup[]) settingGroups.toArray(new QuickSettingsGroup[settingGroups.size()]);
    }

    private static void mergeAndAppend(List<QuickSettingsGroup> settingGroups, QuickSettingsGroup newSettings) {
        if (newSettings != null) {
            boolean matchFound = false;
            int matchIndex = -1;
            int N = settingGroups.size();
            for (int i = 0; i < N && !matchFound; i++) {
                matchFound = settingGroups.get(i).key.equals(newSettings.key);
                if (matchFound) {
                    matchIndex = i;
                }
            }
            if (matchFound) {
                QuickSettingsGroup mergedSettings = settingGroups.get(matchIndex).mergeWith(newSettings);
                settingGroups.remove(matchIndex);
                settingGroups.add(mergedSettings);
                return;
            }
            settingGroups.add(newSettings);
        }
    }

    public static abstract class TypicalSettingChangeHandler<T> implements SettingChangeListener<T> {
        private final SkEye skeye;

        public abstract void onGLThread(String str, Float f, boolean z);

        public TypicalSettingChangeHandler(SkEye skeye2) {
            this.skeye = skeye2;
        }

        public void onUIThread(String key, Float newValue, boolean trackingStopped) {
        }

        public void onChange(final String key, final Float newValue, final boolean trackingStopped) {
            onUIThread(key, newValue, trackingStopped);
            this.skeye.runOnGLThread(new Runnable() {
                /* class com.lavadip.skeye.QuickSettingsManager.TypicalSettingChangeHandler.RunnableC00301 */

                public void run() {
                    TypicalSettingChangeHandler.this.onGLThread(key, newValue, trackingStopped);
                }
            });
            this.skeye.requestGLRedraw();
            if (trackingStopped) {
                this.skeye.settingsManager.saveQuickPref(key, newValue.floatValue());
            }
        }
    }

    public void start(boolean isPortrait) {
        Display defaultDisplay = this.skeye.getWindowManager().getDefaultDisplay();
        final int maxHeight = (int) (0.4f * ((float) defaultDisplay.getHeight()));
        int maxWidth = (int) ((isPortrait ? 0.9f : 0.6f) * ((float) defaultDisplay.getWidth()));
        ScrollView sv = new ScrollView(this.skeye) {
            /* class com.lavadip.skeye.QuickSettingsManager.C00231 */

            /* access modifiers changed from: protected */
            public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                if (maxHeight < View.MeasureSpec.getSize(heightMeasureSpec)) {
                    heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(maxHeight, View.MeasureSpec.getMode(heightMeasureSpec));
                }
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            }
        };
        sv.setScrollbarFadingEnabled(false);
        LinearLayout lv = new LinearLayout(this.skeye);
        lv.setOrientation(1);
        int padding = (int) (10.0f * this.skeye.getResources().getDisplayMetrics().density);
        lv.setPadding(padding * 3, padding, padding * 3, padding);
        sv.addView(lv);
        CustomDialog dialog = new CustomDialog.Builder(this.skeye).setContentView(sv).create();
        mkSettingsView(lv, dialog);
        Window dialogWin = dialog.getWindow();
        WindowManager.LayoutParams attrs = dialogWin.getAttributes();
        attrs.dimAmount = 0.0f;
        attrs.alpha = 0.8f;
        attrs.flags |= 32;
        dialogWin.setAttributes(attrs);
        dialogWin.setGravity(isPortrait ? 80 : 85);
        dialogWin.setLayout(maxWidth, -2);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            /* class com.lavadip.skeye.QuickSettingsManager.DialogInterface$OnDismissListenerC00242 */

            public void onDismiss(DialogInterface dialog) {
                QuickSettingsManager.this.currState = state.HIDDEN;
            }
        });
        dialog.show();
        this.currState = state.SHOWING;
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void mkSettingsView(LinearLayout lv, final CustomDialog dialog) {
        QuickSettingsGroup[] settingGroups = createSettings(this.skeye, this.renderer);
        dialog.setTitle(this.skeye.getString(C0031R.string.quick_settings));
        dialog.setTitleButtonIcon(C0031R.drawable.close_icon, new View.OnClickListener() {
            /* class com.lavadip.skeye.QuickSettingsManager.View$OnClickListenerC00253 */

            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        lv.removeAllViews();
        for (QuickSettingsGroup group : settingGroups) {
            mkGroupView(lv, group, dialog, settingGroups);
        }
    }

    private void mkGroupView(final LinearLayout lv, final QuickSettingsGroup group, final CustomDialog dialog, QuickSettingsGroup[] settingGroups) {
        TextView groupTitle = new TextView(this.skeye);
        groupTitle.setText(group.title);
        final int padding = (int) (7.0f * this.skeye.getResources().getDisplayMetrics().density);
        groupTitle.setPadding(padding, padding, padding, padding);
        groupTitle.setTextSize(2, 20.0f);
        groupTitle.setOnClickListener(new View.OnClickListener() {
            /* class com.lavadip.skeye.QuickSettingsManager.View$OnClickListenerC00264 */

            public void onClick(View v) {
                lv.removeAllViews();
                dialog.setTitle(group.title);
                CustomDialog customDialog = dialog;
                int i = C0031R.drawable.back_icon;
                final LinearLayout linearLayout = lv;
                final CustomDialog customDialog2 = dialog;
                customDialog.setTitleButtonIcon(i, new View.OnClickListener() {
                    /* class com.lavadip.skeye.QuickSettingsManager.View$OnClickListenerC00264.View$OnClickListenerC00271 */

                    public void onClick(View v) {
                        QuickSettingsManager.this.mkSettingsView(linearLayout, customDialog2);
                    }
                });
                boolean firstSetting = true;
                SettingDetails<?>[] settingDetailsArr = group.settings;
                for (SettingDetails<?> s : settingDetailsArr) {
                    TextView label = new TextView(QuickSettingsManager.this.skeye);
                    label.setText(s.setting.label);
                    label.setPadding(0, (firstSetting ? 1 : 4) * padding, 0, 0);
                    firstSetting = false;
                    lv.addView(label);
                    lv.addView(s.createView(QuickSettingsManager.this.skeye));
                }
            }
        });
        lv.addView(groupTitle);
    }
}
