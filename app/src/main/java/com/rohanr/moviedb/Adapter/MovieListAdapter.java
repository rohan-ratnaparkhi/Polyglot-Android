package com.rohanr.moviedb.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.rohanr.moviedb.Entity.MovieData;
import com.rohanr.moviedb.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by rohanr on 6/15/16.
 */


public class MovieListAdapter extends BaseAdapter {

    ArrayList<MovieData> movies;
    Context context;

    public MovieListAdapter(ArrayList<MovieData> movies, Context context){
        this.movies = movies;
        this.context = context;
    }

    class ViewHolder{
        ImageView myMovie;
        ViewHolder(View v){
            myMovie = (ImageView) v.findViewById(R.id.imageView);
        }
    }

    @Override
    public int getCount() {
        return this.movies.size();
    }

    @Override
    public Object getItem(int position) {
        return this.movies.get(position);
    }

    @Override
    public long getItemId(int position) {
        return this.movies.get(position).getMovieId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder = null;
        if(row == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.single_item, parent, false);
            holder = new ViewHolder(row);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        Picasso.with(context).load(movies.get(position).getImageUrl()).into(holder.myMovie);
        holder.myMovie.setAdjustViewBounds(true);

        return row;
    }
}
