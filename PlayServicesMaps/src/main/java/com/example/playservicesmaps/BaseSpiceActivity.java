package com.example.playservicesmaps;

import android.support.v4.app.FragmentActivity;

import com.octo.android.robospice.SpiceManager;

/**
 * Created by dacosta on 7/21/13.
 */
public abstract class BaseSpiceActivity extends FragmentActivity {
    private SpiceManager spiceManager = new SpiceManager(SearchService.class);

    @Override
    protected void onStart() {
        spiceManager.start(this);
        super.onStart();
    }

    @Override
    protected void onStop() {
        spiceManager.shouldStop();
        super.onStop();
    }

    protected SpiceManager getSpiceManager() {
        return spiceManager;
    }

}
