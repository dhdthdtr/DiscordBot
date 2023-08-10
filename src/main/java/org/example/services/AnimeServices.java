package org.example.services;

import org.example.impl.AnimeImpl;

import java.net.http.HttpResponse;

public class AnimeServices {
    private AnimeImpl animeImpl;
    public AnimeServices() {
        animeImpl = new AnimeImpl();
    }

    public HttpResponse<String> getAnimePicrePic() {
        return animeImpl.getAnimePicrePic();
    }

    public HttpResponse<String> getAnimeInfo(String name, String type){
        return animeImpl.getAnimeInfo(name, type);
    }

    public HttpResponse<String> getAnimeWaifuPic(String type, String category){
        return animeImpl.getAnimeWaifuPic(type, category);
    }
}
