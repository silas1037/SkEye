package com.lavadip.skeye.config;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.lavadip.skeye.C0031R;

public final class GetCalibrationActivity extends Activity implements View.OnClickListener {
    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(C0031R.layout.calibration);
        ((Button) findViewById(C0031R.C0032id.calibrateButton)).setOnClickListener(this);
        ((Button) findViewById(C0031R.C0032id.resetCalibrateButton)).setOnClickListener(this);
    }

    public void onClick(View v) {
        if (v.getId() == C0031R.C0032id.calibrateButton) {
            setResult(-1);
        } else {
            setResult(1);
        }
        finish();
    }
}
