package com.dangeol.voicerecorder;

import com.dangeol.voicerecorder.utils.MessageUtil;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.security.auth.login.LoginException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.github.cdimascio.dotenv.Dotenv;

public class VoiceRecorder extends ListenerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(VoiceRecorder.class);
    private static final MessageUtil messages = new MessageUtil();
    private final Commands commands = new Commands();

    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.load();

        try {
            JDABuilder builder = new JDABuilder(AccountType.BOT);
            builder.setToken(dotenv.get("BOTTOKEN"));
            builder.addEventListeners(new VoiceRecorder());
            builder.setActivity(Activity.playing("https://github.com/dangeol/voice-recorder"));
            builder.build();
        } catch (LoginException e) {
            logger.error(e.getMessage());
        }
        logger.info("Bot is online!");
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
            commands.onRecordCommand(event, guild, arg);
        } else if (content.equals("!record")) {
            commands.onRecordCommand(event);
        } else if (content.equals("!stop")) {
            commands.onStopCommand(event);
        }
    }
}
