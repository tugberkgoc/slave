package com.goct.command.commands.music;

import com.goct.command.CommandContext;
import com.goct.command.ICommand;
import com.goct.music.IYoutubeApi;
import com.goct.music.PlayerManager;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.managers.AudioManager;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

public class PlayCommand implements ICommand {

    private final IYoutubeApi youtube;

    public PlayCommand() {
        youtube = new IYoutubeApi();
    }

    @Override
    public void handle(CommandContext ctx) {
        List<String> args = ctx.getArgs();
        TextChannel channel = ctx.getChannel();
        AudioManager audioManager = ctx.getGuild().getAudioManager();

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

        if(searchListResponse == null) {
            channel.sendMessage("Please provide a valid youtube name").queue();
            return;
        }

        List<SearchResult> searchResults = searchListResponse.getItems();

        if (searchResults.isEmpty()) {
            channel.sendMessage("Please provide a valid youtube name").queue();
            return;
        }

        if (audioManager.isConnected()) {
            channel.sendMessage("I'm already connected to a channel").queue();
            return;
        }

        GuildVoiceState memberVoiceState = ctx.getMember().getVoiceState();

        assert memberVoiceState != null;
        if (!memberVoiceState.inVoiceChannel()) {
            channel.sendMessage("Please join a voice channel first").queue();
            return;
        }

        VoiceChannel voiceChannel = memberVoiceState.getChannel();
        Member selfMember = ctx.getGuild().getSelfMember();

        assert voiceChannel != null;
        if (!selfMember.hasPermission(voiceChannel, Permission.VOICE_CONNECT)) {
            channel.sendMessageFormat("I am missing permission to join %s", voiceChannel).queue();
            return;
        }

        PlayerManager manager = PlayerManager.getInstance();
        audioManager.openAudioConnection(voiceChannel); // Adding bot to the voice chat
        manager.loadAndPlay(channel, "https://www.youtube.com/watch?v=" + searchResults.get(0).getId().getVideoId());
    }

    @Override
    public String getName() {
        return "play";
    }

    @Override
    public String getHelp() {
        return "Plays a song\n" +
                "Usage: `!!play <song title>`";
    }
}
