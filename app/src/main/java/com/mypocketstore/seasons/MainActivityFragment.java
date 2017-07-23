package com.mypocketstore.seasons;

import android.content.Intent;
import android.database.Cursor;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.mypocketstore.seasons.Utility.Utility;
import com.mypocketstore.seasons.adaptors.ShowsAdapter;
import com.mypocketstore.seasons.data.DatabaseContract.ShowEntry;
import com.mypocketstore.seasons.sync.FetchListTask;


public class MainActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int SHOWS_LOADER = 0;

    private ShowsAdapter mAdapter;

    public MainActivityFragment() {
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(SHOWS_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        AddShowActivity.PopularFragment.mSearchPage = 1;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootview = inflater.inflate(R.layout.fragment_main, container, false);

        mAdapter = new ShowsAdapter(
                getActivity(),
                null,
                0);


        ListView listView = ((ListView) rootview.findViewById(R.id.list_view));
        listView.setEmptyView(rootview.findViewById(R.id.empty));
        listView.setAdapter(mAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = mAdapter.getCursor();
                if (cursor != null && cursor.moveToPosition(position)) {
                    Intent intent = new Intent(getActivity(), DetailActivity.class)
                            .putExtra("id", cursor.getString(7)).putExtra("direct_to_episode", 0);

                    //(Intent.EXTRA_TEXT, cursor.getString(1) + "----" + cursor.getString(2) + "----"
                    //          + cursor.getString(3) + "---" + cursor.getString(4) + "> ");
                    startActivity(intent);
                }
                //cursor.close();
                // Intent intent = new Intent(getActivity(), DetailActivity.class).putExtra(Intent.EXTRA_TEXT, title);
                // startActivity(intent);
            }
        });



        /*final ImageView menu = (ImageView) rootview.findViewById(R.id.imageViewShowsContextMenu);
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(getContext(), menu);
                MenuInflater inflater = popup.getMenuInflater();
                inflater.inflate(R.menu.listitem_menu, popup.getMenu());

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getItemId() == R.id.action_remove) {
                            Utility.removeShowFromDb(menu.getContentDescription().toString());
                        }
                        return true;
                    }
                });

                popup.show();
            }
        });*/


        return rootview;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //inflater.inflate(R.menu.fragmentmenu, menu);
    }

    /*public void updateList(int id){

        FetchListTask fetchListTask = new FetchListTask(getContext());
        fetchListTask.execute(String.valueOf(id), "getShowById");
    }*/

    //http://api.themoviedb.org/3/discover/tv?api_key=7210257ce98ebcdcc9e4daa7aa235114&sortby=popularity.desc
    //http://jsonplaceholder.typicode.com/posts/1
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        return new CursorLoader(getActivity(),
                ShowEntry.CONTENT_URI,
                new String[]{ShowEntry.TABLE_NAME + "." + ShowEntry._ID,
                        ShowEntry.COLUMN_TITLE,
                        ShowEntry.COLUMN_EPISODE_COUNT,
                        ShowEntry.COLUMN_GENRES,
                        ShowEntry.COLUMN_VOTE_COUNT,
                        ShowEntry.COLUMN_VOTE_AVERAGE,
                        ShowEntry.COLUMN_SEASON_COUNT,
                        ShowEntry.COLUMN_ID,
                        ShowEntry.COLUMN_POSTER_PATH
                },
                null,
                null,
                null);

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }
}

