package com.dangeol.voicerecorder.audio;

import de.sciss.jump3r.mp3.Lame;
import de.sciss.jump3r.lowlevel.LameEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sound.sampled.AudioFormat;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Mp3Encoder {

    private final int bitRate = 96;
    private final int channelMode = 2;
    private final int lameQuality = Lame.STANDARD_FAST;
    private final boolean vbr = false;

    private final AudioFormat audioFormat = new AudioFormat(48000.0f, 16, 2,
            true, true);
    private final LameEncoder encoder = new LameEncoder(audioFormat, bitRate, channelMode, lameQuality, vbr);
    private final String fileName = new SimpleDateFormat("'mp3/'yyyyMMddHHmmss'.mp3'").format(new Date());

    private static final Logger logger = LoggerFactory.getLogger(Mp3Encoder.class);

    /**
     * Encodes the pcm byte array stream to MP3
     * @param pcm: pcm byte array
     */
    public String encodePcmToMp3(byte[] pcm) {

        try (OutputStream mp3 = new FileOutputStream(fileName, true)) {

            final byte[] buffer = new byte[encoder.getPCMBufferSize()];

            int bytesToTransfer = Math.min(buffer.length, pcm.length);
            int bytesWritten;
            int currentPcmPosition = 0;
            while (0 < (bytesWritten = encoder.encodeBuffer(pcm, currentPcmPosition, bytesToTransfer, buffer))) {
                currentPcmPosition += bytesToTransfer;
                bytesToTransfer = Math.min(buffer.length, pcm.length - currentPcmPosition);
                mp3.write(buffer, 0, bytesWritten);
            }
        } catch (IOException | OutOfMemoryError e) {
            logger.error(e.getMessage());
        }
        return fileName;
    }
}
