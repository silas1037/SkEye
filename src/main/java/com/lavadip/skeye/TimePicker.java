package com.lavadip.skeye;

import android.content.Context;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import java.util.Calendar;

public class TimePicker extends LinearLayout {
    public static final int HOUR_12 = 12;
    public static final int HOUR_24 = 24;
    private Button am_pm;
    private final View.OnClickListener am_pm_listener = new View.OnClickListener() {
        /* class com.lavadip.skeye.TimePicker.View$OnClickListenerC00525 */

        public void onClick(View v) {
            try {
                if (TimePicker.this.cal.get(9) == 0) {
                    TimePicker.this.cal.set(9, 1);
                } else {
                    TimePicker.this.cal.set(9, 0);
                }
                TimePicker.this.sendToDisplay();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    private Calendar cal;
    private int currentTimeFormate = 12;
    private EditText hour_display;
    private Button hour_minus;
    private final View.OnClickListener hour_minus_listener = new View.OnClickListener() {
        /* class com.lavadip.skeye.TimePicker.View$OnClickListenerC00492 */

        public void onClick(View v) {
            TimePicker.this.hour_display.requestFocus();
            try {
                if (TimePicker.this.currentTimeFormate == 12) {
                    TimePicker.this.cal.add(10, -1);
                } else {
                    TimePicker.this.cal.add(11, -1);
                }
                TimePicker.this.sendToDisplay();
            } catch (Exception e) {
                Log.e("", e.toString());
            }
        }
    };
    private Button hour_plus;
    private final View.OnClickListener hour_plus_listener = new View.OnClickListener() {
        /* class com.lavadip.skeye.TimePicker.View$OnClickListenerC00481 */

        public void onClick(View v) {
            TimePicker.this.hour_display.requestFocus();
            try {
                if (TimePicker.this.currentTimeFormate == 12) {
                    TimePicker.this.cal.add(10, 1);
                } else {
                    TimePicker.this.cal.add(11, 1);
                }
                TimePicker.this.sendToDisplay();
            } catch (Exception e) {
                Log.e("", e.toString());
            }
        }
    };
    private final TextWatcher hour_watcher = new TextWatcher() {
        /* class com.lavadip.skeye.TimePicker.C00536 */

        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void afterTextChanged(Editable s) {
            try {
                String str = s.toString();
                if (str.length() > 0) {
                    if (TimePicker.this.currentTimeFormate == 12) {
                        int hour = Integer.parseInt(str);
                        if (hour == 12) {
                            hour = 0;
                        }
                        TimePicker.this.cal.set(10, hour);
                    } else {
                        TimePicker.this.cal.set(11, Integer.parseInt(str));
                    }
                    TimePicker.this.sendToListener();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    private boolean isAMPMVisible = true;
    private TimeWatcher mTimeWatcher = null;
    private EditText min_display;
    private Button min_minus;
    private final View.OnClickListener min_minus_listener = new View.OnClickListener() {
        /* class com.lavadip.skeye.TimePicker.View$OnClickListenerC00514 */

        public void onClick(View v) {
            TimePicker.this.min_display.requestFocus();
            try {
                TimePicker.this.cal.add(12, -1);
                TimePicker.this.sendToDisplay();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    private Button min_plus;
    private final View.OnClickListener min_plus_listener = new View.OnClickListener() {
        /* class com.lavadip.skeye.TimePicker.View$OnClickListenerC00503 */

        public void onClick(View v) {
            TimePicker.this.min_display.requestFocus();
            try {
                TimePicker.this.cal.add(12, 1);
                TimePicker.this.sendToDisplay();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    private final TextWatcher min_watcher = new TextWatcher() {
        /* class com.lavadip.skeye.TimePicker.C00547 */

        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void afterTextChanged(Editable s) {
            try {
                if (s.toString().length() > 0) {
                    TimePicker.this.cal.set(12, Integer.parseInt(s.toString()));
                    TimePicker.this.sendToListener();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    private View myPickerView;

    public interface TimeWatcher {
        void onTimeChanged(int i, int i2, int i3);
    }

    public TimePicker(Context context) {
        super(context);
        init(context);
    }

    private void init(Context mContext) {
        this.myPickerView = ((LayoutInflater) mContext.getSystemService("layout_inflater")).inflate(C0031R.layout.timepicker, (ViewGroup) null);
        addView(this.myPickerView);
        this.hour_plus = (Button) this.myPickerView.findViewById(C0031R.C0032id.hour_plus);
        this.hour_plus.setOnClickListener(this.hour_plus_listener);
        this.hour_display = (EditText) this.myPickerView.findViewById(C0031R.C0032id.hour_display);
        this.hour_display.addTextChangedListener(this.hour_watcher);
        this.hour_minus = (Button) this.myPickerView.findViewById(C0031R.C0032id.hour_minus);
        this.hour_minus.setOnClickListener(this.hour_minus_listener);
        this.min_plus = (Button) this.myPickerView.findViewById(C0031R.C0032id.min_plus);
        this.min_plus.setOnClickListener(this.min_plus_listener);
        this.min_display = (EditText) this.myPickerView.findViewById(C0031R.C0032id.min_display);
        this.min_display.addTextChangedListener(this.min_watcher);
        this.min_minus = (Button) this.myPickerView.findViewById(C0031R.C0032id.min_minus);
        this.min_minus.setOnClickListener(this.min_minus_listener);
        this.am_pm = (Button) this.myPickerView.findViewById(C0031R.C0032id.am_pm);
        this.am_pm.setOnClickListener(this.am_pm_listener);
        this.cal = Calendar.getInstance();
        initData();
        initFilterNumericDigit();
    }

    private void initData() {
        sendToDisplay();
    }

    private void initFilterNumericDigit() {
        try {
            if (this.currentTimeFormate == 12) {
                this.hour_display.setFilters(new InputFilter[]{new InputFilterMinMax(1, 12)});
            } else {
                this.hour_display.setFilters(new InputFilter[]{new InputFilterMinMax(0, 23)});
            }
            this.min_display.setFilters(new InputFilter[]{new InputFilterMinMax(0, 59)});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setTimeChangedListener(TimeWatcher listener) {
        this.mTimeWatcher = listener;
        sendToListener();
    }

    public void removeTimeChangedListener() {
        this.mTimeWatcher = null;
    }

    /* access modifiers changed from: package-private */
    public static class InputFilterMinMax implements InputFilter {
        private final int max;
        private final int min;

        public InputFilterMinMax(int min2, int max2) {
            this.min = min2;
            this.max = max2;
        }

        public InputFilterMinMax(String min2, String max2) {
            this.min = Integer.parseInt(min2);
            this.max = Integer.parseInt(max2);
        }

        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            try {
                if (isInRange(this.min, this.max, Integer.parseInt(String.valueOf(dest.toString()) + source.toString()))) {
                    return null;
                }
                return "";
            } catch (NumberFormatException e) {
            }
        }

        private static boolean isInRange(int a, int b, int c) {
            return b > a ? c >= a && c <= b : c >= b && c <= a;
        }
    }

    public void reset() {
        this.cal = Calendar.getInstance();
        initFilterNumericDigit();
        initData();
        sendToDisplay();
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private synchronized void sendToListener() {
        if (this.mTimeWatcher != null) {
            if (this.currentTimeFormate == 12) {
                this.mTimeWatcher.onTimeChanged(this.cal.get(10), this.cal.get(12), this.cal.get(9));
            } else {
                this.mTimeWatcher.onTimeChanged(this.cal.get(11), this.cal.get(12), -1);
            }
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void sendToDisplay() {
        if (this.currentTimeFormate == 12) {
            int hour = this.cal.get(10);
            if (hour == 0) {
                hour = 12;
            }
            this.hour_display.setText(String.valueOf(hour));
        } else {
            this.hour_display.setText(String.valueOf(this.cal.get(11)));
        }
        this.min_display.setText(String.valueOf(this.cal.get(12)));
        if (!this.isAMPMVisible) {
            return;
        }
        if (this.cal.get(9) == 0) {
            this.am_pm.setText("AM");
        } else {
            this.am_pm.setText("PM");
        }
    }

    public void setCurrentTimeFormate(int currentTimeFormate2) {
        this.currentTimeFormate = currentTimeFormate2;
        if (currentTimeFormate2 == 24) {
            this.isAMPMVisible = false;
            this.am_pm.setVisibility(8);
        }
        initFilterNumericDigit();
        sendToDisplay();
    }

    public int getCurrentTimeFormate() {
        return this.currentTimeFormate;
    }

    public void setAMPMVisible(boolean isAMPMVisible2) {
        this.isAMPMVisible = isAMPMVisible2;
        if (!isAMPMVisible2) {
            this.am_pm.setVisibility(8);
        }
    }

    public void setCalender(Calendar c) {
        this.cal = c;
        initFilterNumericDigit();
        sendToDisplay();
    }
}
