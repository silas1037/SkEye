package com.lavadip.skeyepro;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import com.lavadip.skeye.NightModeMgr;
import com.lavadip.skeye.catalog.Catalog;

public final class SatelliteConfigDialog extends Dialog {
    private static final int CONTEXTMENU_CHOOSE_ITEM = 2;
    private static final int CONTEXTMENU_DELETE_ITEM = 1;
    private static final int CONTEXTMENU_EDIT_ITEM = 0;
    private final Context context;

    /* renamed from: db */
    private final SQLiteDatabase f108db;
    private ListView lstView = null;

    public SatelliteConfigDialog(final Context context2, int theme, Catalog catalogParam) {
        super(context2, theme);
        this.context = context2;
        getWindow().setFlags(1024, 1024);
        setContentView(C0139R.layout.tle_config_dialog);
        this.lstView = (ListView) findViewById(C0139R.C0140id.listViewSrc);
        this.lstView.setChoiceMode(1);
        this.lstView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            /* class com.lavadip.skeyepro.SatelliteConfigDialog.View$OnCreateContextMenuListenerC01411 */

            public void onCreateContextMenu(ContextMenu conMenu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                conMenu.setHeaderTitle(((Cursor) SatelliteConfigDialog.this.lstView.getAdapter().getItem(((AdapterView.AdapterContextMenuInfo) menuInfo).position)).getString(1));
                conMenu.add(0, 0, 0, context2.getString(C0139R.string.location_context_edit));
                conMenu.add(0, 1, 0, context2.getString(C0139R.string.location_context_delete));
                conMenu.add(0, 2, 0, context2.getString(C0139R.string.location_context_select));
            }
        });
        this.f108db = new TLESourceDatabaseHelper(context2).getWritableDatabase();
        updateSourceList();
    }

    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        NightModeMgr.setThemeForDialog(this, getWindow().getDecorView());
    }

    public boolean onContextItemSelected(MenuItem aItem) {
        Cursor c = (Cursor) this.lstView.getAdapter().getItem(((AdapterView.AdapterContextMenuInfo) aItem.getMenuInfo()).position);
        final int sourceId = c.getInt(0);
        switch (aItem.getItemId()) {
            case 0:
            case 1:
                new AlertDialog.Builder(this.context).setMessage("Are you sure you want to delete source '" + c.getString(1) + "'?").setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    /* class com.lavadip.skeyepro.SatelliteConfigDialog.DialogInterface$OnClickListenerC01422 */

                    public void onClick(DialogInterface dialog, int which) {
                        SatelliteConfigDialog.this.f108db.delete("tle_sources", "id = ?", new String[]{new StringBuilder().append(sourceId).toString()});
                        SatelliteConfigDialog.this.updateSourceList();
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
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void updateSourceList() {
        Log.d("SKEYE", "Updating source List");
        Cursor result = this.f108db.rawQuery("SELECT id AS _id, name,url FROM tle_sources;", null);
        Log.d("SKEYE", "count: " + result.getCount());
        this.lstView.setAdapter((ListAdapter) new SimpleCursorAdapter(this.context, C0139R.layout.tle_src_view, result, new String[]{"name", "url"}, new int[]{C0139R.C0140id.src_name, C0139R.C0140id.src_url}));
    }

    public void onItemClick(AdapterView<?> dataView, View arg1, int pos, long id) {
        selectLocation((Cursor) dataView.getItemAtPosition(pos));
    }

    private static void fillIntentWithData(Intent intent, Cursor c) {
        intent.putExtra("name", c.getString(1));
        intent.putExtra("url", c.getString(2));
    }

    private void selectLocation(Cursor c) {
    }
}
