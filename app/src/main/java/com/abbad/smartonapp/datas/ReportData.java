package com.abbad.smartonapp.datas;

import android.app.Notification;
import android.app.Service;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.abbad.smartonapp.R;
import com.abbad.smartonapp.activities.MainActivity;
import com.abbad.smartonapp.classes.Intervention;
import com.abbad.smartonapp.services.UploadReportService;
import com.abbad.smartonapp.utils.InterventionManager;
import com.abbad.smartonapp.utils.SessionManager;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.abbad.smartonapp.services.UploadReportService.CHANNEL_ID;

public class ReportData {


    public static JSONObject setupReport(Intervention intervention,UploadReportService service, boolean etatIntervention) throws JSONException, IOException {
        JSONObject globalObject = new JSONObject();

        if (intervention == null) {
            return null;
        }

        //
        globalObject.put("id_intervention", intervention.getId());
        globalObject.put("id_site", intervention.getIdSite());
        globalObject.put("date_rapport", todayDate());
        globalObject.put("date_intervetion", intervention.getFullDateFormat());
        globalObject.put("id_responsable", intervention.getIdResponsable());
        globalObject.put("id_responsable_executif", intervention.getIdResponsableExecutif());
        globalObject.put("id_contremaitre_exploitation", intervention.getIdContremaitreExploitation());

        globalObject.put("nom_intervention", intervention.getTitle());
        globalObject.put("nom_site", intervention.getNomSite());
        globalObject.put("nom_responsable", intervention.getNomResponsable());
        globalObject.put("nom_responsable_executif", intervention.getNomResponsableExecutif());
        globalObject.put("nom_contremaitre_exploitation", intervention.getNomContremaitreExploitation());

        //
        JSONObject generalComment = new JSONObject();
        if (InterventionData.getComment(intervention.getId(), service) != null)
            generalComment.put("text", ReportData.readTextFile(Uri.fromFile(InterventionData.getComment(intervention.getId(), service)), service));

        if (InterventionData.getAudio(intervention.getId(), service) != null)
            generalComment.put("audio", InterventionData.getAudio(intervention.getId(), service).getName());

        if (InterventionData.getImage(intervention.getId(), service) != null)
            generalComment.put("image", InterventionData.getImage(intervention.getId(), service).getName());

        if (InterventionData.getVideo(intervention.getId(), service) != null)
            generalComment.put("video", InterventionData.getVideo(intervention.getId(), service).getName());
        generalComment.put("etat", etatIntervention);
        generalComment.put("id_commentaire", 0);
        generalComment.put("id_tache", 0);

        globalObject.put("commantaire_generale", generalComment);


        JSONArray tasks = new JSONArray();
        for (int i = 0; i < intervention.getListTaches().size(); i++) {
            JSONObject taskComment = new JSONObject();
            if (TaskData.getComment(intervention.getId(), i, service) != null)
                taskComment.put("text", ReportData.readTextFile(Uri.fromFile(TaskData.getComment(intervention.getId(), i, service)), service));

            if (TaskData.getAudio(intervention.getId(), i, service) != null)
                taskComment.put("audio", TaskData.getAudio(intervention.getId(), i, service).getName());

            if (TaskData.getImage(intervention.getId(), i, service) != null)
                taskComment.put("image", TaskData.getImage(intervention.getId(), i, service).getName());

            if (TaskData.getVideo(intervention.getId(), i, service) != null)
                taskComment.put("video", TaskData.getVideo(intervention.getId(), i, service).getName());
            taskComment.put("id_tache", String.valueOf(i + 1));
            taskComment.put("id_commentaire", String.valueOf(i + 1));
            taskComment.put("etat", InterventionManager.getTaskStatus(intervention.getId(), i));
            tasks.put(taskComment);
        }

        globalObject.put("commentaire_tache", tasks);

        return globalObject;
    }

