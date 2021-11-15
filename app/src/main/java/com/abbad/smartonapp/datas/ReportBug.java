package com.abbad.smartonapp.datas;

import android.os.AsyncTask;
import android.util.Log;

import com.abbad.smartonapp.utils.SessionManager;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class ReportBug extends AsyncTask<Void,Void,Void> {

    private String exceptionMsg,exceptionLocation;

    public ReportBug(String msg,String location){
        exceptionMsg = msg;
        exceptionLocation = location;
    }

    @Override
    protected void onPreExecute() {
        //TODO Display a notification of uploading the exception
    }

    @Override
    protected Void doInBackground(Void... voids) {
        //TODO Replace the link by a correct one :/
        try{
            URL url = new URL("http://admin.smartonviatoile.com/api/");
            HttpURLConnection cnn = (HttpURLConnection) url.openConnection();
            cnn.setDoOutput(true);
            cnn.setConnectTimeout(10000000);
            cnn.setRequestProperty("Accept", "application/json");
            cnn.setRequestProperty("Authorization", "Bearer " + SessionManager.getAuthToken());
            cnn.setRequestProperty("Content-Type", "application/json");

            //TODO Setup the request body
            String error = "Error Here";

            byte[] out = error.getBytes(StandardCharsets.UTF_8);

            OutputStream stream = cnn.getOutputStream();
            stream.write(out);
            Log.e("ServiceLog", cnn.getResponseCode() + " " + cnn.getResponseMessage() + " : Exception Uploaded");
            cnn.disconnect();

        }catch (Exception ex){
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        //TODO Display the result and return to the home page

    }
}
