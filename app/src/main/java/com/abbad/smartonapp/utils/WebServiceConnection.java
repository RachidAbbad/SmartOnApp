package com.abbad.smartonapp.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;

import androidx.core.content.res.ResourcesCompat;

import com.abbad.smartonapp.R;
import com.abbad.smartonapp.activities.LoginActivity;
import com.abbad.smartonapp.activities.MainActivity;
import com.abbad.smartonapp.classes.User;
import com.google.gson.JsonObject;

import net.steamcrafted.loadtoast.LoadToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Timer;
import java.util.TimerTask;


public class WebServiceConnection {

    public static class loginSyncTask extends AsyncTask<Void, Void, Void> {
        LoginActivity loginActivity;
        boolean isValid;
        public loginSyncTask(LoginActivity loginA) {
            this.loginActivity = loginA;
        }

        @Override
        protected void onPreExecute(){
            loginActivity.getBtnLogin().startLoading();
            loginActivity.getBtnLogin().setEnabled(false);
            loginActivity.getPassInput().setEnabled(false);
            loginActivity.getEmailInput().setEnabled(false);

        }

        @Override
        protected Void doInBackground(Void... arg0) {

            try{
                URL url = new URL("http://smartonviatoile.com/api/Auth/login");
                HttpURLConnection http = (HttpURLConnection)url.openConnection();
                http.setRequestMethod("POST");
                http.setDoOutput(true);
                http.setRequestProperty("Content-Type", "application/json");

                String data = "{\"email\": \""+loginActivity.getEmailInput().getText().toString()
                        +"\",\n \"password\": \""+loginActivity.getPassInput().getText().toString()+"\"}";

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
                http.disconnect();
                isValid = obj.getBoolean("status");

                if(isValid){
                    SessionManager.saveUserInfos((JSONObject) ((JSONObject) obj.get("data")).get("user"),
                            loginActivity.getPassInput().getText().toString(),((JSONObject) obj.get("data")).getString("auth_token"));
                    Log.i("userInfos",((JSONObject) obj.get("data")).getString("auth_token"));
                }


            }catch(IOException | JSONException e){
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void param){
            isValid = true;
            if(isValid){
                loginActivity.getBtnLogin().loadingSuccessful();
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(loginActivity, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        loginActivity.startActivity(intent);
                        loginActivity.finish();
                    }
                },1000);
            }
            else{
                loginActivity.getBtnLogin().setEnabled(true);
                loginActivity.getPassInput().setEnabled(true);
                loginActivity.getEmailInput().setEnabled(true);
                loginActivity.getBtnLogin().loadingFailed();
            }
        }


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
