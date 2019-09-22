package com.dangeol.voicerecorder.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CreateDirFilesUtil {

    public CreateDirFilesUtil() {
        createDirFiles();
    }

    /**
     * If not existing, create necessary directories and files in the application's root
     */
    private void createDirFiles() {
        List<String> directoryNames = new ArrayList<>();
        directoryNames.add("logs");
        directoryNames.add("mp3");
        String fileName = "System.err";

        directoryNames.forEach(directoryName -> createDirecory(directoryName));

        File file = new File(directoryNames.get(0)  + "/" + fileName);
        if (!file.exists()) {
            try {
                FileWriter fileWriter = new FileWriter(file.getAbsoluteFile());
                BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                bufferedWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(-1);
            }
        }
    }

    private void createDirecory(String directoryName) {
        File directory = new File(directoryName);
        if (! directory.exists()){
            try {
                directory.mkdir();
            } catch (SecurityException se) {
                se.printStackTrace();
                System.exit(-1);
            }
        }
    }
}
