package org.example.services;

import org.example.impl.RequestImpl;

import java.net.http.HttpResponse;

public class LeagueServices {
    private RequestImpl request;
    public LeagueServices() {
        request = new RequestImpl();
    }

    public HttpResponse<String> getSummonerInfo(String summonerName){
        return request.getSummonerInfo(summonerName);
    }

    public HttpResponse<String> getSummonerDB(String summonerID){
        return request.getSummonerDB(summonerID);
    }

    public HttpResponse<String> getChampInfo(){
        return request.getChampInfo();
    }

    public HttpResponse<String> getChampTopMasteries(String summonerID){
        return request.getChampTopMasteries(summonerID);
    }

    public HttpResponse<String> getSummonerRank(String puuid){
        return request.getSummonerRank(puuid);
    }
}
