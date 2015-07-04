package com.example.sunshine;

import android.content.Intent;
import android.database.Cursor;
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
    private final static int sLoaderId = 0;
    private static ShareActionProvider mShareActionProvider;
    private static String mMessage;

    private final static String[] FORECAST_COLUMNS = {
            WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
            WeatherContract.WeatherEntry.COLUMN_DATE,
            WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract.WeatherEntry.COLUMN_HUMIDITY,
            WeatherContract.WeatherEntry.COLUMN_PRESSURE,
            WeatherContract.WeatherEntry.COLUMN_WIND_SPEED
    };
    static final int COL_WEATHER_ID = 0;
    static final int COL_WEATHER_DATE = 1;
    static final int COL_WEATHER_DESC = 2;
    static final int COL_WEATHER_MAX_TEMP = 3;
    static final int COL_WEATHER_MIN_TEMP = 4;
    static final int COL_WEARTHER_HUMIDITY = 5;
    static final int COL_WEARTHER_PRESSURE = 6;
    static final int COL_WEARTHER_WIND = 7;

    public DetailFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        return rootView;
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
        intent.putExtra(Intent.EXTRA_TEXT, mMessage + " #SunshineApp");
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
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(sLoaderId, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        Intent intent = getActivity().getIntent();
        if (intent==null) return null;
        return new CursorLoader(getActivity(),
                intent.getData(), FORECAST_COLUMNS, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (!cursor.moveToFirst()) return;
        String dateString = Utility.getFriendlyDayString(
                getActivity(), cursor.getLong(COL_WEATHER_DATE));
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
        ImageView iconView = (ImageView) getView().findViewById(R.id.icon_detail_fragment);
        iconView.setImageResource(R.mipmap.ic_launcher);
        TextView dateView = (TextView) getView().findViewById(R.id.date_text_view_detail_fragment);
        dateView.setText(dateString);
        TextView descView = (TextView) getView().findViewById(R.id.desc_text_view_detail_fragment);
        descView.setText(weatherDescription);
        TextView highView = (TextView) getView().findViewById(R.id.text_view_high_detail_fragment);
        highView.setText(high);
        TextView lowView = (TextView) getView().findViewById(R.id.low_text_view_detail_fragment);
        lowView.setText(low);
        TextView humidityView = (TextView) getView().findViewById(R.id.humidity_text_view);
        humidityView.setText(humidity);
        TextView pressureView = (TextView) getView().findViewById(R.id.pressure_text_view_detail_fragment);
        pressureView.setText(pressure);
        TextView windView = (TextView) getView().findViewById(R.id.wind_text_view_detail_fragment);
        windView.setText(wind);
        if (mShareActionProvider!=null) {
            mShareActionProvider.setShareIntent(createShareIntent());
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

}
