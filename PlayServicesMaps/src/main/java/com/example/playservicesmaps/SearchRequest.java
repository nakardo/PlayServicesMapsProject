package com.example.playservicesmaps;

import com.example.playservicesmaps.dto.SearchResult;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.json.jackson.JacksonFactory;
import com.octo.android.robospice.request.googlehttpclient.GoogleHttpClientSpiceRequest;

import java.io.IOException;

/**
 * Created by dacosta on 7/21/13.
 */
public class SearchRequest extends GoogleHttpClientSpiceRequest<SearchResult> {
    private String baseUrl;

    public SearchRequest() {
        super(SearchResult.class);
        this.baseUrl = String.format("https://api.mercadolibre.com/sites/MLA/search?q=%s&state=%s",
                "depto", "TUxBUENBUGw3M2E1");
    }

    @Override
    public SearchResult loadDataFromNetwork() throws IOException {
        HttpRequest request = getHttpRequestFactory().buildGetRequest(new GenericUrl(baseUrl));
        request.setParser(new JacksonFactory().createJsonObjectParser());
        return request.execute().parseAs(getResultType());
    }
}
