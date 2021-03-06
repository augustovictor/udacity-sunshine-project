package com.example.android.sunshine.app.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.example.android.sunshine.app.R;
import com.example.android.sunshine.app.fragment.DetailFragment;
import com.example.android.sunshine.app.fragment.ForecastFragment;
import com.example.android.sunshine.app.sync.SunshineSyncAdapter;
import com.example.android.sunshine.app.utils.Utility;


public class MainActivity extends ActionBarActivity implements ForecastFragment.Callback{

    // Used to tag a fragment in a fragmentManager so we can find it easily
    // Fragments can only be tagged in fragment transitions. Thus the same
    // fragment can be used many times with different tags.
    private static final String DETAILFRAGMENT_TAG = "DFTAG";

    private String mLocation;

    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLocation = Utility.getPreferredLocation(this);
        setContentView(R.layout.activity_main);

        if (findViewById(R.id.weather_detail_container) != null) {
            // The detail container view will be present only in the large-screen layouts
            // (res/layout-sw600dp). If this view is present, then the activity should be
            // in two-pane mode.
            mTwoPane = true;

            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            if (savedInstanceState == null) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.weather_detail_container, new DetailFragment(), DETAILFRAGMENT_TAG)
                        .commit();
            }
        } else {
            mTwoPane = false;
            getSupportActionBar().setElevation(0f);
        }

        ForecastFragment ff = ((ForecastFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_forecast));
        ff.setmUseTodayLayout(!mTwoPane);

        SunshineSyncAdapter.initializeSyncAdapter(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        String location = Utility.getPreferredLocation(this);

        // update the location in our second pane using the fragment manager
        if (location != null && !location.equals(mLocation)) {
            ForecastFragment ff = (ForecastFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_forecast);
            if (ff != null) {
                ff.onLocationChanged();
            }

            DetailFragment df = (DetailFragment) getSupportFragmentManager().findFragmentByTag(DETAILFRAGMENT_TAG);
            if (df != null) {
                df.onLocationChanged(location);
            }

            mLocation = location;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifeharedt.xml.
        switch (item.getItemId()) {
            case R.id.action_settings: {
                Intent i = new Intent(this, SettingsActivity.class);
                startActivity(i);
                return true;
            }

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void onItemSelected(Uri dateUri) {
        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle args = new Bundle();
            args.putParcelable(DetailFragment.DETAIL_URI, dateUri);

            DetailFragment df = new DetailFragment();
            df.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.weather_detail_container, df, DETAILFRAGMENT_TAG)
                    .commit();
        } else {
            Intent i = new Intent(this, DetailActivity.class)
                    .setData(dateUri);
            startActivity(i);

        }
    }
}
