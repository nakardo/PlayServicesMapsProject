package com.example.playservicesmaps.dto;

import java.util.ArrayList;
import java.util.List;

import com.google.api.client.util.Key;

/**
 * Created by dacosta on 7/21/13.
 */
public class SearchResult {
    @Key private List<Item> results;

    public List<Item> getResultsWithLocation() {
        List<Item> filteredResults = new ArrayList<Item>();
        for (Item item : results) {
            if (item.getLocation().hasLocation()) filteredResults.add(item);
        }

        return filteredResults;
    }
}
