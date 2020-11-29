package com.lavadip.skeye;

import android.app.Activity;
import android.content.DialogInterface;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.lavadip.skeye.CustomDialog;
import com.lavadip.skeye.TimePicker;
import com.lavadip.skeye.calendar.CalendarView;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import org.apache.commons.math3.random.EmpiricalDistribution;

/* access modifiers changed from: package-private */
public final class TimeMgr implements View.OnClickListener {
    private static final long FAST_SKY_UPDATE_INTERVAL = 1000;
    private static final int MAX_SPEED_INDEX = (speeds.length - 1);
    private static final long SKY_UPDATE_INTERVAL = 1000;
    private static final String[] speedTitles = {"", "1s", "2s", "5s", "15s", "30s", "1m", "5m", "15m", "1h", "4h", "1d"};
    private static final int[] speeds;
    private Activity activity;
    private Button btnDate;
    private View btnJump;
    private Button btnTime;
    private int currDirection = 1;
    private int currSpeed = 1;
    private int currSpeedIndex = 1;
    private DateFormat dateFormat;
    private boolean isTravelling = false;
    private boolean minimised = false;
    private View panelTimeBarHeader;
    private long refTime = 0;
    private long refreshInterval = 1000;
    private long simRefTime = 0;
    private DateFormat timeFastFormat;
    private DateFormat timeFormat;
    private TextView txtFwd;
    private TextView txtRew;

    TimeMgr() {
    }

    /* access modifiers changed from: package-private */
    public void cancelTravel() {
        this.currSpeed = 1;
        this.currSpeedIndex = 1;
        this.currDirection = 1;
        this.isTravelling = false;
        this.refreshInterval = 1000;
        updateLabels();
    }

    /* access modifiers changed from: protected */
    public synchronized void setTime(long time) {
        this.isTravelling = true;
        this.simRefTime = time;
        this.refTime = System.currentTimeMillis();
        updateLabels();
    }

    static {
        int[] iArr = new int[12];
        iArr[1] = 1;
        iArr[2] = 2;
        iArr[3] = 5;
        iArr[4] = 15;
        iArr[5] = 30;
        iArr[6] = 60;
        iArr[7] = 300;
        iArr[8] = 900;
        iArr[9] = 3600;
        iArr[10] = 14400;
        iArr[11] = 86400;
        speeds = iArr;
    }

    private synchronized void increaseSpeed() {
        int nextIndex = Math.min(this.currSpeedIndex + this.currDirection, MAX_SPEED_INDEX);
        int nextAbsIndex = nextIndex;
        if (nextIndex < 0) {
            nextAbsIndex *= -1;
            this.currDirection = 1;
        }
        this.currSpeedIndex = nextAbsIndex;
        setSpeed(this.currDirection * speeds[this.currSpeedIndex]);
    }

    private synchronized void decreaseSpeed() {
        int nextIndex = Math.min(this.currSpeedIndex - this.currDirection, MAX_SPEED_INDEX);
        int nextAbsIndex = nextIndex;
        if (nextIndex < 0) {
            nextAbsIndex *= -1;
            this.currDirection = -1;
        }
        this.currSpeedIndex = nextAbsIndex;
        setSpeed(this.currDirection * speeds[this.currSpeedIndex]);
    }

    private synchronized void pauseTime() {
        this.currSpeedIndex = 0;
        setSpeed(0);
    }

    private synchronized void setSpeed(int newSpeed) {
        long j;
        this.simRefTime = getCurrentTime();
        this.refTime = System.currentTimeMillis();
        this.currSpeed = newSpeed;
        this.isTravelling = true;
        int absSpeed = Math.abs(newSpeed);
        if (absSpeed > 4) {
            j = (long) (absSpeed > 128 ? 100 : 300);
        } else {
            j = 1000;
        }
        this.refreshInterval = j;
        updateLabels();
    }

    private void updateLabels() {
        updateControls();
        if (!this.isTravelling) {
            this.txtFwd.setText(String.format("%3dx", Integer.valueOf(this.currSpeed)));
            this.txtRew.setText("    ");
            this.btnJump.setVisibility(4);
            this.panelTimeBarHeader.setBackgroundColor(this.activity.getResources().getColor(C0031R.color.timebar_header_bg));
            this.panelTimeBarHeader.findViewById(C0031R.C0032id.btn_timebar_close).setVisibility(0);
            return;
        }
        this.panelTimeBarHeader.findViewById(C0031R.C0032id.btn_timebar_close).setVisibility(8);
        this.panelTimeBarHeader.setBackgroundColor(this.activity.getResources().getColor(C0031R.color.timebar_header_active_bg));
        if (this.currSpeedIndex == 0) {
            this.txtRew.setText(" ├  ");
            this.txtFwd.setText("  ┤ ");
            return;
        }
        String title = String.format("%3s", speedTitles[this.currSpeedIndex]);
        if (this.currDirection < 0) {
            this.txtRew.setText(title);
            this.txtFwd.setText("    ");
            return;
        }
        this.txtFwd.setText(title);
        this.txtRew.setText("    ");
    }

