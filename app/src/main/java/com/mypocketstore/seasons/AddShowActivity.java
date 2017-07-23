package com.mypocketstore.seasons;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.view.inputmethod.EditorInfo;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mypocketstore.seasons.Utility.UrlBuilderClass;
import com.mypocketstore.seasons.Utility.Utility;
import com.mypocketstore.seasons.adaptors.ListAdapter;
import com.mypocketstore.seasons.sync.FetchHandpickedShowsTask;
import com.mypocketstore.seasons.sync.FetchListTask;
import com.mypocketstore.seasons.sync.FetchPopularTask;
import com.mypocketstore.seasons.sync.SearchTask;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class AddShowActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_show);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(getResources().getColor(R.color.actionBar));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setBackgroundColor(getResources().getColor(R.color.actionBar));
        tabLayout.setupWithViewPager(mViewPager);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_add_show, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        /*int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

    public void addToDatabase(View view){
        FetchListTask fetchListTask = new FetchListTask(this);
        fetchListTask.execute(view.getContentDescription().toString());
        ImageView i = (ImageView)view;
        i.setImageResource(R.drawable.ic_ticked);
    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return SearchFragment.newInstance();
                case 2:
                    return PopularFragment.newInstance();
                case 1:
                    return DiscoverFragment.newInstance();
            }
            return null;
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Search";
                case 1:
                    return "Discover";
                case 2:
                    return "Worldwide";
            }
            return null;
        }
    }

    /******************************************************************************************************************************
     * A placeholder fragment containing a simple view.
     */
    public static class SearchFragment extends Fragment {

        public static SearchFragment newInstance() {
            SearchFragment fragment = new SearchFragment();
            return fragment;
        }

        public SearchFragment() {
        }

        ListAdapter mAdapter;


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            super.onCreateView(inflater, container, savedInstanceState);

            PopularFragment.mSearchPage = 1;

            final View rootView = inflater.inflate(R.layout.fragment_add_show, container, false);
            AutoCompleteTextView textView = (AutoCompleteTextView) rootView.findViewById(R.id.autoCompleteTextView);

            final SharedPreferences sharedPreferences = getActivity().getSharedPreferences("mainFolder", Context.MODE_PRIVATE);

            final String rawString =  sharedPreferences.getString("user_search_history", "kuddAaA");
            if (rawString!=null) {
                Log.v("raw str", rawString);
                String[] user_search_history_array = rawString.split("AaA");
                ArrayAdapter<String> adapter =
                        new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, user_search_history_array);
                textView.setAdapter(adapter);
            }

            EditText editText = (EditText) rootView.findViewById(R.id.autoCompleteTextView);
            editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    boolean handled = false;
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        String input = v.getText().toString();
                        if (!input.equals("")) {
                            String history_string = sharedPreferences.getString("user_search_history", null);
                            int flag = 0;
                            if (history_string != null) {
                                String[] search_value = history_string.split("AaA");
                                for (int i = 0; i < search_value.length; i++) {
                                    if (input.equals(search_value[i])) {
                                        flag = 1;
                                    }
                                }
                            }
                            if (flag == 0) {
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("user_search_history", rawString + input + "AaA");
                                editor.commit();
                                Log.v("commit", "Commited");
                            }

                            try {
                                searchAndUpdate(input, (TextView) rootView.findViewById(R.id.noSearchResultText));
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        }

                        handled = true;
                    }
                    return handled;
                }
            });

            mAdapter = new ListAdapter(getActivity(), R.layout.list_item_search, new ArrayList<HashMap<String, String>>());

            final ListView listView = ((ListView) rootView.findViewById(R.id.list_view_add_show));
            listView.setAdapter(mAdapter);

            String url_handpicked = UrlBuilderClass.buildHandpickedUrl(getActivity());
            FetchHandpickedShowsTask fetchHandpickedShowsTask = new FetchHandpickedShowsTask(getActivity(), mAdapter);
            fetchHandpickedShowsTask.execute(url_handpicked);

            listView.setOnScrollListener(new AbsListView.OnScrollListener() {

                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {
                    Utility.hide_keyboard(getActivity());
                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                }
            });

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    int showID = Integer.parseInt(mAdapter.getItem(position).get("id"));
                    Bundle args = new Bundle();
                    args.putInt("showid", showID);
                    AddShowDialogFragment obj = new AddShowDialogFragment();
                    obj.setArguments(args);
                    obj.show(getFragmentManager(), "Dialog0");
                }
            });


            return rootView;
        }


        public void searchAndUpdate (String input, TextView textView) throws UnsupportedEncodingException {
            String encodedInputString = URLEncoder.encode(input, "utf-8");
            textView.setVisibility(View.GONE);
            SearchTask searchTask = new SearchTask(getActivity(), mAdapter, textView);
            searchTask.execute(encodedInputString);
            Utility.hide_keyboard(getActivity());

        }

    }

    public static class PopularFragment extends Fragment {

        ListAdapter mAdapter;
        public static int mSearchPage = 1 ;

        public static PopularFragment newInstance() {
            PopularFragment fragment = new PopularFragment();
            return fragment;
        }


        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            super.onCreateView(inflater, container, savedInstanceState);

            View rootView = inflater.inflate(R.layout.fragment_popular, container, false);
            mAdapter = new ListAdapter(getActivity(), R.layout.list_item_search, new ArrayList<HashMap<String, String>>());
            final ListView listView = ((ListView) rootView.findViewById(R.id.list_view_add_show));
            listView.setAdapter(mAdapter);

            FetchPopularTask fetchPopularTask = new FetchPopularTask(getActivity(), mAdapter);
            fetchPopularTask.execute(String.valueOf((mSearchPage)));

            listView.setOnScrollListener(new AbsListView.OnScrollListener() {

                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {
                    if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE
                            && (listView.getLastVisiblePosition() - listView.getHeaderViewsCount() -
                            listView.getFooterViewsCount()) >= (mAdapter.getCount() - 1)) {

                        mSearchPage++;
                        FetchPopularTask fetchPopularTask = new FetchPopularTask(getActivity(), mAdapter);
                        fetchPopularTask.execute(String.valueOf((mSearchPage)));

                    }
                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                }
            });

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    int showID = Integer.parseInt(mAdapter.getItem(position).get("id"));
                    Bundle args = new Bundle();
                    args.putInt("showid", showID);
                    AddShowDialogFragment obj = new AddShowDialogFragment();
                    obj.setArguments(args);
                    obj.show(getFragmentManager(), "Dialog0");
                }
            });

            Utility.hide_keyboard(getActivity());
            return rootView;
        }

    }

    public static class DiscoverFragment extends Fragment {
        public static DiscoverFragment newInstance() {
            DiscoverFragment fragment = new DiscoverFragment();
            return fragment;
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            super.onCreateView(inflater, container, savedInstanceState);

            return  inflater.inflate(R.layout.fragment_discover, container, false);

        }
    }
}
