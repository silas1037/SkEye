package com.lavadip.skeye.config;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import com.lavadip.skeye.C0031R;
import com.lavadip.skeye.CustomDialog;
import com.lavadip.skeye.FlowLayout;
import com.lavadip.skeye.SkEye;
import com.lavadip.skeye.astro.CatalogedLocation;
import com.lavadip.skeye.astro.IntList;
import com.lavadip.skeye.astro.LocationInSky;
import com.lavadip.skeye.astro.Sky;
import com.lavadip.skeye.catalog.Catalog;
import com.lavadip.skeye.catalog.CatalogManager;
import com.lavadip.skeye.util.Util;
import com.lavadip.skeye.view.CustomSpinner;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public final class GetAlignmentActivity extends Activity implements AdapterView.OnItemClickListener {
    static final int ACCEPT_ALIGN_DIALOG = 0;
    private static final int MSG_UPDATE_DATA = 0;
    private static boolean isAligning = false;
    private final Comparator<CatalogedLocation> alphaComparator = new Comparator<CatalogedLocation>() {
        /* class com.lavadip.skeye.config.GetAlignmentActivity.C01002 */

        public int compare(CatalogedLocation object1, CatalogedLocation object2) {
            return object1.getName().compareToIgnoreCase(object2.getName());
        }
    };
    private final Map<String, Comparator<CatalogedLocation>> byNameComparators = new HashMap();
    private sortMethod currSortMethod = sortMethod.BY_POSITION;
    private String currTab = null;
    private final Map<String, VisibleObjectAdapter> dataAdapters = new HashMap();
    private final Handler msgHndlr = new Handler() {
        /* class com.lavadip.skeye.config.GetAlignmentActivity.HandlerC01024 */

        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                GetAlignmentActivity.this.sortData();
                Iterator it = GetAlignmentActivity.this.tags.iterator();
                while (it.hasNext()) {
                    String tag = (String) it.next();
                    GetAlignmentActivity.this.dataAdapters.put(tag, new VisibleObjectAdapter((ArrayList) GetAlignmentActivity.this.rawData.get(tag)));
                }
                GetAlignmentActivity.this.createTabs();
                GetAlignmentActivity.this.spinner.setOnItemSelectedListener(GetAlignmentActivity.this.searchListener);
                if (GetAlignmentActivity.this.waitMsg != null && GetAlignmentActivity.this.waitMsg.isShowing()) {
                    try {
                        GetAlignmentActivity.this.waitMsg.cancel();
                        GetAlignmentActivity.this.waitMsg.dismiss();
                    } catch (IllegalArgumentException e) {
                    }
                }
            }
        }
    };
    private final Comparator<CatalogedLocation> posComparator = new Comparator<CatalogedLocation>() {
        /* class com.lavadip.skeye.config.GetAlignmentActivity.C01013 */

        public int compare(CatalogedLocation object1, CatalogedLocation object2) {
            return object1.compareTo(object2) * -1;
        }
    };
    private final Map<String, ArrayList<CatalogedLocation>> rawData = new HashMap();
    private boolean requestSearch = false;
    private final AdapterView.OnItemSelectedListener searchListener = new AdapterView.OnItemSelectedListener() {
        /* class com.lavadip.skeye.config.GetAlignmentActivity.C00941 */

        @Override // android.widget.AdapterView.OnItemSelectedListener
        public void onItemSelected(AdapterView<?> adapterView, View v, int pos, long id) {
            GetAlignmentActivity.this.setSortMethod(sortMethod.values()[pos]);
            GetAlignmentActivity.this.sortData();
            GetAlignmentActivity.this.refreshCursors();
        }

        @Override // android.widget.AdapterView.OnItemSelectedListener
        public void onNothingSelected(AdapterView<?> adapterView) {
        }
    };
    private CatalogedLocation selectedObject = null;
    private CustomSpinner spinner;
    private final Map<String, View> tagViews = new HashMap();
    private final ArrayList<String> tags = new ArrayList<>();
    private CustomDialog waitMsg;

    /* access modifiers changed from: package-private */
    public enum sortMethod {
        BY_NAME,
        BY_POSITION
    }

    /* access modifiers changed from: package-private */
    public final class VisibleObjectAdapter extends BaseAdapter {
        private String filterStr = null;
        private final IntList filteredObjs;
        private final ArrayList<CatalogedLocation> visibles;
        private final StringBuffer workBuffer = new StringBuffer(40);

        /* access modifiers changed from: package-private */
        public void setFilterStr(String newStr) {
            this.filterStr = newStr == null ? null : newStr.toLowerCase();
            updateFilteredObjs();
            notifyDataSetChanged();
        }

        /* access modifiers changed from: private */
        /* access modifiers changed from: public */
        private void updateFilteredObjs() {
            this.filteredObjs.clearList();
            if (this.filterStr != null) {
                int numVisibles = this.visibles.size();
                for (int i = 0; i < numVisibles; i++) {
                    if (this.visibles.get(i).matches(this.filterStr)) {
                        this.filteredObjs.add(i);
                    }
                }
            }
        }

        public VisibleObjectAdapter(ArrayList<CatalogedLocation> visibles2) {
            this.visibles = visibles2;
            this.filteredObjs = new IntList(visibles2.size());
        }

        public boolean hasStableIds() {
            return true;
        }

        public int getCount() {
            return this.filterStr == null ? this.visibles.size() : this.filteredObjs.size;
        }

        private int getActualPos(int pos) {
            return this.filterStr == null ? pos : this.filteredObjs.get(pos);
        }

        public Object getItem(int pos) {
            return this.visibles.get(getActualPos(pos));
        }

        public long getItemId(int pos) {
            return (long) this.visibles.get(getActualPos(pos)).f21id;
        }

        private final class ViewTag {
            final TextView descr;
            final TextView fuzzy_loc;
            final boolean isPortrait;
            final TextView name;

            public ViewTag(TextView name2, TextView descr2, TextView fuzzy_loc2, boolean isPortrait2) {
                this.name = name2;
                this.descr = descr2;
                this.fuzzy_loc = fuzzy_loc2;
                this.isPortrait = isPortrait2;
            }
        }

        public View getView(int pos, View convertView, ViewGroup parent) {
            View resView = convertView;
            if (resView == null || ((ViewTag) resView.getTag()).isPortrait != currentlyPortrait()) {
                resView = View.inflate(parent.getContext(), C0031R.layout.listitem_object, null);
                resView.setTag(new ViewTag((TextView) resView.findViewById(C0031R.C0032id.name_entry), (TextView) resView.findViewById(C0031R.C0032id.descr_entry), (TextView) resView.findViewById(C0031R.C0032id.fuzzy_loc), currentlyPortrait()));
            }
            LocationInSky visible = this.visibles.get(getActualPos(pos));
            ViewTag viewTag = (ViewTag) resView.getTag();
            ((View) viewTag.name.getParent()).setBackgroundColor(visible.altitude > 0.0d ? -1157619200 : 0);
            viewTag.name.setText(visible.getFullName());
            String descr = visible.getDescr();
            viewTag.descr.setVisibility(descr == null ? 8 : 0);
            viewTag.descr.setText(descr);
            viewTag.fuzzy_loc.setText(visible.getFuzzyLocation(true, this.workBuffer));
            return resView;
        }

        private boolean currentlyPortrait() {
            return GetAlignmentActivity.this.getResources().getConfiguration().orientation == 1;
        }
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(C0031R.layout.activity_selectobjectforalign);
        SkEye.setupActivity(this);
        this.requestSearch = getIntent().getBooleanExtra("search", false);
        TextView objHint = (TextView) findViewById(C0031R.C0032id.selectObjectHint);
        if (this.requestSearch) {
            objHint.setHint(getString(C0031R.string.search));
        }
        initSortMethod();
        objHint.addTextChangedListener(new TextWatcher() {
            /* class com.lavadip.skeye.config.GetAlignmentActivity.C01035 */

            public void onTextChanged(CharSequence cs, int start, int before, int count) {
                String s = cs.length() == 0 ? null : cs.toString();
                Iterator it = GetAlignmentActivity.this.tags.iterator();
                while (it.hasNext()) {
                    String tag = (String) it.next();
                    VisibleObjectAdapter adapter = (VisibleObjectAdapter) GetAlignmentActivity.this.dataAdapters.get(tag);
                    adapter.setFilterStr(s);
                    View tagView = (View) GetAlignmentActivity.this.tagViews.get(tag);
                    if (tagView != null) {
                        ((TextView) tagView.findViewById(C0031R.C0032id.tabsCount)).setText("(" + adapter.getCount() + ")");
                    }
                }
            }

            public void afterTextChanged(Editable unused) {
            }

            public void beforeTextChanged(CharSequence unused0, int unused1, int unused2, int unused3) {
            }
        });
        this.spinner = (CustomSpinner) findViewById(C0031R.C0032id.sortChoice);
        this.spinner.setItems(getResources().getTextArray(C0031R.array.sort_choices));
        this.spinner.setCurrPos(this.currSortMethod.ordinal());
        Catalog[] catalogArr = CatalogManager.catalogs;
        for (Catalog c : catalogArr) {
            this.byNameComparators.put(getString(c.tagResId), c.getByNameComparator(this.alphaComparator));
        }
        this.waitMsg = new CustomDialog.Builder(this).setTitle("Please wait...").setContentView(getLayoutInflater().inflate(C0031R.layout.part_progress, (ViewGroup) null)).create();
        this.waitMsg.show();
        new PopulateDataThread(this, null).start();
    }

    private void initSortMethod() {
        setSortMethod(sortMethod.values()[SettingsManager.getAppPrefs(this).getInt(this.requestSearch ? "search_sort_method" : "align_sort_method", (this.requestSearch ? sortMethod.BY_NAME : sortMethod.BY_POSITION).ordinal())]);
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void setSortMethod(sortMethod method) {
        this.currSortMethod = method;
        SettingsManager.getAppPrefs(this).edit().putInt(this.requestSearch ? "search_sort_method" : "align_sort_method", method.ordinal()).commit();
    }

    /* access modifiers changed from: protected */
    public void setPingEnabled(boolean isChecked) {
        SettingsManager.getAppPrefs(this).edit().putBoolean("align_ping_enabled", isChecked).commit();
    }

    public static boolean shouldPingNow(Context context) {
        return isAligning && getPingEnabled(context);
    }

    private static boolean getPingEnabled(Context context) {
        return SettingsManager.getAppPrefs(context).getBoolean("align_ping_enabled", false);
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void sortData() {
        if (!this.rawData.isEmpty()) {
            for (String tag : this.rawData.keySet()) {
                Collections.sort(this.rawData.get(tag), this.currSortMethod == sortMethod.BY_NAME ? this.byNameComparators.get(tag) : this.posComparator);
                VisibleObjectAdapter adapter = this.dataAdapters.get(tag);
                if (adapter != null) {
                    adapter.updateFilteredObjs();
                }
            }
            return;
        }
        Util.showToast(this, "Not initialised");
    }

    private static View makeIndicator(Context context, String tag) {
        View view = LayoutInflater.from(context).inflate(C0031R.layout.tabs_bg, (ViewGroup) null);
        ((TextView) view.findViewById(C0031R.C0032id.tabsText)).setText(tag);
        return view;
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void createTabs() {
        TabHost.TabContentFactory tabMaker = new TabHost.TabContentFactory() {
            /* class com.lavadip.skeye.config.GetAlignmentActivity.C01046 */

            public View createTabContent(String tag) {
                ListView tabView = new ListView(GetAlignmentActivity.this);
                tabView.setAdapter((ListAdapter) GetAlignmentActivity.this.dataAdapters.get(tag));
                tabView.setOnItemClickListener(GetAlignmentActivity.this);
                return tabView;
            }
        };
        final TabHost tabHost = (TabHost) findViewById(C0031R.C0032id.objectTypeTab);
        FlowLayout tabFlowWidget = (FlowLayout) findViewById(C0031R.C0032id.flowTabs);
        tabHost.setup();
        tabHost.getTabWidget().setDividerDrawable(C0031R.drawable.tab_divider);
        Iterator<String> it = this.tags.iterator();
        while (it.hasNext()) {
            final String tag = it.next();
            int elemCount = this.dataAdapters.get(tag).getCount();
            if (elemCount > 0) {
                View indicator = makeIndicator(tabHost.getContext(), tag);
                indicator.setSelected(tag.equals(this.tags.get(0)));
                tabFlowWidget.addView(indicator);
                ((TextView) indicator.findViewById(C0031R.C0032id.tabsCount)).setText("(" + elemCount + ")");
                this.tagViews.put(tag, indicator);
                indicator.setOnClickListener(new View.OnClickListener() {
                    /* class com.lavadip.skeye.config.GetAlignmentActivity.View$OnClickListenerC01057 */

                    public void onClick(View v) {
                        tabHost.setCurrentTabByTag(tag);
                    }
                });
                tabHost.addTab(tabHost.newTabSpec(tag).setContent(tabMaker).setIndicator(new View(this)));
            }
        }
        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            /* class com.lavadip.skeye.config.GetAlignmentActivity.C01068 */

            public void onTabChanged(String tag) {
                GetAlignmentActivity.this.currTab = tag;
                GetAlignmentActivity.this.selectTabView(tag);
            }
        });
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void selectTabView(String tag) {
        for (View view : this.tagViews.values()) {
            view.setSelected(false);
        }
        this.tagViews.get(tag).setSelected(true);
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void refreshCursors() {
        for (VisibleObjectAdapter a : this.dataAdapters.values()) {
            a.notifyDataSetChanged();
        }
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();
    }

    /* access modifiers changed from: protected */
    public void onPause() {
        super.onPause();
    }

    private class PopulateDataThread extends Thread {
        private PopulateDataThread() {
        }

        /* synthetic */ PopulateDataThread(GetAlignmentActivity getAlignmentActivity, PopulateDataThread populateDataThread) {
            this();
        }

        public void run() {
            Catalog[] catalogArr = CatalogManager.catalogs;
            for (Catalog catalog : catalogArr) {
                String catalogTag = GetAlignmentActivity.this.getString(catalog.tagResId);
                if (catalog.searchable && (GetAlignmentActivity.this.requestSearch || catalog.hasPointPos)) {
                    if (GetAlignmentActivity.this.currTab == null) {
                        GetAlignmentActivity.this.currTab = catalogTag;
                    }
                    GetAlignmentActivity.this.tags.add(catalogTag);
                    GetAlignmentActivity.this.rawData.put(catalogTag, Sky.getVisibleObjs(GetAlignmentActivity.this.requestSearch, catalog));
                }
            }
            GetAlignmentActivity.this.msgHndlr.sendEmptyMessage(0);
        }
    }

    @Override // android.widget.AdapterView.OnItemClickListener
    public void onItemClick(AdapterView<?> adapterView, View arg1, int pos, long id) {
        this.selectedObject = (CatalogedLocation) this.dataAdapters.get(this.currTab).getItem(pos);
        if (this.requestSearch) {
            Intent result = new Intent(getIntent());
            result.putExtra("selectedId", this.selectedObject.f21id);
            setResult(-1, result);
            finish();
            return;
        }
        showDialog(0);
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    /* access modifiers changed from: protected */
    public Dialog onCreateDialog(int dialogId) {
        if (dialogId != 0) {
            return null;
        }
        CustomDialog.Builder builder = new CustomDialog.Builder(this);
        View content = getLayoutInflater().inflate(C0031R.layout.dialog_confirm_align, (ViewGroup) null);
        ((TextView) content.findViewById(C0031R.C0032id.align_confirm_text)).setText(Html.fromHtml(String.format(getString(C0031R.string.align_confirm), this.selectedObject.getName())));
        CheckBox checkPing = (CheckBox) content.findViewById(C0031R.C0032id.align_ping_checkbox);
        checkPing.setChecked(getPingEnabled(this));
        checkPing.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            /* class com.lavadip.skeye.config.GetAlignmentActivity.C01079 */

            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                GetAlignmentActivity.this.setPingEnabled(isChecked);
            }
        });
        builder.setContentView(content).setCancelable(true).setPositiveButton(getString(C0031R.string.accept), new DialogInterface.OnClickListener() {
            /* class com.lavadip.skeye.config.GetAlignmentActivity.DialogInterface$OnClickListenerC009510 */

            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
                GetAlignmentActivity.this.acceptAlignment();
            }
        }).setNegativeButton(getString(17039360), (DialogInterface.OnClickListener) null);
        Dialog dlg = builder.create();
        dlg.setOnDismissListener(new DialogInterface.OnDismissListener() {
            /* class com.lavadip.skeye.config.GetAlignmentActivity.DialogInterface$OnDismissListenerC009611 */

            public void onDismiss(DialogInterface dialog) {
                GetAlignmentActivity.this.removeDialog(0);
            }
        });
        dlg.setOnKeyListener(new DialogInterface.OnKeyListener() {
            /* class com.lavadip.skeye.config.GetAlignmentActivity.DialogInterface$OnKeyListenerC009712 */

            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode != 25 || event.getAction() != 0) {
                    return false;
                }
                new ToneGenerator(5, 100).startTone(24);
                GetAlignmentActivity.this.acceptAlignment();
                return true;
            }
        });
        dlg.setOnShowListener(new DialogInterface.OnShowListener() {
            /* class com.lavadip.skeye.config.GetAlignmentActivity.DialogInterface$OnShowListenerC009813 */

            public void onShow(DialogInterface dialog) {
                GetAlignmentActivity.isAligning = true;
            }
        });
        dlg.setOnDismissListener(new DialogInterface.OnDismissListener() {
            /* class com.lavadip.skeye.config.GetAlignmentActivity.DialogInterface$OnDismissListenerC009914 */

            public void onDismiss(DialogInterface dialog) {
                GetAlignmentActivity.isAligning = false;
            }
        });
        return dlg;
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void acceptAlignment() {
        Intent result = new Intent(getIntent());
        result.putExtra("selectedId", this.selectedObject.f21id);
        setResult(-1, result);
        finish();
    }
}
