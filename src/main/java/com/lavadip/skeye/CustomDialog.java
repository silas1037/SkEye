package com.lavadip.skeye;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.PorterDuff;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class CustomDialog extends Dialog {
    public CustomDialog(Context context, int theme) {
        super(context, theme);
    }

    public CustomDialog(Context context) {
        super(context);
    }

    public void setTitle(String title) {
        TextView dialogTitleView = (TextView) findViewById(C0031R.C0032id.title);
        dialogTitleView.setText(title);
        dialogTitleView.setVisibility(0);
    }

    public void setTitleIcon(int imgResId) {
        ((TextView) findViewById(C0031R.C0032id.title)).setCompoundDrawablesWithIntrinsicBounds(imgResId, 0, 0, 0);
    }

    public void setTitleButtonIcon(int imgResId, View.OnClickListener clickListener) {
        Button titleButtonView = (Button) findViewById(C0031R.C0032id.titleButton);
        titleButtonView.setBackgroundColor(0);
        titleButtonView.setCompoundDrawablesWithIntrinsicBounds(imgResId, 0, 0, 0);
        titleButtonView.setVisibility(0);
        titleButtonView.setOnClickListener(clickListener);
    }

    public void hideTitleButtonIcon() {
        Button titleButtonView = (Button) findViewById(C0031R.C0032id.titleButton);
        titleButtonView.setVisibility(8);
        titleButtonView.setOnClickListener(null);
    }

    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        NightModeMgr.setThemeForDialog(this, getWindow().getDecorView());
    }

    public static Dialog createMessageDialog(Context context, String title, String message) {
        return new Builder(context).setIcon(17301543).setTitle(title).setMessage(message).setPositiveButton(context.getString(17039370), (DialogInterface.OnClickListener) null).create();
    }

    public static Dialog createMessageDialog(Context context, int titleId, int messageId) {
        return createMessageDialog(context, context.getString(titleId), context.getString(messageId));
    }

    public static class Builder {
        private boolean cancelable = true;
        private int checkedItem;
        private View contentView;
        private final Context context;
        private int imageRes = 0;
        private DialogInterface.OnClickListener itemClickListener;
        private CharSequence[] items;
        private CharSequence message;
        private DialogInterface.OnClickListener negativeButtonClickListener;
        private CharSequence negativeButtonText;
        private DialogInterface.OnClickListener neutralButtonClickListener;
        private CharSequence neutralButtonText;
        private DialogInterface.OnCancelListener onCancelListener;
        private DialogInterface.OnClickListener positiveButtonClickListener;
        private CharSequence positiveButtonText;
        private CharSequence title;

        public Builder(Context context2) {
            this.context = context2;
        }

        public Builder setCancelable(boolean cancelable2) {
            this.cancelable = cancelable2;
            return this;
        }

        public Builder setIcon(int resource) {
            this.imageRes = resource;
            return this;
        }

        public Builder setMessage(CharSequence message2) {
            this.message = message2;
            return this;
        }

        public Builder setMessage(int message2) {
            this.message = this.context.getText(message2);
            return this;
        }

        public Builder setTitle(int title2) {
            this.title = this.context.getText(title2);
            return this;
        }

        public Builder setTitle(CharSequence title2) {
            this.title = title2;
            return this;
        }

        public Builder setContentView(View v) {
            this.contentView = v;
            return this;
        }

        public Builder setPositiveButton(int positiveButtonText2, DialogInterface.OnClickListener listener) {
            this.positiveButtonText = this.context.getText(positiveButtonText2);
            this.positiveButtonClickListener = listener;
            return this;
        }

        public Builder setPositiveButton(CharSequence positiveButtonText2, DialogInterface.OnClickListener listener) {
            this.positiveButtonText = positiveButtonText2;
            this.positiveButtonClickListener = listener;
            return this;
        }

        public Builder setNeutralButton(int neutralButtonText2, DialogInterface.OnClickListener listener) {
            this.neutralButtonText = this.context.getText(neutralButtonText2);
            this.neutralButtonClickListener = listener;
            return this;
        }

        public Builder setNeutralButton(CharSequence neutralButtonText2, DialogInterface.OnClickListener listener) {
            this.neutralButtonText = neutralButtonText2;
            this.neutralButtonClickListener = listener;
            return this;
        }

        public Builder setNegativeButton(int negativeButtonText2, DialogInterface.OnClickListener listener) {
            this.negativeButtonText = this.context.getText(negativeButtonText2);
            this.negativeButtonClickListener = listener;
            return this;
        }

        public Builder setNegativeButton(CharSequence negativeButtonText2, DialogInterface.OnClickListener listener) {
            this.negativeButtonText = negativeButtonText2;
            this.negativeButtonClickListener = listener;
            return this;
        }

        public CustomDialog create() {
            final CustomDialog dialog = new CustomDialog(this.context, C0031R.style.Dialog);
            dialog.setCancelable(this.cancelable);
            View.OnClickListener dismisser = new View.OnClickListener() {
                /* class com.lavadip.skeye.CustomDialog.Builder.View$OnClickListenerC00101 */

                public void onClick(View v) {
                    dialog.dismiss();
                }
            };
            View layout = ((LayoutInflater) this.context.getSystemService("layout_inflater")).inflate(C0031R.layout.dialog, (ViewGroup) null);
            dialog.addContentView(layout, new ViewGroup.LayoutParams(-1, -2));
            TextView titleView = (TextView) layout.findViewById(C0031R.C0032id.title);
            if (this.title != null) {
                titleView.setText(this.title);
                if (this.imageRes != 0) {
                    titleView.setCompoundDrawablesWithIntrinsicBounds(this.imageRes, 0, 0, 0);
                }
            } else {
                titleView.setVisibility(8);
            }
            Button positiveButton = (Button) layout.findViewById(C0031R.C0032id.positiveButton);
            positiveButton.getBackground().setColorFilter(-5570646, PorterDuff.Mode.MULTIPLY);
            if (this.positiveButtonText != null) {
                positiveButton.setText(this.positiveButtonText);
                if (this.positiveButtonClickListener != null) {
                    positiveButton.setOnClickListener(new View.OnClickListener() {
                        /* class com.lavadip.skeye.CustomDialog.Builder.View$OnClickListenerC00112 */

                        public void onClick(View v) {
                            Builder.this.positiveButtonClickListener.onClick(dialog, -1);
                            dialog.dismiss();
                        }
                    });
                } else {
                    positiveButton.setOnClickListener(dismisser);
                }
            } else {
                positiveButton.setVisibility(8);
            }
            Button neutralButton = (Button) layout.findViewById(C0031R.C0032id.neutralButton);
            if (this.neutralButtonText != null) {
                neutralButton.setText(this.neutralButtonText);
                neutralButton.setOnClickListener(new View.OnClickListener() {
                    /* class com.lavadip.skeye.CustomDialog.Builder.View$OnClickListenerC00123 */

                    public void onClick(View v) {
                        if (Builder.this.neutralButtonClickListener != null) {
                            Builder.this.neutralButtonClickListener.onClick(dialog, -1);
                        }
                        dialog.dismiss();
                    }
                });
            } else {
                neutralButton.setVisibility(8);
            }
            Button negativeButton = (Button) layout.findViewById(C0031R.C0032id.negativeButton);
            negativeButton.getBackground().setColorFilter(-21846, PorterDuff.Mode.MULTIPLY);
            if (this.negativeButtonText != null) {
                negativeButton.setText(this.negativeButtonText);
                if (this.negativeButtonClickListener != null) {
                    negativeButton.setOnClickListener(new View.OnClickListener() {
                        /* class com.lavadip.skeye.CustomDialog.Builder.View$OnClickListenerC00134 */

                        public void onClick(View v) {
                            Builder.this.negativeButtonClickListener.onClick(dialog, -2);
                        }
                    });
                } else {
                    negativeButton.setOnClickListener(dismisser);
                }
            } else {
                negativeButton.setVisibility(8);
            }
            if (this.message != null) {
                TextView messageView = (TextView) layout.findViewById(C0031R.C0032id.message);
                messageView.setText(this.message);
                messageView.setMovementMethod(LinkMovementMethod.getInstance());
            } else if (this.items != null) {
                ListView lv = new ListView(this.context);
                lv.setChoiceMode(1);
                lv.setAdapter((ListAdapter) new ArrayAdapter<>(this.context, C0031R.layout.select_dialog_singlechoice, 16908308, this.items));
                lv.setItemChecked(this.checkedItem, true);
                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    /* class com.lavadip.skeye.CustomDialog.Builder.C00145 */

                    @Override // android.widget.AdapterView.OnItemClickListener
                    public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {
                        Builder.this.itemClickListener.onClick(dialog, pos);
                    }
                });
                ((ViewGroup) layout.findViewById(C0031R.C0032id.content)).removeAllViews();
                ((ViewGroup) layout.findViewById(C0031R.C0032id.content)).addView(lv, new ViewGroup.LayoutParams(-1, -1));
            } else if (this.contentView != null) {
                ((ViewGroup) layout.findViewById(C0031R.C0032id.content)).removeAllViews();
                ((ViewGroup) layout.findViewById(C0031R.C0032id.content)).addView(this.contentView, new ViewGroup.LayoutParams(-1, -1));
            }
            if (this.onCancelListener != null) {
                dialog.setOnCancelListener(this.onCancelListener);
            }
            dialog.setContentView(layout);
            dialog.setCanceledOnTouchOutside(false);
            return dialog;
        }

        public Builder setOnCancelListener(DialogInterface.OnCancelListener onCancelListener2) {
            this.onCancelListener = onCancelListener2;
            return this;
        }

        public Builder setSingleChoiceItems(CharSequence[] items2, int checkedItem2, DialogInterface.OnClickListener onClickListener) {
            this.items = items2;
            this.checkedItem = checkedItem2;
            this.itemClickListener = onClickListener;
            return this;
        }
    }

    public void replaceContent(View view) {
        ((ViewGroup) findViewById(C0031R.C0032id.content)).removeAllViews();
        ((ViewGroup) findViewById(C0031R.C0032id.content)).addView(view, new ViewGroup.LayoutParams(-1, -1));
    }
}
