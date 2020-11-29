package com.lavadip.skeye.widget;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.widget.RemoteViews;
import android.widget.Toast;
import com.lavadip.skeye.C0031R;
import com.lavadip.skeye.calendar.CalendarView;
import java.util.Calendar;
import java.util.Date;

public final class SkEyeWidgetProvider extends AppWidgetProvider {
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        int length = appWidgetIds.length;
        for (int i = 0; i < length; i++) {
            int widgetId = appWidgetIds[i];
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), C0031R.layout.calendar_widget);
            float density = context.getResources().getDisplayMetrics().density;
            int width = (int) (300.0f * density);
            int height = (int) (360.0f * density);
            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date(getCurrentTime()));
            CalendarView calView = new CalendarView(context, cal.get(1), cal.get(2), cal.get(5), true);
            calView.findViewById(C0031R.C0032id.calendar_previous).setVisibility(8);
            calView.findViewById(C0031R.C0032id.calendar_next).setVisibility(8);
            calView.measure(Integer.MIN_VALUE | width, Integer.MIN_VALUE | height);
            calView.layout(0, 0, width, height);
            Bitmap bitmap = Bitmap.createBitmap(calView.getMeasuredWidth(), calView.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
            calView.draw(new Canvas(bitmap));
            remoteViews.setImageViewBitmap(C0031R.C0032id.view_calendar_image, bitmap);
            try {
                Intent intent = new Intent("android.intent.action.MAIN");
                intent.addCategory("android.intent.category.LAUNCHER");
                intent.addFlags(65536);
                intent.setComponent(new ComponentName(context.getApplicationContext().getPackageName(), "com.lavadip.skeye.SkEye"));
                remoteViews.setOnClickPendingIntent(C0031R.C0032id.view_calendar_image, PendingIntent.getActivity(context, 0, intent, 0));
            } catch (ActivityNotFoundException e) {
                Toast.makeText(context.getApplicationContext(), "There was a problem loading the application: ", 0).show();
            }
            appWidgetManager.updateAppWidget(widgetId, remoteViews);
        }
    }

    private static long getCurrentTime() {
        return System.currentTimeMillis();
    }

    public void onEnabled(Context context) {
        super.onEnabled(context);
        setAlarm(context);
    }

    public void onDisabled(Context context) {
        super.onDisabled(context);
        PendingIntent newPending = makeAlarmIntent(context);
        if (newPending != null) {
            ((AlarmManager) context.getSystemService("alarm")).cancel(newPending);
        }
    }

    private static void setAlarm(Context context) {
        PendingIntent newPending = makeAlarmIntent(context);
        ((AlarmManager) context.getSystemService("alarm")).setInexactRepeating(1, ((System.currentTimeMillis() / 60000) * 60000) + 60000, 120000, newPending);
    }

    private static PendingIntent makeAlarmIntent(Context context) {
        int[] allWidgetIds = AppWidgetManager.getInstance(context).getAppWidgetIds(new ComponentName(context, SkEyeWidgetProvider.class));
        Intent updateIntent = new Intent();
        updateIntent.setAction("android.appwidget.action.APPWIDGET_UPDATE");
        updateIntent.putExtra("appWidgetIds", allWidgetIds);
        return PendingIntent.getBroadcast(context, 0, updateIntent, 134217728);
    }
}
