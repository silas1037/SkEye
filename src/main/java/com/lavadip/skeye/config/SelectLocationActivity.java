package com.lavadip.skeye.config;

import android.app.Activity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import com.lavadip.skeye.C0031R;
import com.lavadip.skeye.CustomDialog;
import com.lavadip.skeye.SkEye;

public final class SelectLocationActivity extends Activity implements AdapterView.OnItemClickListener {

    /* renamed from: db */
    private SQLiteDatabase f86db;
    private ListView lstView = null;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(C0031R.layout.activity_selectlocation);
        SkEye.setupActivity(this);
        this.lstView = (ListView) findViewById(C0031R.C0032id.ListViewLocation);
        this.lstView.setOnItemClickListener(this);
        this.lstView.setChoiceMode(1);
        this.lstView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            /* class com.lavadip.skeye.config.SelectLocationActivity.C01131 */

            @Override // android.widget.AdapterView.OnItemLongClickListener
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int pos, long id) {
                final Cursor c = (Cursor) SelectLocationActivity.this.lstView.getAdapter().getItem(pos);
                String selectedLoc = c.getString(1);
                View actionListView = SelectLocationActivity.this.getLayoutInflater().inflate(C0031R.layout.contextmenu_location, (ViewGroup) null);
                final CustomDialog dialog = new CustomDialog.Builder(SelectLocationActivity.this).setTitle(selectedLoc).setContentView(actionListView).create();
                ((Button) actionListView.findViewById(C0031R.C0032id.button_edit)).setOnClickListener(new View.OnClickListener() {
                    /* class com.lavadip.skeye.config.SelectLocationActivity.C01131.View$OnClickListenerC01141 */

                    public void onClick(View v) {
                        SelectLocationActivity.this.editLocation(c);
                        dialog.dismiss();
                    }
                });
                ((Button) actionListView.findViewById(C0031R.C0032id.button_delete)).setOnClickListener(new View.OnClickListener() {
                    /* class com.lavadip.skeye.config.SelectLocationActivity.C01131.View$OnClickListenerC01152 */

                    public void onClick(View v) {
                        SelectLocationActivity.this.deleteLocation(c);
                        dialog.dismiss();
                    }
                });
                ((Button) actionListView.findViewById(C0031R.C0032id.button_choose)).setOnClickListener(new View.OnClickListener() {
                    /* class com.lavadip.skeye.config.SelectLocationActivity.C01131.View$OnClickListenerC01163 */

                    public void onClick(View v) {
                        SelectLocationActivity.this.selectLocation(c);
                        dialog.dismiss();
                    }
                });
                dialog.show();
                return true;
            }
        });
        this.f86db = new LocationDatabaseHelper(this).getWritableDatabase();
        updateLocationList();
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void editLocation(Cursor c) {
        int locationId = c.getInt(0);
        Intent editIntent = new Intent(this, AddLocationActivity.class);
        editIntent.putExtra("editRequested", true);
        editIntent.putExtra("location_id", locationId);
        fillIntentWithLocation(editIntent, c);
        startActivityForResult(editIntent, 1);
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void deleteLocation(Cursor c) {
        final int locationId = c.getInt(0);
        new CustomDialog.Builder(this).setMessage("Are you sure you want to delete location '" + c.getString(1) + "'?").setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            /* class com.lavadip.skeye.config.SelectLocationActivity.DialogInterface$OnClickListenerC01172 */

            public void onClick(DialogInterface dialog, int which) {
                SelectLocationActivity.this.f86db.delete("locations", "id = ?", new String[]{new StringBuilder().append(locationId).toString()});
                SelectLocationActivity.this.updateLocationList();
            }
        }).setCancelable(true).create().show();
    }

    public void clickAddLocation(View v) {
        startActivityForResult(new Intent(this, AddLocationActivity.class), 0);
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void updateLocationList() {
        Cursor result = this.f86db.rawQuery("SELECT id AS _id, name,latitude,longitude,altitude FROM locations;", null);
        this.lstView.setAdapter((ListAdapter) new SimpleCursorAdapter(this, C0031R.layout.listitem_location, result, new String[]{"name", "latitude", "longitude", "altitude"}, new int[]{C0031R.C0032id.name_entry, C0031R.C0032id.lat_entry, C0031R.C0032id.long_entry, C0031R.C0032id.altitude_entry}));
    }

    @Override // android.widget.AdapterView.OnItemClickListener
    public void onItemClick(AdapterView<?> dataView, View arg1, int pos, long id) {
        selectLocation((Cursor) dataView.getItemAtPosition(pos));
    }

    private static void fillIntentWithLocation(Intent intent, Cursor c) {
        intent.putExtra("name", c.getString(1));
        intent.putExtra("latitude", c.getFloat(2));
        intent.putExtra("longitude", c.getFloat(3));
        intent.putExtra("altitude", c.getFloat(4));
    }

    private static void fillIntentWithLocationRadians(Intent intent, Cursor c) {
        intent.putExtra("name", c.getString(1));
        intent.putExtra("latitude", (float) Math.toRadians((double) c.getFloat(2)));
        intent.putExtra("longitude", (float) Math.toRadians((double) c.getFloat(3)));
        intent.putExtra("altitude", c.getFloat(4));
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void selectLocation(Cursor c) {
        Intent data = new Intent(getIntent());
        fillIntentWithLocationRadians(data, c);
        setResult(-1, data);
        finish();
    }

    /* access modifiers changed from: protected */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == -1) {
            switch (requestCode) {
                case 0:
                    this.f86db.insert("locations", null, createContentValues(data));
                    updateLocationList();
                    return;
                case 1:
                    this.f86db.update("locations", createContentValues(data), "id = " + data.getIntExtra("location_id", -1), null);
                    updateLocationList();
                    return;
                default:
                    return;
            }
        }
    }

    private static ContentValues createContentValues(Intent data) {
        ContentValues cv = new ContentValues();
        cv.put("name", data.getStringExtra("name"));
        cv.put("latitude", Float.valueOf(data.getFloatExtra("latitude", 0.0f)));
        cv.put("longitude", Float.valueOf(data.getFloatExtra("longitude", 0.0f)));
        cv.put("altitude", Float.valueOf(data.getFloatExtra("altitude", 0.0f)));
        return cv;
    }

    /* access modifiers changed from: protected */
    public void onStop() {
        super.onStop();
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        this.f86db.close();
        super.onDestroy();
    }
}
