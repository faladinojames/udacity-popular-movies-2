package com.faladinojames.popularmovies;

import android.database.Cursor;


/**
 * Created by jamesfalade on 05/08/2017.
 */

public class FavouriteMovie {
    String id, title;
    public FavouriteMovie(Cursor cursor)
    {
        id=cursor.getString(cursor.getColumnIndex("movieId"));
        title=cursor.getString(cursor.getColumnIndex("movieTitle"));
    }
}
