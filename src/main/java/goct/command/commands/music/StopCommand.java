package goct.command.commands.music;

import goct.command.CommandContext;
import goct.command.ICommand;
import goct.music.GuildMusicManager;
import goct.music.PlayerManager;

public class StopCommand implements ICommand {

    private LeaveCommand leaveCommand;

    public StopCommand(LeaveCommand leaveCommand) {
        this.leaveCommand = leaveCommand;
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
