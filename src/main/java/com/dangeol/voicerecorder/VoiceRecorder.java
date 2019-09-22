package com.dangeol.voicerecorder;

import com.dangeol.voicerecorder.utils.CreateDirFilesUtil;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.security.auth.login.LoginException;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public class VoiceRecorder extends ListenerAdapter {

    private static final CreateDirFilesUtil createDirFilesUtil = new CreateDirFilesUtil();
    private static final Logger logger = LoggerFactory.getLogger(VoiceRecorder.class);
    private static final Commands commands = new Commands();

    public static void main(String[] args) {

        try {
            JDABuilder builder = new JDABuilder(AccountType.BOT);
            builder.setToken(getEnvItem("bot_token"));
            builder.addEventListeners(new VoiceRecorder());
            builder.setActivity(Activity.playing("https://github.com/dangeol/voice-recorder"));
            builder.build();

        } catch (LoginException le) {
            logger.error(le.getMessage());
        } catch (Exception e) {
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

    /**
     * Gets from env.json the value of the key which is passed as argument
     * @param key
     * @return: the String value of the requested item in env.json
     * @throws Exception
     */
    public static String getEnvItem(String key) throws Exception {
        InputStream inputStream = VoiceRecorder.class.getResourceAsStream("/env.json");
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject = (JSONObject)jsonParser.parse(
                new InputStreamReader(inputStream, "UTF-8"));
        return jsonObject.get(key).toString();
    }
}
