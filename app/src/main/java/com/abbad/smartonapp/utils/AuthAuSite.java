package com.abbad.smartonapp.utils;

import android.os.AsyncTask;
import android.util.Log;

import com.abbad.smartonapp.R;
import com.abbad.smartonapp.activities.MainActivity;
import com.abbad.smartonapp.dialogs.AuthSiteDialog;
import com.abbad.smartonapp.dialogs.LoadingBottomDialog;
import com.abbad.smartonapp.dialogs.ResultBottomDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class AuthAuSite extends AsyncTask<Void, Void, Void> {

    MainActivity activity;
    LoadingBottomDialog loadingBottomDialog;
    boolean isValid;
    String idUser,tag;
    AuthSiteDialog authSiteDialog;
    int method;

    public AuthAuSite(MainActivity mainActivity, String idUser, String tag, AuthSiteDialog authSiteDialog,int method) {
        activity = mainActivity;
        this.idUser = idUser;
        this.tag = tag;
        this.authSiteDialog = authSiteDialog;
        loadingBottomDialog = new LoadingBottomDialog("Loading ...");
        loadingBottomDialog.setCancelable(false);
    }

    @Override
    protected void onPreExecute() {
        showLoading();
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            URL url = new URL("http://admin.smartonviatoile.com/api/User/TestNFC/"+idUser+"/"+tag);
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http = (HttpURLConnection) url.openConnection();
            http.setRequestProperty("Accept", "application/json");
            http.setRequestProperty("Authorization", "Bearer " + SessionManager.getAuthToken());

            BufferedReader reader = new BufferedReader(new InputStreamReader(http.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            JSONObject obj = new JSONObject(sb.toString());
            http.disconnect();
            isValid = obj.getBoolean("data");

        } catch (IOException | JSONException e) {
            showError();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        if (isValid)
            showSuccess();
        else
            showFailed();
    }

    private void showLoading() {
        loadingBottomDialog.show(activity.getSupportFragmentManager(), null);
    }

    private void showSuccess() {
        loadingBottomDialog.dismiss();
        new ResultBottomDialog(activity.getResources().getString(R.string.authSiteSuccess), 1).show(activity.getSupportFragmentManager(), null);
    }

    private void showFailed() {
        loadingBottomDialog.dismiss();
        new ResultBottomDialog(activity.getResources().getString(R.string.authSiteFailed), 2).show(activity.getSupportFragmentManager(), null);
        if (method == AuthSiteDialog.AUTH_NFC)
            authSiteDialog.retryNfcScaning();
    }

    private void showError() {
        loadingBottomDialog.dismiss();
        new ResultBottomDialog(activity.getResources().getString(R.string.errorOccured), 3).show(activity.getSupportFragmentManager(), null);
    }
}
