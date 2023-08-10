package org.example.services;

import org.example.impl.IMDBImpl;
import reactor.util.annotation.Nullable;

import java.net.http.HttpResponse;

public class IMDBServices {
    private IMDBImpl imdb;
    public IMDBServices() {
        imdb = new IMDBImpl();
    }

    public HttpResponse<String> getMovieInfo(String apikey, String title) {
        return imdb.getMovieInfo(apikey, title);
    }
}
