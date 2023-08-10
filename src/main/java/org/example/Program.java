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
import org.example.entity.Discord.Guild;
import org.example.entity.leagueoflegends.Champion;
import org.example.entity.leagueoflegends.Mastery;
import org.example.entity.leagueoflegends.Rank;
import org.example.entity.leagueoflegends.Summoner;
import org.example.entity.movie.Anime;
import org.example.entity.movie.IMDb;
import org.example.interfaces.RankType;
import org.example.provider.LavaPlayerAudioProvider;
import org.example.scheduler.TrackScheduler;
import org.example.services.AnimeServices;
import org.example.services.GuildServices;
import org.example.services.IMDBServices;
import org.example.services.LeagueServices;
import org.example.utils.MailUtils;
import org.example.utils.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import reactor.core.publisher.Mono;

import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Program{
    private static final String prefix = "^";

    public static void main(String[] args) {
        // services init
        LeagueServices leagueServices = new LeagueServices();
        AnimeServices animeServices = new AnimeServices();
        IMDBServices imdbServices = new IMDBServices();
        GuildServices guildServices = new GuildServices();

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

            // TODO: --------------- general commands ---------------
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

            // TODO: me
            Mono<Void> handleMeCommand = gateway.on(MessageCreateEvent.class, event -> {
                Message message = event.getMessage();
                if(message.getContent().equalsIgnoreCase(prefix + "author")){
                    return message.getChannel()
                            .flatMap(channel -> channel.createMessage("Thiên tài IT, Chúa tể coder, IQ 300, Bill Gate VN, Kẻ huỷ diệt mọi dòng code a.k.a Bằng Nguyễn"));
                }
                return Mono.empty();
            }).then();

            // TODO: --------------- mail commands ---------------
            // TODO: mail
            Mono<Void> handleMailCommand = gateway.on(MessageCreateEvent.class, event -> {
                Message message = event.getMessage();
                String[] msgSplit = message.getContent().split(" ", 2);
                if(msgSplit.length > 1 && msgSplit[0].equalsIgnoreCase(prefix + "mailLogReg")){
                    // validate email
                    Pattern pattern = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
                    Matcher matcher = pattern.matcher(msgSplit[1]);
                    String id = message.getAuthor().get().getId().asString();
                    String name = message.getAuthor().get().getGlobalName().get();
                    if(matcher.matches()){  // if match
                        if(guildServices.checkExistMail(msgSplit[1])){
                            return message.getChannel()
                                    .flatMap(channel -> channel.createMessage("Mail này đã được đăng ký trước đó. Hãy kiểm tra lại mail!!"));
                        }

                        if(guildServices.checkExist(id)){
                            guildServices.updateMail(id, msgSplit[1]);
                            StringBuilder sb = new StringBuilder();
                            sb.append("Đã cập nhật đăng ký thành công logging qua mail\n");
                            sb.append("Mail bạn đã đăng ký: " + msgSplit[1] + "\n");
                            sb.append("ID discord tác giả: " + id + "\n\n\n");
                            sb.append("Thân gửi\n");
                            sb.append("Bot của Bằng");
                            // send mail
                            MailUtils.sendMail("mybot.system@gmail.com", msgSplit[1], "Cập nhật đăng ký logging qua mail", sb.toString());
                            return message.getChannel()
                                    .flatMap(channel -> channel.createMessage("Trạng thái - cập nhật\nĐã gửi mail thông báo!!"));
                        } else {
                            guildServices.insertMail(id, name, msgSplit[1]);
                            StringBuilder sb = new StringBuilder();
                            sb.append("Đã đăng ký thành công logging qua mail\n");
                            sb.append("Mail bạn đã đăng ký: " + msgSplit[1] + "\n");
                            sb.append("ID discord tác giả: " + id + "\n\n\n");
                            sb.append("Thân gửi\n");
                            sb.append("Bot của Bằng");
                            MailUtils.sendMail("mybot.system@gmail.com", msgSplit[1], "Đăng ký logging qua mail", sb.toString());
                            return message.getChannel()
                                    .flatMap(channel -> channel.createMessage("Trạng thái - Đăng ký\nĐã gửi mail thông báo!!"));
                        }
                    } else {
                        return message.getChannel()
                                .flatMap(channel -> channel.createMessage("Giá trị đang truyền vào đang không phải là mail, mời nhập lại"));
                    }
                }
                if(msgSplit.length == 1 && msgSplit[0].equalsIgnoreCase(prefix + "mailLogReg")){
                    return message.getChannel()
                            .flatMap(channel -> channel.createMessage("Để đăng ký logging qua mail, hãy dùng lệnh ^mail {mail_của_bạn}"));
                }
                return Mono.empty();
            }).then();

            // TODO: --------------- server commands ---------------
            // TODO: server
            Mono<Void> handleTestCommand = gateway.on(MessageCreateEvent.class, event -> {
                Message message = event.getMessage();
                if(message.getContent().equalsIgnoreCase(prefix + "server")){
                    Guild guild = guildServices.getJoinedGuildInfo(event.getGuild().block().getName(), event.getGuild().block().getId().asString());
                    if(guild != null){
                        //String guildIcon = event.getGuild().block().getIconUrl().orElse((Object)null);
                        EmbedCreateSpec embed = EmbedCreateSpec.builder()
                                .title(guild.getGuildName())
                                .addField(":homes: Tên server: ", guild.getGuildName(),  false)
                                .addField(":exclamation: Prefix: ", guild.getPrefix(),  true)
                                .build();
                        return message.getChannel()
                                .flatMap(channel -> channel.createMessage(MessageCreateSpec.builder()
                                        .addEmbed(embed)
                                        .build()));
                    }
                }
                return Mono.empty();
            }).then();

            // TODO: --------------- league commands ---------------
            // TODO: league API from lmssplus (đang phát triển)
            Mono<Void> handleLeagueCommand = gateway.on(MessageCreateEvent.class, event -> {
                Message message = event.getMessage();
                String[] msgSplit = message.getContent().split(" ",2);
                if(msgSplit.length != 1 && msgSplit[0].equalsIgnoreCase(prefix + "lmss")){
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
                                        .addField("Tên người chơi", summoner.getSummonerName(), true)
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

            // TODO: --------------- anime commands ---------------
            // TODO: pic.re anime pics API
            Mono<Void> handlePicreCommand = gateway.on(MessageCreateEvent.class, event -> {
                Message message = event.getMessage();
                if(message.getContent().equalsIgnoreCase(prefix + "picre")){
                    Optional<User> author = message.getAuthor();
                    HttpResponse<String> response = animeServices.getAnimePicrePic();
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

            // TODO: waifu.pics anime pics API
            Mono<Void> handleWaifuCommand = gateway.on(MessageCreateEvent.class, event -> {
                Message message = event.getMessage();
                String[] sfwCategory = {"waifu", "neko", "shinobu", "megumin", "bully", "cuddle", "cry", "hug", "awoo", "kiss", "lick", "pat", "smug", "bonk", "yeet", "blush", "smile", "wave", "highfive", "handhold", "nom", "bite", "glomp", "slap", "kill", "kick", "happy", "wink", "poke", "dance", "cringe"};
                String[] nsfwCategory = {"waifu", "neko", "trap", "blowjob"};
                String[] msgSplit = message.getContent().split(" ",2);
                if(msgSplit.length != 1 && msgSplit[0].equalsIgnoreCase(prefix + "waifupic")){
                    if("sfw".equals(msgSplit[1])){
                        int max = sfwCategory.length;
                        int min = 1;
                        Random random = new Random();
                        int result = (random.nextInt(max - min + 1) + min) - 1;
                        HttpResponse<String> response = animeServices.getAnimeWaifuPic((String) msgSplit[1], (String) sfwCategory[result]);
                        if (response != null){
                            JSONObject jsonResponse = new JSONObject((String) response.body());
                            String url = jsonResponse.getString("url");
                            return message.getChannel()
                                    .flatMap(channel -> channel.createMessage(spec -> {
                                        try {
                                            spec.addFile("image.png", new URL(url).openStream());
                                        } catch (IOException e) {
                                            throw new RuntimeException(e);
                                        }
                                    }));
                        }
                    } else if ("nsfw".equals(msgSplit[1])){
                        int max = nsfwCategory.length;
                        int min = 1;
                        Random random = new Random();
                        int result = (random.nextInt(max - min + 1) + min) - 1;
                        HttpResponse<String> response = animeServices.getAnimeWaifuPic((String) msgSplit[1], (String) nsfwCategory[result]);
                        if (response != null) {
                            JSONObject jsonResponse = new JSONObject((String) response.body());
                            String url = jsonResponse.getString("url");
                            return message.getChannel()
                                    .flatMap(channel -> channel.createMessage(spec -> {
                                        try {
                                            spec.addFile("image.png", new URL(url).openStream());
                                        } catch (IOException e) {
                                            throw new RuntimeException(e);
                                        }
                                    }));
                        }
                    } else {
                        return message.getChannel()
                                .flatMap(channel -> channel.createMessage("Để sử dụng được lệnh này, cần nhập đúng format (VD: ^waifupic {sfw|nsfw}"));
                    }
                }
                if (msgSplit.length == 1 && msgSplit[0].equalsIgnoreCase(prefix + "waifupic")){
                    return message.getChannel()
                            .flatMap(channel -> channel.createMessage("Để sử dụng được lệnh này, cần nhập đúng format (VD: ^waifupic {sfw|nsfw}"));
                }
                return Mono.empty();
            }).then();

            // TODO: Jikan
            Mono<Void> handleJikanCommand = gateway.on(MessageCreateEvent.class, event -> {
                Message message = event.getMessage();
                String[] msgSplit = message.getContent().split(" ",2);
                if(msgSplit.length != 1 && msgSplit[0].equalsIgnoreCase(prefix + "jikansfw")){
                    HttpResponse<String> response = animeServices.getAnimeInfo(msgSplit[1].replace(" ", "+"), "sfw");
                    if(response != null){
                        JSONObject jsonResponse = new JSONObject((String) response.body());
                        JSONArray jsonData = new JSONArray((JSONArray) jsonResponse.get("data"));
                        if(jsonData.length() != 0) {
                            JSONObject objData = new JSONObject(jsonData.get(0).toString());
                            JSONObject images = new JSONObject(objData.get("images").toString());

                            String img = images.getJSONObject("jpg").getString("large_image_url");
                            String[] duration = objData.getString("duration").split(" ",3);
                            List<String> genres = new ArrayList<>();
                            JSONArray genresArr = objData.getJSONArray("genres");
                            for(int i = 0; i < genresArr.length(); i++){
                                JSONObject obj = genresArr.getJSONObject(i);
                                genres.add((String) obj.getString("name"));
                            }
                            List<String> themes = new ArrayList<>();
                            JSONArray themesArr = objData.getJSONArray("themes");
                            for(int i = 0; i < themesArr.length(); i++){
                                JSONObject obj = themesArr.getJSONObject(i);
                                themes.add((String) obj.getString("name"));
                            }

                            Anime anime = new Anime();
                            anime.setTitle((String) objData.getString("title"));
                            anime.setId(String.valueOf(objData.getInt("mal_id")));
                            anime.setUrl(objData.getString("url"));
                            anime.setImg(img);
                            anime.setStatus((String) objData.getString("status"));
                            anime.setDuration(duration[0]+" "+duration[1]);
                            anime.setEpisodes(objData.getInt("episodes"));
                            anime.setSynopsis((String) objData.getString("synopsis"));
                            anime.setGenres(String.join(", ", genres));
                            anime.setThemes(String.join(", ", themes));

                            EmbedCreateSpec embed = EmbedCreateSpec.builder()
                                    .title(anime.getTitle())
                                    .url(anime.getUrl())
                                    .thumbnail(anime.getImg())
                                    .description(anime.getSynopsis())
                                    .addField("Status :", anime.getStatus(), false)
                                    .addField(":film_frames: Episodes :", String.valueOf(anime.getEpisodes()), true)
                                    .addField(":stopwatch: Duration :", anime.getDuration(), true)
                                    .addField("Genres:", anime.getGenres(), false)
                                    .addField("Themes:", anime.getThemes(), false)
                                    .build();
                            return message.getChannel()
                                    .flatMap(channel -> channel.createMessage(MessageCreateSpec.builder().addEmbed(embed).build()));
                        } else {
                            return message.getChannel()
                                    .flatMap(channel -> channel.createMessage("Không có thông tin về anime tên \""+ (String) msgSplit[1] +"\""));
                        }
                    }
                }
                if (msgSplit.length == 1 && msgSplit[0].equalsIgnoreCase(prefix + "jikansfw")){
                    return message.getChannel()
                            .flatMap(channel -> channel.createMessage("Để sử dụng được lệnh này, cần nhập đúng format (VD: !jikan {tựa_anime}"));
                }
                return Mono.empty();
            }).then();

            // TODO: --------------- IMDB commands ---------------
            // TODO: IMDB API
            Mono<Void> handleIMDBCommand = gateway.on(MessageCreateEvent.class, event -> {
                Message message = event.getMessage();
                String[] msgSplit = message.getContent().split(" ",2);
                if(msgSplit.length != 1 && msgSplit[0].equalsIgnoreCase(prefix + "imdb")){
                    IMDb imdb = new IMDb();
                    HttpResponse<String> response = imdbServices.getMovieInfo(StringUtils.IMDB_API_KEY, (String) msgSplit[1]);
                    if(response != null){
                        JSONObject jsonIMDB = new JSONObject((String) response.body());
                        if(!jsonIMDB.has("Error")){
                            if (jsonIMDB.has("imdbID")){
                                imdb.setImdbId((String) jsonIMDB.get("imdbID"));
                            }
                            if (jsonIMDB.has("Title")) {
                                imdb.setTitle((String) jsonIMDB.get("Title"));
                            }
                            if (jsonIMDB.has("Year")) {
                                imdb.setYear((String) jsonIMDB.get("Year"));
                            }
                            if (jsonIMDB.has("Released")) {
                                imdb.setReleased((String) jsonIMDB.get("Released"));
                            }
                            if (jsonIMDB.has("Runtime")) {
                                imdb.setRuntime((String) jsonIMDB.get("Runtime"));
                            }
                            if (jsonIMDB.has("Genre")) {
                                imdb.setGenre((String) jsonIMDB.get("Genre"));
                            }
                            if (jsonIMDB.has("Director")) {
                                imdb.setDirector((String) jsonIMDB.get("Director"));
                            }
                            if (jsonIMDB.has("Actors")) {
                                imdb.setActors((String) jsonIMDB.get("Actors"));
                            }
                            if (jsonIMDB.has("Plot")) {
                                imdb.setPlot((String) jsonIMDB.get("Plot"));
                            }
                            if (jsonIMDB.has("Language")) {
                                imdb.setLanguage((String) jsonIMDB.get("Language"));
                            }
                            if (jsonIMDB.has("Poster")) {
                                imdb.setPoster((String) jsonIMDB.get("Poster"));
                            }
                            if (jsonIMDB.has("imdbRating")) {
                                imdb.setImdbRating((String) jsonIMDB.get("imdbRating"));
                            }
                            if (jsonIMDB.has("imdbVotes")) {
                                imdb.setImdbVotes((String) jsonIMDB.get("imdbVotes"));
                            }
                            if (jsonIMDB.has("BoxOffice")) {
                                imdb.setBoxOffice((String) jsonIMDB.get("BoxOffice"));
                            }
                            if (jsonIMDB.has("Type")) {
                                imdb.setType((String) jsonIMDB.get("Type"));
                            }

                            EmbedCreateSpec embed = EmbedCreateSpec.builder()
                                    .color(Color.of(255,202,204))
                                    .title(imdb.getTitle())
                                    .description(imdb.getPlot())
                                    .thumbnail(imdb.getPoster())
                                    .addField(":star: IMDB Rating", imdb.getImdbRating() + "/10", true)
                                    .addField(":star: IMDB Vote", imdb.getImdbVotes(), true)
                                    .addField(":money_with_wings: Box Office", imdb.getBoxOffice() == null ? "NaN" : imdb.getBoxOffice(), false)
                                    .addField(":cinema: Director", imdb.getDirector(), false)
                                    .addField(":film_frames: Actor", imdb.getActors(), false)
                                    .addField(":stopwatch: Runtime", imdb.getRuntime(), true)
                                    .addField(":clapper: Type", imdb.getType(), true)
                                    .addField(":arrow_right: Genres", imdb.getGenre(), false)
                                    .addField(":abc: Language", imdb.getLanguage(), false)
                                    .addField(":calendar_spiral: Released", imdb.getReleased(), false)
                                    .build();

                            return message.getChannel()
                                    .flatMap(channel -> channel.createMessage(embed));
                        } else {
                            return message.getChannel()
                                    .flatMap(channel -> channel.createMessage("Không có thông tin của phim bạn tìm hoặc lỗi API, vui lòng thử lại"));
                        }
                    } else {
                        return message.getChannel()
                                .flatMap(channel -> channel.createMessage("Không có thông tin của phim bạn tìm hoặc lỗi API"));
                    }
                }
                if (msgSplit.length == 1 && msgSplit[0].equalsIgnoreCase(prefix + "imdb")){
                    return message.getChannel()
                            .flatMap(channel -> channel.createMessage("Để sử dụng được lệnh này, cần nhập đúng format (VD: !imdb {tên_phim}"));
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
                    .and(handleTestCommand)
                    .and(handleMeCommand)
                    .and(handleMailCommand)
                    // anime command
                    .and(handlePicreCommand)
                    .and(handleWaifuCommand)
                    .and(handleJikanCommand)
                    // imdb command
                    .and(handleIMDBCommand)
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