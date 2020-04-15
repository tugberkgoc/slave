package com.goct.command.commands;

import com.goct.command.CommandContext;
import com.goct.command.ICommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import org.menudocs.paste.PasteClient;
import org.menudocs.paste.PasteClientBuilder;
import org.menudocs.paste.PasteHost;

import java.util.List;

public class PasteCommand implements ICommand {

    private final PasteClient client = new PasteClientBuilder()
            .setUserAgent("Menudocs Tutorial bot")
            .setDefaultExpiry("10m")
            .setPasteHost(PasteHost.MENUDOCS) // Optional
            .build();

    @Override
    public void handle(CommandContext ctx) {
        final List<String> args = ctx.getArgs();
        final TextChannel channel = ctx.getChannel();

        if (args.size() < 2) {
            channel.sendMessage("Missing arguments").queue();
            return;
        }

        final String language = args.get(0);
        // final String body = String.join(" ", args.subList(1, args.size()));

        /**
         *  In this method, we have lost new lines
         */

        final String contentRaw = ctx.getMessage().getContentRaw();
        final int index = contentRaw.indexOf(language) + language.length();
        final String body = contentRaw.substring(index).trim();

        client.createPaste(language, body).async(
                (id) -> client.getPaste(id).async((paste) -> {
                    EmbedBuilder embedBuilder = new EmbedBuilder()
                            .setTitle("Paste " + id, paste.getPasteUrl())
                            .setDescription("```")
                            .appendDescription(paste.getLanguage().getId())
                            .appendDescription("\n")
                            .appendDescription(paste.getBody())
                            .appendDescription("```");

                    channel.sendMessage(embedBuilder.build()).queue();
                })
        );
    }

    @Override
    public String getName() {
        return "paste";
    }

    @Override
    public String getHelp() {
        return "Creates a paste on the menudocs paste\n" +
                "Usage: `!!paste [language] [text]`";
    }
}
