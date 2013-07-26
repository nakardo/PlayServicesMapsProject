package com.example.playservicesmaps.dto;

import com.google.api.client.json.JsonString;
import com.google.api.client.util.Key;

import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;

/**
 * Created by dacosta on 7/21/13.
 */
public class Location {
    @Key private String address_line;
    @Key private Filter neighborhood;
    @Key private Filter state;
    @Key private Object latitude;
    @Key private Object longitude;

    public String getAddressLine() {
        return address_line;
    }

    public double getLatitude() {
        return Double.valueOf(latitude.toString());
    }

    public double getLongitude() {
        return Double.valueOf(longitude.toString());
    }

    @Override
    public String toString() {
        return neighborhood.getName() + " - " + state.getName();
    }

    public boolean hasLocation() {
        return StringUtils.isNotEmpty(latitude.toString()) &&
               StringUtils.isNotEmpty(longitude.toString());
    }
}
