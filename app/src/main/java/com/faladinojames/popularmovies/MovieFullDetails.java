package com.faladinojames.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.transition.Fade;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.InjectViews;
import butterknife.OnClick;

public class MovieFullDetails extends PopularMovieActivity {

    Movie movie;
    @InjectView(R.id.releaseDate)
    TextView releaseDate;
    @InjectView(R.id.ratingBar)
    RatingBar ratingBar;
    @InjectView(R.id.tvSynopsis)
    TextView synopsis;
    @InjectView(R.id.ivPoster)
    ImageView poster; 

    @InjectView(R.id.trailer1)
    ImageView trailer1;
    @InjectView(R.id.trailer2)
    ImageView trailer2;
    @InjectView(R.id.trailer3)
    ImageView trailer3;

    MovieTrailer movieTrailer;
    boolean trailerLoadFailed=false;

    @InjectView(R.id.favourite)
    ImageButton favourite;

    Toolbar toolbar;
    boolean isFavourite;

    @InjectView(R.id.toolbar_layout)
    CollapsingToolbarLayout collapsingToolbarLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_full_details);
        ButterKnife.inject(this);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        


        setupWindowAnimations();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        
        String m= getIntent().getStringExtra("movie");
        if(m==null)
        {
            final String id=getIntent().getStringExtra("id");


            new AsyncTask<Void,Void,String>(){
                @Override
                protected String doInBackground(Void... params) {
                    // Instantiate the RequestQueue.
                    RequestQueue queue = Volley.newRequestQueue(getApplicationContext());


// Request a string response from the provided URL.
                    StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://api.themoviedb.org/3/movie/"+id+"?api_key="+Constants.API_KEY,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    onPostExecute(response);

                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            error.printStackTrace();
                        }
                    });
// Add the request to the RequestQueue.
                    queue.add(stringRequest);
                    return null;
                }

                @Override
                protected void onPostExecute(String s) {
                    if(s!=null) {
                       try{
                           setUp(new Movie(new JSONObject(s)));
                       }
                       catch (JSONException e)
                       {
                           e.printStackTrace();
                       }
                    }
                }
            }.execute();


        }
        else
        try {
           setUp(new Movie(new JSONObject(m)));


        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

    }

    private void setUp(Movie movie)
    {
        this.movie=movie;
        collapsingToolbarLayout.setTitle(movie.getTitle());
        setTitle(movie.getTitle());
        ratingBar.setRating(movie.getRating());
        synopsis.setText(movie.getSynopsis());
        releaseDate.setText(movie.getReleaseDate());
        Picasso.with(this).load(movie.getPoster()).into(poster);
        getTrailers();
        isFavourite=manager.isFavourite(movie.getId());

        if(isFavourite)
        {
            favourite.setBackgroundDrawable(getResources().getDrawable(R.drawable.favorite));
        }
        else                 favourite.setBackgroundDrawable(getResources().getDrawable(R.drawable.not_favourite));
    }
    private void getTrailers()
    {
        new AsyncTask<Void,Void,String>(){
            @Override
            protected String doInBackground(Void... params) {
                // Instantiate the RequestQueue.
                RequestQueue queue = Volley.newRequestQueue(getApplicationContext());


// Request a string response from the provided URL.
                StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://api.themoviedb.org/3/movie/"+movie.getId()+"/videos?api_key="+Constants.API_KEY,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {

                                onPostExecute(response);

                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        trailerLoadFailed=true;
                    }
                });
// Add the request to the RequestQueue.
                queue.add(stringRequest);
                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                if(s!=null) {
                    try {
                        movieTrailer = new MovieTrailer(new JSONObject(s));
                    }
                    catch (JSONException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }.execute();
    }


    private void openYoutube(String id)
    {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube://" +id));
        startActivity(intent);
    }
    private void setupWindowAnimations() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Fade fade = new Fade();
            fade.setDuration(1000);
            getWindow().setEnterTransition(fade);
        }
    }

    private void trailerNotReady()
    {
        if(!trailerLoadFailed)
        Toast.makeText(this, "Please Wait loading trailer...", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(this, "Trailer load failed. Please refresh the page", Toast.LENGTH_SHORT).show();
    }
    @OnClick(R.id.trailer1)
    public void trailer1()
    {
        if(movieTrailer!=null)
        {
            openYoutube(movieTrailer.getYoutubeId(0));
        }
        else trailerNotReady();
    }

    @OnClick(R.id.trailer2)
    public void trailer2()
    {
        if(movieTrailer!=null)
        {
            openYoutube(movieTrailer.getYoutubeId(1));
        }
        else trailerNotReady();
    }

    @OnClick(R.id.trailer3)
    public void trailer3()
    {
        if(movieTrailer!=null)
        {
            openYoutube(movieTrailer.getYoutubeId(2));
        }
        else trailerNotReady();
    }

    public void reviews(View v)
    {
        startActivity(new Intent(this,MovieReviews.class).putExtra("id",String.valueOf(movie.getId())));
    }

    public void markFavourite(View V)
    {
       if(isFavourite)
       {
           manager.removeFromFavourites(String.valueOf(movie.getId()));
           isFavourite=false;
           favourite.setBackgroundDrawable(getResources().getDrawable(R.drawable.not_favourite));
       }
       else{
           manager.addToFavourites(String.valueOf(movie.getId()),movie.getTitle());
           isFavourite=true;
           favourite.setBackgroundDrawable(getResources().getDrawable(R.drawable.favorite));

       }
    }
}
