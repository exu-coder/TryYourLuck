package com.my.luck.features;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.media.ExifInterface;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CameraModule implements Camera.PictureCallback {
    private static final String TAG = "CameraModule";
    private Context context;
    private Camera camera;
    private File currentPhoto;
    private CameraPhotoCallback callback;

    public interface CameraPhotoCallback {
        void onPhotoCaptured(String path);
        void onError(String error);
    }

    public CameraModule(Context context) {
        this.context = context;
    }

    public void capturePhoto(CameraPhotoCallback callback) {
        this.callback = callback;
        try {
            camera = Camera.open();
            if (camera != null) {
                SurfaceView dummyView = new SurfaceView(context);
                SurfaceHolder holder = dummyView.getHolder();
                camera.setPreviewDisplay(holder);
                camera.startPreview();
                camera.takePicture(null, null, this);
                Log.d(TAG, "Photo capture initiated");
            } else {
                if (callback != null) {
                    callback.onError("Camera not available");
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error capturing photo", e);
            if (callback != null) {
                callback.onError(e.getMessage());
            }
        }
    }

    @Override
    public void onPictureTaken(byte[] data, Camera camera) {
        try {
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            File dcim = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
            if (dcim != null && !dcim.exists()) {
                dcim.mkdirs();
            }
            currentPhoto = new File(dcim, "IMG_" + timestamp + ".jpg");
            
            FileOutputStream fos = new FileOutputStream(currentPhoto);
            fos.write(data);
            fos.close();
            
            camera.release();
            camera = null;
            
            Log.d(TAG, "Photo saved: " + currentPhoto.getAbsolutePath());
            if (callback != null) {
                callback.onPhotoCaptured(currentPhoto.getAbsolutePath());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error saving photo", e);
            if (callback != null) {
                callback.onError(e.getMessage());
            }
        }
    }

    public void capturePhotoWithFlash(CameraPhotoCallback callback) {
        this.callback = callback;
        try {
            camera = Camera.open();
            if (camera != null) {
                Camera.Parameters params = camera.getParameters();
                params.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
                camera.setParameters(params);
                
                SurfaceView dummyView = new SurfaceView(context);
                SurfaceHolder holder = dummyView.getHolder();
                camera.setPreviewDisplay(holder);
                camera.startPreview();
                camera.takePicture(null, null, this);
                Log.d(TAG, "Photo capture with flash initiated");
            } else {
                if (callback != null) {
                    callback.onError("Camera not available");
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error capturing photo with flash", e);
            if (callback != null) {
                callback.onError(e.getMessage());
            }
        }
    }

    public void toggleFlash(boolean enable) {
        try {
            if (camera != null) {
                Camera.Parameters params = camera.getParameters();
                params.setFlashMode(enable ? Camera.Parameters.FLASH_MODE_ON : Camera.Parameters.FLASH_MODE_OFF);
                camera.setParameters(params);
                Log.d(TAG, "Flash toggled: " + enable);
            } else {
                // If camera is not open, open temporarily
                Camera tempCamera = Camera.open();
                if (tempCamera != null) {
                    Camera.Parameters params = tempCamera.getParameters();
                    params.setFlashMode(enable ? Camera.Parameters.FLASH_MODE_TORCH : Camera.Parameters.FLASH_MODE_OFF);
                    tempCamera.setParameters(params);
                    tempCamera.release();
                    Log.d(TAG, "Flash toggled (temp): " + enable);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error toggling flash", e);
        }
    }
}