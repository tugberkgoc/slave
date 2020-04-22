package com.goct.command.commands;

import com.goct.command.CommandContext;
import com.goct.command.ICommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;

import java.awt.Color;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class UserInfo implements ICommand {

    @Override
    public void handle(CommandContext ctx) {
        TextChannel channel = ctx.getChannel();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        if (ctx.getArgs().isEmpty()) {
            channel.sendMessage("Missing arguments.").queue();
            return;
        }

        if (ctx.getMessage().getMentionedMembers().isEmpty()) {
            channel.sendMessage("You need to mention someone on the guild.").queue();
            return;
        }

        Member member = ctx.getMessage().getMentionedMembers().get(0);
        EmbedBuilder eb = new EmbedBuilder()
                .setColor(Color.magenta)
                .setThumbnail(member.getUser().getAvatarUrl())
                .setAuthor("Information on " + member.getUser().getName(), member.getUser().getAvatarUrl())
                .setDescription(member.getUser().getName() + " joined on " + member.getTimeJoined().format(fmt) + " :clock: ")
                .addField("Status:", member.getOnlineStatus().toString(), true)
                .addField("Roles:", getRolesAsString(member.getRoles()), true)
                .addField("Nickname: ", member.getNickname() == null ? "No Nickname" : member.getNickname(), true);
        channel.sendMessage(eb.build()).queue();
        channel.sendMessage(ctx.getAuthor().getAsMention() + " there you go").queue();
    }

    private String getRolesAsString(List<Role> rolesList) {
        StringBuilder roles;
        if (!rolesList.isEmpty()) {
            Role tempRole = rolesList.get(0);
            roles = new StringBuilder(tempRole.getName());
            for (int i = 1; i < rolesList.size(); i++) {
                tempRole = rolesList.get(i);
                roles.append(", ").append(tempRole.getName());
            }
        } else {
            roles = new StringBuilder("No Roles");
        }
        return roles.toString();
    }

    @Override
    public String getName() {
        return "userinfo";
    }

    @Override
    public String getHelp() {
        return "It gets all information about single user." +
                "Usage: !!userinfo [@name-of-the-user]";
    }

}
