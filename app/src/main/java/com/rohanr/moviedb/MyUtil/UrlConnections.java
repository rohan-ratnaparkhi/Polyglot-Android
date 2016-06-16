package com.rohanr.moviedb.MyUtil;

import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by rohanr on 6/16/16.
 */
public class UrlConnections {
    public static JSONObject getData(URL url) throws Exception{
        JSONObject response;
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
        return response;
    }
}
