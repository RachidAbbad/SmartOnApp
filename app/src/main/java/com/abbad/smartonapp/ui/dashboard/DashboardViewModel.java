package com.abbad.smartonapp.ui.dashboard;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.util.JsonReader;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;


import com.abbad.smartonapp.classes.Chaudiere;
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

public class DashboardViewModel extends ViewModel {


    public DashboardViewModel() {

    }

    /*public void initialize(TextView textView) throws IOException {
        String tokenUser = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ0ZWNobmljaWVudEB2aWF0b2lsZS5jb20iLCJqdGkiOiJlOTQ4ZDJkNi1hZWVlLTQ0YzktOGZmZS04MzBiNzQwZDM1NjIiLCJpYXQiOjE2MjIwMTcxMTksInJvbCI6ImFwaV9hY2Nlc3MiLCJpZCI6IjYwYWQ2MzBhZWE2ZjVhNDQ1NmQ1ODE4NCIsIm5iZiI6MTYyMjAxNzExOSwiZXhwIjoxNjIyMDIwNzE5LCJpc3MiOiJBcGkiLCJhdWQiOiJodHRwOi8vbG9jYWxob3N0OjUzODE2LyJ9.vt-06Kp64bCicpGbdRA_Sx_eQvHKMcH0NN-IPXiNG60";

        URL url = new URL("http://smartonviatoile.com/api/Data/currentChaudiere/5fbccf26e06d8cb8a4ac500e/1");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestProperty(HttpHeaders.AUTHORIZATION,"Bearer "+tokenUser);
        //e.g. bearer token= eyJhbGciOiXXXzUxMiJ9.eyJzdWIiOiPyc2hhcm1hQHBsdW1zbGljZS5jb206OjE6OjkwIiwiZXhwIjoxNTM3MzQyNTIxLCJpYXQiOjE1MzY3Mzc3MjF9.O33zP2l_0eDNfcqSQz29jUGJC-_THYsXllrmkFnk85dNRbAw66dyEKBP5dVcFUuNTA8zhA83kk3Y41_qZYx43T

        conn.setRequestProperty("Content-Type","application/json");
        conn.setRequestMethod("GET");


        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String output;

        StringBuffer response = new StringBuffer();
        while ((output = in.readLine()) != null) {
            response.append(output);
        }

        in.close();
        // printing result from response
        Log.i("response_serv",response.toString());
        Log.i("response_serv","response.toString()");

    }*/

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
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                Chaudiere chaudiere = null;
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
                    JSONObject obj = new JSONObject(sb.toString());
                    JSONArray data = obj.getJSONArray("data");
                    JSONObject id= (JSONObject) data.get(0);


                        chaudiere = new Chaudiere(id.get("id_capteur").toString(),
                                (float)id.getDouble("temperatura_Ida"),
                                (float)id.getDouble("temperatura_Retorno"),
                                (float)id.getDouble("temperatura_Inercia"),
                                (float)id.getDouble("temperatura_Humos"),
                                (float)id.getDouble("dépression"),
                                (float)id.getDouble("luminosidad")
                        );


                    http.disconnect();
                }catch(IOException | JSONException e){
                    e.printStackTrace();
                }
                Chaudiere finalChaudiere = chaudiere;
                ((Activity)context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        s1.speedTo(finalChaudiere.getT_depart());
                        s2.speedTo(finalChaudiere.getT_retour());
                        s3.speedTo(finalChaudiere.getT_inertie());
                        s4.speedTo(finalChaudiere.getT_fumee());
                        s5.speedTo(finalChaudiere.getDepression());
                        s6.speedTo(finalChaudiere.getLuminositee());
                    }
                });

            }
        });

    }
}