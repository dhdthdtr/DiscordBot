package org.example.impl;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class RequestImpl {
    public HttpResponse<String> getSummonerInfo(String summonerName){
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://lmssplus.com/api/v2/lol/search?q=" + summonerName))
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = null;
        try {
            response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return response;
    }

    public HttpResponse<String> getSummonerDB(String summonerID){
        HttpRequest requestDetail = HttpRequest.newBuilder()
                .uri(URI.create("https://lmssplus.com/api/v2/db/summoner/db-lmssplus/" + summonerID))
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = null;
        try {
            response = HttpClient.newHttpClient().send(requestDetail, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return response;
    }

    public HttpResponse<String> getChampInfo(){
        HttpRequest requestDetail = HttpRequest.newBuilder()
                .uri(URI.create("https://ddragon.leagueoflegends.com/cdn/13.13.1/data/en_US/champion.json"))
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = null;
        try {
            response = HttpClient.newHttpClient().send(requestDetail, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return response;
    }

    public HttpResponse<String> getChampTopMasteries(String summonerID){
        HttpRequest requestDetail = HttpRequest.newBuilder()
                .uri(URI.create("https://lmssplus.com/api/v2/lol/mastery/" + summonerID))
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = null;
        try {
            response = HttpClient.newHttpClient().send(requestDetail, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return response;
    }

    public HttpResponse<String> getSummonerRank(String puuid){
        HttpRequest requestDetail = HttpRequest.newBuilder()
                .uri(URI.create("https://lmssplus.com/api/v2/lol/rank/" + puuid))
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = null;
        try {
            response = HttpClient.newHttpClient().send(requestDetail, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return response;
    }
}
