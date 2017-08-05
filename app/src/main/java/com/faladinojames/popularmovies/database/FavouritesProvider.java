package com.faladinojames.popularmovies.database;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

public class FavouritesProvider extends android.content.ContentProvider {
   static final String PROVIDER_NAME = "com.faladinojames.popularmovies.favourites";
   public static final Uri CONTENT_URI = Uri.parse( "content://" + PROVIDER_NAME + "/favourites");


   private SQLiteDatabase db;

   public static final String TABLE_NAME = "favourites";
   static final int FAVOURITES = 1;
   static final int FAVOURITES_ID = 2;

   static final UriMatcher uriMatcher;
   static{
      uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
      uriMatcher.addURI(PROVIDER_NAME, "favourites", FAVOURITES);
      uriMatcher.addURI(PROVIDER_NAME, "favourites/#", FAVOURITES_ID);
   }

   

   @Override
   public boolean onCreate() {
      Context context = getContext();
      DatabaseHelper dbHelper = new DatabaseHelper(context);
      db = dbHelper.getWritableDatabase();
      return true;
   }

   @Override
   public Uri insert(Uri uri, ContentValues values) {

      long id;
      switch(uriMatcher.match(uri)) {
         case FAVOURITES:
            id = db.insertOrThrow(TABLE_NAME, null, values);
            break;

         default:
            throw new IllegalArgumentException("Unsupported URI: " + uri);
      }
      Uri insertUri = ContentUris.withAppendedId(uri, id);
      getContext().getContentResolver().notifyChange(insertUri, null);
      return insertUri;
   }

   @Override
   public Cursor query(Uri uri, String[] projection, 
      String selection,String[] selectionArgs, String sortOrder) {
      SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
      qb.setTables(TABLE_NAME);
      switch(uriMatcher.match(uri)) {
         case FAVOURITES:
            break;

         case FAVOURITES_ID:
            qb.appendWhere("_id = " + uri.getLastPathSegment());
            break;

         default:
            throw new IllegalArgumentException("Unsupported URI: " + uri);
      }

      Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);
      c.setNotificationUri(getContext().getContentResolver(), uri);
      return c;
   }

   @Override
   public int delete(Uri uri, String selection, String[] selectionArgs) {
      switch (uriMatcher.match(uri)) {
         case FAVOURITES:
            selection = (selection == null) ? "1" : selection;
            break;
         case FAVOURITES_ID:
            long id = ContentUris.parseId(uri);
            selection = String.format("%s = ?","_id");
            selectionArgs = new String[]{String.valueOf(id)};
            break;
         default:
            throw new IllegalArgumentException("Illegal delete URI");
      }
      int count = db.delete(TABLE_NAME, selection, selectionArgs);

      if (count > 0) {
         getContext().getContentResolver().notifyChange(uri, null);
      }

      return count;
   }

   @Override
   public int update(Uri uri, ContentValues values, 
      String selection, String[] selectionArgs) {
      int count = 0;
      switch (uriMatcher.match(uri)) {
         case FAVOURITES:
            count = db.update(TABLE_NAME, values, selection, selectionArgs);
         break;

         case FAVOURITES_ID:
            count = db.update(TABLE_NAME, values, "_id = ?", new String[]{uri.getLastPathSegment()});
            break;
         default:
            throw new IllegalArgumentException("Unknown URI " + uri );
      }
        
      getContext().getContentResolver().notifyChange(uri, null);
      return count;
   }

   @Override
   public String getType(Uri uri) {
      return null;
   }
}
