package com.lavadip.skeye;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TableLayout;
import android.widget.TableRow;

public class ToggleButtonTableLayout extends TableLayout implements View.OnClickListener {
    private RadioButton activeRadioButton;
    private ChangeListener changeListener = null;

    /* access modifiers changed from: package-private */
    public interface ChangeListener {
        void onCheckedChanged(int i);
    }

    public void setChangeListener(ChangeListener listener) {
        this.changeListener = listener;
    }

    public ToggleButtonTableLayout(Context context) {
        super(context);
    }

    public ToggleButtonTableLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void onClick(View v) {
        RadioButton rb = (RadioButton) v;
        if (this.activeRadioButton != null) {
            this.activeRadioButton.setChecked(false);
        }
        rb.setChecked(true);
        this.activeRadioButton = rb;
        if (this.changeListener != null) {
            this.changeListener.onCheckedChanged(this.activeRadioButton.getId());
        }
    }

    @Override // android.widget.TableLayout, android.view.ViewGroup
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        super.addView(child, index, params);
        setChildrenOnClickListener((TableRow) child);
    }

    @Override // android.widget.TableLayout, android.view.ViewGroup
    public void addView(View child, ViewGroup.LayoutParams params) {
        super.addView(child, params);
        setChildrenOnClickListener((TableRow) child);
    }

    private void setChildrenOnClickListener(TableRow tr) {
        int c = tr.getChildCount();
        for (int i = 0; i < c; i++) {
            View v = tr.getChildAt(i);
            if (v instanceof RadioButton) {
                v.setOnClickListener(this);
            }
        }
    }

    public int getCheckedRadioButtonId() {
        if (this.activeRadioButton != null) {
            return this.activeRadioButton.getId();
        }
        return -1;
    }

    public void check(int childId) {
        onClick(findViewById(childId));
    }
}
