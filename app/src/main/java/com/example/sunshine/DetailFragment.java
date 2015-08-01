package com.example.sunshine;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sunshine.data.WeatherContract;

/**
 * Author: Anatol Salanevich
 * Date: 04.07.2015
 */
public class DetailFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private final String LOG_TAG = DetailFragment.class.getSimpleName();
    private final static int DETAIL_LOADER = 0;
    private static final String LOCATION_KEY = "location";
    private ShareActionProvider mShareActionProvider;
    private String mForecast;
    private String mLocation;
    private String mDateStr;

    private final static String[] DETAIL_COLUMNS = {
            WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
            WeatherContract.WeatherEntry.COLUMN_DATE,
            WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract.WeatherEntry.COLUMN_HUMIDITY,
            WeatherContract.WeatherEntry.COLUMN_PRESSURE,
            WeatherContract.WeatherEntry.COLUMN_WIND_SPEED,
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID
    };
    static final int COL_WEATHER_ID = 0;
    static final int COL_WEATHER_DATE = 1;
    static final int COL_WEATHER_DESC = 2;
    static final int COL_WEATHER_MAX_TEMP = 3;
    static final int COL_WEATHER_MIN_TEMP = 4;
    static final int COL_WEARTHER_HUMIDITY = 5;
    static final int COL_WEARTHER_PRESSURE = 6;
    static final int COL_WEARTHER_WIND = 7;
    static final int COL_WEATHER_CONDITION_ID = 8;

    private ImageView mIconView;
    private TextView mFriendlyDateView;
    private TextView mDateView;
    private TextView mDescriptionView;
    private TextView mHighTempView;
    private TextView mLowTempView;
    private TextView mHumidityView;
    private TextView mWindView;
    private TextView mPressureView;

    public DetailFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        if (arguments != null) {
            mDateStr = arguments.getString(DetailActivity.DATE_KEY);
        }
        if (savedInstanceState != null) {
            mLocation = savedInstanceState.getString(LOCATION_KEY);
        }
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        mIconView = (ImageView) rootView.findViewById(R.id.icon_detail_fragment);
        mDateView = (TextView) rootView.findViewById(R.id.date_text_view_detail_fragment);
        mFriendlyDateView = (TextView) rootView.findViewById(R.id.detail_day_textview);
        mDescriptionView = (TextView) rootView.findViewById(R.id.desc_text_view_detail_fragment);
        mHighTempView = (TextView) rootView.findViewById(R.id.text_view_high_detail_fragment);
        mLowTempView = (TextView) rootView.findViewById(R.id.low_text_view_detail_fragment);
        mHumidityView = (TextView) rootView.findViewById(R.id.humidity_text_view);
        mPressureView = (TextView) rootView.findViewById(R.id.pressure_text_view_detail_fragment);
        mWindView = (TextView) rootView.findViewById(R.id.wind_text_view_detail_fragment);
        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(LOCATION_KEY, mLocation);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_detail_fragment, menu);
        MenuItem menuItem = menu.findItem(R.id.menu_item_share);
        mShareActionProvider =
                (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);
        setShareIntent(createShareIntent());
    }

    private Intent createShareIntent() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, mForecast + " #SunshineApp");
        intent.setType("text/plain");
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        return intent;
    }

    private void setShareIntent(Intent shareIntent) {
        if (mShareActionProvider!=null) {
            mShareActionProvider.setShareIntent(shareIntent);
        } else {
            Log.d(LOG_TAG, "Share Action Provider is null?");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Bundle arguments = getArguments();
        if (arguments != null && arguments.containsKey(DetailActivity.DATE_KEY)
                && mLocation != null && !mLocation.equals(Utility.getPreferredLocation(getActivity()))) {
            getLoaderManager().restartLoader(DETAIL_LOADER, null, this);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            mLocation = savedInstanceState.getString(LOCATION_KEY);
        }
        Bundle arguments = getArguments();
        if (arguments != null && arguments.containsKey(DetailActivity.DATE_KEY)) {
            getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String sortOrder = WeatherContract.WeatherEntry.COLUMN_DATE + " ASC";
        mLocation = Utility.getPreferredLocation(getActivity());
        Uri weatherForLocationUri =
                WeatherContract.WeatherEntry.buildWeatherLocationWithStartDate(mLocation, mDateStr);
        return new CursorLoader(getActivity(),
                weatherForLocationUri, DETAIL_COLUMNS, null, null, sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (!cursor.moveToFirst()) return;
        long date = cursor.getLong(COL_WEATHER_DATE);
        String friendlyDateText = Utility.getDayName(getActivity(), date);
        String dateText = Utility.getFormattedMonthDay(getActivity(), date);
        String weatherDescription = cursor.getString(COL_WEATHER_DESC);
        boolean isMetric = Utility.isMetric(getActivity());
        String high = Utility.formatTemperature(
                getActivity(), cursor.getDouble(COL_WEATHER_MAX_TEMP), isMetric);
        String low = Utility.formatTemperature(
                getActivity(), cursor.getDouble(COL_WEATHER_MIN_TEMP), isMetric);
        String humidity = Utility.formatHumidity(
                getActivity(), cursor.getFloat(COL_WEARTHER_HUMIDITY));
        String pressure = Utility.formatPressure(
                getActivity(), cursor.getFloat(COL_WEARTHER_PRESSURE));
        String wind = Utility.formatWind(getActivity(), cursor.getFloat(COL_WEARTHER_WIND));
        int iconResId = Utility.getArtResourceForWeatherCondition(getWeatherId(cursor));
        mIconView.setImageResource(iconResId);
        mFriendlyDateView.setText(friendlyDateText);
        mDateView.setText(dateText);
        mDescriptionView.setText(weatherDescription);
        mHighTempView.setText(high);
        mLowTempView.setText(low);
        mHumidityView.setText(humidity);
        mPressureView.setText(pressure);
        mWindView.setText(wind);
        // We still need this for the share intent
        mForecast = String.format("%s - %s - %s/%s", dateText, weatherDescription, high, low);
        if (mShareActionProvider!=null) {
            mShareActionProvider.setShareIntent(createShareIntent());
        }
    }

    private int getWeatherId(Cursor cursor) {
        return cursor.getInt(COL_WEATHER_CONDITION_ID);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

}
