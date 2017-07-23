package com.mypocketstore.seasons.sync;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.mypocketstore.seasons.R;
import com.mypocketstore.seasons.adaptors.ListAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by anshdedha7 on 04/11/15.
 */
public class SearchTask extends AsyncTask<String, Void, ArrayList<HashMap<String, String>>> {

    Context mContext;
    ListAdapter mAdapter;
    TextView mTextView;
    int mConnectionProblemFlag = 0;
    int mNoResultFlag = 0;

    public SearchTask(Context c, ListAdapter a, TextView textView) {
        mContext = c;
        mAdapter = a;
        mTextView = textView;
    }

    private ArrayList<HashMap<String, String>> parseJson(String rawStr) throws JSONException {
        if (!rawStr.equals(null)){
            try {
                ArrayList<HashMap<String, String>> arrayList = new ArrayList<>();
                JSONObject parentObject = new JSONObject(rawStr);
                if(parentObject.getInt("total_results")==0){
                    mNoResultFlag = 1;
                    return null;
                }
                JSONArray resultArray = parentObject.getJSONArray("results");

                for (int i = 0; i < resultArray.length(); i++) {
                    JSONObject show = resultArray.getJSONObject(i);
                    HashMap<String, String> map = new HashMap<>();
                    map.put("poster", show.getString("poster_path"));
                    map.put("title", show.getString("name"));
                    map.put("avg_vote", show.getString("vote_average"));
                    map.put("id", show.getString("id"));
                    map.put("voteCount", show.getString("vote_count"));
                    map.put("genres", null);
                    arrayList.add(map);
                }
                return arrayList;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected ArrayList<HashMap<String, String>> doInBackground(String... params) {

        String string_url = "http://api.themoviedb.org/3/search/tv?api_key=7210257ce98ebcdcc9e4daa7aa235114&query=" + params[0];
        Log.v("SEARCH URL", string_url);
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        try {
            URL url = new URL(string_url);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();

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

        } catch (IOException e) {
            mConnectionProblemFlag = 1;
            e.printStackTrace();
        } catch (JSONException e){
            e.printStackTrace();
        }
        finally {
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

    @Override
    protected void onPostExecute(ArrayList<HashMap<String, String>> list) {
        super.onPostExecute(list);
        if (list != null) {
            mAdapter.clear();
            mAdapter.addAll(list);

        }

        if (mNoResultFlag == 1) {
            mAdapter.clear();
            Toast t = new Toast(mContext).makeText(mContext, "No show found!", Toast.LENGTH_SHORT);
            mTextView.setVisibility(View.VISIBLE);
            t.show();
        }

        if (mConnectionProblemFlag == 1){
            Toast t = new Toast(mContext).makeText(mContext, "No internet connection!", Toast.LENGTH_SHORT);
            t.show();
        }
    }
}
