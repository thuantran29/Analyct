package com.example.thuannguyen.analyct;

import android.app.Application;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

import java.util.HashMap;

public class MyAnalytics extends Application {
    private static final String PROPERTY_ID = "";

    public static  int GENERAL_TRACKER = 0;
    public enum TrackerName{
        APP_TRACKER,
        GLOBAL_TRACKER,
        ECOMMERE_TRACKER
    }

    public HashMap<TrackerName,Tracker> mTracker = new HashMap<>();

    public MyAnalytics(){
        super();
    }

    public synchronized Tracker getTracker(TrackerName trackerId){
        if (mTracker.containsKey(trackerId)){
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            Tracker t =
                    (trackerId == TrackerName.APP_TRACKER) ? analytics.newTracker(PROPERTY_ID):
                            (trackerId == TrackerName.GLOBAL_TRACKER) ?
                                    analytics.newTracker(R.xml.global_tracker):
                                    analytics.newTracker(R.xml.global_tracker);
            mTracker.put(trackerId,t);

        }
        return mTracker.get(trackerId);
    }
}
