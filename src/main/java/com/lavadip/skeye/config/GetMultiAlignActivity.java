package com.lavadip.skeye.config;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import com.lavadip.skeye.AlignManager;
import com.lavadip.skeye.C0031R;
import com.lavadip.skeye.CustomDialog;
import com.lavadip.skeye.OrientationManager;
import com.lavadip.skeye.SkEye;
import com.lavadip.skeye.Vector3d;
import com.lavadip.skeye.astro.LocationInSky;
import com.lavadip.skeye.astro.Sky;
import com.lavadip.skeye.catalog.CatalogManager;
import com.lavadip.skeye.util.Util;

public final class GetMultiAlignActivity extends Activity {
    static final int DIALOG_CONFIRM_CLEAR_ALL = 0;
    private static final int GET_ALIGN_REQUEST = 0;

    /* renamed from: am */
    private AlignManager f85am;
    private MatrixCursor data;
    private SimpleCursorAdapter dataAdapter;
    private ListView lstView;

    private static MatrixCursor makeNewCursor() {
        return new MatrixCursor(new String[]{"_id", "name", "descr", "fuzzyLoc"});
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(C0031R.layout.activity_get_multi_align);
        SkEye.setupActivity(this);
        this.f85am = SkEye.getAlignMgr();
        this.lstView = (ListView) findViewById(C0031R.C0032id.alignList);
        this.dataAdapter = new SimpleCursorAdapter(this, C0031R.layout.listitem_object, this.data, new String[]{"name", "descr", "fuzzyLoc"}, new int[]{C0031R.C0032id.name_entry, C0031R.C0032id.descr_entry, C0031R.C0032id.fuzzy_loc});
        updateData();
        Button clearAllButton = new Button(this);
        clearAllButton.setText(getString(C0031R.string.clear_all_alignments));
        clearAllButton.setCompoundDrawablesWithIntrinsicBounds(17301533, 0, 0, 0);
        clearAllButton.setOnClickListener(new View.OnClickListener() {
            /* class com.lavadip.skeye.config.GetMultiAlignActivity.View$OnClickListenerC01081 */

            public void onClick(View v) {
                GetMultiAlignActivity.this.clickClearAll(null);
            }
        });
        this.lstView.addFooterView(clearAllButton);
        this.lstView.setAdapter((ListAdapter) this.dataAdapter);
        this.lstView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            /* class com.lavadip.skeye.config.GetMultiAlignActivity.C01092 */

            @Override // android.widget.AdapterView.OnItemLongClickListener
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int pos, long id) {
                String descr = String.format(GetMultiAlignActivity.this.getString(C0031R.string.alignment_removal_descr), ((Cursor) GetMultiAlignActivity.this.lstView.getAdapter().getItem(pos)).getString(1));
                String completeDescr = descr;
                if (pos < GetMultiAlignActivity.this.f85am.getNumAligns() - 1) {
                    completeDescr = String.valueOf(GetMultiAlignActivity.this.getString(C0031R.string.alignment_removal_later_warning)) + "<br/>" + descr;
                }
                new CustomDialog.Builder(GetMultiAlignActivity.this).setTitle(C0031R.string.alignment_removal_title).setMessage(Html.fromHtml(completeDescr)).setNegativeButton(C0031R.string.yes, new DialogInterface.OnClickListener() {
                    /* class com.lavadip.skeye.config.GetMultiAlignActivity.C01092.DialogInterface$OnClickListenerC01101 */

                    public void onClick(DialogInterface dialog, int which) {
                        GetMultiAlignActivity.this.f85am.removeAlignment(pos);
                        GetMultiAlignActivity.this.updateData();
                        dialog.dismiss();
                    }
                }).setNeutralButton(C0031R.string.f6no, new DialogInterface.OnClickListener() {
                    /* class com.lavadip.skeye.config.GetMultiAlignActivity.C01092.DialogInterface$OnClickListenerC01112 */

                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create().show();
                return true;
            }
        });
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void updateData() {
        this.data = makeNewCursor();
        int numAligns = this.f85am.getNumAligns();
        LocationInSky[] targets = this.f85am.getTargetObjs();
        for (int i = 0; i < numAligns; i++) {
            addObject(targets[i]);
        }
        this.dataAdapter.changeCursor(this.data);
    }

