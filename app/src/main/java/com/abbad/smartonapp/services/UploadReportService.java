package com.abbad.smartonapp.services;

import static com.abbad.smartonapp.utils.Comun.API_URL;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.abbad.smartonapp.R;
import com.abbad.smartonapp.activities.MainActivity;
import com.abbad.smartonapp.classes.Intervention;
import com.abbad.smartonapp.classes.Task;
import com.abbad.smartonapp.classes.User;
import com.abbad.smartonapp.datas.InterventionData;
import com.abbad.smartonapp.datas.ReportData;
import com.abbad.smartonapp.dialogs.ResultBottomDialog;
import com.abbad.smartonapp.ui.interventions.InterventionFragment;
import com.abbad.smartonapp.utils.Comun;
import com.abbad.smartonapp.utils.InterventionManager;
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
import java.util.Collections;
import java.util.List;

public class UploadReportService extends Service {

    public static final String CHANNEL_ID = "UploadReportService";
    public String INTERVENTION_ID;
    public boolean intervStat;
    public int counter = 0, nbTaches, counterNone = 0;
    private Notification notification;
    private PendingIntent pendingIntent;
    private NotificationManager manager;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        INTERVENTION_ID = intent.getStringExtra("interv_id");
        nbTaches = intent.getIntExtra("nbTaches", 0);
        intervStat = intent.getBooleanExtra("etatIntervention",false);
        Log.e("ServiceLog", "Service Started");
        createNotificationChannel();
        Intent notificationIntent = new Intent(this, MainActivity.class);
        pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Upload Report")
                .setContentText("Uploading ...")
                .setSmallIcon(R.drawable.app_logo)
                .setVibrate(null) // Passing null here silently fails
                .setProgress(0, 100, true)
                .build();

        manager.notify(1, notification);


        startForeground(1, notification);

        //Log.e("RapportContent", ReportData.getFileToBin(InterventionData.getVideo(listInterventions.get(0).getId(), activity)));
        new GetInterventions().execute();

        Log.e("ServiceLog", "Service Returned");
        return START_REDELIVER_INTENT;

    }

    public Notification getNotification() {
        return notification;
    }

    public NotificationManager getManager() {
        return manager;
    }

    public PendingIntent getPendingIntent() {
        return pendingIntent;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Upload Report Service",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            serviceChannel.enableVibration(false);

            manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

    public void doneUpload() {

        notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Upload Report")
                .setContentText("Upload Done")
                .setSmallIcon(R.drawable.app_logo)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setVibrate(null) // Passing null here silently fails
                .setProgress(0, 0, false)
                .build();
        getManager().notify(1, notification);

        InterventionManager.resetAllData(INTERVENTION_ID, nbTaches, getApplicationContext());
    }

    public class GetInterventions extends AsyncTask<Void, Void, Void> {
        JSONObject infosJson;
        InterventionFragment fragment;
        HttpURLConnection http;
        SimpleDateFormat sdf;
        SimpleDateFormat output;
        boolean server_error = false;

        public GetInterventions() {
        }

        @Override
        protected void onPreExecute() {
            sdf = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");
            output = new SimpleDateFormat("dd-MM-yyyy");

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            try {
                URL url = new URL(API_URL+"/api/Intervention/" + InterventionManager.getCurrentIntervention());
                http = (HttpURLConnection) url.openConnection();
                http.setRequestProperty("Accept", "application/json");
                http.setRequestProperty("Authorization", "Bearer " + SessionManager.getAuthToken());
                if (http.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    server_error = true;
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
                infosJson = obj.getJSONObject("data");
                http.disconnect();
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        protected void onPostExecute(Void param) {
            if (!server_error) {
                try {
                    JSONObject object = infosJson;

                    //Get Collaborators :
                    List<User> collaborators = new ArrayList<>();
                    for (int j = 0; j < object.getJSONArray("id_collaborateur").length(); j++) {
                        collaborators.add(new User(object.getJSONArray("id_collaborateur").getString(j)));
                    }

                    //Get Tasks :
                    List<Task> tasks = new ArrayList<>();
                    for (int j = 0; j < object.getJSONArray("taches").length(); j++) {
                        List<String> equips = new ArrayList<>();
                        List<String> actions = new ArrayList<>();
                        String actionsTask = "";

                        for (int z = 0; z < object.getJSONArray("taches").getJSONObject(j).getJSONArray("actions").length(); z++) {
                            JSONObject action = object.getJSONArray("taches").getJSONObject(j).getJSONArray("actions").getJSONObject(z);
                            equips.add(action.getString("nom_equipement"));

                            for (int k = 0; k < action.getJSONArray("action").length(); k++) {
                                actionsTask = actionsTask.concat(action.getJSONArray("action").getString(k));
                                if (k + 1 != action.getJSONArray("action").length())
                                    actionsTask += " - ";
                                else
                                    actionsTask += ".";
                            }
                            actions.add(actionsTask);
                            actionsTask = "";
                        }

                        tasks.add(new Task(object.getString("id"), j,
                                object.getJSONArray("taches").getJSONObject(j).getString("zone"),
                                equips,
                                actions));
                    }

                    //Get Materials :
                    List<String> materials = new ArrayList<>();
                    for (int j = 0; j < object.getJSONArray("materiels").length(); j++) {
                        materials.add(object.getJSONArray("materiels").getString(j));
                    }

                    //Get Tools :
                    List<String> tools = new ArrayList<>();
                    for (int j = 0; j < object.getJSONArray("outils").length(); j++) {
                        tools.add(object.getJSONArray("outils").getString(j));
                    }


                    Intervention intervention = new Intervention(object.getString("id"),
                            object.getString("nom"),
                            output.format(sdf.parse(object.getString("date"))),
                            collaborators,
                            tasks,
                            materials,
                            tools
                    );

                    intervention.setType(object.getString("type"));
                    intervention.setIdResponsable(object.getString("id_responsable"));
                    intervention.setIdResponsableExecutif(object.getString("id_responsable_executif"));
                    intervention.setIdContremaitreExploitation(object.getString("id_contremaitre_exploitation"));
                    intervention.setIdSite(object.getString("id_site"));
                    intervention.setFullDateFormat(object.getString("date"));

                    intervention.setNomResponsable(object.getString("nom_responsable"));
                    intervention.setNomResponsableExecutif(object.getString("nom_responsable_executif"));
                    intervention.setNomContremaitreExploitation(object.getString("nom_contremaitre_exploitation"));
                    intervention.setNomSite(object.getString("nom_site"));

                    new ReportData.SendReportFiles(UploadReportService.this, intervention).execute();

                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }
        }
    }
}