    public static JSONObject setupReport1(Service service, boolean etatIntervention) throws JSONException {
        JSONObject globalObject = new JSONObject();
        /*Intervention intervention = InterventionData.getInterventionById(InterventionManager.getCurrentIntervention());

        if (intervention == null){
            return null;
        }*/

        //
        globalObject.put("id_intervention", "6126849f233211df4ed9d5f5");
        globalObject.put("id_site", "5fbccf26e06d8cb8a4ac500e");
        globalObject.put("date_rapport", "2021-08-26T20:45:21Z");
        globalObject.put("date_intervetion", "2021-08-30T09:20:33.515-07:00");
        globalObject.put("id_responsable", "60ad60e1ea6f5a4456d58183");
        globalObject.put("id_responsable_executif", "60ad60e1ea6f5a4456d58183");
        globalObject.put("id_contremaitre_exploitation", "60ad60e1ea6f5a4456d58183");

        //
        JSONObject generalComment = new JSONObject();
        generalComment.put("text", "commentTestGeneral");
        generalComment.put("audio", "This_is_the_binary_audio");
        generalComment.put("image", "This_is_the_binary_audio");
        generalComment.put("video", "This_is_the_binary_audio");

        globalObject.put("commantaire_generale", generalComment);

        //
        JSONArray tasks = new JSONArray();

        JSONObject taskComment = new JSONObject();


        taskComment.put("text", "commentTestTask1");
        taskComment.put("text", "commentTestTask1");
        taskComment.put("audio", "This_is_the_binary_audio_of_task_" + 1);
        taskComment.put("image", "This_is_the_binary_audio_of_task_" + 1);
        taskComment.put("video", "This_is_the_binary_audio_of_task_" + 1);
        taskComment.put("id_tache", "1");
        taskComment.put("etat", true);
        tasks.put(taskComment);

        taskComment = new JSONObject();
        taskComment.put("text", "commentTestTask1");
        taskComment.put("audio", "This_is_the_binary_audio_of_task_" + 2);
        taskComment.put("image", "This_is_the_binary_audio_of_task_" + 2);
        taskComment.put("video", "This_is_the_binary_audio_of_task_" + 2);
        taskComment.put("id_tache", "2");
        taskComment.put("etat", true);
        tasks.put(taskComment);

        globalObject.put("commentaire_tache", tasks);

        return globalObject;
    }

