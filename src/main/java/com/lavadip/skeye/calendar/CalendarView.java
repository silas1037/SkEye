package com.lavadip.skeye.calendar;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;
import com.lavadip.skeye.C0031R;
import com.lavadip.skeye.CustomDialog;
import com.lavadip.skeye.Vector3d;
import com.lavadip.skeye.astro.Sky;
import com.lavadip.skeye.astro.ephemeris.Ephemeris;
import com.lavadip.skeye.astro.ephemeris.EphemerisImplementation;
import com.lavadip.skeye.calendar.CalendarAdapter;
import com.lavadip.skeye.util.Util;
import com.lavadip.skeye.view.CustomSpinner;
import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class CalendarView extends LinearLayout {
    private static final CalendarMarker[] markers = {moonMarker};
    private static final CalendarMarker moonMarker = new CalendarMarker() {
        /* class com.lavadip.skeye.calendar.CalendarView.C00662 */
        private static final int MOON_INDEX = 1;
        private static final int SUN_INDEX = 0;

        /* access modifiers changed from: package-private */
        @Override // com.lavadip.skeye.calendar.CalendarMarker
        public CalendarAdapter.CalendarItem findMark(int day, int month, int year) {
            Ephemeris.PlanetData[] newPlanetPos = new EphemerisImplementation().getSunMoonPositions(new Date(year - 1900, month - 1, day, 23, 59, 30), Math.toDegrees((double) Sky.getUserLongitude()), Math.toDegrees((double) Sky.getUserLatitude()));
            Vector3d sunPos = Util.map3d((double) ((float) Math.toRadians(newPlanetPos[0].f37RA)), (double) ((float) Math.toRadians(newPlanetPos[0].Dec)));
            Vector3d moonPos = Util.map3d((double) ((float) Math.toRadians(newPlanetPos[1].f37RA)), (double) ((float) Math.toRadians(newPlanetPos[1].Dec)));
            return new MoonPhaseCalendarItem(sunPos.crossMult(moonPos, true).f17y > 0.0d, Math.toDegrees(moonPos.angleBetweenMag(sunPos)));
        }
    };
    private final CalendarAdapter adapter;
    private final Runnable calendarUpdater = new Runnable() {
        /* class com.lavadip.skeye.calendar.CalendarView.RunnableC00641 */

        public void run() {
            Map<Integer, List<CalendarAdapter.CalendarItem>> items = new HashMap<>();
            int m = CalendarView.this.month.get(2) + 1;
            int y = CalendarView.this.month.get(1);
            int maxDays = CalendarView.this.month.getActualMaximum(5);
            for (int i = 1; i <= maxDays; i++) {
                List<CalendarAdapter.CalendarItem> dayItems = new LinkedList<>();
                for (CalendarMarker marker : CalendarView.markers) {
                    CalendarAdapter.CalendarItem item = marker.findMark(i, m, y);
                    if (item != null) {
                        dayItems.add(item);
                    }
                }
                if (dayItems.size() > 0) {
                    items.put(Integer.valueOf(i), dayItems);
                }
            }
            CalendarView.this.adapter.setItems(items);
            CalendarView.this.adapter.notifyDataSetChanged();
        }
    };
    private final Handler handler = new Handler();
    private DateWatcher mDateWatcher = null;
    private final Calendar month;
    private final boolean synchronous;

    public interface DateWatcher {
        void onDateChanged(int i, int i2, int i3);
    }

    public CalendarView(final Context context, int yearIn, int monthIn, int dayIn, boolean synchronous2) {
        super(context);
        this.synchronous = synchronous2;
        this.month = Calendar.getInstance();
        this.month.set(yearIn, monthIn, dayIn);
        this.adapter = new CalendarAdapter(context, this.month);
        addView(((LayoutInflater) context.getSystemService("layout_inflater")).inflate(C0031R.layout.calendar, (ViewGroup) null));
        GridView gridview = (GridView) findViewById(C0031R.C0032id.gridview);
        gridview.setAdapter((ListAdapter) this.adapter);
        gridview.setFocusable(false);
        if (synchronous2) {
            this.calendarUpdater.run();
        } else {
            this.handler.post(this.calendarUpdater);
        }
        TextView title = (TextView) findViewById(C0031R.C0032id.calendar_title);
        title.setText(DateFormat.format("MMMM yyyy", this.month));
        title.setOnClickListener(new View.OnClickListener() {
            /* class com.lavadip.skeye.calendar.CalendarView.View$OnClickListenerC00673 */

            public void onClick(View v) {
                CalendarView.this.showDatePicker(context);
            }
        });
        ((TextView) findViewById(C0031R.C0032id.calendar_previous)).setOnClickListener(new View.OnClickListener() {
            /* class com.lavadip.skeye.calendar.CalendarView.View$OnClickListenerC00684 */

            public void onClick(View v) {
                if (CalendarView.this.month.get(2) == CalendarView.this.month.getActualMinimum(2)) {
                    CalendarView.this.month.set(CalendarView.this.month.get(1) - 1, CalendarView.this.month.getActualMaximum(2), 1);
                } else {
                    CalendarView.this.month.set(2, CalendarView.this.month.get(2) - 1);
                }
                CalendarView.this.refreshCalendar();
            }
        });
        ((TextView) findViewById(C0031R.C0032id.calendar_next)).setOnClickListener(new View.OnClickListener() {
            /* class com.lavadip.skeye.calendar.CalendarView.View$OnClickListenerC00695 */

            public void onClick(View v) {
                if (CalendarView.this.month.get(2) == CalendarView.this.month.getActualMaximum(2)) {
                    CalendarView.this.month.set(CalendarView.this.month.get(1) + 1, CalendarView.this.month.getActualMinimum(2), 1);
                } else {
                    CalendarView.this.month.set(2, CalendarView.this.month.get(2) + 1);
                }
                CalendarView.this.refreshCalendar();
            }
        });
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            /* class com.lavadip.skeye.calendar.CalendarView.C00706 */

            @Override // android.widget.AdapterView.OnItemClickListener
            public void onItemClick(AdapterView<?> adapterView, View v, int position, long id) {
                TextView date = (TextView) v.findViewById(C0031R.C0032id.date);
                if (!date.getText().equals("")) {
                    CalendarView.this.sendToListener(CalendarView.this.month.get(1), CalendarView.this.month.get(2), Integer.parseInt(date.getText().toString()));
                }
            }
        });
    }

    public void refreshCalendar() {
        this.adapter.refreshDays();
        if (this.synchronous) {
            this.calendarUpdater.run();
        } else {
            this.handler.post(this.calendarUpdater);
        }
        ((TextView) findViewById(C0031R.C0032id.calendar_title)).setText(DateFormat.format("MMMM yyyy", this.month));
    }

    public void setDateChangedListener(DateWatcher listener) {
        this.mDateWatcher = listener;
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private synchronized void sendToListener(int y, int m, int d) {
        if (this.mDateWatcher != null) {
            this.mDateWatcher.onDateChanged(y, m, d);
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void showDatePicker(Context context) {
        final View datePickerView = ((LayoutInflater) context.getSystemService("layout_inflater")).inflate(C0031R.layout.date_picker, (ViewGroup) null);
        String[] months = new DateFormatSymbols().getMonths();
        final CustomSpinner spinner = (CustomSpinner) datePickerView.findViewById(C0031R.C0032id.spinnerMonth);
        spinner.setItems(months);
        spinner.setCurrPos(this.month.get(2));
        final TextView textYear = (TextView) datePickerView.findViewById(C0031R.C0032id.year_display);
        textYear.setText(new StringBuilder().append(this.month.get(1)).toString());
        datePickerView.findViewById(C0031R.C0032id.year_minus).setOnClickListener(new View.OnClickListener() {
            /* class com.lavadip.skeye.calendar.CalendarView.View$OnClickListenerC00717 */

            public void onClick(View v) {
                textYear.setText(new StringBuilder().append(Integer.parseInt(textYear.getText().toString()) - 1).toString());
            }
        });
        datePickerView.findViewById(C0031R.C0032id.year_plus).setOnClickListener(new View.OnClickListener() {
            /* class com.lavadip.skeye.calendar.CalendarView.View$OnClickListenerC00728 */

            public void onClick(View v) {
                TextView textYear = (TextView) datePickerView.findViewById(C0031R.C0032id.year_display);
                textYear.setText(new StringBuilder().append(Integer.parseInt(textYear.getText().toString()) + 1).toString());
            }
        });
        new CustomDialog.Builder(context).setTitle(C0031R.string.set_month).setContentView(datePickerView).setPositiveButton(C0031R.string.set_month, new DialogInterface.OnClickListener() {
            /* class com.lavadip.skeye.calendar.CalendarView.DialogInterface$OnClickListenerC00739 */

            public void onClick(DialogInterface d, int arg1) {
                d.cancel();
                CalendarView.this.month.set(2, spinner.getCurrPos());
                CalendarView.this.month.set(1, Integer.parseInt(textYear.getText().toString()));
                CalendarView.this.refreshCalendar();
            }
        }).setNegativeButton(17039360, new DialogInterface.OnClickListener() {
            /* class com.lavadip.skeye.calendar.CalendarView.DialogInterface$OnClickListenerC006510 */

            public void onClick(DialogInterface d, int arg1) {
                d.cancel();
            }
        }).create().show();
    }
}
