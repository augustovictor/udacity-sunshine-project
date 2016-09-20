package com.example.android.sunshine.app.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.android.sunshine.app.R;
import com.example.android.sunshine.app.activity.DetailActivity;
import com.example.android.sunshine.app.utils.FetchWeatherTask;

import java.util.ArrayList;

/**
 * A forecastFragment fragment containing a simple view.
 */
public class ForecastFragment extends Fragment {

    private ArrayList<String> weekData;
    private ArrayAdapter<String> mForecastAdapter;

    public ForecastFragment() {
    }

    @Override
    public void onStart() {
        super.onStart();
        updateWeather();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.forecast_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh: {
                updateWeather();
                return true;
            }

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateWeather() {
        SharedPreferences sharedPreferences= PreferenceManager.getDefaultSharedPreferences(getActivity());
        String zipCode = sharedPreferences.getString(
                getString(R.string.pref_location_key),
                getString(R.string.pref_location_default)
        );

        FetchWeatherTask fetchWeatherTask = new FetchWeatherTask(getActivity(), mForecastAdapter);
        fetchWeatherTask.execute(zipCode);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);


        mForecastAdapter = new ArrayAdapter<String>(
                getActivity(),
                R.layout.list_item_forecast,
                R.id.list_item_forecast_tv,
                new ArrayList<String>()
        );

        ListView weatherListView = (ListView) rootView.findViewById(R.id.listview_forecast);
        weatherListView.setAdapter(mForecastAdapter);

        weatherListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String forecast = mForecastAdapter.getItem(position);
                Intent i = new Intent(getActivity(), DetailActivity.class)
                        .putExtra(Intent.EXTRA_TEXT, forecast);
                startActivity(i);

//                Toast.makeText(getActivity(), forecast, Toast.LENGTH_SHORT).show();
            }
        });

        return rootView;
    }

}