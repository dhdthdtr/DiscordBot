package org.example;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.*;
import com.sedmelluq.discord.lavaplayer.track.playback.NonAllocatingAudioFrameBuffer;
import discord4j.common.util.Snowflake;
import discord4j.core.DiscordClient;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.lifecycle.ConnectEvent;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.VoiceState;
import discord4j.core.object.entity.*;
import discord4j.core.object.entity.channel.AudioChannel;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.object.entity.channel.VoiceChannel;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.rest.util.Color;
import discord4j.voice.AudioProvider;
import discord4j.voice.VoiceConnection;
import org.apache.http.HttpEntity;
import org.example.impl.Command;
import org.example.provider.LavaPlayerAudioProvider;
import org.example.scheduler.TrackScheduler;
import org.example.utils.StringUtils;
import org.json.JSONObject;
import org.json.JSONString;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.sound.midi.Track;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

public class Program{
    private static final Map<String, String> commandsHelp = new HashMap<>();
    private static final String prefix = "!";

    public static void main(String[] args) {
        commandsHelp.put("ping", "test bot connection");
        commandsHelp.put("avatar", "get avatar from author or user if mentioned");
        commandsHelp.put("myinfo", "get your own information");
        commandsHelp.put("join", "join your voice channel");

        // Creates AudioPlayer instances and translates URLs to AudioTrack instances
        final AudioPlayerManager playerManager = new DefaultAudioPlayerManager();
        // This is an optimization strategy that Discord4J can utilize.
        // It is not important to understand
        playerManager.getConfiguration()
            .setFrameBufferFactory(NonAllocatingAudioFrameBuffer::new);
        // Allow playerManager to parse remote sources like YouTube links
        AudioSourceManagers.registerRemoteSources(playerManager);
        // Create an AudioPlayer so Discord4J can receive audio data
        final AudioPlayer player = playerManager.createPlayer();
        // We will be creating LavaPlayerAudioProvider in the next step
        AudioProvider provider = new LavaPlayerAudioProvider(player);

        DiscordClient client = DiscordClient.create(StringUtils.TOKEN);
        Mono<Void> login = client.withGateway((GatewayDiscordClient gateway) -> {
            // ReadyEvent example
            // TODO: login log
            Mono<Void> printOnLogin = gateway.on(ReadyEvent.class, event ->
                    Mono.fromRunnable(() -> {
                        final User self = event.getSelf();
                        System.out.printf("Logged in as %s#%s%n", self.getUsername(), self.getDiscriminator());
                    }))
            .then();

            // TODO: avatar
            Mono<Void> handleAvatarCommand = gateway.on(MessageCreateEvent.class, event -> {
                Message message = event.getMessage();
                List<PartialMember> mentionedMembers = message.getMemberMentions();
                if (message.getContent().split(" ")[0].equalsIgnoreCase(prefix + "avatar")) {
                    if(mentionedMembers.size() == 0){
                        Optional<User> author = message.getAuthor();
                        if(author.isPresent()) {
                            return message.getChannel()
                                    .flatMap(channel -> channel.createMessage(author.get().getAvatarUrl() + "?size=1024"));
                        }
                    } else if(mentionedMembers.size() == 1){
                        Optional<PartialMember> member = Optional.ofNullable(mentionedMembers.get(0));
                        if(member.isPresent()){
                            return message.getChannel()
                                    .flatMap(channel -> channel.createMessage(member.get().getAvatarUrl() + "?size=1024"));
                        }
                    }
                }

                return Mono.empty();
            }).then();

            // TODO: ping
            // MessageCreateEvent example
            Mono<Void> handlePingCommand = gateway.on(MessageCreateEvent.class, event -> {
                Message message = event.getMessage();

                if (message.getContent().equalsIgnoreCase(prefix + "ping")) {
                    return message.getChannel()
                            .flatMap(channel -> channel.createMessage("Pong!"));
                }

                return Mono.empty();
            }).then();

            // TODO: info
            Mono<Void> handleInfoCommand = gateway.on(MessageCreateEvent.class, event -> {
                Message message = event.getMessage();

                Optional<User> author = message.getAuthor();

                Color authorColorVar = null;
                String authorGlobalNameStr = "";
                String authorAvatarUrlStr = "";

                if(author.isPresent()){
                    Optional<Color> authorColor = author.get().getAccentColor();
                    if(authorColor.isPresent()){
                        authorColorVar = authorColor.get();
                    }

                    Optional<String> authorGlobalName = author.get().getGlobalName();
                    if(authorGlobalName.isPresent()){
                        authorGlobalNameStr = authorGlobalName.get();
                    }

                    authorAvatarUrlStr = author.get().getAvatarUrl();
                }

                if(message.getContent().split(" ")[0].equalsIgnoreCase(prefix + "myinfo")) {
                    EmbedCreateSpec embed = EmbedCreateSpec.builder()
                            .color(authorColorVar == null ? Color.WHITE : authorColorVar)
                            .title(authorGlobalNameStr + "'s info")
//                            .url("https://www.facebook.com/bangnguyen011001/")
                            .author(authorGlobalNameStr, null, authorAvatarUrlStr)
                            .thumbnail(authorAvatarUrlStr + "?size=1024")
                            .addField("- inline field", "value", true)
                            .addField("- inline field", "value", true)
                            .build();
                    return message.getChannel()
                            .flatMap(channel -> channel.createMessage(embed));
                }
                return Mono.empty();
            }).then();

            // TODO: API
            Mono<Void> handleAPICommand = gateway.on(MessageCreateEvent.class, event -> {
                Message message = event.getMessage();
                if(message.getContent().equalsIgnoreCase("!animepic")){
                    Optional<User> author = message.getAuthor();
                    HttpRequest request = HttpRequest.newBuilder()
                            .uri(URI.create("https://pic.re/image"))
                            .method("POST", HttpRequest.BodyPublishers.noBody())
                            .build();
                    HttpResponse<String> response = null;
                    try {
                        response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
                    } catch (IOException | InterruptedException e) {
                        e.printStackTrace();
                    }
                    if(author.isPresent()) {
                        if (response != null) {
                            try {
                                String result = response.body();
                                JSONObject json = new JSONObject(result);

                                // file log API result from user using this command
                                FileWriter fw = new FileWriter("D:/discord_bot_log_api/output_"+author.get().getGlobalName().get()+".json");
                                fw.write(result);
                                fw.close();

                                return message.getChannel()
                                        .flatMap(channel -> channel.createMessage(spec -> {
                                            try {
                                                spec.addFile("image.png", new URL((String) json.get("file_url")).openStream());
                                            } catch (IOException e) {
                                                throw new RuntimeException(e);
                                            }
                                        }));
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                }
                return Mono.empty();
            }).then();

//            // done
//            commands.put("join", event -> Mono.justOrEmpty(event.getMember())
//                    .flatMap(Member::getVoiceState)
//                    .flatMap(VoiceState::getChannel)
//                    // join returns a VoiceConnection which would be required if we were
//                    // adding disconnection features, but for now we are just ignoring it.
//                    .flatMap(channel -> channel.join(spec -> spec.setProvider(provider)))
//                    .then());

            // TODO: join
            Mono<Void> handleJoinCommand = gateway.on(MessageCreateEvent.class, event -> {
                Message message = event.getMessage();

                if (message.getContent().equalsIgnoreCase(prefix + "join")){
                    Member member = event.getMember().get();
                    Mono<VoiceState> voiceState = member.getVoiceState();
                    Mono<AudioChannel> audioChannel = voiceState.flatMap(VoiceState::getChannel);
                    return audioChannel.flatMap(channel -> channel.join(spec -> spec.setProvider(provider)));
                }
                return Mono.empty();
            }).then();

            // TODO: play
            Mono<Void> handlePlayCommand = gateway.on(MessageCreateEvent.class, event -> {
                Message message = event.getMessage();
                String[] strArr = message.getContent().split(" ");
                if (strArr[0].equalsIgnoreCase(prefix + "play")){
                    final TrackScheduler scheduler = new TrackScheduler(player);
                    playerManager.loadItem(strArr[1], scheduler);
                }
                return Mono.empty();
            }).then();

            // TODO: pause
            Mono<Void> handlePauseCommand = gateway.on(MessageCreateEvent.class, event -> {
                Message message = event.getMessage();
                if(message.getContent().equalsIgnoreCase(prefix + "pause")){
                    if(player.getPlayingTrack() != null){
                        player.setPaused(true);
                    }
                }
                return Mono.empty();
            }).then();

            // TODO: resume
            Mono<Void> handleResumeCommand = gateway.on(MessageCreateEvent.class, event -> {
                Message message = event.getMessage();
                if(message.getContent().equalsIgnoreCase(prefix + "resume")){
                    if(player.getPlayingTrack() != null){
                        if(player.isPaused()){
                            player.setPaused(false);
                        }
                    }
                }
                return Mono.empty();
            }).then();

            // TODO: disconnect
            Mono<Void> handleLeaveCommand = gateway.on(MessageCreateEvent.class, event -> {
                Message message = event.getMessage();

                if (message.getContent().equalsIgnoreCase(prefix + "disconnect")){
                    Member member = event.getMember().get();
                    Mono<VoiceState> voiceState = member.getVoiceState();
                    Mono<AudioChannel> audioChannel = voiceState.flatMap(VoiceState::getChannel);
                    if(player.getPlayingTrack() != null){
                        player.destroy();
                    }
                    return audioChannel.flatMap(AudioChannel::sendDisconnectVoiceState);
                }
                return Mono.empty();
            }).then();

            // combine them!
            return printOnLogin
                    // general command
                    .and(handlePingCommand)
                    .and(handleAvatarCommand)
                    .and(handleInfoCommand)
                    .and(handleAPICommand)
                    // voice command
                    .and(handleJoinCommand)
                    .and(handlePlayCommand)
                    .and(handlePauseCommand)
                    .and(handleResumeCommand)
                    .and(handleLeaveCommand);
        });
        login.block();
    }
}