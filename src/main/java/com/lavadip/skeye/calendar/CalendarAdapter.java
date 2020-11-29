package com.lavadip.skeye.calendar;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.lavadip.skeye.C0031R;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/* access modifiers changed from: package-private */
public final class CalendarAdapter extends BaseAdapter {
    static final int FIRST_DAY_OF_WEEK = 0;
    public Integer[] days;
    private Map<Integer, List<CalendarItem>> items = new HashMap();
    private final Context mContext;
    private final Calendar month;
    private final Calendar selectedDate;

    public CalendarAdapter(Context c, Calendar monthCalendar) {
        this.month = monthCalendar;
        this.selectedDate = (Calendar) monthCalendar.clone();
        this.mContext = c;
        this.month.set(5, 1);
        refreshDays();
    }

    public void setItems(Map<Integer, List<CalendarItem>> items2) {
        this.items = items2;
    }

    public int getCount() {
        return this.days.length;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (convertView == null) {
            v = ((LayoutInflater) this.mContext.getSystemService("layout_inflater")).inflate(C0031R.layout.calendar_item, (ViewGroup) null);
        }
        TextView dayView = (TextView) v.findViewById(C0031R.C0032id.date);
        Integer date = this.days[position];
        if (date == null) {
            v.setVisibility(4);
        } else {
            v.setVisibility(0);
            dayView.setText(new StringBuilder().append(date).toString());
            Calendar calendarDate = (Calendar) this.month.clone();
            calendarDate.set(5, date.intValue());
            if (this.month.get(1) == this.selectedDate.get(1) && this.month.get(2) == this.selectedDate.get(2) && date.equals(Integer.valueOf(this.selectedDate.get(5)))) {
                v.setBackgroundColor(Color.argb(180, 120, 120, 0));
            } else if (calendarDate.get(7) == 1) {
                v.setBackgroundColor(Color.argb(180, 80, 30, 30));
            } else {
                v.setBackgroundColor(Color.argb(180, 40, 40, 40));
            }
            List<CalendarItem> dayItems = this.items.get(date);
            if (dayItems != null) {
                for (CalendarItem i : dayItems) {
                    i.updateView(v);
                }
            }
        }
        return v;
    }

    /* access modifiers changed from: package-private */
    public static abstract class CalendarItem {
        /* access modifiers changed from: package-private */
        public abstract void updateView(View view);

        CalendarItem() {
        }
    }

    public void refreshDays() {
        int j = 1;
        this.items.clear();
        int lastDay = this.month.getActualMaximum(5);
        int firstDay = this.month.get(7);
        if (firstDay == 1) {
            this.days = new Integer[(lastDay + 0)];
        } else {
            this.days = new Integer[((lastDay + firstDay) - 1)];
        }
        if (firstDay > 1) {
            j = firstDay + 0;
        }
        int i = j - 1;
        int dayNumber = 1;
        while (i < this.days.length) {
            this.days[i] = new Integer(dayNumber);
            i++;
            dayNumber++;
        }
    }
}
