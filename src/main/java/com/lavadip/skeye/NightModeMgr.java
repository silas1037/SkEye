package com.lavadip.skeye;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import com.lavadip.skeye.view.OverlaidFilterView;

public final class NightModeMgr {
    private static final float BRIGHTNESS_OVERRIDE_NONE = -1.0f;
    private static final float BRIGHTNESS_OVERRIDE_OFF = 0.0f;
    private static final String PREF_THEME_SELECTED = "theme_selected";
    private static final int SYSTEM_UI_FLAG_FULLSCREEN = 4;
    private static final int SYSTEM_UI_FLAG_HIDE_NAVIGATION = 2;
    private static final int SYSTEM_UI_FLAG_IMMERSIVE = 2048;
    private static final int SYSTEM_UI_FLAG_IMMERSIVE_STICKY = 4096;
    private static final int SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN = 1024;
    private static final int SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION = 512;
    private static final int SYSTEM_UI_FLAG_LAYOUT_STABLE = 256;
    private static final int SYSTEM_UI_FLAG_LOW_PROFILE = 1;
    private static final int SYSTEM_UI_FLAG_VISIBLE = 0;

    public enum Theme {
        Day(0),
        Dusk(50),
        Night(100);
        
        final int prefValue;
        final int redness;

        private Theme(int prefValue2) {
            this.prefValue = prefValue2;
            this.redness = (int) (255.0f * (((float) prefValue2) / 100.0f));
        }

        public static Theme fromPrefValue(int value) {
            if (value == Day.prefValue) {
                return Day;
            }
            return value == Dusk.prefValue ? Dusk : Night;
        }
    }

    public static Theme getTheme(Context c) {
        return Theme.fromPrefValue(PreferenceManager.getDefaultSharedPreferences(c).getInt(PREF_THEME_SELECTED, Theme.Day.prefValue));
    }

    private static void setThemeForContext(Context c, View rootView, Theme themeSelected) {
        OverlaidFilterView overlaidFilterView = (OverlaidFilterView) rootView.findViewById(C0031R.C0032id.overlaidFilterView);
        if (overlaidFilterView != null) {
            if (themeSelected != Theme.Day) {
                overlaidFilterView.setVisibility(0);
                overlaidFilterView.setReddness(themeSelected.redness);
                return;
            }
            overlaidFilterView.setVisibility(8);
        }
    }

    public static void setThemeForActivity(Activity a) {
        Theme currTheme = getTheme(a);
        setThemeForContext(a, a.getWindow().getDecorView(), currTheme);
        updateNavUI(a, a.findViewById(16908290).getRootView());
        setScreenAndButtonBrightness(a, currTheme);
    }

    public static void setThemeForDialog(Dialog d, View baseView) {
        setThemeForContext(d.getContext(), baseView, getTheme(d.getContext()));
        updateNavUI(d.getContext(), d.findViewById(16908290).getRootView());
    }

    /* access modifiers changed from: private */
    public static void updateNavUI(Context c, View rootView) {
        changeNavUI(c, rootView, isCurrentNightMode(c));
    }

    public static int getCurrentThemeOrdinal(Context c) {
        return getTheme(c).ordinal();
    }

    public static boolean isCurrentNightMode(Context c) {
        return getTheme(c) != Theme.Day;
    }

    /* access modifiers changed from: private */
    public static void setWindowParams(Window win, float buttonBrightness, float screenBrightness) {
        WindowManager.LayoutParams winParams = win.getAttributes();
        try {
            winParams.getClass().getField("buttonBrightness").setFloat(winParams, buttonBrightness);
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e2) {
            e2.printStackTrace();
        } catch (IllegalArgumentException e3) {
            e3.printStackTrace();
        } catch (IllegalAccessException e4) {
            e4.printStackTrace();
        }
        winParams.screenBrightness = screenBrightness;
        win.setAttributes(winParams);
    }

    private static void setScreenAndButtonBrightness(Activity a, Theme theme) {
        final float buttonBrightness;
        final float screenBrightness;
        final Window win = a.getWindow();
        if (theme != Theme.Day) {
            buttonBrightness = 0.0f;
        } else {
            buttonBrightness = -1.0f;
        }
        if (theme == Theme.Night) {
            screenBrightness = 0.1f;
        } else {
            screenBrightness = -1.0f;
        }
        setWindowParams(win, BRIGHTNESS_OVERRIDE_NONE, BRIGHTNESS_OVERRIDE_NONE);
        new Handler(a.getMainLooper()).postDelayed(new Runnable() {
            /* class com.lavadip.skeye.NightModeMgr.RunnableC00201 */

            public void run() {
                NightModeMgr.setWindowParams(win, buttonBrightness, screenBrightness);
            }
        }, 10);
    }

    public void setTheme(Activity a, Theme newTheme) {
        PreferenceManager.getDefaultSharedPreferences(a).edit().putInt(PREF_THEME_SELECTED, newTheme.prefValue).commit();
    }

    @TargetApi(11)
    private static void changeNavUI(final Context context, final View rootView, boolean hide) {
        final int profileMask = 1;
        if (!hide) {
            profileMask = 0;
        }
        try {
            rootView.setSystemUiVisibility(profileMask | SYSTEM_UI_FLAG_LAYOUT_STABLE);
            rootView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
                /* class com.lavadip.skeye.NightModeMgr.View$OnSystemUiVisibilityChangeListenerC00212 */

                public void onSystemUiVisibilityChange(int visibility) {
                    if (profileMask != (visibility & 1)) {
                        rootView.setOnSystemUiVisibilityChangeListener(null);
                        Handler h = new Handler();
                        if (h != null) {
                            final Context context = context;
                            final View view = rootView;
                            h.postDelayed(new Runnable() {
                                /* class com.lavadip.skeye.NightModeMgr.View$OnSystemUiVisibilityChangeListenerC00212.RunnableC00221 */

                                public void run() {
                                    NightModeMgr.updateNavUI(context, view);
                                }
                            }, 1500);
                        }
                    }
                }
            });
            rootView.getClass().getMethod("setSystemUiVisibility", Integer.TYPE);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (SecurityException e2) {
            e2.printStackTrace();
        } catch (NoSuchMethodError | NoSuchMethodException e3) {
        }
    }
}
