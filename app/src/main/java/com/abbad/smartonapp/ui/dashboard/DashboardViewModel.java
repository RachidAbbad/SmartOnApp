package com.abbad.smartonapp.ui.dashboard;

import android.annotation.SuppressLint;
import android.os.AsyncTask;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.lifecycle.ViewModel;

import com.abbad.smartonapp.R;
import com.abbad.smartonapp.activities.MainActivity;
import com.abbad.smartonapp.dialogs.ResultBottomDialog;
import com.abbad.smartonapp.ui.interventions.InterventionFragment;
import com.abbad.smartonapp.utils.Comun;
import com.abbad.smartonapp.utils.SessionManager;
import com.github.anastr.speedviewlib.SpeedView;
import com.ramijemli.percentagechartview.PercentageChartView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class DashboardViewModel extends ViewModel {

    public boolean server_error = false, first_time = true, firstUse = true;
    private Timer timer = new Timer();
    public List<View> ballonsViews,silosViews;

    public DashboardViewModel() {

    }

    public void refreshData(DashboardFragment dash) throws IOException {

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Log.i("sys_output", "DashBoard values has been updated");

                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        new GetInfoTask(dash).execute();
                    }
                });
            }
        }, 0, 10000);
    }

    public class GetInfoTask extends AsyncTask<Void, Void, Void> {
        JSONObject infosJson;
        JSONArray infosBallons, infosSilo;
        DashboardFragment dash;
        HttpURLConnection http;



        public GetInfoTask(DashboardFragment f) {
            dash = f;
        }

        @Override
        protected void onPreExecute() {


        }

        @Override
        protected Void doInBackground(Void... arg0) {
            try {
                if (dash.getContext() == null)
                    this.cancel(true);
                //Get Boiler Data
                URL url = new URL("http://smartonviatoile.com/api/Data/currentChaudiere/" + SessionManager.getIdCapteur(dash.getContext()) + "/1");
                http = (HttpURLConnection) url.openConnection();
                http.setRequestProperty("Accept", "application/json");
                http.setRequestProperty("Authorization", "Bearer " + SessionManager.getAuthTokenWithContext(dash.getContext()));
                if (http.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    server_error = true;
                    return null;
                }
                if (!first_time) {
                    first_time = true;
                }
                BufferedReader reader = new BufferedReader(new InputStreamReader(http.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                JSONObject obj = new JSONObject(sb.toString());
                JSONArray data = obj.getJSONArray("data");
                infosJson = (JSONObject) data.get(0);
                http.disconnect();

                //Get Silos Data
                url = new URL("http://admin.smartonviatoile.com/api/Silo/Site/" + SessionManager.getIdOrg(dash.getContext()));
                http = (HttpURLConnection) url.openConnection();
                http.setRequestProperty("Accept", "application/json");
                http.setRequestProperty("Authorization", "Bearer " + SessionManager.getAuthToken());
                if (http.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    server_error = true;
                    return null;
                }
                if (!first_time) {
                    first_time = true;
                }
                reader = new BufferedReader(new InputStreamReader(http.getInputStream()));
                sb = new StringBuilder();
                line = "";
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                obj = new JSONObject(sb.toString());
                infosSilo = obj.getJSONArray("data");
                http.disconnect();


                //Get Ballons Data

                url = new URL("http://admin.smartonviatoile.com/api/Ballon/Site/" + SessionManager.getIdOrg(dash.getContext()));
                http = (HttpURLConnection) url.openConnection();
                http.setRequestProperty("Accept", "application/json");
                http.setRequestProperty("Authorization", "Bearer " + SessionManager.getAuthToken());
                if (http.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    Log.e("ErrorServer", http.getResponseCode() + " " + http.getResponseMessage());
                    server_error = true;
                    return null;
                }
                if (!first_time) {
                    first_time = true;
                }
                server_error = false;
                reader = new BufferedReader(new InputStreamReader(http.getInputStream()));
                sb = new StringBuilder();
                line = "";
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }

                //Log.e("Ballons",SessionManager.);
                obj = new JSONObject(sb.toString());
                infosBallons = obj.getJSONArray("data");
                http.disconnect();


            } catch (IOException | JSONException e) {
                Log.i("Exception :", "NoConnection to get chaudiere values");
                dash.reportError(dash.getResources().getString(R.string.reconnectMsg));
            }
            return null;
        }

        @SuppressLint("SetTextI18n")
        @Override
        protected void onPostExecute(Void param) {
            if (!server_error) {
                try {
                    //Apply Boiler Data
                    dash.errorSolved();
                    dash.getS1().speedTo((float) infosJson.getDouble("temperatura_Ida"));
                    dash.getS2().speedTo((float) infosJson.getDouble("temperatura_Retorno"));
                    dash.getS3().speedTo((float) infosJson.getDouble("temperatura_Inercia"));
                    dash.getS4().speedTo((float) infosJson.getDouble("temperatura_Humos"));
                    dash.getS5().speedTo((float) infosJson.getDouble("dépression"));
                    dash.getS6().speedTo((float) infosJson.getDouble("luminosidad"));

                    //Apply Ballons Data

                    if (firstUse) {
                        if (infosBallons.length() == 0) {
                            View v = LayoutInflater.from(dash.getContext()).inflate(R.layout.ballon_layout, null);
                            v.findViewById(R.id.noBalloons).setVisibility(View.VISIBLE);
                            v.findViewById(R.id.mainLayout).setVisibility(View.GONE);
                            dash.getContainerLayout().addView(v);
                        } else {
                            ballonsViews = new ArrayList<>();
                            for (int i = 0; i < infosBallons.length(); i++) {
                                JSONObject ballon = infosBallons.getJSONObject(i);
                                View v = LayoutInflater.from(dash.getContext()).inflate(R.layout.ballon_layout, null);
                                ballonsViews.add(v);
                                SpeedView tempView = v.findViewById(R.id.ballon_temp);
                                SpeedView pressureView = v.findViewById(R.id.ballon_pressure);
                                TextView idBallon = v.findViewById(R.id.idBallon);

                                tempView.speedTo((float) ballon.getDouble("temperature"));
                                pressureView.speedTo((float) ballon.getDouble("pression"));
                                idBallon.append(ballon.getString("id"));

                                dash.getContainerLayout().addView(v);

                            }
                        }

                        //Apply Silos Data
                        if (infosSilo.length() == 0) {
                            View v = LayoutInflater.from(dash.getContext()).inflate(R.layout.silos_container, null);
                            v.findViewById(R.id.noSilo).setVisibility(View.VISIBLE);
                            v.findViewById(R.id.silosContainer).setVisibility(View.GONE);
                            dash.getContainerLayout().addView(v);
                        }else {

                            silosViews = new ArrayList<>();

                            for (int i = 0; i < infosSilo.length(); i++) {
                                JSONObject silo = infosSilo.getJSONObject(i);
                                LinearLayout siloContainer = null;
                                LinearLayout siloGeneralContainer = null;
                                LinearLayout v = (LinearLayout) LayoutInflater.from(dash.getContext()).inflate(R.layout.silo_layout, null);
                                silosViews.add(v);
                                TextView nomSilo = v.findViewById(R.id.nomSilo);
                                PercentageChartView percentSilo = v.findViewById(R.id.siloPercent);

                                nomSilo.setText("Silo "+(i+1));
                                if (silo.getString("niveau")==null)
                                    percentSilo.setProgress(0,true);
                                else
                                    percentSilo.setProgress((float) silo.getDouble("niveau"),true);

                                dash.getContainerLayout().addView(v);
                                /*if (i%2==0){
                                    siloGeneralContainer = (LinearLayout) LayoutInflater.from(dash.getContext()).inflate(R.layout.silos_container, null);
                                    siloContainer = siloGeneralContainer.findViewById(R.id.silosContainer);

                                    siloContainer.addView(v);
                                    dash.getContainerLayout().addView(siloGeneralContainer);
                                }
                                else {
                                    siloContainer.addView(v);
                                }*/
                            }
                        }
                        firstUse = false;
                    }else {
                        if (infosBallons.length() != 0){
                            for (int i = 0; i < ballonsViews.size(); i++) {
                                JSONObject ballon = infosBallons.getJSONObject(i);
                                View v = ballonsViews.get(i);
                                SpeedView tempView = v.findViewById(R.id.ballon_temp);
                                SpeedView pressureView = v.findViewById(R.id.ballon_pressure);

                                tempView.speedTo((float) ballon.getDouble("temperature"));
                                pressureView.speedTo((float) ballon.getDouble("pression"));
                            }
                        }


                        if (infosSilo.length() != 0){
                            for (int i = 0; i < silosViews.size(); i++) {
                                JSONObject silo = infosSilo.getJSONObject(i);
                                View v = silosViews.get(i);
                                TextView nomSilo = v.findViewById(R.id.nomSilo);
                                PercentageChartView percentSilo = v.findViewById(R.id.siloPercent);

                                nomSilo.setText("Silo "+(i+1));
                                if (silo.getString("niveau")==null)
                                    percentSilo.setProgress(0,true);
                                else
                                    percentSilo.setProgress((float) silo.getDouble("niveau"),true);
                            }
                        }
                    }

                } catch (Exception ex) {
                    dash.reportError(dash.getResources().getString(R.string.errorOccured));
                    ex.printStackTrace();
                }
            } else if (first_time) {
                first_time = false;
                try {
                    dash.reportError(http.getResponseCode() + " " + http.getResponseMessage());
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            }
            Comun.nbTasksOnline++;
            if (Comun.nbTasksOnline == Comun.totalTasks && !Comun.isAllTasksFinished){
                MainActivity.loadingBottomDialog.dismiss();
                InterventionFragment.checkInCompletedIntervention((MainActivity) dash.getActivity());
                Comun.isAllTasksFinished = true;
            }
        }
    }

}