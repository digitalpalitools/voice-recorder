package com.dangeol.voicerecorder;

import com.dangeol.voicerecorder.utils.MessageUtil;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.audio.AudioSendHandler;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.managers.AudioManager;

import javax.security.auth.login.LoginException;
import java.nio.ByteBuffer;
import java.util.List;
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
        if (channel != null) {
            connectTo(channel);
            messages.onConnectionMessage(channel, event.getChannel());
        }
        else {
            messages.onUnknownChannelMessage(event.getChannel(), "your voice channel");
        }
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
        connectTo(channel);
        messages.onConnectionMessage(channel, textChannel);
    }

    /**
     * Connect to requested channel and start audio handler
     * @param channel: The channel to connect to
     */
    private void connectTo(VoiceChannel channel) {
        Guild guild = channel.getGuild();
        AudioManager audioManager = guild.getAudioManager();
        AudioHandler handler = new AudioHandler();

        // Send initially some silence to circumvent a Discord bug which might already have been resolved
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

        // Set the sending handler to our audio system
         audioManager.setSendingHandler(handler);
         audioManager.setReceivingHandler(handler);
         audioManager.openAudioConnection(channel);
    }

    /**
     * Disconnect from channel and close the audio connection
     * @param event
     */
    private void onStopCommand(GuildMessageReceivedEvent event) {
        VoiceChannel connectedChannel = event.getGuild().getSelfMember().getVoiceState().getChannel();
        if(connectedChannel == null) {
            messages.onNotConnVoiceChMessage(event.getChannel());
            return;
        }

        event.getGuild().getAudioManager().closeAudioConnection();
        messages.onDisconnectionMessage(event.getChannel());
    }
}