    public static String todayDate() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(cal.getTime());
    }

    public static String readTextFile(Uri uri, Service activity) {
        BufferedReader reader = null;
        StringBuilder builder = new StringBuilder();
        try {
            reader = new BufferedReader(new InputStreamReader(activity.getContentResolver().openInputStream(uri)));
            String line = "";

            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return builder.toString();
    }

    public static class SendReportFiles extends AsyncTask<Void, Void, Void> {

        UploadReportService service;
        Intervention intervention;
        boolean etatIntervention;
        int total;


        public SendReportFiles(UploadReportService service, Intervention intervention, boolean etatIntervention) {
            this.service = service;
            this.intervention = intervention;
            this.etatIntervention = etatIntervention;
            Log.e("ServiceLog", "Task Created" + this.intervention.getId());
            total = totalMedia();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                //Upload Media

                sendVideos();
                sendImages();
                sendAudios();

                Log.e("ServiceLog", setupReport(intervention,service, etatIntervention).toString());

                //Upload ReportInfos

                URL url = new URL("http://admin.smartonviatoile.com/api/Rapport");
                HttpURLConnection http = (HttpURLConnection) url.openConnection();
                http.setRequestMethod("POST");
                http.setDoOutput(true);
                http.setConnectTimeout(10000000);
                http.setRequestProperty("Accept", "application/json");
                http.setRequestProperty("Authorization", "Bearer " + SessionManager.getAuthToken());
                http.setRequestProperty("Content-Type", "application/json");


                String data = setupReport(intervention,service, etatIntervention).toString();
                Log.e("ServiceLog", data);
                byte[] out = data.getBytes(StandardCharsets.UTF_8);

                OutputStream stream = http.getOutputStream();
                stream.write(out);
                Log.e("ServiceLog", http.getResponseCode() + " " + http.getResponseMessage() + " : Data Uploaded");
                http.disconnect();


            } catch (Exception ex) {
                //ex.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Log.e("ServiceLog", "Task Completed "+totalMedia());

            if (totalMedia() == 0)
                service.doneUpload();
        }

        private void sendVideos() {
            if (InterventionData.getVideo(intervention.getId(), service.getApplicationContext()) != null) {
                OkHttpClient.Builder builder = new OkHttpClient.Builder();
                builder.connectTimeout(4, TimeUnit.MINUTES);
                builder.readTimeout(4, TimeUnit.MINUTES);
                builder.writeTimeout(4, TimeUnit.MINUTES);
                OkHttpClient client = builder.build();
                RequestBody formBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("file", InterventionData.getVideo(intervention.getId(), service.getApplicationContext()).getName(),
                                RequestBody.create(MediaType.parse("text/plain"), InterventionData.getVideo(intervention.getId(), service)))
                        .build();

                Request request = new Request.Builder()
                        .url("http://admin.smartonviatoile.com/api/Rapport/uploadvideo")
                        .addHeader("Authorization", "Bearer " + SessionManager.getAuthToken())
                        .post(formBody).build();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        Log.e("ServiceLog", response.code() + " - " + response.message() + " - " + response.body());
                        service.counter++;
                        checkFinish();
                    }
                });
            }
            for (int i = 0; i < intervention.getListTaches().size(); i++) {
                if (TaskData.getVideo(intervention.getId(), i, service.getApplicationContext()) != null) {
                    OkHttpClient.Builder builderTask = new OkHttpClient.Builder();
                    builderTask.connectTimeout(4, TimeUnit.MINUTES);
                    builderTask.readTimeout(4, TimeUnit.MINUTES);
                    builderTask.writeTimeout(4, TimeUnit.MINUTES);
                    OkHttpClient clientTask = builderTask.build();
                    RequestBody formBodyTask = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("file", TaskData.getVideo(intervention.getId(), i, service.getApplicationContext()).getName(),
                                    RequestBody.create(MediaType.parse("text/plain"), TaskData.getVideo(intervention.getId(), i, service)))
                            .build();

                    Request requestTask = new Request.Builder()
                            .url("http://admin.smartonviatoile.com/api/Rapport/uploadvideo")
                            .addHeader("Authorization", "Bearer " + SessionManager.getAuthToken())
                            .post(formBodyTask).build();
                    int finalI = i;
                    clientTask.newCall(requestTask).enqueue(new Callback() {
                        @Override
                        public void onFailure(@NotNull Call call, @NotNull IOException e) {
                            e.printStackTrace();
                        }

                        @Override
                        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                            Log.e("ServiceLog", "Task Video" + finalI + " : " + response.code() + " - " + response.message() + " - " + response.body());
                            service.counter++;
                            checkFinish();
                        }
                    });
                }
            }
        }

        private void sendImages() {
            if (InterventionData.getImage(intervention.getId(), service.getApplicationContext()) != null) {
                OkHttpClient.Builder builder = new OkHttpClient.Builder();
                builder.connectTimeout(4, TimeUnit.MINUTES);
                builder.readTimeout(4, TimeUnit.MINUTES);
                builder.writeTimeout(4, TimeUnit.MINUTES);
                OkHttpClient client = builder.build();
                RequestBody formBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("file", InterventionData.getImage(intervention.getId(), service.getApplicationContext()).getName(),
                                RequestBody.create(MediaType.parse("text/plain"), InterventionData.getImage(intervention.getId(), service)))
                        .build();

                Request request = new Request.Builder()
                        .url("http://admin.smartonviatoile.com/api/Rapport/uploadimage")
                        .addHeader("Authorization", "Bearer " + SessionManager.getAuthToken())
                        .post(formBody).build();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        Log.e("ServiceLog", response.code() + " - " + response.message() + " - " + response.body());
                        service.counter++;
                        checkFinish();
                    }
                });
            }

            for (int i = 0; i < intervention.getListTaches().size(); i++) {
                if (TaskData.getImage(intervention.getId(), i, service.getApplicationContext()) != null) {
                    OkHttpClient.Builder builderTask = new OkHttpClient.Builder();
                    builderTask.connectTimeout(4, TimeUnit.MINUTES);
                    builderTask.readTimeout(4, TimeUnit.MINUTES);
                    builderTask.writeTimeout(4, TimeUnit.MINUTES);
                    OkHttpClient clientTask = builderTask.build();
                    RequestBody formBodyTask = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("file", TaskData.getImage(intervention.getId(), i, service.getApplicationContext()).getName(),
                                    RequestBody.create(MediaType.parse("text/plain"), TaskData.getImage(intervention.getId(), i, service)))
                            .build();

                    Request requestTask = new Request.Builder()
                            .url("http://admin.smartonviatoile.com/api/Rapport/uploadimage")
                            .addHeader("Authorization", "Bearer " + SessionManager.getAuthToken())
                            .post(formBodyTask).build();
                    int finalI = i;
                    clientTask.newCall(requestTask).enqueue(new Callback() {
                        @Override
                        public void onFailure(@NotNull Call call, @NotNull IOException e) {
                            e.printStackTrace();
                        }

                        @Override
                        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                            Log.e("ServiceLog", "Task Image" + finalI + " : " + response.code() + " - " + response.message() + " - " + response.body());
                            service.counter++;
                            checkFinish();
                        }
                    });
                }
            }
        }

        private void sendAudios() {
            if (InterventionData.getAudio(intervention.getId(), service.getApplicationContext()) != null) {
                OkHttpClient.Builder builder = new OkHttpClient.Builder();
                builder.connectTimeout(100, TimeUnit.SECONDS);
                builder.readTimeout(100, TimeUnit.SECONDS);
                builder.writeTimeout(100, TimeUnit.SECONDS);
                OkHttpClient client = builder.build();
                RequestBody formBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("file", InterventionData.getAudio(intervention.getId(), service.getApplicationContext()).getName(),
                                RequestBody.create(MediaType.parse("text/plain"), InterventionData.getAudio(intervention.getId(), service)))
                        .build();

                Request request = new Request.Builder()
                        .url("http://admin.smartonviatoile.com/api/Rapport/uploadaudio")
                        .addHeader("Authorization", "Bearer " + SessionManager.getAuthToken())
                        .post(formBody).build();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        Log.e("ServiceLog", response.code() + " - " + response.message() + " - " + response.body());
                        service.counter++;
                        checkFinish();
                    }
                });
            }
            for (int i = 0; i < intervention.getListTaches().size(); i++) {
                if (TaskData.getAudio(intervention.getId(), i, service.getApplicationContext()) != null) {

                    OkHttpClient.Builder builderTask = new OkHttpClient.Builder();
                    builderTask.connectTimeout(100, TimeUnit.SECONDS);
                    builderTask.readTimeout(100, TimeUnit.SECONDS);
                    builderTask.writeTimeout(100, TimeUnit.SECONDS);
                    OkHttpClient clientTask = builderTask.build();
                    RequestBody formBodyTask = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("file", TaskData.getAudio(intervention.getId(), i, service.getApplicationContext()).getName(),
                                    RequestBody.create(MediaType.parse("text/plain"), TaskData.getAudio(intervention.getId(), i, service)))
                            .build();

                    Request requestTask = new Request.Builder()
                            .url("http://admin.smartonviatoile.com/api/Rapport/uploadaudio")
                            .addHeader("Authorization", "Bearer " + SessionManager.getAuthToken())
                            .post(formBodyTask).build();
                    int finalI = i;
                    clientTask.newCall(requestTask).enqueue(new Callback() {
                        @Override
                        public void onFailure(@NotNull Call call, @NotNull IOException e) {
                            e.printStackTrace();
                        }

                        @Override
                        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                            Log.e("ServiceLog", "Task Audio" + finalI + " : " + response.code() + " - " + response.message() + " - " + response.body());
                            service.counter++;
                            checkFinish();
                        }
                    });
                }
            }
        }

        public int totalMedia() {
            int total = 0;
            if (InterventionData.getAudio(intervention.getId(), service.getApplicationContext()) != null)
                total++;

            if (InterventionData.getVideo(intervention.getId(), service.getApplicationContext()) != null)
                total++;

            if (InterventionData.getImage(intervention.getId(), service.getApplicationContext()) != null)
                total++;

            for (int i = 0; i < intervention.getListTaches().size(); i++) {
                if (TaskData.getAudio(intervention.getId(), i, service.getApplicationContext()) != null)
                    total++;
                if (TaskData.getImage(intervention.getId(), i, service.getApplicationContext()) != null)
                    total++;
                if (TaskData.getVideo(intervention.getId(), i, service.getApplicationContext()) != null)
                    total++;
            }

            return total;
        }

        public void checkFinish(){
            if (service.counter == total){
                service.doneUpload();
            }
        }

    }

}
