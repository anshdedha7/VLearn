package com.mypocketstore.seasons.sync;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubeStandalonePlayer;
import com.mypocketstore.seasons.Utility.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * Created by anshdedha7 on 11/11/15.
 */

public class FetchVideoTask extends AsyncTask<String, Void, String> {

    Activity mContext;

    public FetchVideoTask(Activity c){
        mContext = c;
    }

    private String parseJson(String rawStr) throws JSONException {

        if (!rawStr.equals(null)){
            JSONObject parentObject = new JSONObject(rawStr);
            JSONArray resultArray = parentObject.getJSONArray("results");
            if (resultArray.length()>0) {
                JSONObject video = resultArray.getJSONObject(0);
                String type = video.getString("type");
                if (type.equals("Clip")) {
                    return video.getString("key");
                }
            }
        }
        return null;
    }

    @Override
    protected String doInBackground(String... params) {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String string_url = "http://api.themoviedb.org/3/tv/" + params[0] +
        "/season/" + params[1] + "/episode/" + params[2] + "/videos" + Utility.API_KEY;
        Log.v("Video url", string_url);
        try {
            URL url = new URL(string_url);
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

            String string_json = buffer.toString();
            Log.d("MY TAG", string_json);
            return parseJson(string_json);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    private boolean canResolveIntent(Intent intent) {
        List<ResolveInfo> resolveInfo = mContext.getPackageManager().queryIntentActivities(intent, 0);
        return resolveInfo != null && !resolveInfo.isEmpty();
    }

    @Override
    protected void onPostExecute(String path) {
        super.onPostExecute(path);
        if (path!=null) {

            Intent intent = YouTubeStandalonePlayer.createVideoIntent(
                    mContext, Utility.DEVELOPER_KEY, path, 0, true, true);

            if (intent != null) {
                if (canResolveIntent(intent)) {
                    mContext.startActivityForResult(intent, 1);
                } else {
                    // Could not resolve the intent - must need to install or update the YouTube API service.
                    YouTubeInitializationResult.SERVICE_MISSING
                            .getErrorDialog(mContext, 2).show();
                }
            }
        }else {
            Toast.makeText(mContext, "Video not found", Toast.LENGTH_SHORT).show();
        }
    }
}
