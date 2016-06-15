package com.rohanr.moviedb;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class MovieDetails extends ActionBarActivity {

    String movieId;
    TextView title;
    ImageView img;
    TextView desc;
    TextView runTime;
    TextView rating;
    TextView year;
    Context parent;
    ArrayList<Trailers> trailersList;
    ArrayList<Review> reviewsList;
    RecyclerView recyclerView, recyclerViewReview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        recyclerView = (RecyclerView) findViewById(R.id.rv_trailers);
        recyclerViewReview = (RecyclerView) findViewById(R.id.rv_reviews);

        Bundle bundle = getIntent().getExtras();
        title = (TextView) findViewById(R.id.dtl_title);
        desc = (TextView) findViewById(R.id.dtl_desc);
        runTime = (TextView) findViewById(R.id.dtl_run_time);
        rating = (TextView) findViewById(R.id.dtl_rating);
        img = (ImageView) findViewById(R.id.dtl_image);
        year = (TextView) findViewById(R.id.dtl_year);
        movieId = bundle.get("movieId").toString();
        parent = this;
        new LongOperation().execute("");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_movie_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class LongOperation extends AsyncTask<String, Integer, JSONObject>{

        @Override
        protected void onPreExecute() {
//            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            JSONObject response;
            int i = 0;
            publishProgress(i);
            StringBuilder urlString = new StringBuilder();
            urlString.append(getString(R.string.base_url));
            urlString.append(getString(R.string.find_movie).replace("id",movieId)).append("?");
            urlString.append(getString(R.string.moviedb_api_key));
            try{
                URL url = new URL(urlString.toString());
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line);
                }
                bufferedReader.close();
                urlConnection.disconnect();
                response = (JSONObject) new JSONTokener(stringBuilder.toString()).nextValue();
            } catch (Exception e){
                Log.e("movieDb", e.getMessage());
                response = null;
            }
            return response;
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            Log.d("movieDb", "reached in post execute");
            try{
                if(result != null){
                    title.setText(result.getString("title"));
                    rating.setText(result.getString("vote_average") + "/10");
                    runTime.setText(result.get("runtime").toString() + " mins");
                    year.setText(result.get("release_date").toString());
                    desc.setText(result.getString("overview"));
                    Picasso.with(parent).load("http://image.tmdb.org/t/p/" + getString(R.string.default_poster_size) + result.getString("poster_path")).into(img);
                    new TrailersData().execute("");
                }

            } catch (Exception e){
                Log.e("movieDb", e.getMessage());
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {

        }

    }

    private class TrailersData extends AsyncTask<String, Integer, JSONObject> implements AdapterView.OnItemClickListener{

        private  TrailerVideoAdapter mAdapter;

        @Override
        protected void onPreExecute() {
//            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            JSONObject response;
            int i = 0;
            publishProgress(i);
            StringBuilder urlString = new StringBuilder();
            urlString.append(getString(R.string.base_url));
            urlString.append(getString(R.string.find_movie).replace("id",movieId));
            urlString.append(getString(R.string.get_videos)).append("?");
            urlString.append(getString(R.string.moviedb_api_key));
            try{
                URL url = new URL(urlString.toString());
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line);
                }
                bufferedReader.close();
                urlConnection.disconnect();
                response = (JSONObject) new JSONTokener(stringBuilder.toString()).nextValue();
            } catch (Exception e){
                Log.e("movieDb", e.getMessage());
                response = null;
            }
            return response;
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            trailersList = new ArrayList<Trailers>();
            Log.d("movieDb", "reached in post execute");
            try{
                if(result != null){
                    JSONArray trailers = result.getJSONArray("results");
                    for(int i=0;i<trailers.length();i++){
                        JSONObject obj = trailers.getJSONObject(i);
                        Trailers tr = new Trailers();
                        tr.name = obj.getString("name");
                        tr.site = obj.getString("site");
                        tr.type = obj.getString("type");
                        tr.key = obj.getString("key");
                        trailersList.add(tr);
                    }
                }
                if(trailersList.size() > 0){

                    mAdapter = new TrailerVideoAdapter(parent, trailersList);
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                    recyclerView.setLayoutManager(mLayoutManager);
                    recyclerView.setAdapter(mAdapter);

                    new ReviewOperation().execute("");

                }
            } catch (Exception e){
                Log.e("movieDb", e.getMessage());
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {

        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + trailersList.get(position).key)));
        }

    }




    private class ReviewOperation extends AsyncTask<String, Integer, JSONObject>{

        private  ReviewAdapter mAdapter;

        @Override
        protected void onPreExecute() {
//            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            JSONObject response;
            int i = 0;
            publishProgress(i);
            StringBuilder urlString = new StringBuilder();
            urlString.append(getString(R.string.base_url));
            urlString.append(getString(R.string.find_movie).replace("id",movieId));
            urlString.append(getString(R.string.get_reviews)).append("?");
            urlString.append(getString(R.string.moviedb_api_key));
            try{
                URL url = new URL(urlString.toString());
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line);
                }
                bufferedReader.close();
                urlConnection.disconnect();
                response = (JSONObject) new JSONTokener(stringBuilder.toString()).nextValue();
            } catch (Exception e){
                Log.e("movieDb", e.getMessage());
                response = null;
            }
            return response;
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            reviewsList = new ArrayList<Review>();
            Log.d("movieDb", "reached in post execute for Reviews");
            try{
                if(result != null){
                    JSONArray reviews = result.getJSONArray("results");
                    for(int i=0;i<reviews.length();i++){
                        JSONObject obj = reviews.getJSONObject(i);
                        Review rev = new Review();
                        rev.reviewer = obj.getString("author");
                        rev.comment = obj.getString("content");
                        reviewsList.add(rev);
                    }
                }
                if(reviewsList.size() > 0){

                    mAdapter = new ReviewAdapter(parent, reviewsList);
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                    recyclerViewReview.setLayoutManager(mLayoutManager);
                    recyclerViewReview.setAdapter(mAdapter);

                }
            } catch (Exception e){
                Log.e("movieDb", e.getMessage());
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {

        }

    }

}

class Trailers {
    String name;
    String site;
    String type;
    String key;
}

class TrailerVideoAdapter extends RecyclerView.Adapter<TrailerVideoAdapter.MyViewHolder> {

    Context c;
    ArrayList<Trailers> tList;

    TrailerVideoAdapter(Context c, ArrayList<Trailers> tList){
        this.c = c;
        this.tList = tList;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tName, tType, tSite;
        public ImageView tImage;

        public MyViewHolder(View view) {
            super(view);
            tName = (TextView) view.findViewById(R.id.trl_name);
            tType = (TextView) view.findViewById(R.id.trl_type);
            tSite = (TextView) view.findViewById(R.id.trl_site);
            tImage = (ImageView) view.findViewById(R.id.trl_image);
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
        holder.tName.setText(trl.name);
        holder.tSite.setText(trl.site);
        holder.tType.setText(trl.type);
    }

    @Override
    public int getItemCount() {
        return this.tList.size();
    }
}


class Review {
    String reviewer;
    String comment;
}

class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.MyViewHolder> {

    Context c;
    ArrayList<Review> rList;

    ReviewAdapter(Context c, ArrayList<Review> rList){
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
        holder.rName.setText(rev.reviewer);
        holder.rComment.setText(rev.comment);
    }

    @Override
    public int getItemCount() {
        return this.rList.size();
    }
}


