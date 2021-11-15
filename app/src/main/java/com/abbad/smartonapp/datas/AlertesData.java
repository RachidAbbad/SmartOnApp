package com.abbad.smartonapp.datas;

import android.os.AsyncTask;
import android.util.Log;

import com.abbad.smartonapp.activities.MainActivity;
import com.abbad.smartonapp.classes.Notification;
import com.abbad.smartonapp.ui.interventions.InterventionFragment;
import com.abbad.smartonapp.utils.Comun;
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
import java.util.concurrent.ExecutionException;

public class AlertesData {

    public static List<Notification> listAlertes;

    public static List<Notification> getListAlertes(MainActivity mainActivity) {
        GetAlerts getAlerts = new GetAlerts(mainActivity);
        getAlerts.execute();
        return listAlertes;
    }

    public static class GetAlerts extends AsyncTask<Void, Void, Void> {
        private MainActivity mainActivity;
        private SimpleDateFormat sdf, out;
        private JSONArray infosJson;
        private HttpURLConnection http;
        private boolean serverError = false;

        public List<Notification> tempListNotification;


        public GetAlerts(MainActivity ma) {
            this.mainActivity = ma;
        }

        @Override
        protected void onPreExecute() {
            sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            out = new SimpleDateFormat("dd-MM-yyyy HH:mm");
            listAlertes = new ArrayList<>();
            tempListNotification = new ArrayList<>();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                URL url = new URL("http://admin.smartonviatoile.com/api/Notification/Alertes/" + SessionManager.getUserId(mainActivity.getApplicationContext()));
                http = (HttpURLConnection) url.openConnection();
                http.setRequestProperty("Accept", "application/json");
                http.setRequestProperty("Authorization", "Bearer " + SessionManager.getAuthToken());
                if (http.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    serverError = true;
                    return null;
                }
                serverError = false;

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
                //TODO Notification Error
                this.cancel(true);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (!serverError){
                for (int i = 0; i < infosJson.length(); i++) {
                    Notification notification = null;
                    try {
                        JSONObject notif = infosJson.getJSONObject(i);
                        notification = new Notification();
                        notification.setTitle(notif.getString("title"));
                        notification.setId(notif.getString("id"));
                        notification.setBody(notif.getString("body"));
                        notification.setGravity(notif.getString("gravity"));
                        notification.setLu(notif.getString("lu"));
                        notification.setVu(notif.getString("vu"));
                        notification.setTime(out.format(sdf.parse(notif.getString("timestamp"))));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    listAlertes.add(notification);
                    tempListNotification.add(notification);
                }
            }
            Comun.nbTasksOnline++;
            if (Comun.nbTasksOnline == Comun.totalTasks && !Comun.isAllTasksFinished){
                MainActivity.loadingBottomDialog.dismiss();
                InterventionFragment.checkInCompletedIntervention(mainActivity);
                Comun.isAllTasksFinished = true;
            }

        }

        private void reportError() {
        }

    }
}


