package com.mypocketstore.seasons;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubeStandalonePlayer;

import android.app.ActionBar;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.mypocketstore.seasons.Tabs.SlidingTabLayout;
import com.mypocketstore.seasons.Utility.Utility;
import com.mypocketstore.seasons.data.DatabaseContract;
import com.mypocketstore.seasons.sync.FetchEpisodeImageTask;
import com.mypocketstore.seasons.sync.FetchThumbnailFromServer;
import com.mypocketstore.seasons.sync.FetchVideoTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

public class DetailActivity extends AppCompatActivity {

    private static SectionsPagerAdapter mSectionsPagerAdapter;

    private ViewPager mViewPager;
    private SlidingTabLayout mTabLayout;

    public static int mShowId;
    static int mSeasonNumber;
    int mEpisodeCount;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        mShowId = Integer.parseInt(getIntent().getExtras().getString("id"));
        SharedPreferences sharedPreferences = getSharedPreferences(String.valueOf(mShowId), Context.MODE_PRIVATE);
        mSeasonNumber = sharedPreferences.getInt("selected_season_number", 1);
        mEpisodeCount = getEpisodeCount();
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
        mTabLayout.setViewPager(mViewPager);
        if(getIntent().getExtras().getInt("direct_to_episode")==0) {
            mViewPager.setCurrentItem(1, true);
        }else {
            mViewPager.setCurrentItem(2, true);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        //int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        //if (id == R.id.action_settings) {
            //return true;
        //}

        return super.onOptionsItemSelected(item);
    }

    int getEpisodeCount(){

        Cursor cursor = getContentResolver().query(
                DatabaseContract.ShowEntry.CONTENT_URI,
                new String[]{DatabaseContract.ShowEntry._ID,
                        DatabaseContract.ShowEntry.COLUMN_TITLE,
                        DatabaseContract.ShowEntry.COLUMN_VOTE_AVERAGE,
                        DatabaseContract.ShowEntry.COLUMN_GENRES,
                        DatabaseContract.ShowEntry.COLUMN_OVERVIEW,
                        DatabaseContract.ShowEntry.COLUMN_POSTER_PATH,
                        DatabaseContract.ShowEntry.COLUMN_VOTE_COUNT,
                        DatabaseContract.ShowEntry.COLUMN_FIRST_AIR_DATE,
                        DatabaseContract.ShowEntry.COLUMN_SEASON_COUNT,
                        DatabaseContract.ShowEntry.COLUMN_EPISODE_COUNT,
                        DatabaseContract.ShowEntry.COLUMN_IN_PROD
                },
                DatabaseContract.ShowEntry.COLUMN_ID + " = ?",
                new String[]{String.valueOf(mShowId)},
                null
        );

        boolean b = cursor.moveToFirst();
        Log.v("CURSOR", "" + b);

        getSupportActionBar().setTitle(cursor.getString(1));


        if(b) {
            ShowFragment.setShowCursor(cursor);
        } else {
            Toast toast = new Toast(this).makeText(this, "Error : Show not found in database", Toast.LENGTH_SHORT);
            toast.show();
        }

        int _IDshow = cursor.getInt(0);
        Log.v("SHOW ID", String.valueOf(_IDshow));

        cursor = getContentResolver().query(
                DatabaseContract.SeasonEntry.CONTENT_URI,
                new String[]{DatabaseContract.SeasonEntry._ID,
                        DatabaseContract.SeasonEntry.COLUMN_NAME
                },
                DatabaseContract.SeasonEntry.COLUMN_SHOW_KEY + " = ?",
                new String[]{String.valueOf(_IDshow)},
                null
        );

        assert cursor != null;
        SeasonsFragment.setSeasonsCursor(cursor);

        boolean b1 = cursor.moveToPosition(mSeasonNumber-1);
        Log.v("B1", String.valueOf(b1));

        int _IDseason = cursor.getInt(0);
        Log.v("SEASON ID", String.valueOf(_IDseason));

        cursor = getContentResolver().query(
                DatabaseContract.EpisodeEntry.CONTENT_URI,
                new String[]{DatabaseContract.EpisodeEntry._ID,
                        DatabaseContract.EpisodeEntry.COLUMN_NAME,
                        DatabaseContract.EpisodeEntry.COLUMN_DATE,
                        DatabaseContract.EpisodeEntry.COLUMN_OVERVIEW,
                        DatabaseContract.EpisodeEntry.COLUMN_BACKDROP,
                        DatabaseContract.EpisodeEntry.COLUMN_SET_WATCHED
                },
                DatabaseContract.EpisodeEntry.COLUMN_SEASON_KEY + " = ?",
                new String[]{String.valueOf(_IDseason)},
                null
        );

        /*cursor.moveToPosition(1);
        String s = cursor.getColumnName(2);
        Log.d("Column name", s);*/

        EpisodeFragment.setEpisodeCursor(cursor);


        return cursor.getCount();
    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentStatePagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            if (position==1){
                return ShowFragment.newInstance();
            } else if (position == 0) {
                return SeasonsFragment.newInstance();
            } else {
                return EpisodeFragment.newInstance(position);
            }
        }

