package com.example.android.ringerapp;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by amith on 7/18/17.
 */

public class CalenderHelper {

    public static ArrayList<HashMap<String,String>> eventMap = new ArrayList<HashMap<String,String>>();

    public ArrayList<HashMap<String,String>> readCalendarEvent(Context context) {

        Calendar startTime = Calendar.getInstance();
        startTime.set(Calendar.HOUR_OF_DAY,0);
        startTime.set(Calendar.MINUTE,0);
        startTime.set(Calendar.SECOND, 0);

        Calendar endTime= Calendar.getInstance();
        endTime.add(Calendar.DATE, 1);

        String selection = "(( " + CalendarContract.Events.DTSTART + " >= " + startTime.getTimeInMillis()
                + " ) AND ( " + CalendarContract.Events.DTSTART + " <= " + endTime.getTimeInMillis()
                + " ) AND ( deleted != 1 ))";

        Cursor cursor = context.getContentResolver().query(Uri.parse("content://com.android.calendar/events"),
                new String[]{"calendar_id", "title", "description","dtstart", "dtend", "eventLocation"},
                selection, null, null);
        cursor.moveToFirst();

        String CNames[] = new String[cursor.getCount()];

        for (int i = 0; i < CNames.length; i++) {

            Map<String,String> map = new HashMap<String, String>();
            map.put("title",cursor.getString(1));
            map.put("dtstart",getDate(Long.parseLong(cursor.getString(3))));
            map.put("dtend",getDate(Long.parseLong(cursor.getString(4))));
            map.put("description",cursor.getString(2));

            eventMap.add((HashMap<String, String>) map);

            CNames[i] = cursor.getString(1);
            cursor.moveToNext();
        }
        return eventMap;
    }

    public static String getDate(long milliSeconds) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

    public CharSequence getCurrentDate(){
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        String formattedDate = df.format(c.getTime());
        Log.v("LOG_TAG","Value of date"+formattedDate);
        return formattedDate;
    }
}
