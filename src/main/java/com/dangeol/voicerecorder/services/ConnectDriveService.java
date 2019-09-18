package com.dangeol.voicerecorder.services;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;

public class ConnectDriveService {
    private static final HttpTransport httpTransport = new NetHttpTransport();
    private static final JacksonFactory jsonFactory = new JacksonFactory();
    private static final String CREDENTIALS_FILE_PATH = "/auth.json";

    /**
     * Connects to Google Drive on behalf of a service account.
     * @return A Google Drive object.
     * @throws IOException If the file defined by CREDENTIALS_FILE_PATH cannot be found.
     */
    public Drive connect() throws IOException {

        InputStream in = ConnectDriveService.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleCredential credential = GoogleCredential.fromStream(in)
                .createScoped(Collections.singleton(DriveScopes.DRIVE));
        Drive drive = new Drive.Builder(httpTransport, jsonFactory, credential)
                .setHttpRequestInitializer(credential).build();

        return drive;
    }
}
