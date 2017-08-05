package com.faladinojames.popularmovies;

import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.InjectViews;

public class MovieReviews extends PopularMovieActivity {

    @InjectView(R.id.refresh)
    SwipeRefreshLayout refresh;
    @InjectView(R.id.reviewsRecycler)
    RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_revies);
        setTitle("Reviews");
        ButterKnife.inject(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getReviews(getIntent().getStringExtra("id"));



        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getReviews(getIntent().getStringExtra("id"));
            }
        });


    }

    private void setUpAdapter(String s)
    {
        try{
            JSONObject jsonObject = new JSONObject(s);
            JSONArray r=jsonObject.getJSONArray("results");

            if(r.length()==1)
            {
                Toast.makeText(this, "No reviews for this movie", Toast.LENGTH_SHORT).show();
                return;

            }
            List<Review> reviews= new ArrayList<>();

            for(int i=0; i<r.length(); i++)
            {
                JSONObject review =r.getJSONObject(i);
                reviews.add(new Review(review.getString("author"),review.getString("content")));
            }

            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(new Adapter(reviews));
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }
    private void getReviews(final String id)
    {
        refresh.setRefreshing(true);

        new AsyncTask<Void,Void,String>(){
            @Override
            protected String doInBackground(Void... params) {
                // Instantiate the RequestQueue.
                RequestQueue queue = Volley.newRequestQueue(getApplicationContext());


// Request a string response from the provided URL.
                StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://api.themoviedb.org/3/movie/"+id+"/reviews?api_key="+Constants.API_KEY,
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
                    refresh.setRefreshing(false);
                    setUpAdapter(s);
                }
            }
        }.execute();

    }



    public class Adapter extends RecyclerView.Adapter<ViewHolder>{
        List<Review> reviews;
        public Adapter(List<Review> reviews)
        {
            this.reviews=reviews;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(getLayoutInflater().inflate(R.layout.review_item,parent,false));
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Review review =reviews.get(position);
            holder.author.setText(review.author);
            holder.content.setText(review.content);

        }

        @Override
        public int getItemCount() {
            return reviews.size();
        }
    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        @InjectView(R.id.author)
        TextView author;
        @InjectView(R.id.content)
        TextView content;
        public ViewHolder(View v)
        {
            super(v);
            ButterKnife.inject(this,v);
        }
    }
    public class Review{
        String author,content;
        public Review(String author,String content)
        {
            this.author=author;
            this.content=content;
        }
    }
}
