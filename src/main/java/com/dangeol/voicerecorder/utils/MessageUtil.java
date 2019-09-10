package com.dangeol.voicerecorder.utils;

import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageUtil {

    private static final Logger logger = LoggerFactory.getLogger(MessageUtil.class);

    /**
     * Inform user about successful connection.
     * @param channel: The voice channel we connected to
     * @param textChannel: The text channel to send the message in
     */
    public void onConnectionMessage(VoiceChannel channel, TextChannel textChannel) {
        String msg = "Connecting to " + channel.getName() + "\n" + ":red_circle: **Audio is being recorded on " +
                channel.getName() + "**";
        textChannel.sendMessage(msg).queue();
        logger.info(msg);
    }

    /**
     * The channel to connect to is not known to us.
     * @param channel: The message channel (text channel abstraction) to send failure information to
     * @param comment: The information of this channel
     */
    public void onUnknownChannelMessage(MessageChannel channel, String comment) {
        String msg = ":no_entry_sign: You must be in a voice channel!";
        channel.sendMessage(msg).queue();
        logger.warn(msg);
    }

    /**
     * The Bot is not connected to any voice channel.
     * @param channel: The information of this channel
     */
    public void onNotConnVoiceChMessage(MessageChannel channel) {
        String msg = ":no_entry_sign: The Bot is not connected to a voice channel!";
        channel.sendMessage(msg).queue();
        logger.warn(msg);
    }

    /**
     * Disconnection confirmation message
     * @param channel: The message channel (text channel abstraction) to send information to
     */
    public void onDisconnectionMessage(MessageChannel channel) {
        String msg = "Stopped recording!" + "\n" + ":floppy_disk: **Saving recording... **" +"\n" + ":wave: Leaving " +
                channel.getName();
        channel.sendMessage(msg).queue();
        logger.info(msg);
    }
}
