package com.rohanr.moviedb.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.rohanr.moviedb.Entity.Trailers;
import com.rohanr.moviedb.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by rohanr on 6/15/16.
 */

public class TrailerVideoAdapter extends RecyclerView.Adapter<TrailerVideoAdapter.MyViewHolder> {

    Context c;
    ArrayList<Trailers> tList;

    public TrailerVideoAdapter(Context c, ArrayList<Trailers> tList){
        this.c = c;
        this.tList = tList;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView tName, tType, tSite;
        public ImageView tImage;

        public MyViewHolder(View view) {
            super(view);
            tName = (TextView) view.findViewById(R.id.trl_name);
            tType = (TextView) view.findViewById(R.id.trl_type);
            tSite = (TextView) view.findViewById(R.id.trl_site);
            tImage = (ImageView) view.findViewById(R.id.trl_image);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            c.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + tList.get(position).getKey())));
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.trailer_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Trailers trl = tList.get(position);
        holder.tName.setText(trl.getName());
        holder.tSite.setText(trl.getSite());
        holder.tType.setText(trl.getType());
        Picasso.with(c).load("http://img.youtube.com/vi/"+ trl.getKey() +"/0.jpg").into(holder.tImage);
    }

    @Override
    public int getItemCount() {
        return this.tList.size();
    }
}

