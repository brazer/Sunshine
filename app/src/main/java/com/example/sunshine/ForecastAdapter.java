package com.example.sunshine;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * {@link ForecastAdapter} exposes a list of weather forecasts
 * from a {@link android.database.Cursor} to a {@link android.widget.ListView}.
 */
public class ForecastAdapter extends CursorAdapter {

    private final int VIEW_TYPE_TODAY = 0;
    private final int VIEW_TYPE_FUTURE_DAY = 1;

    public ForecastAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    /*
        Remember that these views are reused as needed.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        int viewType = getItemViewType(cursor.getPosition());
        int layoutId = -1;
        if (viewType==VIEW_TYPE_TODAY) layoutId = R.layout.list_item_forecast_today;
        else if (viewType==VIEW_TYPE_FUTURE_DAY) layoutId = R.layout.list_item_forecast;

        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);
        ViewHolder holder = new ViewHolder(view);
        view.setTag(holder);
        return view;
    }

    @Override
    public int getItemViewType(int position) {
        return (position==0) ? VIEW_TYPE_TODAY : VIEW_TYPE_FUTURE_DAY;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    /*
        This is where we fill-in the views with the contents of the cursor.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // our view is pretty simple here --- just a text view
        // we'll keep the UI functional with a simple (and slow!) binding.
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        viewHolder.iconView.setImageResource(R.mipmap.ic_launcher);
        viewHolder.dateView.setText(getDate(cursor));
        viewHolder.descView.setText(getForecast(cursor));
        viewHolder.highView.setText(getHighTemp(cursor));
        viewHolder.lowView.setText(getLowTemp(cursor));
    }

    private String getDate(Cursor cursor) {
        return Utility.getFriendlyDayString(
                mContext, cursor.getLong(ForecastFragment.COL_WEATHER_DATE));
    }

    private String getForecast(Cursor cursor) {
        return cursor.getString(ForecastFragment.COL_WEATHER_DESC);
    }

    private String getHighTemp(Cursor cursor) {
        boolean isMetric = Utility.isMetric(mContext);
        return Utility.formatTemperature(
                mContext, cursor.getDouble(ForecastFragment.COL_WEATHER_MAX_TEMP), isMetric);
    }

    private String getLowTemp(Cursor cursor) {
        boolean isMetric = Utility.isMetric(mContext);
        return Utility.formatTemperature(
                mContext, cursor.getDouble(ForecastFragment.COL_WEATHER_MIN_TEMP), isMetric);
    }

    private static class ViewHolder {

        public ImageView iconView;
        public TextView dateView;
        public TextView descView;
        public TextView highView;
        public TextView lowView;

        public ViewHolder(View view) {
            iconView = (ImageView) view.findViewById(R.id.list_item_icon);
            dateView = (TextView) view.findViewById(R.id.list_item_date_textview);
            descView = (TextView) view.findViewById(R.id.list_item_forecast_textview);
            highView = (TextView) view.findViewById(R.id.list_item_high_textview);
            lowView = (TextView) view.findViewById(R.id.list_item_low_textview);
        }

    }

}