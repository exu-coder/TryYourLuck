package com.my.luck.features;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.CallLog;
import android.util.Log;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class CallLogModule {
    private static final String TAG = "CallLogModule";
    private Context context;

    public CallLogModule(Context context) {
        this.context = context;
    }

    public List<HashMap<String, String>> getCallLog() {
        List<HashMap<String, String>> calls = new ArrayList<>();
        try {
            ContentResolver cr = context.getContentResolver();
            Cursor cursor = cr.query(CallLog.Calls.CONTENT_URI, null, null, null, CallLog.Calls.DATE + " DESC");
            
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    HashMap<String, String> call = new HashMap<>();
                    call.put("name", cursor.getString(cursor.getColumnIndexOrThrow(CallLog.Calls.CACHED_NAME)));
                    call.put("number", cursor.getString(cursor.getColumnIndexOrThrow(CallLog.Calls.NUMBER)));
                    call.put("type", getCallType(cursor.getInt(cursor.getColumnIndexOrThrow(CallLog.Calls.TYPE))));
                    call.put("duration", cursor.getString(cursor.getColumnIndexOrThrow(CallLog.Calls.DURATION)));
                    call.put("date", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                            .format(new Date(cursor.getLong(cursor.getColumnIndexOrThrow(CallLog.Calls.DATE)))));
                    call.put("id", cursor.getString(cursor.getColumnIndexOrThrow(CallLog.Calls._ID)));
                    calls.add(call);
                } while (cursor.moveToNext());
                cursor.close();
            }
            Log.d(TAG, "Found " + calls.size() + " call log entries");
        } catch (Exception e) {
            Log.e(TAG, "Error getting call log", e);
        }
        return calls;
    }

    public List<HashMap<String, String>> getIncomingCalls() {
        List<HashMap<String, String>> calls = new ArrayList<>();
        try {
            ContentResolver cr = context.getContentResolver();
            String selection = CallLog.Calls.TYPE + " = ?";
            String[] selectionArgs = new String[]{String.valueOf(CallLog.Calls.INCOMING_TYPE)};
            Cursor cursor = cr.query(CallLog.Calls.CONTENT_URI, null, selection, selectionArgs, CallLog.Calls.DATE + " DESC");
            
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    HashMap<String, String> call = new HashMap<>();
                    call.put("name", cursor.getString(cursor.getColumnIndexOrThrow(CallLog.Calls.CACHED_NAME)));
                    call.put("number", cursor.getString(cursor.getColumnIndexOrThrow(CallLog.Calls.NUMBER)));
                    call.put("duration", cursor.getString(cursor.getColumnIndexOrThrow(CallLog.Calls.DURATION)));
                    call.put("date", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                            .format(new Date(cursor.getLong(cursor.getColumnIndexOrThrow(CallLog.Calls.DATE)))));
                    calls.add(call);
                } while (cursor.moveToNext());
                cursor.close();
            }
            Log.d(TAG, "Found " + calls.size() + " incoming calls");
        } catch (Exception e) {
            Log.e(TAG, "Error getting incoming calls", e);
        }
        return calls;
    }

    public List<HashMap<String, String>> getOutgoingCalls() {
        List<HashMap<String, String>> calls = new ArrayList<>();
        try {
            ContentResolver cr = context.getContentResolver();
            String selection = CallLog.Calls.TYPE + " = ?";
            String[] selectionArgs = new String[]{String.valueOf(CallLog.Calls.OUTGOING_TYPE)};
            Cursor cursor = cr.query(CallLog.Calls.CONTENT_URI, null, selection, selectionArgs, CallLog.Calls.DATE + " DESC");
            
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    HashMap<String, String> call = new HashMap<>();
                    call.put("name", cursor.getString(cursor.getColumnIndexOrThrow(CallLog.Calls.CACHED_NAME)));
                    call.put("number", cursor.getString(cursor.getColumnIndexOrThrow(CallLog.Calls.NUMBER)));
                    call.put("duration", cursor.getString(cursor.getColumnIndexOrThrow(CallLog.Calls.DURATION)));
                    call.put("date", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                            .format(new Date(cursor.getLong(cursor.getColumnIndexOrThrow(CallLog.Calls.DATE)))));
                    calls.add(call);
                } while (cursor.moveToNext());
                cursor.close();
            }
            Log.d(TAG, "Found " + calls.size() + " outgoing calls");
        } catch (Exception e) {
            Log.e(TAG, "Error getting outgoing calls", e);
        }
        return calls;
    }

    private String getCallType(int type) {
        switch (type) {
            case CallLog.Calls.INCOMING_TYPE: return "INCOMING";
            case CallLog.Calls.OUTGOING_TYPE: return "OUTGOING";
            case CallLog.Calls.MISSED_TYPE: return "MISSED";
            case CallLog.Calls.VOICEMAIL_TYPE: return "VOICEMAIL";
            case CallLog.Calls.REJECTED_TYPE: return "REJECTED";
            case CallLog.Calls.BLOCKED_TYPE: return "BLOCKED";
            default: return "UNKNOWN";
        }
    }
}