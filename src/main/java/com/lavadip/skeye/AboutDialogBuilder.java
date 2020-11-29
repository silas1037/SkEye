package com.lavadip.skeye;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Process;
import android.text.Html;
import android.widget.ScrollView;
import android.widget.TextView;
import com.lavadip.skeye.CustomDialog;
import com.lavadip.skeye.util.HtmlUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Pattern;

final class AboutDialogBuilder {
    AboutDialogBuilder() {
    }

    public static Dialog create(Context context) {
        try {
            String versionInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 128).versionName;
            String aboutTitle = String.format("About %s", context.getString(C0031R.string.app_name));
            String versionString = String.format("Version: %s", versionInfo);
            String shortVersionString = String.format("V %s", versionInfo);
            return new CustomDialog.Builder(context).setTitle(aboutTitle).setCancelable(true).setIcon(C0031R.drawable.icon).setNeutralButton(context.getString(17039370), (DialogInterface.OnClickListener) null).setNegativeButton("Logs", logClickListener(context)).setMessage(Html.fromHtml(String.format("%s<br/><br/>%s", versionString, String.format(context.getString(C0031R.string.about_text), shortVersionString)))).create();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static DialogInterface.OnClickListener logClickListener(final Context context) {
        return new DialogInterface.OnClickListener() {
            /* class com.lavadip.skeye.AboutDialogBuilder.DialogInterface$OnClickListenerC00001 */

            public void onClick(DialogInterface dialog, int which) {
                String logText;
                Pattern pidPattern = Pattern.compile(".*\\(\\s*" + Process.myPid() + "\\):.*");
                try {
                    Process process = Runtime.getRuntime().exec("logcat -d");
                    if (process.waitFor() == 0) {
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                        StringBuilder log = new StringBuilder();
                        StringBuilder oldLog = new StringBuilder();
                        oldLog.append("<font size='small' color='grey'>");
                        while (true) {
                            String line = bufferedReader.readLine();
                            if (line == null) {
                                break;
                            } else if (pidPattern.matcher(line).matches()) {
                                log.append("<p>" + HtmlUtils.escapeHtml(line) + "</p>");
                            } else {
                                oldLog.append(String.valueOf(HtmlUtils.escapeHtml(line)) + "<br/>");
                            }
                        }
                        oldLog.append("</font>");
                        logText = String.valueOf(oldLog.toString()) + log.toString();
                    } else {
                        logText = "Viewing logs is not supported on this device!";
                    }
                } catch (IOException e) {
                    logText = e.toString();
                    e.printStackTrace();
                } catch (InterruptedException e2) {
                    logText = e2.toString();
                    e2.printStackTrace();
                }
                ScrollView sv = new ScrollView(context);
                TextView tv = new TextView(context);
                sv.setFillViewport(false);
                tv.setText(Html.fromHtml(logText));
                sv.addView(tv);
                new CustomDialog.Builder(context).setTitle("SkEye Log").setCancelable(true).setIcon(C0031R.drawable.icon).setNeutralButton(context.getString(17039370), (DialogInterface.OnClickListener) null).setNegativeButton("Clear", AboutDialogBuilder.logClearListener()).setContentView(sv).create().show();
            }
        };
    }

    /* access modifiers changed from: private */
    public static DialogInterface.OnClickListener logClearListener() {
        return new DialogInterface.OnClickListener() {
            /* class com.lavadip.skeye.AboutDialogBuilder.DialogInterface$OnClickListenerC00012 */

            public void onClick(DialogInterface dialog, int which) {
                try {
                    Runtime.getRuntime().exec("logcat -c");
                    dialog.dismiss();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
    }
}
