package com.lavadip.skeyepro;

import android.app.Dialog;
import android.content.Context;
import android.widget.SeekBar;
import android.widget.TextView;
import com.lavadip.skeye.NightModeMgr;
import com.lavadip.skeye.catalog.CometCatalog;

public final class CometConfigDialog extends Dialog {
    private static final float scale = 10.0f;

    public CometConfigDialog(final Context context, int theme, final CometCatalog catalogParam) {
        super(context, theme);
        getWindow().setFlags(1024, 1024);
        setContentView(C0139R.layout.comet_config_dialog);
        SeekBar seekBar = (SeekBar) findViewById(C0139R.C0140id.seekCometMinimumMag);
        final TextView textStatus = (TextView) findViewById(C0139R.C0140id.statusCometMinimumMag);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            /* class com.lavadip.skeyepro.CometConfigDialog.C01381 */

            public void onStopTrackingTouch(SeekBar seekBar) {
                catalogParam.setMaxMagnitude(context, ((float) seekBar.getProgress()) / CometConfigDialog.scale);
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                textStatus.setText(String.format("%4.1f", Float.valueOf(((float) progress) / CometConfigDialog.scale)));
            }
        });
        seekBar.setProgress((int) (catalogParam.getMaxMagnitude(context) * scale));
    }

    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        NightModeMgr.setThemeForDialog(this, getWindow().getDecorView());
    }
}
