package com.goct.command.commands.music;

import com.goct.command.CommandContext;
import com.goct.command.ICommand;
import com.goct.music.IYoutubeApi;
import com.goct.music.PlayerManager;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.managers.AudioManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class SearchCommand implements ICommand {

    private final IYoutubeApi youtube;
    private final EventWaiter waiter;
    private final Logger LOGGER = LoggerFactory.getLogger(SearchCommand.class);

    public SearchCommand(EventWaiter waiter) {
        youtube = new IYoutubeApi();
        this.waiter = waiter;
    }

    @Override
    public void handle(CommandContext ctx) {
        List<String> args = ctx.getArgs();
        TextChannel channel = ctx.getChannel();

        if (args.isEmpty()) {
            channel.sendMessage("Please provide some arguments").queue();
            return;
        }

        SearchListResponse searchListResponse = null;
        try {
            searchListResponse = youtube.getVideoList(String.join(" ", args));
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }

        if (searchListResponse == null) {
            channel.sendMessage("Please provide a valid youtube name").queue();
            return;
        }

        List<SearchResult> searchResults = searchListResponse.getItems();

        if (searchResults.isEmpty()) {
            channel.sendMessage("Please provide a valid youtube name").queue();
            return;
        }

        int temp = 1;
        List<String> youtubeIds = new ArrayList<>();
        StringBuilder songNames = new StringBuilder();
        for (SearchResult searchResult : searchResults) {
            songNames.append("\n");
            songNames.append(temp).append(")\t");
            songNames.append(searchResult.getSnippet().getTitle());
            youtubeIds.add(searchResult.getId().getVideoId());
            temp++;
        }

        channel.sendMessage("Choose one of them\n" + songNames.toString()).queue((message) -> {
            LOGGER.info(String.valueOf(message));
            initWaiter(ctx.getMember(), channel, youtubeIds);
        });
    }

    public void initWaiter(Member member, TextChannel channel, List<String> youtubeIds) {
        waiter.waitForEvent(
                MessageReceivedEvent.class,
                (event) -> member == event.getMember() && channel.getIdLong() == event.getChannel().getIdLong(),
                (event) -> {
                    PlayerManager manager = PlayerManager.getInstance();
                    AudioManager audioManager = event.getGuild().getAudioManager();

                    GuildVoiceState memberVoiceState = Objects.requireNonNull(event.getMember()).getVoiceState();

                    assert memberVoiceState != null;
                    if (!memberVoiceState.inVoiceChannel()) {
                        channel.sendMessage("Please join a voice channel first").queue();
                        return;
                    }

                    VoiceChannel voiceChannel = memberVoiceState.getChannel();
                    Member selfMember = event.getGuild().getSelfMember();

                    assert voiceChannel != null;
                    if (!selfMember.hasPermission(voiceChannel, Permission.VOICE_CONNECT)) {
                        channel.sendMessageFormat("I am missing permission to join %s", voiceChannel).queue();
                        return;
                    }

                    int idNumber;
                    try {
                        idNumber = Integer.parseInt(event.getMessage().getContentDisplay());
                    } catch (NumberFormatException e) {
                        LOGGER.info("The user entered wrong type which is different than integer");
                        channel.sendMessage("You entered wrong value. Please, Try it again!").queue();
                        return;
                    }

                    if (idNumber > 5 || idNumber < 1) {
                        channel.sendMessage("You entered wrong interval. Please, Try it again!").queue();
                        return;
                    }

                    audioManager.openAudioConnection(voiceChannel);
                    manager.loadAndPlay(channel, "https://www.youtube.com/watch?v=" + youtubeIds.get(idNumber - 1));
                }, 10, TimeUnit.SECONDS,
                () -> {
                    channel.sendMessage("I stop listening for search command").queue();
                }
        );
    }

    @Override
    public String getName() {
        return "search";
    }

    @Override
    public String getHelp() {
        return "Searches songs\n" +
                "Usage: `!!search <title>`";
    }

}
