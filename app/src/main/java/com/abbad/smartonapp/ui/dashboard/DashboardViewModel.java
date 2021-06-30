package com.abbad.smartonapp.ui.dashboard;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.text.PrecomputedText;
import android.util.JsonReader;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;


import com.abbad.smartonapp.activities.LoginActivity;
import com.abbad.smartonapp.activities.MainActivity;
import com.abbad.smartonapp.activities.NoConnectionActivity;
import com.abbad.smartonapp.classes.Chaudiere;
import com.abbad.smartonapp.utils.SessionManager;
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

    private Timer timer = new Timer();
public boolean stat_timer;
    public DashboardViewModel() {

    }


    public void refreshData(DashboardFragment dash) throws IOException {


        stat_timer = true;
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Log.i("sys_output", "DashBoard values has been updated");

                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        new GetInfoTask(dash).execute();
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


    public static class GetInfoTask extends AsyncTask<Void,Void,Void>{
        JSONObject infosJson;
        DashboardFragment dash;

        public GetInfoTask(DashboardFragment f){
            dash = f;
        }
        @Override
        protected void onPreExecute(){


        }

        @Override
        protected Void doInBackground(Void... arg0) {
            try {
                URL url = new URL("http://smartonviatoile.com/api/Data/currentChaudiere/5fbccf26e06d8cb8a4ac500e/1");
                HttpURLConnection http = (HttpURLConnection) url.openConnection();
                http.setRequestProperty("Accept", "application/json");
                http.setRequestProperty("Authorization", "Bearer "+ SessionManager.getAuthToken());
                BufferedReader reader = new BufferedReader(new InputStreamReader(http.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                JSONObject obj = new JSONObject(sb.toString());
                JSONArray data = obj.getJSONArray("data");
                infosJson = (JSONObject) data.get(0);
                http.disconnect();
            } catch (IOException | JSONException e) {
                Log.i("Exception :","NoConnection to get chaudiere values");
                this.cancel(true);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void param){
            try {
                dash.getS1().speedTo((float) infosJson.getDouble("temperatura_Ida"));
                dash.getS2().speedTo((float) infosJson.getDouble("temperatura_Retorno"));
                dash.getS3().speedTo((float) infosJson.getDouble("temperatura_Inercia"));
                dash.getS4().speedTo((float) infosJson.getDouble("temperatura_Humos"));
                dash.getS5().speedTo((float) infosJson.getDouble("dépression"));
                dash.getS6().speedTo((float) infosJson.getDouble("luminosidad"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}