package com.zebra.jamesswinton.wfctileinterfacepoc.utilities;

import android.content.Context;
import android.os.Environment;

import com.google.gson.Gson;
import com.zebra.jamesswinton.wfctileinterfacepoc.data.Config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileHelper {

    // Debugging
    private static final String TAG = "FileHelper";

    // Data
    private static final String CONFIG_FILE = "Config.json";

    public static Config loadConfigToMemoryFromFile(Context context) throws IOException {
        // Create File Instance
        File configFile = new File(context.getExternalFilesDir(null), CONFIG_FILE);

        // Create Default File
        if (!configFile.exists()) {
            InputStream is = context.getResources().getAssets().open(CONFIG_FILE);
            OutputStream os = new FileOutputStream(configFile);
            byte[] data = new byte[is.available()];
            is.read(data);
            os.write(data);
            is.close();
            os.close();
        }

        // Read JSON File
        return new Gson().fromJson(new BufferedReader(new FileReader(configFile)), Config.class);
    }
}
