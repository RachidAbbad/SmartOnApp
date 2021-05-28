package com.abbad.smartonapp.utils;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import androidx.fragment.app.Fragment;

import com.abbad.smartonapp.classes.Chaudiere;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class WebServiceConnection {
    static JSONObject obj;
    public static JSONObject sendRequest() throws IOException {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try{
                    URL url = new URL("http://smartonviatoile.com/api/Data/currentChaudiere/5fbccf26e06d8cb8a4ac500e/1");
                    HttpURLConnection http = (HttpURLConnection)url.openConnection();
                    http.setRequestProperty("Accept", "application/json");
                    http.setRequestProperty("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ0ZWNobmljaWVudEB2aWF0b2lsZS5jb20iLCJqdGkiOiI0NjliZDY4Ni04MjBlLTRjNTAtODQ5OC0zNTZiNjI3NjFiODEiLCJpYXQiOjE2MjIwMzAwNDgsInJvbCI6ImFwaV9hY2Nlc3MiLCJpZCI6IjYwYWQ2MzBhZWE2ZjVhNDQ1NmQ1ODE4NCIsIm5iZiI6MTYyMjAzMDA0NywiZXhwIjoxNjIyMDMzNjQ3LCJpc3MiOiJBcGkiLCJhdWQiOiJodHRwOi8vbG9jYWxob3N0OjUzODE2LyJ9.uIAwN7s0Wc9BEDU1Zly7UT8pmLReqrIY51qpuewxKgs");
                    BufferedReader reader = new BufferedReader(new InputStreamReader(http.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }
                    obj = new JSONObject(sb.toString());

                    http.disconnect();
                }catch(IOException | JSONException e){
                    e.printStackTrace();
                }

            }

        });
        return obj;
    }

    public static boolean isNetworkAvailable(Context context) {

                try {
                    URL url = new URL("https://www.google.com");
                    HttpURLConnection urlConnect = (HttpURLConnection) url.openConnection();
                    urlConnect.setConnectTimeout(2000);
                    urlConnect.setReadTimeout(2000);
                    BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnect.getInputStream()));
                    return true;
                } catch (Exception e) {
                    return false;
                }

    }
}