    public void clickAddNewAlignment(View v) {
        SkEye.getOrientationMgr().switchToAligned();
        startActivityForResult(new Intent(this, GetAlignmentActivity.class), 0);
    }

    public void clickReturn(View v) {
        setResult(-1);
        finish();
    }

    public void clickClearAll(View v) {
        if (this.f85am.hasAligns()) {
            showDialog(0);
        } else {
            Util.showToast(this, C0031R.string.no_alignments_to_clear);
        }
    }

    /* access modifiers changed from: protected */
    public Dialog onCreateDialog(int id) {
        if (id != 0) {
            return null;
        }
        CustomDialog.Builder builder = new CustomDialog.Builder(this);
        builder.setMessage(Html.fromHtml(getString(C0031R.string.confirm_clear_all_alignments))).setCancelable(true).setPositiveButton(getString(17039379), new DialogInterface.OnClickListener() {
            /* class com.lavadip.skeye.config.GetMultiAlignActivity.DialogInterface$OnClickListenerC01123 */

            public void onClick(DialogInterface dialog, int id) {
                GetMultiAlignActivity.this.f85am.clearAll();
                GetMultiAlignActivity.this.updateData();
            }
        }).setNegativeButton(getString(17039360), (DialogInterface.OnClickListener) null);
        return builder.create();
    }

    private void addObject(LocationInSky v) {
        this.data.addRow(new Object[]{0, v.getName(), v.getDescr(), v.getFuzzyLocation(true, null)});
    }

    /* access modifiers changed from: protected */
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        switch (requestCode) {
            case 0:
                if (resultCode == -1) {
                    int selection = resultData.getIntExtra("selectedId", 0);
                    LocationInSky v = Sky.getSkyObject(selection);
                    addObject(v);
                    this.dataAdapter.notifyDataSetChanged();
                    float[] currRotation = new float[16];
                    OrientationManager om = SkEye.getOrientationMgr();
                    om.getCurrRotationMatrix(currRotation);
                    this.f85am.addAlignment(currRotation, v);
                    if ((getApplicationContext().getApplicationInfo().flags & 2) != 0) {
                        int catalogId = CatalogManager.getCatalog(selection);
                        int objId = CatalogManager.getObjNum(selection);
                        float[] eqPositions = CatalogManager.catalogs[catalogId].getVecPositions();
                        Vector3d targetVec = v.getVector();
                        om.getCurrRotationMatrixNoDec(currRotation);
                        String rotStr = "[";
                        for (int i = 0; i < currRotation.length; i++) {
                            rotStr = String.valueOf(rotStr) + currRotation[i] + ",";
                        }
                        Log.d("SKEYE", String.format("Added alignment: #%3d [%10s] 0x%08x %10dl (%13.10f, %13.10f, %13.10f) (%.10f, %.10f, %.10f)", Integer.valueOf(this.f85am.getNumAligns()), v.getName(), Integer.valueOf(selection), Long.valueOf(System.currentTimeMillis()), Float.valueOf(eqPositions[(objId * 3) + 0]), Float.valueOf(eqPositions[(objId * 3) + 1]), Float.valueOf(eqPositions[(objId * 3) + 2]), Double.valueOf(targetVec.f16x), Double.valueOf(targetVec.f17y), Double.valueOf(targetVec.f18z)));
                        Log.d("SKEYE", "Rot Matrix: " + (String.valueOf(rotStr) + "]"));
                        return;
                    }
                    return;
                }
                return;
            default:
                return;
        }
    }
}
