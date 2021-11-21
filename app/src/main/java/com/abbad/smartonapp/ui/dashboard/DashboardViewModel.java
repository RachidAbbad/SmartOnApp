package com.abbad.smartonapp.ui.dashboard;

import android.annotation.SuppressLint;
import android.os.AsyncTask;

import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.ViewModel;

import com.abbad.smartonapp.R;
import com.abbad.smartonapp.activities.LoginActivity;
import com.abbad.smartonapp.activities.MainActivity;
import com.abbad.smartonapp.classes.Report;
import com.abbad.smartonapp.classes.Task;
import com.abbad.smartonapp.dialogs.LoadingBottomDialog;
import com.abbad.smartonapp.dialogs.ResultBottomDialog;
import com.abbad.smartonapp.ui.interventions.InterventionFragment;
import com.abbad.smartonapp.utils.Comun;
import com.abbad.smartonapp.utils.SessionManager;
import com.github.anastr.speedviewlib.SpeedView;
import com.ramijemli.percentagechartview.PercentageChartView;

import org.jetbrains.annotations.NotNull;
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
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class DashboardViewModel extends ViewModel {

    public boolean server_error = false;
    public List<View> ballonsViews = new ArrayList<>(), silosViews = new ArrayList<>();
    JSONObject infosJson;
    JSONArray infosBallons, infosSilo;
    LoadingBottomDialog loadingReports = new LoadingBottomDialog("Chargement de dashBoard ...");
    private Timer timer = new Timer();
    public DashboardViewModel() {

    }

    public void refreshData(DashboardFragment dash) throws IOException {
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                getDashboardInfo(dash);
            }
        }, 0, 5000);

    }

    private void getDashboardInfo(DashboardFragment dash) {
        Log.e("Token", SessionManager.getAuthToken());
        if (Comun.firstLoadDashboard) {
            if (!loadingReports.isAdded()) {
                loadingReports.show(dash.getActivity().getSupportFragmentManager(), "");
            }
        }

        OkHttpClient.Builder builderClient = new OkHttpClient.Builder();
        builderClient.connectTimeout(300, TimeUnit.MINUTES);
        builderClient.readTimeout(300, TimeUnit.MINUTES);
        builderClient.writeTimeout(300, TimeUnit.MINUTES);

        getBoilerData(builderClient, dash);
    }

    private void getBoilerData(OkHttpClient.Builder builder, DashboardFragment dash) {
        OkHttpClient.Builder builderClient = new OkHttpClient.Builder();
        builderClient.connectTimeout(300, TimeUnit.MINUTES);
        builderClient.readTimeout(300, TimeUnit.MINUTES);
        builderClient.writeTimeout(300, TimeUnit.MINUTES);
        OkHttpClient client = builder.build();
        Request request = new Request.Builder()
                .url("http://smartonviatoile.com/api/Data/currentChaudiere/" + SessionManager.getIdCapteur(dash.getActivity().getApplicationContext()) + "/1")
                .addHeader("Authorization", "Bearer " + SessionManager.getAuthToken())
                .get().build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                new ResultBottomDialog(e.getMessage(), 3).show(dash.getActivity().getSupportFragmentManager(), null);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try {
                    infosJson = new JSONObject(response.body().string()).getJSONArray("data").getJSONObject(0);
                    dash.getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                dash.getS1().speedTo((float) infosJson.getDouble("temperatura_Ida"));
                                dash.getS2().speedTo((float) infosJson.getDouble("temperatura_Retorno"));
                                dash.getS3().speedTo((float) infosJson.getDouble("temperatura_Inercia"));
                                dash.getS4().speedTo((float) infosJson.getDouble("temperatura_Humos"));
                                dash.getS5().speedTo((float) infosJson.getDouble("dépression"));
                                dash.getS6().speedTo((float) infosJson.getDouble("luminosidad"));
                            } catch (Exception ex) {
                                new ResultBottomDialog(ex.getMessage(), 3).show(dash.getActivity().getSupportFragmentManager(), null);
                            }
                        }
                    });

                    getBallonsData(builderClient, dash);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    new ResultBottomDialog(ex.getMessage(), 3).show(dash.getActivity().getSupportFragmentManager(), null);
                }
            }
        });
    }

    private void getBallonsData(OkHttpClient.Builder builder, DashboardFragment dash) {

        OkHttpClient client = builder.build();
        Request request = new Request.Builder()
                .url("http://admin.smartonviatoile.com/api/Ballon/Site/" + SessionManager.getIdOrg(dash.getContext()))
                .addHeader("Authorization", "Bearer " + SessionManager.getAuthToken())
                .get().build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                new ResultBottomDialog(e.getMessage(), 3).show(dash.getActivity().getSupportFragmentManager(), null);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        infosBallons = new JSONObject(response.body().string()).getJSONArray("data");
                        if (Comun.firstLoadDashboard) {
                            if (infosBallons.length() == 0) {
                                View v = LayoutInflater.from(dash.getContext()).inflate(R.layout.ballon_layout, null);
                                dash.getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        v.findViewById(R.id.noBalloons).setVisibility(View.VISIBLE);
                                        v.findViewById(R.id.mainLayout).setVisibility(View.GONE);
                                        dash.getContainerLayout().addView(v);
                                    }
                                });
                            } else {
                                dash.getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            for (int i = 0; i < infosBallons.length(); i++) {
                                                JSONObject ballon = infosBallons.getJSONObject(i);
                                                View v = LayoutInflater.from(dash.getContext()).inflate(R.layout.ballon_layout, null);
                                                ballonsViews.add(v);
                                                SpeedView tempView = v.findViewById(R.id.ballon_temp);
                                                SpeedView pressureView = v.findViewById(R.id.ballon_pressure);

                                                tempView.speedTo((float) ballon.getDouble("temperature"));
                                                pressureView.speedTo((float) ballon.getDouble("pression"));

                                                dash.getContainerLayout().addView(v);
                                            }
                                        } catch (Exception ex) {
                                            ex.printStackTrace();
                                        }

                                    }
                                });

                            }
                        } else {
                            if (ballonsViews.size() != 0) {
                                dash.getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            for (int i = 0; i < ballonsViews.size(); i++) {
                                                JSONObject ballon = infosBallons.getJSONObject(i);
                                                View v = ballonsViews.get(i);
                                                SpeedView tempView = v.findViewById(R.id.ballon_temp);
                                                SpeedView pressureView = v.findViewById(R.id.ballon_pressure);

                                                tempView.speedTo((float) ballon.getDouble("temperature"));
                                                pressureView.speedTo((float) ballon.getDouble("pression"));
                                            }
                                        } catch (Exception ex) {
                                            ex.printStackTrace();
                                        }
                                    }
                                });
                            }
                        }
                        getSilosData(builder, dash);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        new ResultBottomDialog(ex.getMessage(), 3).show(dash.getActivity().getSupportFragmentManager(), null);
                    }
                } else {
                    dash.getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dash.reportError(response.code() + " " + response.message());
                        }
                    });
                }
            }
        });
    }

    private void getSilosData(OkHttpClient.Builder builder, DashboardFragment dash) {
        OkHttpClient client = builder.build();
        Request request = new Request.Builder()
                .url("http://admin.smartonviatoile.com/api/Silo/Site/" + SessionManager.getIdOrg(dash.getContext()))
                .addHeader("Authorization", "Bearer " + SessionManager.getAuthToken())
                .get().build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                new ResultBottomDialog(e.getMessage(), 3).show(dash.getActivity().getSupportFragmentManager(), null);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

                try {
                    infosSilo = new JSONObject(response.body().string()).getJSONArray("data");
                    Log.d("Response silo", infosJson.toString());
                    if (Comun.firstLoadDashboard) {
                        if (infosSilo.length() == 0) {
                            View v = LayoutInflater.from(dash.getContext()).inflate(R.layout.silos_container, null);
                            dash.getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    v.findViewById(R.id.noSilo).setVisibility(View.VISIBLE);
                                    v.findViewById(R.id.silosContainer).setVisibility(View.GONE);
                                    dash.getContainerLayout().addView(v);
                                }
                            });
                        } else {
                            silosViews = new ArrayList<>();
                            try {
                                for (int i = 0; i < infosSilo.length(); i++) {
                                    JSONObject silo = infosSilo.getJSONObject(i);
                                    LinearLayout v = (LinearLayout) LayoutInflater.from(dash.getContext()).inflate(R.layout.silo_layout, null);
                                    silosViews.add(v);

                                    TextView nomSilo = v.findViewById(R.id.nomSilo);
                                    PercentageChartView percentSilo = v.findViewById(R.id.siloPercent);
                                    nomSilo.setText("Silo " + (i + 1));

                                    dash.getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                percentSilo.setProgress((float) silo.getDouble("niveau"), true);
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                            dash.getContainerLayout().addView(v);
                                        }
                                    });
                                }

                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }


                        }
                        Comun.firstLoadDashboard = false;
                        Log.e("firstLoadDashboard", String.valueOf(Comun.firstLoadDashboard));
                    } else {
                        Log.e("Outsade if",infosSilo.length()+"");

                        if (infosSilo.length() != 0) {
                            Log.e("Inside if",infosSilo.length()+"");
                            dash.getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        for (int i = 0; i < silosViews.size(); i++) {
                                            JSONObject silo = infosSilo.getJSONObject(i);
                                            View v = silosViews.get(i);
                                            TextView nomSilo = v.findViewById(R.id.nomSilo);
                                            PercentageChartView percentSilo = v.findViewById(R.id.siloPercent);

                                            nomSilo.setText("Silo " + (i + 1));
                                            percentSilo.setProgress((float) silo.getDouble("niveau"), true);
                                        }
                                    } catch (Exception ex) {
                                        ex.printStackTrace();
                                    }
                                }
                            });

                        }
                    }


                } catch (Exception ex) {
                    ex.printStackTrace();
                    new ResultBottomDialog(ex.getMessage(), 3).show(dash.getActivity().getSupportFragmentManager(), null);
                }

                loadingReports.dismiss();
            }
        });
    }
}