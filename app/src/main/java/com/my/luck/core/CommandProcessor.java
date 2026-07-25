package com.my.luck.core;

import android.content.Context;
import android.util.Log;

public class CommandProcessor {
    private Context context;

    public CommandProcessor(Context context) {
        this.context = context;
    }

    public void processCommand(String commandJson) {
        Log.d("CommandProcessor", "Processing: " + commandJson);
        // Add your command parsing logic here
    }
}