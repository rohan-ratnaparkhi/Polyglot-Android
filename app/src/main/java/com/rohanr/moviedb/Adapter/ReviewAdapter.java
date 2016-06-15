package com.rohanr.moviedb.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rohanr.moviedb.Entity.Review;
import com.rohanr.moviedb.R;

import java.util.ArrayList;

/**
 * Created by rohanr on 6/15/16.
 */

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.MyViewHolder> {

    Context c;
    ArrayList<Review> rList;

    public ReviewAdapter(Context c, ArrayList<Review> rList){
        this.c = c;
        this.rList = rList;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView rName, rComment;

        public MyViewHolder(View view) {
            super(view);
            rName = (TextView) view.findViewById(R.id.rev_author);
            rComment = (TextView) view.findViewById(R.id.rev_comment);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.review_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Review rev = rList.get(position);
        holder.rName.setText(rev.getReviewer());
        holder.rComment.setText(rev.getComment());
    }

    @Override
    public int getItemCount() {
        return this.rList.size();
    }
}
