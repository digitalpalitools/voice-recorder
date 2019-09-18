package com.dangeol.voicerecorder.utils;

import com.dangeol.voicerecorder.listeners.UploadProgressListener;
import com.dangeol.voicerecorder.services.ConnectDriveService;
import com.google.api.client.http.InputStreamContent;
import com.google.api.services.drive.Drive;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.github.cdimascio.dotenv.Dotenv;

public class UploadUtil {

    private static final Logger logger = LoggerFactory.getLogger(UploadUtil.class);

    private final com.google.api.services.drive.model.File file = new com.google.api.services.drive.model.File();
    private final ConnectDriveService connectDriveService = new ConnectDriveService();

    public String uploadMp3() throws IOException {
        String fileName = getFileName();
        File mp3File = new File("mp3/"+fileName);
        Dotenv dotenv = Dotenv.load();

        try {
            Drive drive = connectDriveService.connect();
            InputStreamContent mediaContent = new InputStreamContent("audio/mpeg",
                    new BufferedInputStream(new FileInputStream(mp3File)));
            mediaContent.setLength(mp3File.length());
            file.setParents(Collections.singletonList(dotenv.get("FOLDERID")));
            file.setName(fileName);
            Drive.Files.Create request = drive.files().create(file, mediaContent);
            request.getMediaHttpUploader().setProgressListener(new UploadProgressListener());
            request.execute();
        } catch (FileNotFoundException e) {
            logger.error(e.getMessage());
        }
        return "";
    }

    private String getFileName() {
        List<String> listOfFiles = new ArrayList<String>();
        try (Stream<Path> walk = Files.walk(Paths.get("mp3"))) {
            listOfFiles = walk.filter(Files::isRegularFile)
                    .map(x -> x.getFileName().toString()).collect(Collectors.toList());
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        return listOfFiles.get(listOfFiles.size() - 1);
    }
}
