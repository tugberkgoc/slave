package goct.command.commands;

import goct.command.CommandContext;
import goct.command.ICommand;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.Arrays;
import java.util.List;

public class SquadCommand implements ICommand {

    @Override
    public void handle(CommandContext ctx) {
        TextChannel channel = ctx.getChannel();
        List<Member> members = ctx.getGuild().getMembers();

        channel.sendMessage("<@184047763233832961> , <@316709568342261760> , <@337287311823732737> , <@411149637190418442> GET READY!!!").queue();
    }

    @Override
    public String getName() {
        return "squad";
    }

    @Override
    public String getHelp() {
        return "Call for squad";
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("pubg", "squad");
    }
}
