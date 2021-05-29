package com.abbad.smartonapp.utils;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.text.BoringLayout;
import android.util.Log;

import androidx.annotation.RequiresApi;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class WebServiceConnection {
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void isloginValid(String email, String pass) throws IOException, JSONException {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try{
                    URL url = new URL("http://smartonviatoile.com/api/Auth/login");
                    HttpURLConnection http = (HttpURLConnection)url.openConnection();
                    http.setRequestMethod("POST");
                    http.setDoOutput(true);
                    http.setRequestProperty("Content-Type", "application/json");

                    String data = "{\"email\": \""+email+"\",\n \"password\": \""+pass+"\"}";

                    byte[] out = data.getBytes(StandardCharsets.UTF_8);

                    OutputStream stream = http.getOutputStream();
                    stream.write(out);

                    BufferedReader reader = new BufferedReader(new InputStreamReader(http.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }
                    JSONObject obj = new JSONObject(sb.toString());
                    boolean status = (boolean) obj.getBoolean("status");


                    Log.i("sys_output", Boolean.toString(status));
                    http.disconnect();
                }catch(IOException | JSONException e){
                    e.printStackTrace();
                }

            }
        });

    }
}
