package org.example.entity.leagueoflegends;

public class Rank {
    public Integer leaguePoint;
    public String tier;
    public String division;
    public Integer wins;
    public Integer loses;
    public String queueType;

    public Integer getLeaguePoint() {
        return leaguePoint;
    }

    public void setLeaguePoint(Integer leaguePoint) {
        this.leaguePoint = leaguePoint;
    }

    public String getTier() {
        return tier;
    }

    public void setTier(String tier) {
        this.tier = tier;
    }

    public String getDivision() {
        return division;
    }

    public void setDivision(String division) {
        this.division = division;
    }

    public Integer getWins() {
        return wins;
    }

    public void setWins(Integer wins) {
        this.wins = wins;
    }

    public Integer getLoses() {
        return loses;
    }

    public void setLoses(Integer loses) {
        this.loses = loses;
    }

    public String getQueueType() {
        return queueType;
    }

    public void setQueueType(String queueType) {
        this.queueType = queueType;
    }
}
