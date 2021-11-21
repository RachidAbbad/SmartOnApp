package com.abbad.smartonapp.ui.notifications;

import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.RecyclerView;

import com.abbad.smartonapp.activities.MainActivity;
import com.abbad.smartonapp.adapters.NotificationAdapter;
import com.abbad.smartonapp.classes.Intervention;
import com.abbad.smartonapp.classes.Notification;
import com.abbad.smartonapp.classes.Task;
import com.abbad.smartonapp.classes.User;
import com.abbad.smartonapp.datas.NotificationData;
import com.abbad.smartonapp.dialogs.LoadingBottomDialog;
import com.abbad.smartonapp.dialogs.ResultBottomDialog;
import com.abbad.smartonapp.ui.dashboard.DashboardViewModel;
import com.abbad.smartonapp.ui.interventions.InterventionFragment;
import com.abbad.smartonapp.utils.Comun;
import com.abbad.smartonapp.utils.SessionManager;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NotificationsViewModel extends ViewModel {

    private static NotificationsFragment notificationsFragment;

    public NotificationsViewModel(NotificationsFragment notificationsFragment) {
        this.notificationsFragment = notificationsFragment;

    }

    static JSONArray infosJson;
    static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    static SimpleDateFormat out = new SimpleDateFormat("dd-MM-yyyy HH:mm");
    static List<Notification> tempListNotification = new ArrayList<>();
    static LoadingBottomDialog loadingBottomDialog = new LoadingBottomDialog("Chargement des notifications");

    public static void getNotifications(NotificationsFragment notificationsFragment){
        if(Comun.firstLoadDashboard){
            if (!loadingBottomDialog.isAdded()){
                loadingBottomDialog.show(loadingBottomDialog.getActivity().getSupportFragmentManager(),"");
            }
            Comun.firstLoadInterventions = false;
        }

        OkHttpClient.Builder builderClient = new OkHttpClient.Builder();
        builderClient.connectTimeout(300, TimeUnit.MINUTES);
        builderClient.readTimeout(300, TimeUnit.MINUTES);
        builderClient.writeTimeout(300, TimeUnit.MINUTES);
        OkHttpClient client = builderClient.build();
        Request request = new Request.Builder()
                .url("http://admin.smartonviatoile.com/api/Notification/user/" + SessionManager.getUserId(notificationsFragment.getActivity().getApplicationContext()))
                .addHeader("Authorization", "Bearer " + SessionManager.getAuthToken())
                .get().build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                new ResultBottomDialog("Echec de chargé les notifications", 3).show(notificationsFragment.getActivity().getSupportFragmentManager(), "");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try{
                    infosJson = new JSONObject(response.body().string()).getJSONArray("data");
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
                        tempListNotification.add(notification);
                    }
                    notificationsFragment.getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            notificationsFragment.notificationAdapter.setNewData(tempListNotification);
                        }
                    });

                }catch(Exception ex){
                    ex.printStackTrace();
                    new ResultBottomDialog("Error durant la sérialisation des notifications", 3).show(notificationsFragment.getActivity().getSupportFragmentManager(), "");
                }

                if(loadingBottomDialog.isAdded())
                    loadingBottomDialog.dismiss();
            }
        });
    }

    public static class GetNotificationsOnActivty extends AsyncTask<Void, Void, Void> {
        private MainActivity mainActivity;
        private SimpleDateFormat sdf, out;
        private JSONArray infosJson;
        private HttpURLConnection http;
        private boolean serverError = false;

        public List<Notification> tempListNotification;


        public GetNotificationsOnActivty(MainActivity ma) {
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
                URL url = new URL("http://admin.smartonviatoile.com/api/Notification/user/" + SessionManager.getUserId(mainActivity.getApplicationContext()));
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
                    tempListNotification.add(notification);
                }
                notificationsFragment.notificationAdapter.setNewData(tempListNotification);
            }
        }
    }
}