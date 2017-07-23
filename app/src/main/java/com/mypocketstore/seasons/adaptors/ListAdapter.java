package com.mypocketstore.seasons.adaptors;

import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mypocketstore.seasons.R;
import com.mypocketstore.seasons.Utility.Utility;
import com.mypocketstore.seasons.sync.FetchThumbnailFromServer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by anshdedha7 on 16/10/15.
 */
public class ListAdapter extends ArrayAdapter<HashMap<String, String>> {
    private final Context mContext;
    private final ArrayList<HashMap<String, String>> mData;
    private final int mLayoutResourceId;

    public ListAdapter(Context context, int layoutResourceId, ArrayList<HashMap<String, String>> arrayList) {
        super(context, layoutResourceId, arrayList);
        mContext = context;
        mLayoutResourceId = layoutResourceId;
        mData = arrayList;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        //ViewHolder holder = null;

        if (row == null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            row = inflater.inflate(mLayoutResourceId,parent, false);
        }

        HashMap<String, String> values = mData.get(position);

        if (values != null) {
            TextView textView1 = (TextView) row.findViewById(R.id.seriesname);
            TextView textView2 = (TextView) row.findViewById(R.id.rating);
            TextView textView3 = (TextView) row.findViewById(R.id.votecount);
            TextView textView4 = (TextView) row.findViewById(R.id.genres);

            if (textView1 != null) {
                textView1.setText(values.get("title"));
            }

            if (textView2 != null && values.get("avg_vote")!=null) {
                if (values.get("avg_vote").equals("10.0")){
                    textView2.setText("10");
                }else {
                    textView2.setText(values.get("avg_vote"));
                }
            }

            if (textView3 != null) {
                textView3.setText(values.get("voteCount"));
            }

            if (textView4 != null) {
                textView4.setText(values.get("genres"));
            }

            ImageView addButton = (ImageView) row.findViewById(R.id.imageViewAdd);
            addButton.setContentDescription(values.get("id"));

            if (Utility.existsInDatabase(values.get("id"), mContext)) {
                addButton.setImageResource(R.drawable.ic_ticked);
            }else {
                addButton.setImageResource(R.drawable.ic_action_add);
            }

            ImageView poster = (ImageView) row.findViewById(R.id.showposter);
            poster.setImageResource(R.drawable.none);

            String path = values.get("poster");
            if (path!=null) {
                if (!path.equals("null")) {
                    FetchThumbnailFromServer fetch = new FetchThumbnailFromServer(mContext, poster);
                    fetch.execute(path);
                }
            }
        }
        return row;
    }
}
