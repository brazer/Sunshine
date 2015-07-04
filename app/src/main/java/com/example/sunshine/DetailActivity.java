package com.example.sunshine;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.sunshine.data.WeatherContract;


public class DetailActivity extends ActionBarActivity {

    private static String mMessage;
    private static ShareActionProvider mShareActionProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
        Intent intent = getIntent();
        if (intent!=null) mMessage = intent.getDataString();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment
            implements LoaderManager.LoaderCallbacks<Cursor> {

        private final String LOG_TAG = PlaceholderFragment.class.getSimpleName();
        private final static int sLoaderId = 0;

        private final static String[] FORECAST_COLUMNS = {
                WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
                WeatherContract.WeatherEntry.COLUMN_DATE,
                WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
                WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
                WeatherContract.WeatherEntry.COLUMN_MIN_TEMP
        };
        static final int COL_WEATHER_ID = 0;
        static final int COL_WEATHER_DATE = 1;
        static final int COL_WEATHER_DESC = 2;
        static final int COL_WEATHER_MAX_TEMP = 3;
        static final int COL_WEATHER_MIN_TEMP = 4;

        public PlaceholderFragment() {
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
            String dateString = Utility.formatDate(cursor.getLong(COL_WEATHER_DATE));
            String weatherDescription = cursor.getString(COL_WEATHER_DESC);
            boolean isMetric = Utility.isMetric(getActivity());
            String high = Utility.formatTemperature(
                    getActivity(), cursor.getDouble(COL_WEATHER_MAX_TEMP), isMetric);
            String low = Utility.formatTemperature(
                    getActivity(), cursor.getDouble(COL_WEATHER_MIN_TEMP), isMetric);
            mMessage = String.format("%s - %s - %s/%s", dateString, weatherDescription, high, low);
            TextView detailTextView = (TextView) getView().findViewById(R.id.text_detail);
            detailTextView.setText(mMessage);
            if (mShareActionProvider!=null) {
                mShareActionProvider.setShareIntent(createShareIntent());
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }
    }
}
