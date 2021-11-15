package com.abbad.smartonapp.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.abbad.smartonapp.R;
import com.abbad.smartonapp.adapters.RecycleViewAdapter;
import com.abbad.smartonapp.classes.Intervention;
import com.abbad.smartonapp.classes.Task;
import com.abbad.smartonapp.classes.User;
import com.abbad.smartonapp.dialogs.LoadingBottomDialog;
import com.abbad.smartonapp.dialogs.ResultBottomDialog;
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
import java.util.Collections;
import java.util.List;

public class HistoriqueInteventions extends AppCompatActivity {

    LoadingBottomDialog loadingBottomDialog;
    RecyclerView recyclerView;
    RecycleViewAdapter recycleViewAdapter;
    List<Intervention> listInterventions;
    LinearLayout serverError,noInterventionLayout,workLayout;
    TextView exceptionText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historique_inteventions);

        loadingBottomDialog = new LoadingBottomDialog("Loading Interventions ...");
        recyclerView = findViewById(R.id.historique_recycler);
        noInterventionLayout = findViewById(R.id.noInterventionLayout);
        serverError = findViewById(R.id.serverError);
        workLayout = findViewById(R.id.workLayout);
        exceptionText = findViewById(R.id.exceptionText);

        new GetHistoriqueIntervention().execute();
    }


    private class GetHistoriqueIntervention extends AsyncTask<Void,Void,Void> {
        JSONArray infosJson;
        HttpURLConnection http;
        SimpleDateFormat sdf;
        SimpleDateFormat output,hourOutput;

        boolean server_error;

        @Override
        protected void onPreExecute() {
            showLoading();
            sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            output = new SimpleDateFormat("dd-MM-yyyy");
            hourOutput = new SimpleDateFormat("HH:mm");
            listInterventions = new ArrayList<>();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                URL url = new URL("http://admin.smartonviatoile.com/api/Intervention/Filtre/n/"+SessionManager.getUserId(getApplicationContext())+"/Termin%C3%A9e/n");
                http = (HttpURLConnection) url.openConnection();
                http.setRequestProperty("Accept", "application/json");
                http.setRequestProperty("Authorization", "Bearer " + SessionManager.getAuthToken());
                if (http.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    server_error = true;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                errorServer(http.getResponseCode()+" "+http.getResponseMessage());
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
                new ResultBottomDialog(e.getMessage(), 3).show(getSupportFragmentManager(), null);
                this.cancel(true);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
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
                        intervention.setHeureDebut(hourOutput.format(sdf.parse(object.getString("heure_debut"))));
                        intervention.setHeureFin(hourOutput.format(sdf.parse(object.getString("heure_fin"))));

                        listInterventions.add(intervention);
                    }
                    try {
                        if (listInterventions.size() == 0)
                            noIntervention();
                        else {
                            backToService();
                            Collections.reverse(listInterventions);
                            recyclerView.setAdapter(new RecycleViewAdapter(HistoriqueInteventions.this,listInterventions));
                        }


                    } catch (Exception ex) {
                        ex.printStackTrace();
                        new ResultBottomDialog(getResources().getString(R.string.errorOccured),3).show(getSupportFragmentManager(),null);
                    }

                } catch (Exception ex) {
                    new ResultBottomDialog(ex.getMessage(), 3).show(getSupportFragmentManager(), null);
                }
            }
            stopLoading();
        }
    }

    private void showLoading(){
        loadingBottomDialog.show(getSupportFragmentManager(),null);
    }

    private void stopLoading(){
        loadingBottomDialog.dismiss();
    }

    public void noIntervention(){
        noInterventionLayout.setVisibility(View.VISIBLE);
        workLayout.setVisibility(View.GONE);
        serverError.setVisibility(View.GONE);
    }

    public void errorServer(String textEx){
        noInterventionLayout.setVisibility(View.GONE);
        workLayout.setVisibility(View.GONE);
        serverError.setVisibility(View.VISIBLE);
        exceptionText.setText(textEx);
    }

    public void backToService(){
        noInterventionLayout.setVisibility(View.GONE);
        workLayout.setVisibility(View.VISIBLE);
        serverError.setVisibility(View.GONE);
    }
}