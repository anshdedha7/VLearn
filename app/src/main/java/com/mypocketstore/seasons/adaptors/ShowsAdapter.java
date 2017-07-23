package com.mypocketstore.seasons.adaptors;

import android.content.Context;
import android.content.ContextWrapper;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mypocketstore.seasons.MainActivity;
import com.mypocketstore.seasons.R;
import com.mypocketstore.seasons.Utility.Utility;
import com.mypocketstore.seasons.sync.FetchImageFromServer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * Created by anshdedha7 on 07/11/15.
 */
public class ShowsAdapter extends CursorAdapter {


    public ShowsAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }


    public static class ViewHolder {
        public final ImageView menuView;
        public final ImageView posterView;
        public final TextView titleView;
        public final TextView voteAverageView;
        public final TextView voteCountView;
        public final TextView episodeCountView;
        public final TextView seasonCountView;
        public final TextView genresView;

        public ViewHolder(View view) {
            menuView = (ImageView) view.findViewById(R.id.imageViewShowsContextMenu);
            posterView = (ImageView) view.findViewById(R.id.showposter);
            titleView = (TextView) view.findViewById(R.id.seriesname);
            voteAverageView = (TextView) view.findViewById(R.id.rating);
            voteCountView = (TextView) view.findViewById(R.id.votecount);
            episodeCountView = (TextView) view.findViewById(R.id.episodeCount);
            seasonCountView = (TextView) view.findViewById(R.id.seasonCount);
            genresView = (TextView) view.findViewById(R.id.genres);
        }
    }

    Bitmap getImageFromDb(String filename) throws FileNotFoundException {
        ContextWrapper cw = new ContextWrapper(mContext);
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        File f = new File(directory, filename);
        return BitmapFactory.decodeStream(new FileInputStream(f));
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ViewHolder viewHolder = (ViewHolder) view.getTag();

        try {
            viewHolder.posterView.setImageBitmap(getImageFromDb(cursor.getString(8)));
        }catch (FileNotFoundException e) {
            FetchImageFromServer fetch = new FetchImageFromServer(context, viewHolder);
            fetch.execute(cursor.getString(8));
        }

        viewHolder.menuView.setContentDescription(cursor.getString(7));


        viewHolder.titleView.setText(cursor.getString(1));
        viewHolder.episodeCountView.setText(cursor.getString(2));
        viewHolder.genresView.setText(cursor.getString(3));
        viewHolder.voteCountView.setText(cursor.getString(4));
        viewHolder.voteAverageView.setText(cursor.getString(5));
        viewHolder.seasonCountView.setText(cursor.getString(6));
    }

    @Override
    public View newView(Context context, final Cursor cursor, ViewGroup parent) {

        Log.v("CURSOR", cursor.getString(1));

        View view = LayoutInflater.from(context).inflate(R.layout.list_items, parent, false);
        final ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }
}
