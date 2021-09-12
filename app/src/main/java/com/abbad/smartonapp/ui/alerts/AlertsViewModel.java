package com.abbad.smartonapp.ui.alerts;

import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.ViewModel;

import com.abbad.smartonapp.activities.MainActivity;
import com.abbad.smartonapp.classes.Notification;
import com.abbad.smartonapp.utils.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class AlertsViewModel extends ViewModel {
    public static AlertsFragment alertsFragment;

    public AlertsViewModel(AlertsFragment alertsFragment){
        this.alertsFragment = alertsFragment;
    }

    public static class GetAlertsOnActivty extends AsyncTask<Void, Void, Void> {
        private MainActivity mainActivity;
        private SimpleDateFormat sdf, out;
        private JSONArray infosJson;
        private HttpURLConnection http;
        private boolean serverError = false;

        public List<Notification> tempListNotification;


        public GetAlertsOnActivty(MainActivity ma) {
            this.mainActivity = ma;
        }

        @Override
        protected void onPreExecute() {
            sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            out = new SimpleDateFormat("dd-MM-yyyy HH:mm");
            tempListNotification = new ArrayList<>();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                if (mainActivity == null)
                    this.cancel(true);
                URL url = new URL("http://admin.smartonviatoile.com/api/Notification/Alertes/" + SessionManager.getUserId(mainActivity.getApplicationContext()));
                http = (HttpURLConnection) url.openConnection();
                http.setRequestProperty("Accept", "application/json");
                http.setRequestProperty("Authorization", "Bearer " + SessionManager.getAuthToken());
                if (http.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    serverError = true;
                    return null;
                }
                BufferedReader reader = new BufferedReader(new InputStreamReader(http.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                JSONObject obj = new JSONObject(sb.toString());
                Log.e("IntervResponce", obj.toString());
                infosJson = obj.getJSONArray("data");
                http.disconnect();
            } catch (IOException | JSONException e) {
                Log.i("Exception :", "NoConnection to get Interventions");
                this.cancel(true);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (!serverError){
                for (int i = 0; i < infosJson.length(); i++) {
                    Notification alert = null;
                    try {
                        JSONObject notif = infosJson.getJSONObject(i);
                        alert = new Notification();
                        alert.setTitle(notif.getString("title"));
                        alert.setId(notif.getString("id"));
                        alert.setBody(notif.getString("body"));
                        alert.setGravity(notif.getString("gravity"));
                        alert.setLu(notif.getString("lu"));
                        alert.setVu(notif.getString("vu"));
                        alert.setTime(out.format(sdf.parse(notif.getString("timestamp"))));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    tempListNotification.add(alert);
                }
                alertsFragment.alertsAdapter.setNewData(tempListNotification);
            }
        }
    }
}