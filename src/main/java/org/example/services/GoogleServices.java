package org.example.services;

import org.example.impl.GoogleImpl;

import java.net.http.HttpResponse;

public class GoogleServices {
    private GoogleImpl googleImpl;

    public GoogleServices(){
        googleImpl = new GoogleImpl();
    }

    public HttpResponse<String> getGoogleSearchResult(String query){
        return googleImpl.getGoogleSearchResult(query);
    }
}
