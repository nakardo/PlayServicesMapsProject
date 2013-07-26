package com.example.playservicesmaps.dto;

import com.google.api.client.util.Key;

import java.math.BigDecimal;

/**
 * Created by dacosta on 7/21/13.
 */
public class Item {

    @Key private String thumbnail;
    @Key private String title;
    @Key private BigDecimal price;
    @Key private Address address;
    @Key private Location location;

    public String getTitle() {
        return title;
    }

    public String getPrice() {
        return "$ " + price.toString();
    }

    public Address getAddress() {
        return address;
    }

    public Location getLocation() {
        return location;
    }
}
