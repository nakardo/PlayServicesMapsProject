package com.example.playservicesmaps.dto;

import com.google.api.client.util.Key;

/**
 * Created by dacosta on 7/21/13.
 */
public class Filter {

    @Key
    private String id;

    @Key
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
