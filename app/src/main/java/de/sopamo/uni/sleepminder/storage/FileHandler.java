package de.sopamo.uni.sleepminder.storage;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

import de.sopamo.uni.sleepminder.Hooks;
import de.sopamo.uni.sleepminder.MyApplication;
import de.sopamo.uni.sleepminder.lib.OutputHandler;

public class FileHandler implements OutputHandler {

    @Override
    public void saveData(String data, String identifier) {
        saveFile(data,"recording-" + identifier + ".txt");
        Hooks.call(Hooks.RECORDING_LIST_UPDATE);
    }

    /**
     * Writes the data to the filename location
     *
     * @param data
     * @param filename
     */
    public static void saveFile(String data, String filename) {

        FileOutputStream outputStream;

        try {
            outputStream = new FileOutputStream(new File(getStorageDir(), filename),true);
            outputStream.write(data.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String readFile(File file) {
        //Read text from file
        StringBuilder text = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
            }
            br.close();
        }
        catch (IOException e) {
            //You'll need to add proper error handling here
        }
        return text.toString();
    }

    /**
     * Lists all files from the internal storage
     *
     * @return The list of internal storage files
     */
    public static File[] listFiles() {
        File internalFiles = getStorageDir();
        return internalFiles.listFiles();
    }

    private static File getStorageDir() {
        // Get the directory for the app's private pictures directory.
        File file = MyApplication.context.getExternalFilesDir("recordings");
        if (file == null || !file.mkdirs()) {
            Log.d("FileHandler", "Directory not created");
        }
        return file;
    }

}
