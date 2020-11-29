package com.lavadip.skeye;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import com.lavadip.skeye.ToggleButtonTableLayout;
import com.lavadip.skeye.catalog.Catalog;
import com.lavadip.skeye.catalog.IntersectionResult;
import com.lavadip.skeye.catalog.Selection;
import com.lavadip.skeye.view.RangeFilterView;
import java.util.HashMap;
import java.util.Map;

public final class CatalogFilterDialog extends Dialog implements RangeFilterView.MarkChangeListener {
    private final Map<Integer, RadioButton> buttonMap = new HashMap();
    private final Catalog catalog;
    private int currType = -1;
    private final RangeFilterView filterView1;
    private final RangeFilterView filterView2;
    private final Thread markChangeProcessor = new Thread() {
        /* class com.lavadip.skeye.CatalogFilterDialog.C00061 */

        public void run() {
            while (true) {
                try {
                    SelectionMessage selectionMsg = CatalogFilterDialog.this.selectionQueue.get();
                    int viewID = selectionMsg.viewID;
                    float leftMarkScaled = selectionMsg.leftSel;
                    float rightMarkScaled = selectionMsg.rightSel;
                    Selection currSel = (Selection) CatalogFilterDialog.this.selectionMap.get(Integer.valueOf(CatalogFilterDialog.this.currType));
                    boolean isA = viewID == C0031R.C0032id.filterView1;
                    IntersectionResult intersectedValues = currSel.setMarks(isA, leftMarkScaled, rightMarkScaled);
                    Bundle bundle = new Bundle();
                    bundle.putFloatArray("filteredValues", intersectedValues.filteredValues);
                    Message msg = CatalogFilterDialog.this.statusUpdater.obtainMessage(isA ? 0 : 1, intersectedValues.countCommon, currSel.selectedIds.size);
                    msg.setData(bundle);
                    msg.sendToTarget();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    return;
                }
            }
        }
    };
    private final Map<Integer, Selection> selectionMap;
    final MessageBox<SelectionMessage> selectionQueue = new MessageBox<>();
    private final Handler statusUpdater = new Handler() {
        /* class com.lavadip.skeye.CatalogFilterDialog.HandlerC00072 */

        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                CatalogFilterDialog.this.filterView2.setOverlayData(msg.getData().getFloatArray("filteredValues"));
            } else {
                CatalogFilterDialog.this.filterView1.setOverlayData(msg.getData().getFloatArray("filteredValues"));
            }
            CatalogFilterDialog.this.updateSelectedCount(CatalogFilterDialog.this.currType);
            CatalogFilterDialog.this.updateTotal();
        }
    };
    private final TextView titleView;

    public CatalogFilterDialog(final Context context, int theme, Catalog catalogParam) {
        super(context, theme);
        this.catalog = catalogParam;
        getWindow().setFlags(1024, 1024);
        setContentView(C0031R.layout.catalog_filter_dialog);
        this.titleView = (TextView) findViewById(C0031R.C0032id.titleText);
        this.filterView1 = (RangeFilterView) findViewById(C0031R.C0032id.filterView1);
        this.filterView2 = (RangeFilterView) findViewById(C0031R.C0032id.filterView2);
        this.selectionMap = catalogParam.getSelectionMap(context.getApplicationContext());
        int[] iArr = CatalogActivity.objectTypeResourceIds;
        for (int id : iArr) {
            this.buttonMap.put(Integer.valueOf(id), (RadioButton) findViewById(id));
            updateSelectedCount(id);
        }
        updateTotal();
        ((Button) findViewById(C0031R.C0032id.saveButton)).setOnClickListener(new View.OnClickListener() {
            /* class com.lavadip.skeye.CatalogFilterDialog.View$OnClickListenerC00083 */

            public void onClick(View v) {
                Context appCtxt = context.getApplicationContext();
                for (Map.Entry<Integer, Selection> entry : CatalogFilterDialog.this.selectionMap.entrySet()) {
                    entry.getValue().saveCurrent(appCtxt);
                }
                CatalogFilterDialog.this.dismiss();
            }
        });
        ToggleButtonTableLayout rg = (ToggleButtonTableLayout) findViewById(C0031R.C0032id.objTypeChoice);
        rg.setChangeListener(new ToggleButtonTableLayout.ChangeListener() {
            /* class com.lavadip.skeye.CatalogFilterDialog.C00094 */

            @Override // com.lavadip.skeye.ToggleButtonTableLayout.ChangeListener
            public void onCheckedChanged(int checkedId) {
                boolean useSBr;
                boolean z = true;
                CatalogFilterDialog.this.currType = checkedId;
                Selection currSel = (Selection) CatalogFilterDialog.this.selectionMap.get(Integer.valueOf(CatalogFilterDialog.this.currType));
                if (checkedId != C0031R.C0032id.valuesOpenClusters) {
                    useSBr = true;
                } else {
                    useSBr = false;
                }
                CatalogFilterDialog.this.filterView1.reset();
                CatalogFilterDialog.this.filterView2.reset();
                RangeFilterView rangeFilterView = CatalogFilterDialog.this.filterView1;
                int[] iArr = currSel.sortedIdsA;
                float[] fArr = currSel.valuesA;
                float f = currSel.leftMarkA;
                float f2 = currSel.rightMarkA;
                String str = useSBr ? "Surf Br" : "Magnitude";
                if (currSel.valuesA.length >= 1000) {
                    z = false;
                }
                rangeFilterView.setData(iArr, fArr, f, f2, str, z, 30);
                CatalogFilterDialog.this.filterView2.setData(currSel.sortedIdsB, currSel.valuesB, currSel.leftMarkB, currSel.rightMarkB, "Size", false, 35);
                CatalogFilterDialog.this.filterView1.setMarkChangeListener(CatalogFilterDialog.this);
                CatalogFilterDialog.this.filterView2.setMarkChangeListener(CatalogFilterDialog.this);
            }
        });
        rg.check(C0031R.C0032id.valuesGal);
        this.markChangeProcessor.start();
    }

    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        NightModeMgr.setThemeForDialog(this, getWindow().getDecorView());
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void updateSelectedCount(int type) {
        this.buttonMap.get(Integer.valueOf(type)).setText(String.valueOf(CatalogActivity.typeNames.get(Integer.valueOf(type))) + "\n" + this.selectionMap.get(Integer.valueOf(type)).selectedCount);
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void updateTotal() {
        int totalCount = 0;
        for (Map.Entry<Integer, Selection> sel : this.selectionMap.entrySet()) {
            totalCount += sel.getValue().selectedCount;
        }
        this.titleView.setText(String.valueOf(getContext().getString(this.catalog.tagResId)) + " selected: " + totalCount);
    }

    final class SelectionMessage {
        final float leftSel;
        final float rightSel;
        final int viewID;

        SelectionMessage(int viewID2, float left, float right) {
            this.viewID = viewID2;
            this.leftSel = left;
            this.rightSel = right;
        }

        public String toString() {
            return "SelectionMessage for view: " + this.viewID + " leftSel: " + this.leftSel + ", rightSel: " + this.rightSel;
        }
    }

    final class MessageBox<T> {
        T elem0;
        T elem1;
        final Object notifier = new Object();
        final Object readLock = new Object();

        MessageBox() {
        }

        /* access modifiers changed from: package-private */
        public T get() throws InterruptedException {
            synchronized (this.readLock) {
                if (this.elem0 == null && this.elem1 == null) {
                    synchronized (this.notifier) {
                        this.notifier.wait();
                    }
                }
            }
            synchronized (this) {
                if (this.elem0 != null) {
                    T curr = this.elem0;
                    this.elem0 = null;
                    return curr;
                }
                T curr2 = this.elem1;
                this.elem1 = null;
                return curr2;
            }
        }

        /* access modifiers changed from: package-private */
        public void put0(T e) {
            synchronized (this) {
                synchronized (this.notifier) {
                    this.notifier.notify();
                }
                synchronized (this.readLock) {
                    this.elem0 = e;
                }
            }
        }

        /* access modifiers changed from: package-private */
        public void put1(T e) {
            synchronized (this) {
                synchronized (this.notifier) {
                    this.notifier.notify();
                }
                synchronized (this.readLock) {
                    this.elem1 = e;
                }
            }
        }
    }

    @Override // com.lavadip.skeye.view.RangeFilterView.MarkChangeListener
    public void onMarkChanged(int viewID, float leftMarkScaled, float rightMarkScaled) {
        if (viewID == C0031R.C0032id.filterView1) {
            this.selectionQueue.put0(new SelectionMessage(viewID, leftMarkScaled, rightMarkScaled));
        } else {
            this.selectionQueue.put1(new SelectionMessage(viewID, leftMarkScaled, rightMarkScaled));
        }
    }
}
