package com.mypocketstore.seasons;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.mypocketstore.seasons.Utility.Utility;
import com.mypocketstore.seasons.sync.UpdateConfiguration;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Context context = this;
        setContentView(R.layout.activity_main);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, AddShowActivity.class);
                startActivity(i);
            }
        });

        int currentMonth = Calendar.getInstance().get(Calendar.MONTH);
        SharedPreferences sharedPreferences = getSharedPreferences("main", Context.MODE_PRIVATE);
        int storedMonth = sharedPreferences.getInt("storedMonth", 0);

        if (storedMonth != currentMonth) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("storedMonth", currentMonth);
            editor.commit();

            UpdateConfiguration configuration = new UpdateConfiguration(this);
            configuration.execute();

        }

    }

    public void showPopup(View v) {
        final String showid = v.getContentDescription().toString();
        PopupMenu popup = new PopupMenu(this, v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.listitem_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                if(id == R.id.action_remove) {
                    Utility.removeShowFromDb(showid, getApplicationContext());
                }
                return true;
            }
        });
        popup.show();
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_main, menu);
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

            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }*/

        /*if(id == R.id.search_map){
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
            String search = pref.getString("map_search", "movie");
            Uri gmmIntentUri = Uri.parse("geo:0,0?").buildUpon().appendQueryParameter("q", search).build();
            //Log.v("Built URI", gmmIntentUri.toString());
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            if(mapIntent.resolveActivity(getPackageManager()) != null){
                startActivity(mapIntent);
            }else{
                Log.e("Map Intent", "Error");
            }

        }*/

        return super.onOptionsItemSelected(item);
    }
}
