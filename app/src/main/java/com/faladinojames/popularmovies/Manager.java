package com.faladinojames.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;

import com.faladinojames.popularmovies.database.DatabaseHelper;
import com.faladinojames.popularmovies.database.FavouritesProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/**
 * Created by Falade James on 4/13/2017 All Rights Reserved.
 */

public class Manager {


    Context context;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    public Manager(Context context)
    {
        this.context=context;
        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(context);
        editor=sharedPreferences.edit();

    }
    public boolean isFavourite(int id)
    {
        String where = "movieId=?";
        String[] whereArgs = new String[]{String.valueOf(id)};
        Cursor c = new DatabaseHelper(context).getWritableDatabase().query(true, DatabaseHelper.TABLE_NAME, null, where, whereArgs, null, null, null, null);
        return c!=null && c.moveToFirst();
    }

   

    public void addToFavourites(String id,String title)
    {
        ContentValues values= new ContentValues();
        values.put("movieId",id);
        values.put("movieTitle",title);

        context.getContentResolver().insert(FavouritesProvider.CONTENT_URI,values);
        context.getContentResolver().notifyChange(FavouritesProvider.CONTENT_URI,null);
    }

    public void removeFromFavourites(String id)
    {

        String where = "movieId=?";
        String[] whereArgs = new String[]{id};
        Cursor c = new DatabaseHelper(context).getWritableDatabase().query(true, DatabaseHelper.TABLE_NAME, null, where, whereArgs, null, null, null, null);
        if(c!=null)
            if(c.moveToFirst())
            {
                context.getContentResolver().delete(Uri.withAppendedPath(FavouritesProvider.CONTENT_URI, String.valueOf(c.getInt(c.getColumnIndex("_id")))),null, null);
                c.close();
            }
            
    }
    
    void writeToFile(String data,Context context) {
        try {
            JSONObject jsonObject= new JSONObject(data);

            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("movies.json", Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException |JSONException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    public String getLocalMovies()
    {


        String ret = "";

        try {
            InputStream inputStream = context.openFileInput("movies.json");

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();

                return ret;


            }
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;



    }

   
    public String getFavouritesFile()
    {


        String ret = "";

        try {
            InputStream inputStream = context.openFileInput("favourites.json");

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();

                return ret;


            }
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;



    }


}
