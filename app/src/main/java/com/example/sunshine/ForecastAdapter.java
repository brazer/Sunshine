package com.example.sunshine;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * {@link ForecastAdapter} exposes a list of weather forecasts
 * from a {@link android.database.Cursor} to a {@link android.widget.ListView}.
 */
public class ForecastAdapter extends CursorAdapter {
    public ForecastAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    /**
     * Prepare the weather high/lows for presentation.
     */
    private String formatHighLows(double high, double low) {
        boolean isMetric = Utility.isMetric(mContext);
        String highLowStr = Utility.formatTemperature(high, isMetric) + "/" + Utility.formatTemperature(low, isMetric);
        return highLowStr;
    }

    /*
        This is ported from FetchWeatherTask --- but now we go straight from the cursor to the
        string.
     */
    private String convertCursorRowToUXFormat(Cursor cursor) {
        String highAndLow = formatHighLows(
                cursor.getDouble(ForecastFragment.COL_WEATHER_MAX_TEMP),
                cursor.getDouble(ForecastFragment.COL_WEATHER_MIN_TEMP));

        return Utility.formatDate(cursor.getLong(ForecastFragment.COL_WEATHER_DATE)) +
                " - " + cursor.getString(ForecastFragment.COL_WEATHER_DESC) +
                " - " + highAndLow;
    }

    /*
        Remember that these views are reused as needed.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item_forecast, parent, false);
    }

    /*
        This is where we fill-in the views with the contents of the cursor.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // our view is pretty simple here --- just a text view
        // we'll keep the UI functional with a simple (and slow!) binding.
        TextView date = (TextView) view.findViewById(R.id.list_item_date_textview);
        date.setText(getDate(cursor));
        TextView forecast = (TextView) view.findViewById(R.id.list_item_forecast_textview);
        forecast.setText(getForecast(cursor));
        TextView high = (TextView) view.findViewById(R.id.list_item_high_textview);
        high.setText(getHighTemp(cursor));
        TextView low = (TextView) view.findViewById(R.id.list_item_low_textview);
        low.setText(getLowTemp(cursor));
    }

    private String getDate(Cursor cursor) {
        return Utility.formatDate(cursor.getLong(ForecastFragment.COL_WEATHER_DATE));
    }

    private String getForecast(Cursor cursor) {
        return cursor.getString(ForecastFragment.COL_WEATHER_DESC);
    }

    private String getHighTemp(Cursor cursor) {
        boolean isMetric = Utility.isMetric(mContext);
        return Utility.formatTemperature(
                cursor.getDouble(ForecastFragment.COL_WEATHER_MAX_TEMP), isMetric);
    }

    private String getLowTemp(Cursor cursor) {
        boolean isMetric = Utility.isMetric(mContext);
        return Utility.formatTemperature(
                cursor.getDouble(ForecastFragment.COL_WEATHER_MIN_TEMP), isMetric);
    }
}