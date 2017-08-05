package com.faladinojames.popularmovies.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "Favourites";
    public static final String TABLE_NAME = "favourites";
    public static final int DATABASE_VERSION = 1;
    public static final String CREATE_DB_TABLE =
            " CREATE TABLE " + TABLE_NAME +
                    " (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    " movieId TEXT NOT NULL, " +
                    " movieTitle TEXT NOT NULL);";

      public DatabaseHelper(Context context){
         super(context, DATABASE_NAME, null, DATABASE_VERSION);
      }

      @Override
      public void onCreate(SQLiteDatabase db) {
         db.execSQL(CREATE_DB_TABLE);
      }

      @Override
      public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
         onCreate(db);
      }
   }
