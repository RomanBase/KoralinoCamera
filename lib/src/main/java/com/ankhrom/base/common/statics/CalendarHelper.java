package com.ankhrom.base.common.statics;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;

import java.util.Date;

import com.ankhrom.base.GlobalCode;
import com.ankhrom.base.common.BasePermission;

@SuppressWarnings("ResourceType")
public final class CalendarHelper {

    public static boolean hasPermisionToRead(Context context) {

        return BasePermission.isAvailable(context, Manifest.permission.READ_CALENDAR);
    }

    public static boolean hasPermissionToWrite(Context context) {

        return BasePermission.isAvailable(context, Manifest.permission.WRITE_CALENDAR);
    }

    public static boolean hasEvent(Context context, String title) {

        if (!hasPermisionToRead(context)) {
            return false;
        }

        Cursor cursor = context.getContentResolver().query(CalendarContract.Events.CONTENT_URI,
                new String[]{CalendarContract.Events._ID, CalendarContract.Events.TITLE},
                CalendarContract.Events.TITLE + "=?",
                new String[]{title},
                null);

        if (cursor == null) {
            return false;
        }

        boolean result = cursor.moveToFirst();

        cursor.close();

        return result;
    }

    public static int getEventId(Context context, String title) {

        if (!hasPermisionToRead(context)) {
            return 0;
        }

        Cursor cursor = context.getContentResolver().query(CalendarContract.Events.CONTENT_URI,
                new String[]{CalendarContract.Events._ID, CalendarContract.Events.TITLE},
                CalendarContract.Events.TITLE + "=?",
                new String[]{title},
                null);

        if (cursor == null) {
            return 0;
        }

        boolean result = cursor.moveToFirst();

        int id = 0;
        if (result) {
            id = cursor.getInt(0);
        }

        cursor.close();

        return id;
    }

    public static long getLastEventId(Context context) {

        if (!hasPermisionToRead(context)) {
            return 0;
        }

        Cursor cursor = context.getContentResolver().query(CalendarContract.Events.CONTENT_URI, new String[]{"MAX(_id) as max_id"}, null, null, "_id");

        if (cursor == null) {
            return 0;
        }

        boolean result = cursor.moveToFirst();

        long max_val = 0;
        if (result) {
            max_val = cursor.getLong(cursor.getColumnIndex("max_id"));
        }

        cursor.close();

        return max_val;
    }

    public static void initEvent(Activity activity, Date start, Date end, String title, String description, String location) {

        initEvent(activity, GlobalCode.CALENDAR_REQUEST, start, end, title, description, location);
    }

    public static void initEvent(Activity activity, int requestCode, Date start, Date end, String title, String description, String location) {

        Intent intent;
        int eventId = getEventId(activity, title);
        if (eventId == 0) {
            intent = new Intent(Intent.ACTION_INSERT)
                    .setData(CalendarContract.Events.CONTENT_URI)
                    .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, start.getTime())
                    .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, end.getTime())
                    .putExtra(CalendarContract.Events.TITLE, title)
                    .putExtra(CalendarContract.Events.DESCRIPTION, description)
                    .putExtra(CalendarContract.Events.EVENT_LOCATION, location)
                    .putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY);
        } else {
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("content://com.android.calendar/events/" + String.valueOf(eventId)));
        }

        activity.startActivityForResult(intent, requestCode);
    }

}
