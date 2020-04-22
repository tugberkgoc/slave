package com.goct.command.commands.music;

import com.goct.command.CommandContext;
import com.goct.command.ICommand;
import com.goct.music.GuildMusicManager;
import com.goct.music.PlayerManager;

public class StopCommand implements ICommand {

    private final LeaveCommand leaveCommand;

    public StopCommand() {
        leaveCommand = new LeaveCommand();
    }

    @Override
    public void handle(CommandContext ctx) {
        PlayerManager playerManager = PlayerManager.getInstance();
        GuildMusicManager musicManager = playerManager.getGuildMusicManager(ctx.getGuild());

        musicManager.scheduler.getQueue().clear();
        musicManager.player.stopTrack();
        musicManager.player.setPaused(false);

        ctx.getChannel().sendMessage("Stopping the player and clearing the queue").queue();
        leaveCommand.handle(ctx); //!
    }

    @Override
    public String getName() {
        return "stop";
    }

    @Override
    public String getHelp() {
        return "Stops the music player";
    }
}
