package com.dangeol.voicerecorder.utils;

import java.io.File;
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

        directoryNames.forEach(directoryName -> createDirecory(directoryName));
    }

    private void createDirecory(String directoryName) {
        File directory = new File(directoryName);
        if (!directory.exists()){
            try {
                directory.mkdir();
            } catch (SecurityException se) {
                se.printStackTrace();
                System.exit(-1);
            }
        }
    }
}
