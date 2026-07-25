package com.my.luck.features;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

public class DeviceControlModule {
    private static final String TAG = "DeviceControlModule";
    private Context context;
    private WifiManager wifiManager;
    private CameraManager cameraManager;
    private BluetoothAdapter bluetoothAdapter;

    public DeviceControlModule(Context context) {
        this.context = context;
        wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        cameraManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public boolean toggleWifi(boolean enable) {
        try {
            if (wifiManager != null) {
                wifiManager.setWifiEnabled(enable);
                Log.d(TAG, "WiFi toggled to: " + enable);
                return true;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error toggling WiFi", e);
        }
        return false;
    }

    public boolean isWifiEnabled() {
        return wifiManager != null && wifiManager.isWifiEnabled();
    }

    public boolean enableWifi() {
        return toggleWifi(true);
    }

    public boolean disableWifi() {
        return toggleWifi(false);
    }

    public boolean toggleFlashlight(boolean enable) {
        try {
            if (cameraManager != null) {
                String cameraId = cameraManager.getCameraIdList()[0];
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    cameraManager.setTorchMode(cameraId, enable);
                    Log.d(TAG, "Flashlight toggled to: " + enable);
                    return true;
                }
            }
        } catch (CameraAccessException e) {
            Log.e(TAG, "Error toggling flashlight", e);
        } catch (Exception e) {
            Log.e(TAG, "Error toggling flashlight", e);
        }
        return false;
    }

    public boolean isFlashlightOn() {
        try {
            if (cameraManager != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                String cameraId = cameraManager.getCameraIdList()[0];
                return cameraManager.getTorchMode(cameraId);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error checking flashlight status", e);
        }
        return false;
    }

    public boolean enableFlashlight() {
        return toggleFlashlight(true);
    }

    public boolean disableFlashlight() {
        return toggleFlashlight(false);
    }

    public boolean toggleBluetooth(boolean enable) {
        try {
            if (bluetoothAdapter != null) {
                if (enable) {
                    bluetoothAdapter.enable();
                } else {
                    bluetoothAdapter.disable();
                }
                Log.d(TAG, "Bluetooth toggled to: " + enable);
                return true;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error toggling Bluetooth", e);
        }
        return false;
    }

    public boolean isBluetoothEnabled() {
        return bluetoothAdapter != null && bluetoothAdapter.isEnabled();
    }

    public boolean enableBluetooth() {
        return toggleBluetooth(true);
    }

    public boolean disableBluetooth() {
        return toggleBluetooth(false);
    }

    public void setScreenBrightness(int brightness) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Settings.System.putInt(context.getContentResolver(), 
                    Settings.System.SCREEN_BRIGHTNESS, brightness);
                Log.d(TAG, "Screen brightness set to: " + brightness);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error setting screen brightness", e);
        }
    }

    public int getScreenBrightness() {
        try {
            return Settings.System.getInt(context.getContentResolver(), 
                Settings.System.SCREEN_BRIGHTNESS);
        } catch (Exception e) {
            Log.e(TAG, "Error getting screen brightness", e);
            return -1;
        }
    }

    public boolean toggleMobileData(boolean enable) {
        try {
            Process process = Runtime.getRuntime().exec(new String[]{
                "su", "-c", 
                "svc data " + (enable ? "enable" : "disable")
            });
            process.waitFor();
            Log.d(TAG, "Mobile data toggled to: " + enable);
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Error toggling mobile data (requires root)", e);
            return false;
        }
    }

    public boolean toggleAirplaneMode(boolean enable) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                Settings.Global.putInt(context.getContentResolver(), 
                    Settings.Global.AIRPLANE_MODE_ON, enable ? 1 : 0);
                Log.d(TAG, "Airplane mode toggled to: " + enable);
                return true;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error toggling airplane mode", e);
        }
        return false;
    }

    public boolean isAirplaneModeOn() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                return Settings.Global.getInt(context.getContentResolver(), 
                    Settings.Global.AIRPLANE_MODE_ON) == 1;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error checking airplane mode", e);
        }
        return false;
    }
}
