package com.mypocketstore.seasons.Utility;

import android.content.Context;

import com.mypocketstore.seasons.R;

/**
 * Created by anshdedha7 on 12/10/15.
 */
public class UrlBuilderClass {

    public static String buildHandpickedUrl(Context context){

        return new String(context.getString(R.string.url_base)
                + context.getString(R.string.url_list)
                + context.getString(R.string.url_list_1_id)
                + context.getString(R.string.url_api_key));
    }
}
