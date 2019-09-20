package com.dangeol.voicerecorder.services;

import com.dangeol.voicerecorder.Commands;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class StopSchedulerService {
    /*
        Service class providing the automatic disconnection and recording upload after a given period of time
        defined by "delay" and "timeUnit"
     */
    private static final Logger logger = LoggerFactory.getLogger(StopSchedulerService.class);
    private static final ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);
    private static final int delay = 180;
    private static final TimeUnit timeUnit = TimeUnit.MINUTES;
    private final Commands commands = new Commands();
    private ScheduledFuture<?> scheduledFuture;

    public StopSchedulerService() {
        // prevent Memory leaks
        executor.setRemoveOnCancelPolicy(true);
    }

    public void scheduleStopEvent(GuildMessageReceivedEvent event) {
        Runnable task = new Runnable() {
            public void run() {
                if (event.getGuild().getAudioManager().isConnected()) {
                    commands.onStopCommand(event);
                    logger.info("Scheduled stop down initiated.");
                } else {
                    logger.info("Scheduled stop down not initiated, no Audio connection.");
                }
            }
        };
        try {
            scheduledFuture = executor.scheduleWithFixedDelay(task,
                    0, delay, timeUnit);
        } catch (RejectedExecutionException ree) {
            logger.error(ree.getMessage());
        }
    }

    public void cancelStopEvent() {
        scheduledFuture.cancel(true);
        logger.info("Scheduled stop command cancelled");
    }
}
