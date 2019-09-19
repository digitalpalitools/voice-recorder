package com.dangeol.voicerecorder;

import com.dangeol.voicerecorder.utils.MessageUtil;
import com.dangeol.voicerecorder.utils.UploadUtil;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.audio.AudioSendHandler;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.managers.AudioManager;

import javax.security.auth.login.LoginException;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.github.cdimascio.dotenv.Dotenv;

import com.dangeol.voicerecorder.audio.AudioHandler;

public class VoiceRecorder extends ListenerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(VoiceRecorder.class);
    private static final MessageUtil messages = new MessageUtil();

    public static void main(String[] args) {
        logger.info("Program started!");
        Dotenv dotenv = Dotenv.load();

        try {
            JDABuilder builder = new JDABuilder(AccountType.BOT);
            builder.setToken(dotenv.get("BOTTOKEN"));
            builder.addEventListeners(new VoiceRecorder());
            builder.build();
        } catch (LoginException e) {
            logger.error(e.getMessage());
        }
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        Message message = event.getMessage();
        User author = message.getAuthor();
        String content = message.getContentRaw();
        Guild guild = event.getGuild();

        // Ignore message if bot
        if (author.isBot())
            return;

        if (content.startsWith("!record ")) {
            String arg = content.substring("!record ".length());
            onRecordCommand(event, guild, arg);
        } else if (content.equals("!record")) {
            onRecordCommand(event);
        } else if (content.equals("!stop")) {
            onStopCommand(event);
        }
    }

    /**
     * Handle command without arguments.
     * @param event: The event for this command
     */
    private void onRecordCommand(GuildMessageReceivedEvent event) {
        Member member = event.getMember();
        GuildVoiceState voiceState = member.getVoiceState();
        VoiceChannel channel = voiceState.getChannel();
        if (channel == null) {
            messages.onUnknownChannelMessage(event.getChannel(), "your voice channel");
            return;
        }
        connectTo(channel, event.getChannel());
    }

    /**
     * Handle command with arguments.
     * @param event: The event for this command
     * @param guild: The guild where its happening
     * @param arg: The input argument
     */
    private void onRecordCommand(GuildMessageReceivedEvent event, Guild guild, String arg) {
        boolean isNumber = arg.matches("\\d+"); // This is a regular expression that ensures the input consists of digits
        VoiceChannel channel = null;
        if (isNumber) {
            channel = guild.getVoiceChannelById(arg);
        } if (channel == null) {
            List<VoiceChannel> channels = guild.getVoiceChannelsByName(arg, true);
            if (!channels.isEmpty())
                channel = channels.get(0);
        }

        TextChannel textChannel = event.getChannel();
        if (channel == null) {
            messages.onUnknownChannelMessage(textChannel, arg);
            return;
        }
        connectTo(channel, textChannel);
    }

    /**
     * Connect to requested channel and start audio handler
     * @param voiceChannel: The voiceChannel to connect to
     * @param textChannel: The textChannel to write message to
     */
    private void connectTo(VoiceChannel voiceChannel, TextChannel textChannel) {
        Guild guild = voiceChannel.getGuild();
        AudioManager audioManager = guild.getAudioManager();
        if (audioManager.isConnected() || audioManager.isAttemptingToConnect()) {
            messages.onAlreadyConnectedMessage(textChannel, voiceChannel.getName());
            return;
        }
        messages.disclaimerConsentMessage(voiceChannel, textChannel);
        try {
            TimeUnit.SECONDS.sleep(10);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            logger.error(ie.getMessage());
        }
        sendSilentByte(audioManager);

        // Set the sending and receiving handler to our audio system
        AudioHandler handler = new AudioHandler();
        audioManager.setSendingHandler(handler);
        audioManager.setReceivingHandler(handler);
        audioManager.openAudioConnection(voiceChannel);
        playGong();
        messages.onConnectionMessage(voiceChannel, textChannel);
    }

    /**
     * Disconnect from channel and close the audio connection
     * @param event
     */
    private void onStopCommand(GuildMessageReceivedEvent event) {
        VoiceChannel connectedChannel = event.getGuild().getSelfMember().getVoiceState().getChannel();
        UploadUtil uploadutil = new UploadUtil();
        if(connectedChannel == null) {
            messages.onNotConnVoiceChMessage(event.getChannel());
            return;
        }

        event.getGuild().getAudioManager().closeAudioConnection();
        messages.onDisconnectionMessage(event.getChannel());
        try {
            uploadutil.uploadMp3(event.getChannel());
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    /**
     * Send initially a silent byte to circumvent a Discord bug which might have already been resolved
     * See https://github.com/discordapp/discord-api-docs/issues/808#issuecomment-457962359
     * @param audioManager
     */
    private void sendSilentByte(AudioManager audioManager) {
        audioManager.setSendingHandler(new AudioSendHandler() {
            @Override
            public boolean canProvide() {
                return false;
            }
            @Override
            public ByteBuffer provide20MsAudio() {
                return ByteBuffer.wrap(new byte[0]);
            }
        });
    }

    private void playGong() {
        try {
            File gongBurmese = new File("assets/bell-inside.wav");
            Clip sound = AudioSystem.getClip();
            sound.open(AudioSystem.getAudioInputStream(gongBurmese));
            sound.start();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }
}