    private void updateControls() {
        int i;
        int i2;
        int i3;
        int i4 = 0;
        this.panelTimeBarHeader.findViewById(C0031R.C0032id.btn_timebar_minimise).setVisibility(this.minimised ? 8 : 0);
        View findViewById = this.panelTimeBarHeader.findViewById(C0031R.C0032id.btn_timebar_maximise);
        if (this.minimised) {
            i = 0;
        } else {
            i = 8;
        }
        findViewById.setVisibility(i);
        View view = this.btnJump;
        if (this.minimised) {
            i2 = 8;
        } else {
            i2 = 0;
        }
        view.setVisibility(i2);
        View findViewById2 = this.panelTimeBarHeader.findViewById(C0031R.C0032id.btn_timebar_pause);
        if (this.minimised) {
            i3 = 8;
        } else {
            i3 = 0;
        }
        findViewById2.setVisibility(i3);
        View findViewById3 = this.panelTimeBarHeader.findViewById(C0031R.C0032id.txt_timebar_summary);
        if (!this.minimised) {
            i4 = 8;
        }
        findViewById3.setVisibility(i4);
    }

    /* access modifiers changed from: package-private */
    public long getCurrentTime() {
        if (!this.isTravelling) {
            return System.currentTimeMillis();
        }
        long timeMod = (long) (Math.abs(this.currSpeed) == 86400 ? EmpiricalDistribution.DEFAULT_BIN_COUNT : 500);
        return this.simRefTime + (((long) this.currSpeed) * ((System.currentTimeMillis() - this.refTime) / timeMod) * timeMod);
    }

