package com.lavadip.skeye.view;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.AdapterView;
import android.widget.Button;
import com.lavadip.skeye.C0031R;
import com.lavadip.skeye.CustomDialog;

public final class CustomSpinner extends Button {
    private int mCurrPos = 0;
    private CharSequence[] mItems;
    private String mPrompt;
    private AdapterView.OnItemSelectedListener onItemSelectedListener;

    public CustomSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
        setBackgroundResource(C0031R.drawable.btn_dropdown);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, C0031R.styleable.com_lavadip_skeye_view_CustomSpinner);
        this.mPrompt = a.getString(C0031R.styleable.com_lavadip_skeye_view_CustomSpinner_android_prompt);
        a.recycle();
    }

    private void updateSummary() {
        if (this.mItems != null) {
            setText(this.mItems[getCurrPos()]);
        }
    }

    public boolean performClick() {
        CustomDialog.Builder builder = new CustomDialog.Builder(getContext());
        if (this.mPrompt != null) {
            builder.setTitle(this.mPrompt);
        }
        builder.setSingleChoiceItems(this.mItems, getCurrPos(), new DialogInterface.OnClickListener() {
            /* class com.lavadip.skeye.view.CustomSpinner.DialogInterface$OnClickListenerC01351 */

            public void onClick(DialogInterface dialog, int which) {
                CustomSpinner.this.setCurrPos(which);
                if (CustomSpinner.this.onItemSelectedListener != null) {
                    CustomSpinner.this.onItemSelectedListener.onItemSelected(null, null, which, (long) which);
                }
                dialog.dismiss();
            }
        });
        builder.create().show();
        return true;
    }

    public int getCurrPos() {
        return this.mCurrPos;
    }

    public void setCurrPos(int mCurrPos2) {
        this.mCurrPos = mCurrPos2;
        updateSummary();
    }

    public CharSequence[] getItems() {
        return this.mItems;
    }

    public void setItems(CharSequence[] items) {
        this.mItems = items;
        updateSummary();
    }

    public void setOnItemSelectedListener(AdapterView.OnItemSelectedListener onItemSelectedListener2) {
        this.onItemSelectedListener = onItemSelectedListener2;
    }
}
