package org.example.impl;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class AnimeImpl {
    public HttpResponse<String> getAnimePicrePic(){
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
        return response;
    }

    public HttpResponse<String> getAnimeInfo(String name, String type){
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.jikan.moe/v4/anime?q="+ name + "&" + type))
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

    public HttpResponse<String> getAnimeWaifuPic(String type, String category){
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.waifu.pics/"+type+"/"+category))
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

    public HttpResponse<String> getMangaInfo(String name){
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.jikan.moe/v4/manga?q="+ name))
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
}
