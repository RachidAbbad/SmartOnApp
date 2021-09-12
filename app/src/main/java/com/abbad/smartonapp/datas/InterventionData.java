package com.abbad.smartonapp.datas;

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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class InterventionData {
    public static List<Intervention> listInterventions;

    public static Intervention currentIntervention;

    public static boolean server_error = false;

    public InterventionData() {

    }

    public static List<Intervention> getListInterventions(InterventionFragment fragment) {
        GetInterventions getInterventions = new GetInterventions(fragment);
        getInterventions.execute();

        return listInterventions;
    }

    private static void fillInterv1() {
        List<User> listUsers;
        List<Task> listTasks;
        List<String> listMateriels;
        List<String> listOutils;
        for (int i = 0; i < 10; i++) {
            //List of collaborators
            listUsers = new ArrayList<>();
            listUsers.add(new User("UZY763EUU", "Samir", "ElHilali", "samir.elhilali@viatoile.com"));
            listUsers.add(new User("UDGEU3763", "Alae", "Saadi", "alae.saadi@viatoile.com"));

            //List of tasks
            listTasks = new ArrayList<>();
            listTasks.add(new Task("Interv735651", 1, "le nettoyage intérieur avec repose après travaux", Arrays.asList("Equipement 1", "Equipement 2"), Arrays.asList("Action 1", "Action 2")));
            listTasks.add(new Task("Interv735651", 2, "le nettoyage des éléments et du local après intervention", Arrays.asList("Equipement 1", "Equipement 2"), Arrays.asList("Action 1", "Action 2")));

            //List of Materials :
            listMateriels = new ArrayList<>();
            listMateriels.add("Amperemetre");
            listMateriels.add("Barometre");

            //List of outils
            listOutils = new ArrayList<>();
            listOutils.add("Tournevis");
            listOutils.add("Lunettes de protection");

            Intervention intervention = new Intervention("Interv735651",
                    "la dépose de la buse de fumée du raccordement au conduit",
                    "30-08-2021",
                    listUsers,
                    listTasks,
                    listMateriels,
                    listOutils);

            intervention.setType("corrective");

            listInterventions.add(intervention);
        }
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

    public static class GetInterventions extends AsyncTask<Void, Void, Void> {
        JSONArray infosJson;
        InterventionFragment fragment;
        HttpURLConnection http;
        SimpleDateFormat sdf;
        SimpleDateFormat output;

        public GetInterventions(InterventionFragment f) {
            fragment = f;
        }

        @Override
        protected void onPreExecute() {
            sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            output = new SimpleDateFormat("dd-MM-yyyy");
            listInterventions = new ArrayList<>();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            try {
                if (fragment.getActivity() == null){
                    this.cancel(true);
                }
                URL url = new URL("http://admin.smartonviatoile.com/api/Intervention/Technicien/"+SessionManager.getUserId(fragment.getActivity().getApplicationContext()));
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
                BufferedReader reader = new BufferedReader(new InputStreamReader(http.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                JSONObject obj = new JSONObject(sb.toString());
                Log.e("IntervResponce",obj.toString());
                infosJson = obj.getJSONArray("data");
                http.disconnect();
            } catch (IOException | JSONException e) {
                Log.i("Exception :", "NoConnection to get Interventions");
                if(fragment.getActivity() != null)
                new ResultBottomDialog(e.getMessage(), 3).show(fragment.getActivity().getSupportFragmentManager(), null);
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
                                    if (k+1 != action.getJSONArray("action").length())
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

                        listInterventions.add(intervention);
                    }
                    try {
                        if (listInterventions.size() == 0)
                            fragment.noIntervention();
                        else {
                            fragment.backToService();
                            Collections.reverse(listInterventions);
                            fragment.refreshAll(listInterventions);
                        }
                    } catch (Exception ex) {
                        if(fragment.getActivity() != null)
                        new ResultBottomDialog(fragment.getResources().getString(R.string.errorOccured),3).show(fragment.getActivity().getSupportFragmentManager(),null);
                    }

                } catch (Exception ex) {
                    if(fragment.getActivity() != null)
                        new ResultBottomDialog(ex.getMessage(), 3).show(fragment.getActivity().getSupportFragmentManager(), null);
                }

                Comun.nbTasksOnline++;
                if (Comun.nbTasksOnline == 4 && !Comun.isAllTasksFinished){
                    MainActivity.loadingBottomDialog.dismiss();
                    InterventionFragment.checkInCompletedIntervention((MainActivity) fragment.getActivity());
                    Comun.isAllTasksFinished = true;
                }

            }
        }
    }

    public static File getImage(String idIntervention, Context service) {
        try {

            File directory = new File(service.getExternalCacheDir() + "/FinalImages");
            if (directory.listFiles() == null)
                return null;
            List<File> files = Arrays.asList(directory.listFiles());
            for (int i = 0; i < files.size(); i++) {
                String name = files.get(i).getName();
                if (name.contains(idIntervention))
                    return files.get(i);
            }
            return null;
        } catch (Exception ex) {
            return null;
        }
    }

    public static File getVideo(String idIntervention, Context service) {
        File directory = null;
        try {
            directory = new File(service.getExternalCacheDir() + "/FinalVideo");
            if (directory.listFiles() == null)
                return null;
            List<File> files = Arrays.asList(directory.listFiles());
            for (int i = 0; i < files.size(); i++) {
                String name = files.get(i).getName();
                if (name.contains(idIntervention))
                    return files.get(i);
            }
            return null;

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static File getAudio(String idIntervention, Context service) {
        File directory = null;
        try {
            directory = new File(service.getExternalCacheDir() + "/FinalAudios");
            if (directory.listFiles() == null)
                return null;
            List<File> files = Arrays.asList(directory.listFiles());
            for (int i = 0; i < files.size(); i++) {
                String name = files.get(i).getName();
                if (name.contains(idIntervention))
                    return files.get(i);
            }
            return null;

        } catch (Exception ex) {
            return null;
        }
    }

    public static File getComment(String idIntervention, Context service) {
        File directory = null;
        try {
            directory = new File(service.getExternalCacheDir() + "/FinalComments");
            if (directory.listFiles() == null)
                return null;
            List<File> files = Arrays.asList(directory.listFiles());
            for (int i = 0; i < files.size(); i++) {
                String name = files.get(i).getName();
                if (name.contains(idIntervention))
                    return files.get(i);
            }
            return null;

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

}
