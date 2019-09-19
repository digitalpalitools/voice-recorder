package com.dangeol.voicerecorder.utils;

import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;

public class MessageUtil {

    /**
     * Consent information message.
     * @param voiceChannel: The voice channel we are connected to
     * @param textChannel: The message channel (text channel abstraction) to send information to
     */
    public void disclaimerConsentMessage(VoiceChannel voiceChannel, TextChannel textChannel) {
        String msg = "```fix" + "\n" + "Recording of " + voiceChannel.getName() +
                " will start in 10 seconds."
                + "\n" + "By unmuting his microphone, the participant consents to an audio tape being made of this " +
                "session and to this recording being uploaded to the internet. The purpose of this tape is sharing " +
                "the Dhamma and information concerning Sirimangalo International with interested persons who can't " +
                "attend the meeting." + "\n" +  "```";
        textChannel.sendMessage(msg).queue();
    }

    /**
     * Inform user about successful connection.
     * @param voiceChannel: The voice channel we are connected to
     * @param textChannel: The message channel (text channel abstraction) to send information to
     */
    public void onConnectionMessage(VoiceChannel voiceChannel, TextChannel textChannel) {
        String msg = ":red_circle: **Audio is being recorded on " +
                voiceChannel.getName() + "**";
        textChannel.sendMessage(msg).queue();
    }

    /**
     * The channel to connect to is not known to us.
     * @param channel: The message channel (text channel abstraction) to send failure information to
     * @param comment: The information of this channel
     */
    public void onUnknownChannelMessage(MessageChannel channel, String comment) {
        String msg = ":no_entry_sign: You must be in a voice channel!";
        channel.sendMessage(msg).queue();
    }

    /**
     * The Bot is already connected to and recording this Voice channel.
     * @param channel: The message channel (text channel abstraction) to send failure information to
     * @param comment: The information of this channel
     */
    public void onAlreadyConnectedMessage(MessageChannel channel, String comment) {
        String msg = ":no_entry_sign: Already recording "+comment+" !";
        channel.sendMessage(msg).queue();
    }

    /**
     * The Bot is not connected to any voice channel.
     * @param channel: The message channel (text channel abstraction) to send information to
     */
    public void onNotConnVoiceChMessage(MessageChannel channel) {
        String msg = ":no_entry_sign: The Bot is not connected to a voice channel!";
        channel.sendMessage(msg).queue();
    }

    /**
     * Disconnection confirmation message
     * @param channel: The message channel (text channel abstraction) to send information to
     */
    public void onDisconnectionMessage(MessageChannel channel) {
        String msg = ":wave: Stopped recording and leaving the channel." + "\n" +
                ":floppy_disk: **Saving and uploading the recording... **";
        channel.sendMessage(msg).queue();
    }

    /**
     * Upload confirmation message
     * @param channel: The message channel (text channel abstraction) to send information to
     */
    public void onUploadComplete(MessageChannel channel, String link) {
        String msg = ":thumbsup: Upload complete! Download link: " + "\n" + link;
        channel.sendMessage(msg).queue();
    }
}
