package com.abbad.smartonapp.datas;

import static com.abbad.smartonapp.utils.Comun.API_URL;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.view.View;

import androidx.annotation.RequiresApi;

import com.abbad.smartonapp.R;
import com.abbad.smartonapp.activities.MainActivity;
import com.abbad.smartonapp.classes.Intervention;
import com.abbad.smartonapp.classes.Report;
import com.abbad.smartonapp.classes.Task;
import com.abbad.smartonapp.classes.User;
import com.abbad.smartonapp.dialogs.LoadingBottomDialog;
import com.abbad.smartonapp.dialogs.ResultBottomDialog;
import com.abbad.smartonapp.ui.dashboard.DashboardFragment;
import com.abbad.smartonapp.ui.interventions.InterventionFragment;
import com.abbad.smartonapp.utils.Comun;
import com.abbad.smartonapp.utils.InterventionManager;
import com.abbad.smartonapp.utils.SessionManager;
import com.dx.dxloadingbutton.lib.LoadingButton;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class InterventionData {
    public static List<Intervention> listInterventions;

    public static Intervention currentIntervention;

    public static boolean server_error = false;
    static JSONArray infosJson;
    static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX");
    static SimpleDateFormat output = new SimpleDateFormat("dd-MM-yyyy");
    static SimpleDateFormat hourOutput = new SimpleDateFormat("HH:mm");
    static List<Intervention> interventionList = new ArrayList<>();
    static LoadingBottomDialog loadingInterventons = new LoadingBottomDialog("Chargement des interventions ...");

    public InterventionData() {

    }

    public static List<Intervention> getListInterventions(InterventionFragment fragment) {
        getAllInterventions(fragment);
        return listInterventions;
    }

    public static Intervention getInterventionById(String id) {
        for (Intervention inter : listInterventions) {
            if (inter.getId().equals(id))
                return inter;
        }
        return null;
    }

    public static List<File> getImages(String idIntervention, Context activity) {
        try {

            File directory = new File(activity.getExternalCacheDir() + "/FinalImages");
            List<File> files = Arrays.asList(directory.listFiles());
            List<File> results = new ArrayList<>();
            for (int i = 0; i < files.size(); i++) {
                String name = files.get(i).getName();
                if (name.contains(idIntervention))
                    results.add(files.get(i));
            }
            return results;
        } catch (Exception ex) {
            return new ArrayList<>();
        }
    }

    public static List<File> getVideos(String idIntervention, Context activity) {
        File directory = null;
        try {
            directory = new File(activity.getExternalCacheDir() + "/FinalVideo");
            List<File> files = Arrays.asList(directory.listFiles());
            List<File> results = new ArrayList<>();
            for (int i = 0; i < files.size(); i++) {
                String name = files.get(i).getName();
                if (name.contains(idIntervention))
                    results.add(files.get(i));
            }
            return results;

        } catch (Exception ex) {
            return new ArrayList<>();
        }
    }

    public static List<File> getAudios(String idIntervention, Context activity) {
        File directory = null;
        try {
            directory = new File(activity.getExternalCacheDir() + "/FinalAudios");
            List<File> files = Arrays.asList(directory.listFiles());
            List<File> results = new ArrayList<>();
            for (int i = 0; i < files.size(); i++) {
                String name = files.get(i).getName();
                if (name.contains(idIntervention))
                    results.add(files.get(i));
            }
            return results;

        } catch (Exception ex) {
            return new ArrayList<>();
        }
    }

    ;

    public static List<File> getComments(String idIntervention, Context activity) {
        File directory = null;
        try {
            directory = new File(activity.getExternalCacheDir() + "/FinalComments");
            List<File> files = Arrays.asList(directory.listFiles());
            List<File> results = new ArrayList<>();
            for (int i = 0; i < files.size(); i++) {
                String name = files.get(i).getName();
                if (name.contains(idIntervention))
                    results.add(files.get(i));
            }
            return results;

        } catch (Exception ex) {
            return new ArrayList<>();
        }
    }

    public static boolean deleteAllData(String idIntervention, Context activity) {
        boolean deleteResult = true;
        List<File> listFiles = new ArrayList<>();
        listFiles.addAll(getVideos(idIntervention, activity));
        listFiles.addAll(getAudios(idIntervention, activity));
        listFiles.addAll(getImages(idIntervention, activity));
        listFiles.addAll(getComments(idIntervention, activity));
        try {
            for (File file : listFiles) {
                if (!file.delete())
                    deleteResult = false;
            }
            return deleteResult;
        } catch (Exception ex) {
            System.out.printf(ex.getMessage());
            return false;
        }
    }

    public static void getAllInterventions(InterventionFragment interventionFragment) {
        if (Comun.firstLoadInterventions) {
            if (!loadingInterventons.isAdded())
                loadingInterventons.show(interventionFragment.getActivity().getSupportFragmentManager(), "");
            Comun.firstLoadInterventions = false;
        }
        OkHttpClient.Builder builderClient = new OkHttpClient.Builder();
        builderClient.connectTimeout(300, TimeUnit.MINUTES);
        builderClient.readTimeout(300, TimeUnit.MINUTES);
        builderClient.writeTimeout(300, TimeUnit.MINUTES);
        OkHttpClient client = builderClient.build();
        Request request = new Request.Builder()
                .url(API_URL+"/api/Intervention/Technicien/" + SessionManager.getUserId(interventionFragment.getActivity().getApplicationContext()))
                .addHeader("Authorization", "Bearer " + SessionManager.getAuthToken())
                .get().build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                new ResultBottomDialog("Echec de chargé les interventions", 3).show(interventionFragment.getActivity().getSupportFragmentManager(), "");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try {
                    interventionList.clear();
                    listInterventions.clear();
                    infosJson = new JSONObject(response.body().string()).getJSONArray("data");
                    for (int i = 0; i < infosJson.length(); i++) {
                        JSONObject object = infosJson.getJSONObject(i);

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
                        Date heureDebut = sdf.parse(object.getString("heure_debut"));
                        Date heureFin = sdf.parse(object.getString("heure_fin"));
                        intervention.setHeureDebut(hourOutput.format(heureDebut));
                        intervention.setHeureFin(hourOutput.format(heureFin));

                        interventionList.add(intervention);
                    }
                    listInterventions.clear();
                    Collections.sort(interventionList, new Comparator<Intervention>() {
                        DateFormat df = new SimpleDateFormat("dd-MM-yyyy");

                        @Override
                        public int compare(Intervention s1, Intervention s2) {
                            try {
                                return df.parse(s1.getDate()).compareTo(df.parse(s2.getDate())) * (-1);
                            } catch (ParseException e) {
                                throw new IllegalArgumentException(e);
                            }
                        }
                    });
                    listInterventions.addAll(interventionList);
                    interventionFragment.getActivity().runOnUiThread(new Runnable() {
                        @RequiresApi(api = Build.VERSION_CODES.O)
                        @Override
                        public void run() {
                            try {
                                if (listInterventions.size() == 0)
                                    interventionFragment.noIntervention();
                                else {
                                    interventionFragment.backToService();
                                    interventionFragment.refreshAll(listInterventions);
                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    });

                } catch (Exception ex) {
                    ex.printStackTrace();
                    new ResultBottomDialog(ex.getMessage(), 3).show(interventionFragment.getActivity().getSupportFragmentManager(), null);
                }
                loadingInterventons.dismiss();

            }
        });
    }

    public static class GetInterventions extends AsyncTask<Void, Void, Void> {
        InterventionFragment fragment;
        HttpURLConnection http;
        JSONArray infosJson;
        SimpleDateFormat sdf;
        SimpleDateFormat output;
        SimpleDateFormat hourOutput;
        List<Intervention> interventionList;

        public GetInterventions(InterventionFragment f) {
            fragment = f;
            interventionList = new ArrayList<>();
        }

        @Override
        protected void onPreExecute() {
            sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX");
            output = new SimpleDateFormat("dd-MM-yyyy");
            hourOutput = new SimpleDateFormat("HH:mm");
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            try {
                if (fragment.getActivity() == null) {
                    this.cancel(true);
                }
                URL url = new URL(API_URL+"/api/Intervention/Technicien/" + SessionManager.getUserId(fragment.getActivity().getApplicationContext()));
                http = (HttpURLConnection) url.openConnection();
                http.setRequestProperty("Accept", "application/json");
                http.setRequestProperty("Authorization", "Bearer " + SessionManager.getAuthToken());
                if (http.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    server_error = true;
                    fragment.getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                fragment.errorServer(http.getResponseCode() + " " + http.getResponseMessage());
                            } catch (IOException exception) {
                                exception.printStackTrace();
                            }
                        }
                    });
                    return null;
                }

                server_error = false;
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
                if (fragment.getActivity() != null)
                    //new ResultBottomDialog(e.getMessage(), 3).show(fragment.getActivity().getSupportFragmentManager(), null);
                    this.cancel(true);
            }
            return null;
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        protected void onPostExecute(Void param) {
            if (!server_error) {
                try {
                    for (int i = 0; i < infosJson.length(); i++) {
                        JSONObject object = infosJson.getJSONObject(i);

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
                        Date heureDebut = sdf.parse(object.getString("heure_debut"));
                        Date heureFin = sdf.parse(object.getString("heure_fin"));
                        intervention.setHeureDebut(hourOutput.format(heureDebut));
                        intervention.setHeureFin(hourOutput.format(heureFin));

                        interventionList.add(intervention);
                    }
                    try {
                        listInterventions.clear();
                        Collections.sort(interventionList, new Comparator<Intervention>() {
                            DateFormat df = new SimpleDateFormat("dd-MM-yyyy");

                            @Override
                            public int compare(Intervention s1, Intervention s2) {
                                try {
                                    return df.parse(s1.getDate()).compareTo(df.parse(s2.getDate())) * (-1);
                                } catch (ParseException e) {
                                    throw new IllegalArgumentException(e);
                                }
                            }
                        });
                        listInterventions.addAll(interventionList);
                        if (listInterventions.size() == 0)
                            fragment.noIntervention();
                        else {
                            fragment.backToService();

                            fragment.refreshAll(listInterventions);
                        }
                    } catch (Exception ex) {
                        if (fragment.getActivity() != null)
                            new ResultBottomDialog(fragment.getResources().getString(R.string.errorOccured), 3).show(fragment.getActivity().getSupportFragmentManager(), null);
                    }

                } catch (Exception ex) {
                    if (fragment.getActivity() != null)
                        ex.printStackTrace();
                    new ResultBottomDialog(ex.getMessage(), 3).show(fragment.getActivity().getSupportFragmentManager(), null);
                }

            }

        }
    }
}
