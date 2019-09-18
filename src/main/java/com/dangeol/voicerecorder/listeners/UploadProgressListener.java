package com.dangeol.voicerecorder.listeners;

import com.google.api.client.googleapis.media.MediaHttpUploader;
import com.google.api.client.googleapis.media.MediaHttpUploaderProgressListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class UploadProgressListener implements MediaHttpUploaderProgressListener {
    private static final Logger logger = LoggerFactory.getLogger(UploadProgressListener.class);

    public void progressChanged(MediaHttpUploader uploader) throws IOException {
        switch (uploader.getUploadState()) {
            case INITIATION_STARTED:
                logger.info("Initiation has started!");
                break;
            case INITIATION_COMPLETE:
                logger.info("Initiation is complete!");
                break;
            case MEDIA_IN_PROGRESS:
                logger.info(String.valueOf(uploader.getProgress()));
                break;
            case MEDIA_COMPLETE:
                logger.info("Upload is complete!");
        }
    }
}
