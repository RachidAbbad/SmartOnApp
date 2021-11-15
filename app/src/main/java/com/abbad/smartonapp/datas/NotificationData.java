package com.abbad.smartonapp.datas;

import android.os.AsyncTask;
import android.service.voice.VoiceInteractionService;
import android.util.Log;

import com.abbad.smartonapp.R;
import com.abbad.smartonapp.activities.MainActivity;
import com.abbad.smartonapp.classes.Notification;
import com.abbad.smartonapp.dialogs.ResultBottomDialog;
import com.abbad.smartonapp.services.PushNotificationsService;
import com.abbad.smartonapp.ui.interventions.InterventionFragment;
import com.abbad.smartonapp.ui.notifications.NotificationsFragment;
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

public class NotificationData {

    public static List<Notification> listNotifications;

    public static List<Notification> getListNotifications(NotificationsFragment notificationsFragment) {
        GetNotificationsOnActivty getNotificationsOnActivty = new GetNotificationsOnActivty(notificationsFragment);
        getNotificationsOnActivty.execute();
        return listNotifications;
    }


    public static class GetAllNotification extends AsyncTask<Void, Void, Void> {
        PushNotificationsService pushNotificationsService;
        List<Notification> list_notifications;
        JSONArray infosJson;

        public GetAllNotification(PushNotificationsService pushNotificationsService) {
            this.pushNotificationsService = pushNotificationsService;
        }

        @Override
        protected void onPreExecute() {
            list_notifications = new ArrayList<>();
            infosJson = new JSONArray();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try{
                URL url = new URL("http://admin.smartonviatoile.com/api/Notification/user/" + SessionManager.getUserId(pushNotificationsService.getApplicationContext()));
                HttpURLConnection http = (HttpURLConnection) url.openConnection();
                http.setRequestProperty("Accept", "application/json");
                http.setRequestProperty("Authorization", "Bearer " + SessionManager.getAuthToken());
                if (http.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    pushNotificationsService.errorServer(http.getResponseCode()+" "+http.getResponseMessage());
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
            }catch (Exception ex){
                ex.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            //TODO Check every notification is new or old
            //TODO Add the new notifications to list_notifications
            //TODO if the notification is new -> new Notification will be displayed
        }

        private boolean isNewNotification(){
            return false;
        }
    }

    public static class GetNotificationsOnActivty extends AsyncTask<Void, Void, Void> {
        private NotificationsFragment notificationsFragment;
        private SimpleDateFormat sdf, out;
        private JSONArray infosJson;
        private HttpURLConnection http;
        private boolean serverError = false;


        public GetNotificationsOnActivty(NotificationsFragment ma) {
            this.notificationsFragment = ma;
        }

        @Override
        protected void onPreExecute() {
            sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            out = new SimpleDateFormat("dd-MM-yyyy HH:mm");
            listNotifications = new ArrayList<>();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                URL url = new URL("http://admin.smartonviatoile.com/api/Notification/user/" + SessionManager.getUserId(notificationsFragment.getActivity().getApplicationContext()));
                http = (HttpURLConnection) url.openConnection();
                http.setRequestProperty("Accept", "application/json");
                http.setRequestProperty("Authorization", "Bearer " + SessionManager.getAuthToken());
                if (http.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    serverError = true;
                    notificationsFragment.errorServer(http.getResponseCode()+" "+http.getResponseMessage());
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
                notificationsFragment.errorServer(notificationsFragment.getResources().getString(R.string.errorOccured));
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
                    listNotifications.add(notification);
                }

                if (listNotifications.size()==0){
                    notificationsFragment.noNotifications();
                }
            }

            Comun.nbTasksOnline++;
            if (Comun.nbTasksOnline == Comun.totalTasks && !Comun.isAllTasksFinished){
                MainActivity.loadingBottomDialog.dismiss();
                InterventionFragment.checkInCompletedIntervention((MainActivity) notificationsFragment.getActivity());
                Comun.isAllTasksFinished = true;
            }

        }
    }
}


