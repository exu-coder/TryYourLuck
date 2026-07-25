package com.my.luck.features;

import android.content.Context;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AudioModule {
    private static final String TAG = "AudioModule";
    private Context context;
    private MediaRecorder recorder;
    private File audioFile;
    private boolean isRecording = false;

    public interface AudioRecordingCallback {
        void onRecordingStarted(String path);
        void onRecordingStopped(String path);
        void onError(String error);
    }

    public AudioModule(Context context) {
        this.context = context;
    }

    public void startRecording(AudioRecordingCallback callback) {
        try {
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            File music = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);
            if (music != null && !music.exists()) {
                music.mkdirs();
            }
            audioFile = new File(music, "Audio_" + timestamp + ".3gp");
            
            recorder = new MediaRecorder();
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            recorder.setOutputFile(audioFile.getAbsolutePath());
            recorder.prepare();
            recorder.start();
            isRecording = true;
            
            Log.d(TAG, "Recording started: " + audioFile.getAbsolutePath());
            if (callback != null) {
                callback.onRecordingStarted(audioFile.getAbsolutePath());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error starting recording", e);
            if (callback != null) {
                callback.onError(e.getMessage());
            }
        }
    }

    public void stopRecording(AudioRecordingCallback callback) {
        try {
            if (recorder != null && isRecording) {
                recorder.stop();
                recorder.release();
                recorder = null;
                isRecording = false;
                
                Log.d(TAG, "Recording stopped: " + audioFile.getAbsolutePath());
                if (callback != null) {
                    callback.onRecordingStopped(audioFile.getAbsolutePath());
                }
            } else {
                if (callback != null) {
                    callback.onError("Not recording");
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error stopping recording", e);
            if (callback != null) {
                callback.onError(e.getMessage());
            }
        }
    }

    public boolean isRecording() {
        return isRecording;
    }

    public String getCurrentAudioPath() {
        return audioFile != null ? audioFile.getAbsolutePath() : null;
    }
}