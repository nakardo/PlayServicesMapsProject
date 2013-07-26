package com.example.playservicesmaps.dto;

import com.google.api.client.util.Key;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by nakes on 7/26/13.
 */
public class Address {
    @Key private String area_code;
    @Key private String phone1;

    public String getPhone() {
        if (StringUtils.isNotEmpty(area_code) && StringUtils.isNotEmpty(phone1)) {
            return "(" + area_code + ") " + phone1;
        }

        return null;
    }
}
