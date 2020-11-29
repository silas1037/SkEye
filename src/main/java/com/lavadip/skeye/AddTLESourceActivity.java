package com.lavadip.skeye;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TabHost;
import android.widget.TextView;
import com.lavadip.skeyepro.C0139R;

public final class AddTLESourceActivity extends Activity {
    public static final int REQUEST_ADD_SOURCE = 0;
    public static final int REQUEST_EDIT_SOURCE = 1;
    private Intent myIntent;
    private EditText sourceNameEdit;
    private final String[] tabIds = {"Manual", "Suggested"};
    private EditText urlText;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(C0139R.layout.add_tle_src);
        this.myIntent = getIntent();
        this.sourceNameEdit = (EditText) findViewById(C0139R.C0140id.tle_src_name);
        this.urlText = (EditText) findViewById(C0139R.C0140id.tle_src_url);
        if (this.myIntent.getBooleanExtra("editRequested", false)) {
            ((TextView) findViewById(C0139R.C0140id.location_manage_title)).setText(getString(C0139R.string.editing_location));
            this.sourceNameEdit.setText(this.myIntent.getStringExtra("name"));
        }
        TabHost tabHost = (TabHost) findViewById(C0139R.C0140id.add_tle_src_tabs);
        tabHost.setup();
        int[] tabContentId = {C0139R.C0140id.tle_src_tab_manual, C0139R.C0140id.tle_src_tab_recommended};
        String[] tabNames = {getString(C0139R.string.specify_manually), getString(C0139R.string.recommended)};
        for (int i = 0; i < tabContentId.length; i++) {
            tabHost.addTab(tabHost.newTabSpec(this.tabIds[i]).setIndicator(tabNames[i]).setContent(tabContentId[i]));
        }
    }

    public void clickUseManual(View v) {
        if (checkName()) {
            complete(this.sourceNameEdit.getText().toString(), this.urlText.getText().toString());
        }
    }

    private void complete(String name, String url) {
        Intent data = new Intent(getIntent());
        data.putExtra("name", name);
        data.putExtra("url", url);
        data.putExtra("location_id", this.myIntent.getIntExtra("location_id", -1));
        setResult(-1, data);
        finish();
    }

    private void makeAlert(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(msg).setCancelable(true).setNeutralButton("Ok", (DialogInterface.OnClickListener) null);
        builder.create().show();
    }

    private boolean checkName() {
        boolean ok = this.sourceNameEdit.getText().length() != 0;
        if (!ok) {
            this.sourceNameEdit.requestFocus();
            makeAlert("Please specify a name for this source");
        }
        return ok;
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();
        getWindow().setSoftInputMode(3);
    }
}
