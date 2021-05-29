package com.abbad.smartonapp.ui.dashboard;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.util.JsonReader;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;


import com.abbad.smartonapp.activities.LoginActivity;
import com.abbad.smartonapp.activities.NoConnectionActivity;
import com.abbad.smartonapp.classes.Chaudiere;
import com.abbad.smartonapp.utils.WebServiceConnection;
import com.github.anastr.speedviewlib.SpeedView;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonWriter;


import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class DashboardViewModel extends ViewModel {

    private Timer timer;
public boolean stat_timer;
    public DashboardViewModel() {

    }

    public void onLoadSpeedMeters(SpeedView s1,SpeedView s2,SpeedView s3,SpeedView s4,
                                  SpeedView s5,SpeedView s6,SpeedView s7,SpeedView s8){
            s1.speedTo((float) 500.5);
            s2.speedTo((float) 380.6);
            s3.speedTo((float) 460.3);
            s4.speedTo((float) 720.4);
            s5.speedTo((float) 840.8);
            s6.speedTo((float) 460.2);
            s7.speedTo((float) 736.4);
            s8.speedTo((float) 374.9);

    }

    public void getChaudiereValues(SpeedView s1,SpeedView s2,SpeedView s3,SpeedView s4,
                                       SpeedView s5,SpeedView s6,Context context) throws IOException {

        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Log.i("sys_output", "run time");
                stat_timer = true;
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        Chaudiere chaudiere = null;
                        try {
                            URL url = new URL("http://smartonviatoile.com/api/Data/currentChaudiere/5fbccf26e06d8cb8a4ac500e/1");
                            HttpURLConnection http = (HttpURLConnection) url.openConnection();
                            http.setRequestProperty("Accept", "application/json");
                            http.setRequestProperty("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ0ZWNobmljaWVudEB2aWF0b2lsZS5jb20iLCJqdGkiOiI0NjliZDY4Ni04MjBlLTRjNTAtODQ5OC0zNTZiNjI3NjFiODEiLCJpYXQiOjE2MjIwMzAwNDgsInJvbCI6ImFwaV9hY2Nlc3MiLCJpZCI6IjYwYWQ2MzBhZWE2ZjVhNDQ1NmQ1ODE4NCIsIm5iZiI6MTYyMjAzMDA0NywiZXhwIjoxNjIyMDMzNjQ3LCJpc3MiOiJBcGkiLCJhdWQiOiJodHRwOi8vbG9jYWxob3N0OjUzODE2LyJ9.uIAwN7s0Wc9BEDU1Zly7UT8pmLReqrIY51qpuewxKgs");
                            BufferedReader reader = new BufferedReader(new InputStreamReader(http.getInputStream()));
                            StringBuilder sb = new StringBuilder();
                            String line;
                            while ((line = reader.readLine()) != null) {
                                sb.append(line);
                            }
                            JSONObject obj = new JSONObject(sb.toString());
                            JSONArray data = obj.getJSONArray("data");
                            JSONObject id = (JSONObject) data.get(0);

                            ((Activity) context).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        s1.speedTo((float) id.getDouble("temperatura_Ida"));
                                        s2.speedTo((float) id.getDouble("temperatura_Retorno"));
                                        s3.speedTo((float) id.getDouble("temperatura_Inercia"));
                                        s4.speedTo((float) id.getDouble("temperatura_Humos"));
                                        s5.speedTo((float) id.getDouble("dépression"));
                                        s6.speedTo((float) id.getDouble("luminosidad"));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                            http.disconnect();
                        } catch (IOException | JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }, 0, 2000);
    }

    public void cancelTimer(){
        Log.i("sys_output",Boolean.toString(stat_timer));

        if (stat_timer){
            timer.cancel();
            stat_timer = false;
        }
    }

}