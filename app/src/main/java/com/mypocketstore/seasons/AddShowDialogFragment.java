package com.mypocketstore.seasons;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.support.v4.app.DialogFragment;
import com.mypocketstore.seasons.MainActivityFragment;

import com.mypocketstore.seasons.Utility.Utility;
import com.mypocketstore.seasons.sync.FetchListTask;
import com.mypocketstore.seasons.sync.FetchShowForDialog;

import java.util.zip.Inflater;

/**
 * Created by anshdedha7 on 17/10/15.
 */
public class AddShowDialogFragment extends DialogFragment {

    int mId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mId = getArguments().getInt("showid");
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_add_show, null);

        FetchShowForDialog fetch = new FetchShowForDialog(view, getActivity());
        fetch.execute(mId);

        if (!Utility.existsInDatabase(String.valueOf(mId), getContext())){
            builder.setPositiveButton("ADD", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    FetchListTask fetchListTask = new FetchListTask(getContext());
                    fetchListTask.execute(String.valueOf(mId), "getShowById");
                }
            });
        }

        builder.setView(view)
                .setNegativeButton("DISMISS", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });


        AlertDialog dialog = builder.create();
        dialog.show();

        return dialog;
    }
}
