package org.example.entity.leagueoflegends;

public class Summoner {
    public String summonerId;
    public String puuId;
    public String summonerName;
    public String level;
    public String img;
    public Rank rank;

    public String getSummonerId() {
        return summonerId;
    }

    public void setSummonerId(String summonerId) {
        this.summonerId = summonerId;
    }

    public String getPuuId() {
        return puuId;
    }

    public void setPuuId(String puuId) {
        this.puuId = puuId;
    }

    public String getSummonerName() {
        return summonerName;
    }

    public void setSummonerName(String summonerName) {
        this.summonerName = summonerName;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public Rank getRank() {
        return rank;
    }

    public void setRank(Rank rank) {
        this.rank = rank;
    }
}
