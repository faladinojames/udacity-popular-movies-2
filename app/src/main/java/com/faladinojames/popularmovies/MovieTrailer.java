package com.faladinojames.popularmovies;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Falade James on 4/13/2017 All Rights Reserved.
 */

public class MovieTrailer {

    JSONArray youtubes;
    public MovieTrailer(JSONObject jsonObject)
    {
        try{
        youtubes=jsonObject.getJSONArray("results");
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    public String getYoutubeId(int position)
    {
        try{
            return  youtubes.getJSONObject(position).getString("key");
        }
        catch (JSONException e)
        {
            e.printStackTrace();
            return null;
        }
    }




}
