package com.dangeol.voicerecorder.audio;

import net.dv8tion.jda.api.audio.AudioReceiveHandler;
import net.dv8tion.jda.api.audio.AudioSendHandler;
import net.dv8tion.jda.api.audio.CombinedAudio;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AudioHandler implements AudioSendHandler, AudioReceiveHandler {
    /*
    All methods in this class are called by JDA threads when resources are available/ready for processing.
    See this example:
    https://github.com/DV8FromTheWorld/JDA/blob/master/src/examples/java/AudioEchoExample.java
    */
    private static final Logger logger = LoggerFactory.getLogger(AudioHandler.class);

    private final Queue<byte[]> queue = new ConcurrentLinkedQueue<>();
    private final Mp3Encoder mp3Encoder = new Mp3Encoder();
    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    // Number of bytes per chunk of sent pcm data
    private final int outputStreamSize = 491520;

    /**
     * Combine multiple user audio-streams into a single one and limit queue to 100 entries
     * @return boolean
     */
    @Override
    public boolean canReceiveCombined() {
        return queue.size() < 100;
    }

    /**
     * If canReceiveCombined() returns true, JDA will provide a CombinedAudio object to this method
     * every 20 milliseconds.
     * @param combinedAudio
     */
    @Override
    public void handleCombinedAudio(CombinedAudio combinedAudio) {
        float volume = 1.0f; // volume at 100%
        byte[] data = combinedAudio.getAudioData(volume);

        try {
            queue.add(data);
        } catch (OutOfMemoryError e) {
            logger.error(e.getMessage());
        }
    }

    /**
     * If we have something in our buffer we can provide it to the send system
     * @return boolean
     */
    @Override
    public boolean canProvide() {
        return !queue.isEmpty();
    }

    /**
     * use what we have in our buffer to send audio as PCM
     * @return ByteBuffer;
     */
    @Override
    public ByteBuffer provide20MsAudio() {
        byte[] dataPoll = queue.poll();

        try {
            outputStream.write(dataPoll);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }

        if (outputStream.size() == outputStreamSize) {
            byte data[] = outputStream.toByteArray();
            mp3Encoder.encodePcmToMp3(data == null ? null : data);
            outputStream.reset();
        }

        return dataPoll == null ? null : ByteBuffer.wrap(dataPoll);
    }

    /**
     * determines if we have opus but PCM
     * @return boolean
     */
    @Override
    public boolean isOpus() {
        return false;
    }
}
