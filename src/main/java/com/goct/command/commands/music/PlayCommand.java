package com.goct.command.commands.music;

import com.goct.command.CommandContext;
import com.goct.command.ICommand;
import com.goct.music.PlayerManager;
import net.dv8tion.jda.api.entities.TextChannel;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class PlayCommand implements ICommand {

    private final JoinCommand joinCommand;

    public PlayCommand() {
        joinCommand = new JoinCommand();
    }

    @Override
    public void handle(CommandContext ctx) {
        TextChannel channel = ctx.getChannel();
        List<String> args = ctx.getArgs();

        if (args.isEmpty()) {
            channel.sendMessage("Please provide some arguments").queue();
            return;
        }

        String input = String.join(" ", args);

        if(!isUrl(input) && input.startsWith("ytsearch:")) {
            channel.sendMessage("Please provide a valid youtube, soundcloud or bandcamp link").queue();
            return;
        }

        PlayerManager manager = PlayerManager.getInstance();
        manager.loadAndPlay(channel, input);
        joinCommand.handle(ctx);
    }

    private boolean isUrl(String input) {
        try {
            new URL(input);

            return true;
        } catch (MalformedURLException ignored) {
            return false;
        }
    }

    @Override
    public String getName() {
        return "play";
    }

    @Override
    public String getHelp() {
        return "Plays a song\n" +
                "Usage: `!!play <song url>`";
    }
}
