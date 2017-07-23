package com.mypocketstore.seasons.sync;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.mypocketstore.seasons.data.DatabaseContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;

/**
 * Created by anshdedha7 on 05/10/15.
 */
public class AddSeasonsToDbTask extends AsyncTask<String, Void, Void> {

    private final String LOG_TAG = "LOG TAG";

    private final Context mContext;

    public AddSeasonsToDbTask(Context context){
        mContext = context;
    }

    private void parseAndAddSeasons(String rawStr, String _ID) throws JSONException{
        JSONObject parentObj = new JSONObject(rawStr);

        int season_id = parentObj.getInt("id");
        int season_number = parentObj.getInt("season_number");
        String air_date = parentObj.getString("air_date");
        String name = parentObj.getString("name");
        String overview = parentObj.getString("overview");
        String poster_path = parentObj.getString("poster_path");
        JSONArray episodeArray = parentObj.getJSONArray("episodes");
        int episode_count = episodeArray.length();

        Cursor cursor = mContext.getContentResolver().query(
                DatabaseContract.SeasonEntry.CONTENT_URI,
                new String[]{DatabaseContract.SeasonEntry._ID},
                DatabaseContract.SeasonEntry.COLUMN_ID + " = ?",
                new String[]{String.valueOf(season_id)},
                null);
        long seasonId;

        if (cursor.moveToFirst()) {
            int IdIndex = cursor.getColumnIndex(DatabaseContract.SeasonEntry._ID);
            seasonId = cursor.getLong(IdIndex);
        }
        else {

            ContentValues contentValues = new ContentValues();

            contentValues.put(DatabaseContract.SeasonEntry.COLUMN_SHOW_KEY, _ID);
            contentValues.put(DatabaseContract.SeasonEntry.COLUMN_ID, season_id);
            contentValues.put(DatabaseContract.SeasonEntry.COLUMN_NAME, name);
            contentValues.put(DatabaseContract.SeasonEntry.COLUMN_POSTER_PATH, poster_path);
            contentValues.put(DatabaseContract.SeasonEntry.COLUMN_SEASON_NUMBER, season_number);
            contentValues.put(DatabaseContract.SeasonEntry.COLUMN_AIR_DATE, air_date);
            contentValues.put(DatabaseContract.SeasonEntry.COLUMN_OVERVIEW, overview);
            contentValues.put(DatabaseContract.SeasonEntry.COLUMN_EPISODE_COUNT, episode_count);

            Uri insertedUri = mContext.getContentResolver().insert(DatabaseContract.SeasonEntry.CONTENT_URI, contentValues);
            seasonId = ContentUris.parseId(insertedUri);

            cursor.close();

            Vector<ContentValues> cVVector = new Vector<>(episode_count);

            for (int i = 0; i < episode_count; i++) {
                JSONObject episode = episodeArray.getJSONObject(i);
                int episode_id = episode.getInt("id");
                String ename = episode.getString("name");
                String eposter_path = episode.getString("still_path");
                String eair_date = episode.getString("air_date");
                String eoverview = episode.getString("overview");
                int enumber = episode.getInt("episode_number");

                ContentValues listValues = new ContentValues();

                listValues.put(DatabaseContract.EpisodeEntry.COLUMN_SEASON_KEY, seasonId);
                listValues.put(DatabaseContract.EpisodeEntry.COLUMN_EPISODE_ID, episode_id);
                listValues.put(DatabaseContract.EpisodeEntry.COLUMN_NAME, ename);
                listValues.put(DatabaseContract.EpisodeEntry.COLUMN_BACKDROP, eposter_path);
                listValues.put(DatabaseContract.EpisodeEntry.COLUMN_EPISODE_NO, enumber);
                listValues.put(DatabaseContract.EpisodeEntry.COLUMN_DATE, eair_date);
                listValues.put(DatabaseContract.EpisodeEntry.COLUMN_OVERVIEW, eoverview);

                cVVector.add(listValues);
            }

            if (cVVector.size() > 0) {
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);
                mContext.getContentResolver().bulkInsert(DatabaseContract.EpisodeEntry.CONTENT_URI, cvArray);
            }

            Cursor cur = mContext.getContentResolver().query(DatabaseContract.EpisodeEntry.CONTENT_URI,
                    null, null, null, null);

            cVVector = new Vector<>(cur.getCount());
            if (cur.moveToFirst()) {
                do {
                    ContentValues cv = new ContentValues();
                    DatabaseUtils.cursorRowToContentValues(cur, cv);
                    cVVector.add(cv);
                } while (cur.moveToNext());
            }

            cur.close();
            Log.d(LOG_TAG, "FetchWeatherTask Complete. " + cVVector.size() + " Inserted");
        }
    }

    @Override
    protected Void doInBackground(String... params) {

        final String api_key = "?api_key=7210257ce98ebcdcc9e4daa7aa235114";
        final String base_url = "http://api.themoviedb.org/3/tv/";

        String[] tempStr = params[0].split(" ");
        String _ID = tempStr[0];
        String show_ID = tempStr[1];
        int season_count = Integer.parseInt(tempStr[2]);

        String builtUrl = base_url + show_ID + "/season/";

        String seasonJsonString;
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        for (int i = 1; i <= season_count; i++) {

            try {
                String finalUrl = builtUrl + i + api_key;
                URL url = new URL(finalUrl);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    return null;
                }

                seasonJsonString = buffer.toString();
                Log.v("MY TAG", seasonJsonString);

            }catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
            try{
                parseAndAddSeasons(seasonJsonString, _ID);
            }catch (JSONException e){
                Log.e(LOG_TAG, "Error on Line NUMBER 96");
            }
        }


        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        Toast.makeText(mContext, "Show added!", Toast.LENGTH_SHORT).show();
    }
}
