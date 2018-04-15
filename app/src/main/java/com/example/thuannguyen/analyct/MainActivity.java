package com.example.thuannguyen.analyct;

import android.content.Intent;
import android.content.res.XmlResourceParser;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private static final ImageInfo[] IMAGE_INFOS = {
            new ImageInfo(R.drawable.ic_launcher_foreground, 1),
            new ImageInfo(R.drawable.ic_launcher_foreground, 2),
            new ImageInfo(R.drawable.ic_launcher_foreground, 3),
            new ImageInfo(R.drawable.ic_launcher_foreground, 4),
    };

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide fragments for each image.
     * This uses a {@link FragmentPagerAdapter}, which keeps every loaded fragment in memory.
     */
    @SuppressWarnings("FieldCanBeLocal")
    private ImagePagerAdapter mImagePagerAdapter;

    /**
     * The {@link ViewPager} that will host the patterns.
     */
    private ViewPager mViewPager;

    /**
     * The {@link Tracker} used to record screen views.
     */
    private Tracker mTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Make sure global_tracker.xml is configured
        if (!checkConfiguration()) {
            View contentView = findViewById(android.R.id.content);
            Toast.makeText(this, "Bad config", Toast.LENGTH_SHORT).show();
        }

        // [START shared_tracker]
        // Obtain the shared Tracker instance.
        AnalyticsApplication application = (AnalyticsApplication) getApplication();
        mTracker = application.getDefaultTracker();
        mTracker.setScreenName("MainActivity");
        // [END shared_tracker]

        // Create the adapter that will return a fragment for each image.
        mImagePagerAdapter = new ImagePagerAdapter(getSupportFragmentManager(), IMAGE_INFOS);

        // Set up the ViewPager with the pattern adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mImagePagerAdapter);

        // When the visible image changes, send a screen view hit.
        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                sendScreenImageName();
            }
        });

        // Send initial screen screen view hit.
        sendScreenImageName();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_share:
                // [START custom_event]
                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Action")
                        .setAction("Share")
                        .build());
                // [END custom_event]

                String name = getCurrentImageTitle();
                String text = "I'd love you to hear about " + name;

                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, text);
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
                break;

            case R.id.button:
                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Clicks")
                        .setAction("Button")
                        .setLabel("Clicked")
                        .build());
                break;
        }
        return false;
    }

    /**
     * Return the title of the currently displayed image.
     *
     * @return title of image
     */
    private String getCurrentImageTitle() {
        int position = mViewPager.getCurrentItem();
        ImageInfo info = IMAGE_INFOS[position];
        return getString(info.title);
    }

    /**
     * Record a screen view hit for the visible {@link ImageFragment} displayed
     * inside {@link FragmentPagerAdapter}.
     */
    private void sendScreenImageName() {
        String name = getCurrentImageTitle();

        // [START screen_view_hit]
        Log.i(TAG, "Setting screen name: " + name);
        mTracker.setScreenName("Image~" + name);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        // [END screen_view_hit]
    }

    /**
     * Check to make sure global_tracker.xml was configured correctly (this function only needed
     * for sample apps).
     */
    private boolean checkConfiguration() {
        XmlResourceParser parser = getResources().getXml(R.xml.global_tracker);

        boolean foundTag = false;
        try {
            while (parser.getEventType() != XmlResourceParser.END_DOCUMENT) {
                if (parser.getEventType() == XmlResourceParser.START_TAG) {
                    String tagName = parser.getName();
                    String nameAttr = parser.getAttributeValue(null, "name");

                    foundTag = "string".equals(tagName) && "ga_trackingId".equals(nameAttr);
                }

                if (parser.getEventType() == XmlResourceParser.TEXT) {
                    if (foundTag && parser.getText().contains("REPLACE_ME")) {
                        return false;
                    }
                }

                parser.next();
            }
        } catch (Exception e) {
            Log.w(TAG, "checkConfiguration", e);
            return false;
        }

        return true;
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class ImagePagerAdapter extends FragmentPagerAdapter {

        private final ImageInfo[] infos;

        public ImagePagerAdapter(FragmentManager fm, ImageInfo[] infos) {
            super(fm);
            this.infos = infos;
        }

        @Override
        public Fragment getItem(int position) {
            ImageInfo info = infos[position];
            return ImageFragment.newInstance(info.image);
        }

        @Override
        public int getCount() {
            return infos.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (position < 0 || position >= infos.length) {
                return null;
            }
            Locale l = Locale.getDefault();
            ImageInfo info = infos[position];
            return getString(info.title).toUpperCase(l);
        }
    }
}