        @Override
        public int getCount() {
            //String.valueOf(getIntent().getExtras().getString("count"));
            return mEpisodeCount + 2;
        }


        @Override
        public CharSequence getPageTitle(int position) {

            if (position==0){
                return "Seasons";
            }
            if (position==1){
                return "Overview";
            }else {
                return "S" + String.valueOf(mSeasonNumber) + " E" + String.valueOf(position - 1);
            }
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class EpisodeFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */

        static Cursor mEpisodeCursor;




        public static void setEpisodeCursor(Cursor cursor){
            if (cursor != null) {
                mEpisodeCursor = cursor;
            }
        }


        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static EpisodeFragment newInstance(int sectionNumber) {
            EpisodeFragment fragment = new EpisodeFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public EpisodeFragment() {
        }

        Bitmap getImageFromDb(String filename) throws FileNotFoundException {
            ContextWrapper cw = new ContextWrapper(getActivity());
            File directory = cw.getDir("EpisodeDir", Context.MODE_PRIVATE);
            File f = new File(directory, filename);
            return BitmapFactory.decodeStream(new FileInputStream(f));
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

            mEpisodeCursor.moveToPosition(getArguments().getInt(ARG_SECTION_NUMBER) - 2);

            ((TextView) rootView.findViewById(R.id.textViewEpisodeTitle)).setText(mEpisodeCursor.getString(1));
            ((TextView) rootView.findViewById(R.id.textViewEpisodeReleaseTime)).setText(mEpisodeCursor.getString(2));
            ((TextView) rootView.findViewById(R.id.textViewEpisodeDescription)).setText(mEpisodeCursor.getString(3));

            ImageView posterView = (ImageView) rootView.findViewById(R.id.imageViewEpisode);
            try {
                posterView.setImageBitmap(getImageFromDb(mEpisodeCursor.getString(4)));
                (rootView.findViewById(R.id.progressBarAdd)).setVisibility(View.GONE);
            }catch (FileNotFoundException e) {
                FetchEpisodeImageTask f = new FetchEpisodeImageTask(getActivity(),
                        posterView,
                        rootView.findViewById(R.id.progressBarAdd)
                        );
                f.execute(mEpisodeCursor.getString(4));
            }

            posterView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    FetchVideoTask fetchVideoTask = new FetchVideoTask(getActivity());
                    fetchVideoTask.execute(String.valueOf(mShowId), String.valueOf(mSeasonNumber), String.valueOf(getArguments().getInt(ARG_SECTION_NUMBER) - 1));
                }
            });

