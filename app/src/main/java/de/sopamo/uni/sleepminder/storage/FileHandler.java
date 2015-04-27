package de.sopamo.uni.sleepminder.storage;

import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import de.sopamo.uni.sleepminder.Application;

public class FileHandler {

    /**
     * Writes the data to the filename location
     *
     * @param data
     * @param filename
     */
    public static void saveFile(String data, String filename) {
        FileOutputStream outputStream;

        try {
            outputStream = Application.context.openFileOutput(filename, Context.MODE_APPEND);
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
        File internalFiles = Application.context.getFilesDir();
        return internalFiles.listFiles();
    }
}
