package com.faladinojames.popularmovies;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by jamesfalade on 05/08/2017.
 */

public class FavouriteMovieAdapter extends RecyclerView.Adapter<ViewHolder> {
    List<FavouriteMovie> movies; Activity activity;
    View.OnClickListener listener;
    public FavouriteMovieAdapter(Activity activity, List<FavouriteMovie> movies, View.OnClickListener listener)
    {
        this.movies=movies;
        this.activity=activity;
        this.listener=listener;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(activity.getLayoutInflater().inflate(R.layout.favourite_item,parent,false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        
        holder.title.setText(movies.get(position).title);
        holder.itemView.setTag(movies.get(position).id);
        holder.itemView.setOnClickListener(listener);
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }
}
 class ViewHolder extends RecyclerView.ViewHolder{
     @InjectView(R.id.favourite_title)
     TextView title;
    public ViewHolder(View v)
    {
        super(v);
        ButterKnife.inject(this,v);
    }
}
