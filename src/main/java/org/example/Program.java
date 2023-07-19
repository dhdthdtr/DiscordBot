package org.example;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.track.playback.NonAllocatingAudioFrameBuffer;
import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.VoiceState;
import discord4j.core.object.entity.*;
import discord4j.core.object.entity.channel.AudioChannel;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.MessageCreateSpec;
import discord4j.rest.util.Color;
import discord4j.voice.AudioProvider;
import org.example.entity.leagueoflegends.Champion;
import org.example.entity.leagueoflegends.Mastery;
import org.example.entity.leagueoflegends.Rank;
import org.example.entity.leagueoflegends.Summoner;
import org.example.interfaces.RankType;
import org.example.provider.LavaPlayerAudioProvider;
import org.example.scheduler.TrackScheduler;
import org.example.services.LeagueServices;
import org.example.utils.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import reactor.core.publisher.Mono;

import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.util.*;

public class Program{
    private static final Map<String, String> commandsHelp = new HashMap<>();
    private static final String prefix = "!";

    public static void main(String[] args) {
        commandsHelp.put("ping", "test bot connection");
        commandsHelp.put("avatar", "get avatar from author or user if mentioned");
        commandsHelp.put("myinfo", "get your own information");
        commandsHelp.put("join", "join your voice channel");

        LeagueServices leagueServices = new LeagueServices();

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

            // TODO: pic.re anime pics API
            Mono<Void> handlePicreCommand = gateway.on(MessageCreateEvent.class, event -> {
                Message message = event.getMessage();
                if(message.getContent().equalsIgnoreCase("!picre")){
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

            // TODO: league API from lmssplus (đang phát triển)
            Mono<Void> handleLeagueCommand = gateway.on(MessageCreateEvent.class, event -> {
                Message message = event.getMessage();
                String[] msgSplit = message.getContent().split(" ",2);
                if(msgSplit.length != 1 && msgSplit[0].equalsIgnoreCase("!lmss")){
                    List<Mastery> masteries = null;
                    HttpResponse<String> summonerInfo = leagueServices.getSummonerInfo(msgSplit[1].replace(" ", "+"));
                    if (summonerInfo != null) {
                        try {
                            String result = summonerInfo.body();
                            JSONObject jsonSummonerInfo = new JSONObject(result);

                            if(!jsonSummonerInfo.has("error")){
                                FileWriter fw = new FileWriter("D:/discord_bot_log_api/lol_output_" + msgSplit[1] + ".json");
                                fw.write(result);
                                fw.close();

                                Summoner summoner = new Summoner();
                                summoner.setSummonerId(String.valueOf(jsonSummonerInfo.get("id")));
                                summoner.setPuuId((String) jsonSummonerInfo.get("puuid"));
                                summoner.setLevel(String.valueOf(jsonSummonerInfo.get("level")));
                                summoner.setSummonerName((String) jsonSummonerInfo.get("name"));
                                summoner.setImg((String) jsonSummonerInfo.get("profileIcon"));

                                // get all responses from APIs
                                HttpResponse<String> summonerDB = leagueServices.getSummonerDB(summoner.getSummonerId());
                                HttpResponse<String> topMasteries = leagueServices.getChampTopMasteries(summoner.getSummonerId());
                                HttpResponse<String> champInfo = leagueServices.getChampInfo();
                                HttpResponse<String> summonerRank = leagueServices.getSummonerRank(summoner.getPuuId());

                                if (summonerDB != null) {
                                    String champKey = "";
                                    masteries = new ArrayList<Mastery>();

                                    JSONObject jsonTopMasteries = new JSONObject(topMasteries.body());
                                    if(!jsonTopMasteries.has("error")){
                                        JSONArray mainChamps = new JSONArray((JSONArray) jsonTopMasteries.get("masteries"));

                                        JSONObject jsonChampInfo = new JSONObject(champInfo.body());
                                        JSONObject jsonChampData = new JSONObject(jsonChampInfo.get("data").toString());
                                        for (int i = 0; i < mainChamps.length(); i++) {
                                            Iterator<String> keys = jsonChampData.keys();
                                            while (keys.hasNext()) {
                                                String key = keys.next();
                                                if (jsonChampData.get(key) instanceof JSONObject) {
                                                    champKey = (String) ((JSONObject) jsonChampData.get(key)).get("key");
                                                }
                                                if (champKey.equals(String.valueOf(((JSONObject) mainChamps.get(i)).get("championId")))) {
                                                    Mastery mastery = new Mastery();
                                                    Champion champion = new Champion();

                                                    mastery.setSummoner(summoner);
                                                    champion.setChampionId((String) ((JSONObject) jsonChampData.get(key)).get("key"));
                                                    champion.setName((String) ((JSONObject) jsonChampData.get(key)).get("name"));
                                                    mastery.setChampion(champion);
                                                    mastery.setScore((String) ((JSONObject) mainChamps.get(i)).get("score"));

                                                    masteries.add(mastery);
                                                    break;
                                                }
                                            }
                                        }
                                    } else {
                                        return message.getChannel()
                                                .flatMap(channel -> channel.createMessage("Lỗi API, vui lòng hãy gọi lệnh sau chốc lát!"));
                                    }
                                }

                                if(summonerRank != null){
                                    JSONObject jsonSummonerRank = new JSONObject((String) summonerRank.body());
                                    if(!jsonSummonerRank.has("error")){
                                        JSONObject jsonHighestRankedEntry = new JSONObject(jsonSummonerRank.get("highestRankedEntry").toString());

                                        Rank mRank = new Rank();
                                        mRank.setDivision(String.valueOf(jsonHighestRankedEntry.get("division")));
                                        mRank.setLeaguePoint((Integer) jsonHighestRankedEntry.get("leaguePoints"));
                                        switch ((String) jsonHighestRankedEntry.get("tier")){
                                            case "IRON":
                                                mRank.setTier(RankType.IRON);
                                                break;
                                            case "BRONZE":
                                                mRank.setTier(RankType.BRONZE);
                                                break;
                                            case "SILVER":
                                                mRank.setTier(RankType.SILVER);
                                                break;
                                            case "GOLD":
                                                mRank.setTier(RankType.GOLD);
                                                break;
                                            case "PLATINUM":
                                                mRank.setTier(RankType.PLATINUM);
                                                break;
                                            case "DIAMOND":
                                                mRank.setTier(RankType.DIAMOND);
                                                break;
                                            case "MASTER":
                                                mRank.setTier(RankType.MASTER);
                                                break;
                                            case "GRANDMASTER":
                                                mRank.setTier(RankType.GRANDMASTER);
                                                break;
                                            case "CHALLENGER":
                                                mRank.setTier(RankType.CHALLENGER);
                                                break;
                                            default:
                                                mRank.setTier(RankType.NA);
                                                break;
                                        }

                                        switch ((String) jsonHighestRankedEntry.get("queueType")){
                                            case "RANKED_FLEX_SR":
                                                mRank.setQueueType(RankType.RANKED_FLEX_SR);
                                                break;
                                            case "RANKED_SOLO_5x5":
                                                mRank.setQueueType(RankType.RANKED_SOLO_5v5);
                                                break;
                                            case "RANKED_TFT":
                                                mRank.setQueueType(RankType.RANKED_TFT);
                                                break;
                                        }
                                        summoner.setRank(mRank);
                                    } else {
                                        return message.getChannel()
                                                .flatMap(channel -> channel.createMessage("Lỗi API, vui lòng hãy gọi lệnh sau chốc lát!"));
                                    }
                                }

                                String rankStr = "";
                                if(!RankType.NA.equals(summoner.getRank().getTier())){
                                    rankStr = summoner.getRank().getTier() + " " + summoner.getRank().getDivision() + " (" + summoner.getRank().getQueueType() + ") " + summoner.getRank().getLeaguePoint() + "LP";
                                } else {
                                    rankStr = summoner.getRank().getTier();
                                }

                                EmbedCreateSpec embed = EmbedCreateSpec.builder()
                                        .color(Color.of(22,13,51))
                                        .title(summoner.getSummonerName())
                                        .url("https://lmssplus.com/profile/" + summoner.getSummonerName().replace(" ", "+"))
                                        .thumbnail("https://ddragon.leagueoflegends.com/cdn/13.13.1/img/profileicon/" + summoner.getImg())
                                        .description(summoner.getSummonerId())
                                        .addField("⋅⋅* Tên người chơi", summoner.getSummonerName(), true)
                                        .addField("Cấp", summoner.getLevel(), true)
                                        .addField("Rank", rankStr, true)
                                        .addField("\u200B", "\u200B", false)
                                        .addField(masteries.get(0).getChampion().getName(), masteries.get(0).getScore(), true)
                                        .addField(masteries.get(1).getChampion().getName(), masteries.get(1).getScore(), true)
                                        .addField(masteries.get(2).getChampion().getName(), masteries.get(2).getScore(), true)
                                        .footer(message.getAuthor().get().getUsername(), message.getAuthor().get().getAvatarUrl())
                                        .timestamp(Instant.now())
                                        .build();

                                return message.getChannel()
                                        .flatMap(channel -> channel.createMessage(MessageCreateSpec.builder()
                                                .content("Đã tìm xong người chơi " + summoner.getSummonerName())
                                                .addEmbed(embed)
                                                .build()));
                            } else {
                                return message.getChannel()
                                        .flatMap(channel -> channel.createMessage("Đã xảy ra lỗi, không tìm thấy người chơi " + (String) msgSplit[1]));
                            }
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }

                if (msgSplit.length == 1 && msgSplit[0].equalsIgnoreCase("!lmss")){
                    return message.getChannel()
                            .flatMap(channel -> channel.createMessage("Để sử dụng được lệnh này, cần nhập đúng format (VD: !lmss {tên_người_chơi}"));
                }
                return Mono.empty();
            }).then();

            Mono<Void> handleAnimeInfoCommand = gateway.on(MessageCreateEvent.class, event -> {
                Message message = event.getMessage();
                String[] msgSplit = message.getContent().split(" ",2);
                if(msgSplit.length != 1 && msgSplit[0].equalsIgnoreCase("!animeinfo")){

                }
                if (msgSplit.length == 1 && msgSplit[0].equalsIgnoreCase("!animeinfo")){
                    return message.getChannel()
                            .flatMap(channel -> channel.createMessage("Để sử dụng được lệnh này, cần nhập đúng format (VD: !animeinfo {tựa_anime}"));
                }
                return Mono.empty();
            }).then();

            // TODO: --------------- music commands ---------------
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
                    // anime command
                    .and(handlePicreCommand)
                    .and(handleAnimeInfoCommand)
                    // league command
                    .and(handleLeagueCommand)
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