            return rootView;
        }

    }

    public static class ShowFragment extends Fragment {

        static Cursor mShowCursor;

        public static void setShowCursor(Cursor cursor) {
            if (cursor != null) {
                mShowCursor = cursor;
            }
        }

        public static ShowFragment newInstance() {
            ShowFragment fragment = new ShowFragment();
            return fragment;
        }

        public ShowFragment() {
        }

        Bitmap getImageFromDb(String filename) throws FileNotFoundException {
            ContextWrapper cw = new ContextWrapper(getActivity());
            File directory = cw.getDir("ThumbnailDir", Context.MODE_PRIVATE);
            File f = new File(directory, filename);
            return BitmapFactory.decodeStream(new FileInputStream(f));
        }

        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.fragment_show_overview, container, false);

            mShowCursor.moveToFirst();

            ImageView iv = ((ImageView) rootView.findViewById(R.id.imageViewAddPoster));

            try {
                iv.setImageBitmap(getImageFromDb(mShowCursor.getString(5)));
            }catch (FileNotFoundException e) {
                FetchThumbnailFromServer f = new FetchThumbnailFromServer(getActivity(), iv);
                f.execute(mShowCursor.getString(5));
            }

            ((TextView) rootView.findViewById(R.id.textViewAddTitle)).setText(mShowCursor.getString(1));
            ((TextView) rootView.findViewById(R.id.textViewAddGenres)).setText(mShowCursor.getString(3));
            ((TextView) rootView.findViewById(R.id.textViewAddDescription)).setText(mShowCursor.getString(4));
            String vote_count = "(" + mShowCursor.getString(6) + " votes)";
            ((TextView) rootView.findViewById(R.id.textViewAddRatingRange)).setText(vote_count);
            ((TextView) rootView.findViewById(R.id.textViewAddRatingValue)).setText(mShowCursor.getString(2));
            ((TextView) rootView.findViewById(R.id.textViewAddReleased)).setText(mShowCursor.getString(7));

            String inProd;
            if (mShowCursor.getString(10).equals("1")){
                inProd = "Running";
            }else {
                inProd = "Ended";
            }

            String meta = inProd + "\nSeasons: " + mShowCursor.getString(8)
                    + "\nEpisodes: " + mShowCursor.getString(9)
                    // + "\nPopularity: " + strings[9]
                    ;


            ((TextView) rootView.findViewById(R.id.textViewAddShowMeta)).setText(meta);


            return rootView;
        }

    }

    public static class SeasonsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

        static Cursor mSeasonsCursor;
        private SimpleCursorAdapter mAdapter;

        public static void setSeasonsCursor(Cursor cursor) {
            if (cursor != null) {
                mSeasonsCursor = cursor;
            }
        }

        public static SeasonsFragment newInstance() {
            SeasonsFragment fragment = new SeasonsFragment();
            return fragment;
        }

        public SeasonsFragment() {
        }

        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {



            View rootView = inflater.inflate(R.layout.fragment_seasons, container, false);

            mAdapter = new SimpleCursorAdapter(getContext(),
                    R.layout.list_item_seasons,
                    mSeasonsCursor,
                    new String[]{DatabaseContract.SeasonEntry.COLUMN_NAME},
                    new int[]{R.id.season_name},
                    0);

            ListView lv = (ListView) rootView.findViewById(R.id.seasons_list_view);
            lv.setAdapter(mAdapter);



            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Cursor cursor = mAdapter.getCursor();
                    if (cursor != null && cursor.moveToPosition(position)) {
                        SharedPreferences preferences = getActivity().getSharedPreferences(String.valueOf(mShowId), Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putInt("selected_season_number", position + 1);
                        editor.commit();

                        Intent intent = new Intent(getActivity(), DetailActivity.class).putExtra("id", String.valueOf(mShowId)).putExtra("direct_to_episode", 1);
                        startActivity(intent);
                    }
                }
            });

            return rootView;
        }

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            return new CursorLoader(getActivity(),
                    DatabaseContract.SeasonEntry.CONTENT_URI,
                    new String[]{DatabaseContract.SeasonEntry.TABLE_NAME + "." + DatabaseContract.SeasonEntry._ID,
                            DatabaseContract.SeasonEntry.COLUMN_NAME
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
}
