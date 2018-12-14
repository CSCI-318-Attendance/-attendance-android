package com.ao.android.utils;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.View;

import java.util.Calendar;
import java.util.Locale;

public class AOUtil {

    public static void toggleViews(boolean enabled, View... views) {
        for (View view : views) {
            view.setEnabled(enabled);
        }
    }

    public static AlertDialog alertUser(Context context, String title, String message) {
        AlertDialog.Builder adb = new AlertDialog.Builder(context);
        adb.setTitle(title);
        adb.setMessage(message);
        return adb.create();
    }

    public static String[] getDateDetails() {
        String[] details = new String[3];
        Calendar calendar = Calendar.getInstance(Locale.US);
        details[0] = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.US);
        details[1] = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.US);
        details[2] = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
        return details;
    }

}
