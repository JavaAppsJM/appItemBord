package be.hvwebsites.itembord.services;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class CalenderService {
    // Provides methods for managing events in MyCalendar
    // Input: context from calling activity
    //        contentresolver to manipulate Calendar provider from calling activity

    // Projection array. Creating indices for this array instead of doing
    // dynamic lookups improves performance.
    public static final String[] EVENT_PROJECTION = new String[] {
            CalendarContract.Calendars._ID,                           // 0
            CalendarContract.Calendars.ACCOUNT_NAME,                  // 1
            CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,         // 2
            CalendarContract.Calendars.OWNER_ACCOUNT                  // 3
    };
    // The indices for the projection array above.
    private static final int PROJECTION_ID_INDEX = 0;
    private static final int PROJECTION_ACCOUNT_NAME_INDEX = 1;
    private static final int PROJECTION_DISPLAY_NAME_INDEX = 2;
    private static final int PROJECTION_OWNER_ACCOUNT_INDEX = 3;

    public CalenderService() {
    }

    public long getMyCalendarId(Context context, Activity activity, ContentResolver contentResolver){
        // Query calender ID with projection array declared earlier
        Cursor cur = null;
        //ContentResolver cr = getContentResolver();
        Uri uri = CalendarContract.Calendars.CONTENT_URI;
        String selection = "((" + CalendarContract.Calendars.ACCOUNT_TYPE + " = ?))";
        String[] selectionArgs = new String[] {CalendarContract.ACCOUNT_TYPE_LOCAL};
//        String selection = "((" + CalendarContract.Calendars.ACCOUNT_NAME + " = ?) AND ("
//                + CalendarContract.Calendars.ACCOUNT_TYPE + " = ?) AND ("
//                + CalendarContract.Calendars.OWNER_ACCOUNT + " = ?))";
//        String[] selectionArgs = new String[] {"hera@example.com", "LOCAL",
//                "hera@example.com"};
        // Check permissions
        String[] permissions = {
                Manifest.permission.READ_CALENDAR,
                Manifest.permission.WRITE_CALENDAR
        };
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR)
                == PackageManager.PERMISSION_DENIED){
            // Permissions not yet ok, request permissions
            ActivityCompat.requestPermissions(activity, permissions, 0);
        }
        // Submit the query and get a Cursor object back.
        cur = contentResolver.query(uri, EVENT_PROJECTION, selection, selectionArgs, null);

        long calID = 0;
        // Use the cursor to step through the returned records
        while (cur.moveToNext()) {
            String displayName = null;
            String accountName = null;
            String ownerName = null;

            // Get the field values
            calID = cur.getLong(PROJECTION_ID_INDEX);// ID off the calendar
            displayName = cur.getString(PROJECTION_DISPLAY_NAME_INDEX);
            accountName = cur.getString(PROJECTION_ACCOUNT_NAME_INDEX);
            ownerName = cur.getString(PROJECTION_OWNER_ACCOUNT_INDEX);
            int debug = 0;
        }
        return calID;
    }

    public Long createEventInMyCalendar(ContentResolver contentResolver,
                                        long calenderId,
                                        long startDateMillis, // Startdatum in ms
                                        long endDateMillis, // Enddatum in ms
                                        String eventTitle){
        // add an event in the calendar
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.DTSTART, startDateMillis);
        values.put(CalendarContract.Events.DTEND, endDateMillis);
        values.put(CalendarContract.Events.TITLE, eventTitle);
        values.put(CalendarContract.Events.DESCRIPTION, "CalenderService");
        values.put(CalendarContract.Events.CALENDAR_ID, calenderId);
        values.put(CalendarContract.Events.EVENT_TIMEZONE, "WesternEurope/Brussels");
        Uri uriEvent = contentResolver.insert(CalendarContract.Events.CONTENT_URI, values);

        // get the event ID that is the last element in the Uri
        return Long.parseLong(uriEvent.getLastPathSegment());
    }

    public int updateEventInMyCalendar(ContentResolver contentResolver,
                                       long eventId,
                                       long newStartDateMillis, // Nieuwe startdatum in ms
                                       long newEndDateMillis, // Nieuwe enddatum in ms
                                       String newTitle){

        ContentValues values = new ContentValues();
        Uri updateUri = null;
        // The new title for the event
        values.put(CalendarContract.Events.DTSTART, newStartDateMillis);
        values.put(CalendarContract.Events.DTEND, newEndDateMillis);
        //values.put(CalendarContract.Events.TITLE, newTitle);
        updateUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, eventId);

        return contentResolver.update(updateUri, values, null, null);
    }

    public int deleteEventInMyCalendar(ContentResolver contentResolver,
                                       long eventId){
        Uri deleteUri = null;
        deleteUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, eventId);

        return contentResolver.delete(deleteUri, null, null);
    }

    public long createReminderForEvent(ContentResolver contentResolver,
                                       long eventId){

        ContentValues values = new ContentValues();
        values.put(CalendarContract.Reminders.MINUTES, 15);
        values.put(CalendarContract.Reminders.EVENT_ID, eventId);
        values.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);
        Uri uri = contentResolver.insert(CalendarContract.Reminders.CONTENT_URI, values);

        return Long.parseLong(uri.getLastPathSegment());
    }

    public int deleteReminderForEvent(ContentResolver contentResolver,
                                      long eventId){

        Uri deleteUri = null;
        deleteUri = ContentUris.withAppendedId(CalendarContract.Reminders.CONTENT_URI, eventId);

        return contentResolver.delete(deleteUri, null, null);
    }

}