    public void onClick(View v) {
        int id = v.getId();
        if (id == C0031R.C0032id.txt_timebar_rew) {
            decreaseSpeed();
        } else if (id == C0031R.C0032id.txt_timebar_fwd) {
            increaseSpeed();
        } else if (id == C0031R.C0032id.btn_timebar_time) {
            slowDown();
            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date(getCurrentTime()));
            TimePicker timeView = new TimePicker(this.activity);
            timeView.setCalender(cal);
            TimeDialogMgr timeDlgMgr = new TimeDialogMgr();
            timeView.setTimeChangedListener(timeDlgMgr);
            new CustomDialog.Builder(this.activity).setTitle("Set Time").setContentView(timeView).setPositiveButton(C0031R.string.set_time, timeDlgMgr).setNegativeButton(17039360, new DialogInterface.OnClickListener() {
                /* class com.lavadip.skeye.TimeMgr.DialogInterface$OnClickListenerC00451 */

                public void onClick(DialogInterface d, int arg1) {
                    d.cancel();
                }
            }).create().show();
        } else if (id == C0031R.C0032id.btn_timebar_date) {
            slowDown();
            Calendar cal2 = Calendar.getInstance();
            cal2.setTime(new Date(getCurrentTime()));
            CalendarView calView = new CalendarView(this.activity, cal2.get(1), cal2.get(2), cal2.get(5), false);
            CustomDialog dialog = new CustomDialog.Builder(this.activity).setContentView(calView).setNegativeButton(17039360, new DialogInterface.OnClickListener() {
                /* class com.lavadip.skeye.TimeMgr.DialogInterface$OnClickListenerC00462 */

                public void onClick(DialogInterface d, int arg1) {
                    d.cancel();
                }
            }).create();
            calView.setDateChangedListener(new CalendarView.DateWatcher(cal2, dialog) {
                /* class com.lavadip.skeye.TimeMgr.C00473 */
                final Calendar mCal;
                private final /* synthetic */ CustomDialog val$dialog;

                {
                    this.val$dialog = r4;
                    this.mCal = (Calendar) r3.clone();
                }

                @Override // com.lavadip.skeye.calendar.CalendarView.DateWatcher
                public void onDateChanged(int y, int m, int d) {
                    this.mCal.set(y, m, d);
                    TimeMgr.this.setTime(this.mCal.getTime().getTime());
                    this.val$dialog.dismiss();
                }
            });
            dialog.show();
        } else if (id == C0031R.C0032id.btn_timebar_pause) {
            pauseTime();
        } else if (id == C0031R.C0032id.btn_timebar_close) {
            this.activity.findViewById(C0031R.C0032id.timebar).setVisibility(8);
        } else if (id == C0031R.C0032id.btn_timebar_minimise) {
            this.minimised = true;
            this.activity.findViewById(C0031R.C0032id.panel_timemachine_controls).setVisibility(8);
            updateLabels();
        } else if (id == C0031R.C0032id.btn_timebar_maximise) {
            this.minimised = false;
            this.activity.findViewById(C0031R.C0032id.panel_timemachine_controls).setVisibility(0);
            updateLabels();
        } else if (id == C0031R.C0032id.btn_timebar_jump) {
            cancelTravel();
        }
    }

    /* access modifiers changed from: package-private */
    public long getSkyRefreshInterval() {
        return this.refreshInterval;
    }

    /* access modifiers changed from: package-private */
    public void updateDisplay(Date skyDate) {
        String formattedDate = this.dateFormat.format(skyDate);
        this.btnDate.setText(formattedDate);
        this.btnTime.setText(((!this.isTravelling || Math.abs(this.currSpeed) <= 16) ? this.timeFormat : this.timeFastFormat).format(skyDate));
        ((TextView) this.panelTimeBarHeader.findViewById(C0031R.C0032id.txt_timebar_summary)).setText(String.valueOf(formattedDate) + ", " + this.timeFormat.format(skyDate) + formatTimeZone(skyDate));
    }

    private static String formatTimeZone(Date skyDate) {
        int offsetMinutes = -skyDate.getTimezoneOffset();
        int offsetAbsHours = Math.abs(offsetMinutes / 60);
        return String.valueOf(offsetMinutes >= 0 ? " +" : " -") + offsetAbsHours + ":" + (Math.abs(offsetMinutes) - (offsetAbsHours * 60));
    }

    final class TimeDialogMgr implements TimePicker.TimeWatcher, DialogInterface.OnClickListener {
        private int newHour = 0;
        private int newMinute = 0;
        private int newSecond = 0;

        TimeDialogMgr() {
        }

        @Override // com.lavadip.skeye.TimePicker.TimeWatcher
        public void onTimeChanged(int h, int m, int am_pm) {
            this.newHour = (am_pm * 12) + h;
            this.newMinute = m;
            this.newSecond = 0;
        }

        public void onClick(DialogInterface arg0, int arg1) {
            Date d = new Date(TimeMgr.this.getCurrentTime());
            d.setHours(this.newHour);
            d.setMinutes(this.newMinute);
            d.setSeconds(this.newSecond);
            TimeMgr.this.setTime(d.getTime());
        }
    }

    /* access modifiers changed from: package-private */
    public void showTimeBar() {
        this.minimised = false;
        updateControls();
        this.activity.findViewById(C0031R.C0032id.timebar).setVisibility(0);
        this.activity.findViewById(C0031R.C0032id.panel_timemachine_controls).setVisibility(0);
    }

    /* access modifiers changed from: package-private */
    public void slowDown() {
        if (this.currSpeedIndex > 4) {
            this.currSpeedIndex = 4;
            setSpeed(this.currDirection * speeds[this.currSpeedIndex]);
        }
    }

    public void updateViews(Activity activity2) {
        this.activity = activity2;
        this.txtRew = (TextView) activity2.findViewById(C0031R.C0032id.txt_timebar_rew);
        this.txtFwd = (TextView) activity2.findViewById(C0031R.C0032id.txt_timebar_fwd);
        this.btnJump = activity2.findViewById(C0031R.C0032id.btn_timebar_jump);
        this.panelTimeBarHeader = activity2.findViewById(C0031R.C0032id.timebarHeader);
        this.btnDate = (Button) activity2.findViewById(C0031R.C0032id.btn_timebar_date);
        this.btnTime = (Button) activity2.findViewById(C0031R.C0032id.btn_timebar_time);
        this.txtRew.setOnClickListener(this);
        this.txtFwd.setOnClickListener(this);
        this.btnDate.setOnClickListener(this);
        this.btnTime.setOnClickListener(this);
        this.btnJump.setOnClickListener(this);
        this.panelTimeBarHeader.findViewById(C0031R.C0032id.btn_timebar_close).setOnClickListener(this);
        this.panelTimeBarHeader.findViewById(C0031R.C0032id.btn_timebar_pause).setOnClickListener(this);
        this.panelTimeBarHeader.findViewById(C0031R.C0032id.btn_timebar_minimise).setOnClickListener(this);
        this.panelTimeBarHeader.findViewById(C0031R.C0032id.btn_timebar_maximise).setOnClickListener(this);
        this.dateFormat = android.text.format.DateFormat.getMediumDateFormat(activity2);
        this.timeFormat = new SimpleDateFormat("hh:mm:ss a", Locale.US);
        this.timeFastFormat = new SimpleDateFormat("hh:mm a", Locale.US);
        updateLabels();
        updateDisplay(new Date(getCurrentTime()));
    }
}
