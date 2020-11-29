package com.lavadip.skeyepro;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import com.lavadip.skeye.AddTLESourceActivity;

public final class TLESourceConfigureActivity extends Activity implements AdapterView.OnItemClickListener {
    private static final int CONTEXTMENU_CHOOSE_ITEM = 2;
    private static final int CONTEXTMENU_DELETE_ITEM = 1;
    private static final int CONTEXTMENU_EDIT_ITEM = 0;

    /* renamed from: db */
    private SQLiteDatabase f109db;
    private ListView lstView = null;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(C0139R.layout.tle_config_dialog);
        this.lstView = (ListView) findViewById(C0139R.C0140id.listViewSrc);
        this.lstView.setOnItemClickListener(this);
        this.lstView.setChoiceMode(1);
        this.lstView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            /* class com.lavadip.skeyepro.TLESourceConfigureActivity.View$OnCreateContextMenuListenerC01431 */

            public void onCreateContextMenu(ContextMenu conMenu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                conMenu.setHeaderTitle(((Cursor) TLESourceConfigureActivity.this.lstView.getAdapter().getItem(((AdapterView.AdapterContextMenuInfo) menuInfo).position)).getString(1));
                conMenu.add(0, 0, 0, TLESourceConfigureActivity.this.getString(C0139R.string.location_context_edit));
                conMenu.add(0, 1, 0, TLESourceConfigureActivity.this.getString(C0139R.string.location_context_delete));
                conMenu.add(0, 2, 0, TLESourceConfigureActivity.this.getString(C0139R.string.location_context_select));
            }
        });
        this.f109db = new TLESourceDatabaseHelper(this).getWritableDatabase();
        updateSourceList();
    }

    public boolean onContextItemSelected(MenuItem aItem) {
        Cursor c = (Cursor) this.lstView.getAdapter().getItem(((AdapterView.AdapterContextMenuInfo) aItem.getMenuInfo()).position);
        final int sourceId = c.getInt(0);
        switch (aItem.getItemId()) {
            case 0:
                Intent editIntent = new Intent(this, AddTLESourceActivity.class);
                editIntent.putExtra("editRequested", true);
                editIntent.putExtra("source_id", sourceId);
                fillIntentWithData(editIntent, c);
                startActivityForResult(editIntent, 1);
                return true;
            case 1:
                new AlertDialog.Builder(this).setMessage("Are you sure you want to delete source '" + c.getString(1) + "'?").setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    /* class com.lavadip.skeyepro.TLESourceConfigureActivity.DialogInterface$OnClickListenerC01442 */

                    public void onClick(DialogInterface dialog, int which) {
                        TLESourceConfigureActivity.this.f109db.delete("tle_sources", "id = ?", new String[]{new StringBuilder().append(sourceId).toString()});
                        TLESourceConfigureActivity.this.updateSourceList();
                    }
                }).setCancelable(true).create().show();
                return true;
            case 2:
                selectLocation(c);
                return true;
            default:
                return false;
        }
    }

    public void clickAddLocation(View v) {
        startActivityForResult(new Intent(this, AddTLESourceActivity.class), 0);
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void updateSourceList() {
        Log.d("SKEYE", "Updating source List");
        Cursor result = this.f109db.rawQuery("SELECT id AS _id, name,url FROM tle_sources;", null);
        Log.d("SKEYE", "count: " + result.getCount());
        this.lstView.setAdapter((ListAdapter) new SimpleCursorAdapter(this, C0139R.layout.tle_src_view, result, new String[]{"name", "url"}, new int[]{C0139R.C0140id.src_name, C0139R.C0140id.src_url}));
    }

    @Override // android.widget.AdapterView.OnItemClickListener
    public void onItemClick(AdapterView<?> dataView, View arg1, int pos, long id) {
        selectLocation((Cursor) dataView.getItemAtPosition(pos));
    }

    private static void fillIntentWithData(Intent intent, Cursor c) {
        intent.putExtra("name", c.getString(1));
        intent.putExtra("url", c.getString(2));
    }

    private void selectLocation(Cursor c) {
        Intent data = new Intent(getIntent());
        fillIntentWithData(data, c);
        setResult(-1, data);
        finish();
    }

    /* access modifiers changed from: protected */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == -1) {
            switch (requestCode) {
                case 0:
                    this.f109db.insert("tle_sources", null, createContentValues(data));
                    updateSourceList();
                    return;
                case 1:
                    this.f109db.update("tle_sources", createContentValues(data), "id = " + data.getIntExtra("location_id", -1), null);
                    updateSourceList();
                    return;
                default:
                    return;
            }
        }
    }

    private static ContentValues createContentValues(Intent data) {
        ContentValues cv = new ContentValues();
        cv.put("name", data.getStringExtra("name"));
        cv.put("url", data.getStringExtra("url"));
        return cv;
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        super.onDestroy();
        this.f109db.close();
    }
}
