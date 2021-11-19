package com.abbad.smartonapp.datas;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.abbad.smartonapp.Fragments.TaskPreviewMedias;
import com.abbad.smartonapp.R;
import com.abbad.smartonapp.activities.ListReports;
import com.abbad.smartonapp.activities.MainActivity;
import com.abbad.smartonapp.activities.PreviewReport;
import com.abbad.smartonapp.classes.Intervention;
import com.abbad.smartonapp.classes.Report;
import com.abbad.smartonapp.classes.Task;
import com.abbad.smartonapp.classes.User;
import com.abbad.smartonapp.dialogs.LoadingBottomDialog;
import com.abbad.smartonapp.dialogs.ResultBottomDialog;
import com.abbad.smartonapp.services.UploadReportService;
import com.abbad.smartonapp.ui.interventions.InterventionFragment;
import com.abbad.smartonapp.utils.Comun;
import com.abbad.smartonapp.utils.InterventionManager;
import com.abbad.smartonapp.utils.SessionManager;
import com.dx.dxloadingbutton.lib.LoadingButton;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.List;
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

import static android.content.Context.TELECOM_SERVICE;
import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_AUDIO;
import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;
import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;
import static com.abbad.smartonapp.services.UploadReportService.CHANNEL_ID;

public class ReportData {

    public static Report currentReport;

    public static JSONObject setupReport(Intervention intervention, UploadReportService service) throws JSONException, IOException {
        JSONObject globalObject = new JSONObject();

        if (intervention == null) {
            return null;
        }

        //
        globalObject.put("id_intervention", intervention.getId());
        globalObject.put("id_site", intervention.getIdSite());
        globalObject.put("date_rapport", todayDate());
        globalObject.put("date_intervention", intervention.getFullDateFormat());
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
        JSONArray comments = new JSONArray();
        if (InterventionData.getComments(intervention.getId(), service.getApplicationContext()) != null || InterventionData.getComments(intervention.getId(), service.getApplicationContext()).size() != 0) {
            for (int i = 0; i < InterventionData.getComments(intervention.getId(), service.getApplicationContext()).size(); i++) {
                comments.put(ReportData.readTextFile(Uri.fromFile(InterventionData.getComments(intervention.getId(), service.getApplicationContext()).get(i)), service.getApplicationContext()));
            }
        }
        generalComment.put("text", comments);

        JSONArray audios = new JSONArray();
        if (InterventionData.getAudios(intervention.getId(), service.getApplicationContext()) != null || InterventionData.getAudios(intervention.getId(), service.getApplicationContext()).size() != 0) {
            for (int i = 0; i < InterventionData.getAudios(intervention.getId(), service.getApplicationContext()).size(); i++) {
                audios.put(InterventionData.getAudios(intervention.getId(), service.getApplicationContext()).get(i).getName());
            }
        }
        generalComment.put("audio", audios);

        JSONArray images = new JSONArray();
        if (InterventionData.getImages(intervention.getId(), service.getApplicationContext()) != null || InterventionData.getImages(intervention.getId(), service.getApplicationContext()).size() != 0) {
            for (int i = 0; i < InterventionData.getImages(intervention.getId(), service.getApplicationContext()).size(); i++) {
                images.put(InterventionData.getImages(intervention.getId(), service.getApplicationContext()).get(i).getName());
            }
        }
        generalComment.put("image", images);

        JSONArray videos = new JSONArray();
        if (InterventionData.getVideos(intervention.getId(), service.getApplicationContext()) != null || InterventionData.getVideos(intervention.getId(), service.getApplicationContext()).size() != 0) {
            for (int i = 0; i < InterventionData.getVideos(intervention.getId(), service.getApplicationContext()).size(); i++) {
                videos.put(InterventionData.getVideos(intervention.getId(), service.getApplicationContext()).get(i).getName());
            }
        }
        generalComment.put("video", videos);

        generalComment.put("id_commentaire", 0);
        generalComment.put("id_tache", 0);

        globalObject.put("commantaire_generale", generalComment);


        JSONArray tasks = new JSONArray();
        for (int i = 0; i < intervention.getListTaches().size(); i++) {
            JSONObject taskComment = new JSONObject();

            JSONArray commentsTask = new JSONArray();
            if (TaskData.getComments(intervention.getId(), i, service.getApplicationContext()) != null || TaskData.getComments(intervention.getId(), i, service.getApplicationContext()).size() != 0) {
                for (int j = 0; j < TaskData.getComments(intervention.getId(), i, service.getApplicationContext()).size(); j++) {
                    commentsTask.put(ReportData.readTextFile(Uri.fromFile(TaskData.getComments(intervention.getId(), i, service.getApplicationContext()).get(j)), service.getApplicationContext()));
                }
            }
            taskComment.put("text", commentsTask);

            JSONArray audiosTask = new JSONArray();
            if (TaskData.getAudios(intervention.getId(), i, service.getApplicationContext()) != null || TaskData.getAudios(intervention.getId(), i, service.getApplicationContext()).size() != 0) {
                for (int j = 0; j < TaskData.getAudios(intervention.getId(), i, service.getApplicationContext()).size(); j++) {
                    audiosTask.put(TaskData.getAudios(intervention.getId(), i, service.getApplicationContext()).get(j).getName());
                }
            }
            taskComment.put("audio", audiosTask);

            JSONArray imagesTask = new JSONArray();
            if (TaskData.getImages(intervention.getId(), i, service.getApplicationContext()) != null || TaskData.getImages(intervention.getId(), i, service.getApplicationContext()).size() != 0) {
                for (int j = 0; j < TaskData.getImages(intervention.getId(), i, service.getApplicationContext()).size(); j++) {
                    imagesTask.put(TaskData.getImages(intervention.getId(), i, service.getApplicationContext()).get(j).getName());
                }
            }
            taskComment.put("image", imagesTask);

            JSONArray videoTasks = new JSONArray();
            if (TaskData.getVideos(intervention.getId(), i, service.getApplicationContext()) != null || TaskData.getVideos(intervention.getId(), i, service.getApplicationContext()).size() != 0) {
                for (int j = 0; j < TaskData.getVideos(intervention.getId(), i, service.getApplicationContext()).size(); j++) {
                    videoTasks.put(TaskData.getVideos(intervention.getId(), i, service.getApplicationContext()).get(j).getName());
                }
            }
            taskComment.put("video", videoTasks);

            taskComment.put("id_tache", String.valueOf(i + 1));
            taskComment.put("id_commentaire", String.valueOf(i + 1));
            taskComment.put("etat", InterventionManager.getTaskStatus(intervention.getId(), i));
            tasks.put(taskComment);
        }

        globalObject.put("commentaire_tache", tasks);

        return globalObject;
    }

    public static String todayDate() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(cal.getTime());
    }

    public static String readTextFile(Uri uri, Context activity) {
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

    private static File getFilefromInputStream(int type, File outputFile) throws Exception {
        String urlExtention = "";
        switch (type) {
            case MEDIA_TYPE_AUDIO:
                urlExtention = urlExtention.concat("audios");
                break;
            case MEDIA_TYPE_IMAGE:
                urlExtention = urlExtention.concat("images");
                break;
            case MEDIA_TYPE_VIDEO:
                urlExtention = urlExtention.concat("videos");
                break;
        }
        urlExtention = urlExtention.concat("/" + outputFile.getName());
        URL url = new URL("http://admin.smartonviatoile.com/" + urlExtention);
        HttpURLConnection c = (HttpURLConnection) url.openConnection();
        c.setReadTimeout(3000000);

        FileOutputStream fos = new FileOutputStream(outputFile);
        InputStream is = c.getInputStream();

        byte[] buffer = new byte[4096];
        int len1 = 0;

        while ((len1 = is.read(buffer)) != -1) {
            fos.write(buffer, 0, len1);
        }

        fos.close();
        is.close();

        return outputFile;
    }

    private static File getOutputTempFile(int type, String FileName, Context context) {
        File mediaStorageDir = null;
        File mediaFile = null;

        if (type == MEDIA_TYPE_VIDEO) {
            mediaStorageDir = new File(context.getExternalCacheDir(), "Temp");
            // Create the storage directory(MyCameraVideo) if it does not exist
            if (!mediaStorageDir.exists()) {
                if (!mediaStorageDir.mkdirs()) {
                    //new ResultBottomDialog(context.getResources().getString(R.string.saveFailedVideoMsg), 3).show(getActivity().getSupportFragmentManager(), null);
                }
            }
            // For unique video file name appending current timeStamp with file name
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    FileName);

        } else if (type == MEDIA_TYPE_IMAGE) {
            mediaStorageDir = new File(context.getExternalCacheDir(), "Temp");
            // Create the storage directory(MyCameraVideo) if it does not exist
            if (!mediaStorageDir.exists()) {
                if (!mediaStorageDir.mkdirs()) {
                    //new ResultBottomDialog(context.getResources().getString(R.string.saveFailedImageMsg), 3).show(getActivity().getSupportFragmentManager(), null);
                }
            }
            // For unique video file name appending current timeStamp with file name
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    FileName);


        } else if (type == MEDIA_TYPE_AUDIO) {
            mediaStorageDir = new File(context.getExternalCacheDir(), "Temp");
            if (!mediaStorageDir.exists()) {
                if (!mediaStorageDir.mkdirs()) {
                    //new ResultBottomDialog(getResources().getString(R.string.saveFailedAudioMsg), 3).show(getActivity().getSupportFragmentManager(), null);
                }
            }
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    FileName);

        } else if (type == 10) {
            mediaStorageDir = new File(context.getExternalCacheDir(), "CommentsTemp");
            // Create the storage directory(MyCameraVideo) if it does not exist
            if (!mediaStorageDir.exists()) {
                if (!mediaStorageDir.mkdirs()) {
                    //new ResultBottomDialog(getResources().getString(R.string.saveFailedCommentMsg), 3).show(getActivity().getSupportFragmentManager(), null);
                }
            }
            // For unique video file name appending current timeStamp with file name
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    FileName);

        }


        return mediaFile;
    }

    public static class SendReportFiles extends AsyncTask<Void, Void, Void> {

        UploadReportService service;
        Intervention intervention;
        int total = 0;


        public SendReportFiles(UploadReportService service, Intervention intervention) {
            this.service = service;
            this.intervention = intervention;
            Log.e("ServiceLog", "Task Created" + this.intervention.getId());
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                //Upload Media

                sendVideos();
                sendImages();
                sendAudios();

                Log.e("ServiceLog", setupReport(intervention, service).toString());

                //Upload ReportInfos

                URL url = new URL("http://admin.smartonviatoile.com/api/Rapport");
                HttpURLConnection http = (HttpURLConnection) url.openConnection();
                http.setRequestMethod("POST");
                http.setDoOutput(true);
                http.setConnectTimeout(10000000);
                http.setRequestProperty("Accept", "application/json");
                http.setRequestProperty("Authorization", "Bearer " + SessionManager.getAuthToken());
                http.setRequestProperty("Content-Type", "application/json");


                String data = setupReport(intervention, service).toString();
                Log.e("ServiceLog", data);
                byte[] out = data.getBytes(StandardCharsets.UTF_8);

                OutputStream stream = http.getOutputStream();
                stream.write(out);
                Log.e("ServiceLog", http.getResponseCode() + " " + http.getResponseMessage() + " : Data Uploaded");
                http.disconnect();


            } catch (Exception ex) {
                ex.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Log.e("ServiceLog", "Task Completed " + total);

            if (total == 0)
                service.doneUpload();
        }

        private void sendVideos() {
            List<File> listVideos = new ArrayList<>();

            if (InterventionData.getVideos(intervention.getId(), service.getApplicationContext()) != null || InterventionData.getVideos(intervention.getId(), service.getApplicationContext()).size() != 0) {
                listVideos.addAll(InterventionData.getVideos(intervention.getId(), service.getApplicationContext()));
            }
            for (int i = 0; i < intervention.getListTaches().size(); i++) {
                if (TaskData.getVideos(intervention.getId(), i, service.getApplicationContext()) != null || TaskData.getVideos(intervention.getId(), i, service.getApplicationContext()).size() != 0) {
                    listVideos.addAll(TaskData.getVideos(intervention.getId(), i, service.getApplicationContext()));
                }
            }

            total += listVideos.size();

            for (File file : listVideos) {
                OkHttpClient.Builder builderClient = new OkHttpClient.Builder();
                builderClient.connectTimeout(300, TimeUnit.MINUTES);
                builderClient.readTimeout(300, TimeUnit.MINUTES);
                builderClient.writeTimeout(300, TimeUnit.MINUTES);
                OkHttpClient client = builderClient.build();
                MultipartBody.Builder builder = new MultipartBody.Builder();
                builder.setType(MultipartBody.FORM);
                builder.addFormDataPart("file", file.getName(), RequestBody.create(MediaType.parse("text/plain"), file));
                MultipartBody formBody = builder.build();
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
                        service.counter++;
                        checkFinish();
                    }
                });
            }
        }

        private void sendImages() {
            List<File> listImages = new ArrayList<>();

            if (InterventionData.getImages(intervention.getId(), service.getApplicationContext()) != null || InterventionData.getImages(intervention.getId(), service.getApplicationContext()).size() != 0) {
                listImages.addAll(InterventionData.getImages(intervention.getId(), service.getApplicationContext()));
            }
            for (int i = 0; i < intervention.getListTaches().size(); i++) {
                if (TaskData.getImages(intervention.getId(), i, service.getApplicationContext()) != null || TaskData.getImages(intervention.getId(), i, service.getApplicationContext()).size() != 0) {
                    listImages.addAll(TaskData.getImages(intervention.getId(), i, service.getApplicationContext()));
                }
            }

            total += listImages.size();

            for (File file : listImages) {
                OkHttpClient.Builder builderClient = new OkHttpClient.Builder();
                builderClient.connectTimeout(20, TimeUnit.MINUTES);
                builderClient.readTimeout(20, TimeUnit.MINUTES);
                builderClient.writeTimeout(20, TimeUnit.MINUTES);
                OkHttpClient client = builderClient.build();
                MultipartBody.Builder builder = new MultipartBody.Builder();
                builder.setType(MultipartBody.FORM);
                builder.addFormDataPart("file", file.getName(), RequestBody.create(MediaType.parse("text/plain"), file));
                MultipartBody formBody = builder.build();
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
                        service.counter++;
                        checkFinish();
                    }
                });
            }

        }

        private void sendAudios() {
            List<File> listAudios = new ArrayList<>();

            if (InterventionData.getAudios(intervention.getId(), service.getApplicationContext()) != null || InterventionData.getAudios(intervention.getId(), service.getApplicationContext()).size() != 0) {
                listAudios.addAll(InterventionData.getAudios(intervention.getId(), service.getApplicationContext()));
            }
            for (int i = 0; i < intervention.getListTaches().size(); i++) {
                if (TaskData.getAudios(intervention.getId(), i, service.getApplicationContext()) != null || TaskData.getAudios(intervention.getId(), i, service.getApplicationContext()).size() != 0) {
                    listAudios.addAll(TaskData.getAudios(intervention.getId(), i, service.getApplicationContext()));
                }
            }

            total += listAudios.size();

            for (File file : listAudios) {
                OkHttpClient.Builder builderClient = new OkHttpClient.Builder();
                builderClient.connectTimeout(20, TimeUnit.MINUTES);
                builderClient.readTimeout(20, TimeUnit.MINUTES);
                builderClient.writeTimeout(20, TimeUnit.MINUTES);
                OkHttpClient client = builderClient.build();
                MultipartBody.Builder builder = new MultipartBody.Builder();
                builder.setType(MultipartBody.FORM);
                builder.addFormDataPart("file", file.getName(), RequestBody.create(MediaType.parse("text/plain"), file));
                MultipartBody formBody = builder.build();
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
                        service.counter++;
                        checkFinish();
                    }
                });
            }

        }

        public void checkFinish() {
            if (service.counter == total) {
                service.doneUpload();
            }
        }

    }

    //Update GetAllReports :
    static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    static SimpleDateFormat output = new SimpleDateFormat("dd-MM-yyyy HH:mm");
    static LoadingBottomDialog loadingReports = new LoadingBottomDialog("Loading Reports ...");
    static List<Report> listReports = new ArrayList<>();
    static JSONArray jsonArray = new JSONArray();
    static JSONObject jsonObject = new JSONObject();

    public static void getAllReports(ListReports activity){
        loadingReports.content = "Loading Reports ...";
        loadingReports.show(activity.getSupportFragmentManager(),"");
        OkHttpClient.Builder builderClient = new OkHttpClient.Builder();
        builderClient.connectTimeout(300, TimeUnit.MINUTES);
        builderClient.readTimeout(300, TimeUnit.MINUTES);
        builderClient.writeTimeout(300, TimeUnit.MINUTES);
        OkHttpClient client = builderClient.build();
        Request request = new Request.Builder()
                .url("http://admin.smartonviatoile.com/api/Rapport/ResponsableExecutif/"+SessionManager.getUserId(activity.getApplicationContext()))
                .addHeader("Authorization", "Bearer " + SessionManager.getAuthToken())
                .get().build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                loadingReports.dismiss();
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        activity.errorServer(e.getMessage());
                    }
                });
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

                try {
                    jsonArray = new JSONObject(response.body().string()).getJSONArray("data");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject object = jsonArray.getJSONObject(i);

                        Report report = new Report();
                        report.setNomIntervention(object.getString("nom_intervention"));
                        report.setNomSite(object.getString("nom_site"));
                        report.setDateIntervention(output.format(sdf.parse(object.getString("date_intervention"))));
                        report.setDateValidation(output.format(sdf.parse(object.getString("date_rapport"))));
                        report.setNomResponsable(object.getString("nom_responsable"));
                        report.setEtat(object.getBoolean("etat_rapport"));
                        report.setIdReport(object.getString("id"));


                        JSONObject commentGeneral = object.getJSONObject("commantaire_generale");

                        for (int j = 0; j < commentGeneral.getJSONArray("audio").length(); j++) {
                            report.getAudiosList().add(commentGeneral.getJSONArray("audio").getString(j));
                        }

                        for (int j = 0; j < commentGeneral.getJSONArray("image").length(); j++) {
                            report.getImagesList().add(commentGeneral.getJSONArray("image").getString(j));
                        }

                        for (int j = 0; j < commentGeneral.getJSONArray("video").length(); j++) {
                            report.getVideosList().add(commentGeneral.getJSONArray("video").getString(j));
                        }

                        for (int j = 0; j < commentGeneral.getJSONArray("text").length(); j++) {
                            report.getCommentsList().add(commentGeneral.getJSONArray("text").getString(j));
                        }

                        JSONArray commentTask = object.getJSONArray("commentaire_tache");

                        for (int j = 0; j < commentTask.length(); j++) {
                            JSONObject objectTask = commentTask.getJSONObject(j);
                            Task task = new Task();

                            for (int k = 0; j < objectTask.getJSONArray("audio").length(); j++) {
                                task.getAudiosString().add(objectTask.getJSONArray("audio").getString(k));
                            }

                            for (int k = 0; j < objectTask.getJSONArray("image").length(); j++) {
                                task.getImagesString().add(objectTask.getJSONArray("image").getString(k));
                            }

                            for (int k = 0; j < objectTask.getJSONArray("video").length(); j++) {
                                task.getVideosString().add(objectTask.getJSONArray("video").getString(k));
                            }

                            for (int k = 0; j < objectTask.getJSONArray("text").length(); j++) {
                                task.getCommentsString().add(objectTask.getJSONArray("text").getString(k));
                            }

                            report.getListTasks().add(task);

                            listReports.add(report);
                        }

                    }
                    try {
                        if (listReports.size() == 0)
                            activity.noIntervention();
                        else {
                            Collections.reverse(listReports);
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    activity.backToService();
                                    activity.displayData(listReports);
                                }
                            });
                        }
                    } catch (Exception ex) {
                        if (activity != null)
                            ex.printStackTrace();
                        //new ResultBottomDialog(activity.getResources().getString(R.string.errorOccured),3).show(activity.getSupportFragmentManager(),null);
                    }
                    loadingReports.dismiss();
                } catch (Exception ex) {
                    if (activity != null)
                        ex.printStackTrace();
                    loadingReports.dismiss();
                    //new ResultBottomDialog(ex.getMessage(), 3).show(activity.getSupportFragmentManager(), null);
                }

            }
        });
    }

    //Get Report By Id Update :
    public static void getReportById(PreviewReport activity,String idReport){
        loadingReports.content = "Chargement de rapport ...";
        loadingReports.show(activity.getSupportFragmentManager(),"");
        OkHttpClient.Builder builderClient = new OkHttpClient.Builder();
        builderClient.connectTimeout(300, TimeUnit.MINUTES);
        builderClient.readTimeout(300, TimeUnit.MINUTES);
        builderClient.writeTimeout(300, TimeUnit.MINUTES);
        OkHttpClient client = builderClient.build();
        Request request = new Request.Builder()
                .url("http://admin.smartonviatoile.com/api/Rapport/" + idReport)
                .addHeader("Authorization", "Bearer " + SessionManager.getAuthToken())
                .get().build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                loadingReports.dismiss();
                new ResultBottomDialog("Echec de chargé les rapports",3).show(activity.getSupportFragmentManager(),"");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

                try {
                    jsonObject = new JSONObject(response.body().string()).getJSONObject("data");
                    Report report = new Report();
                    report.setNomIntervention(jsonObject.getString("nom_intervention"));
                    report.setNomSite(jsonObject.getString("nom_site"));
                    report.setDateIntervention(output.format(sdf.parse(jsonObject.getString("date_intervention"))));
                    report.setDateValidation(output.format(sdf.parse(jsonObject.getString("date_rapport"))));
                    report.setNomResponsable(jsonObject.getString("nom_responsable"));
                    report.setEtat(jsonObject.getBoolean("etat_rapport"));
                    report.setIdReport(jsonObject.getString("id"));

                    JSONObject commentGeneral = jsonObject.getJSONObject("commantaire_generale");

                    for (int j = 0; j < commentGeneral.getJSONArray("audio").length(); j++) {
                        report.getAudiosList().add(commentGeneral.getJSONArray("audio").getString(j));
                    }

                    for (int j = 0; j < commentGeneral.getJSONArray("image").length(); j++) {
                        report.getImagesList().add(commentGeneral.getJSONArray("image").getString(j));
                    }

                    for (int j = 0; j < commentGeneral.getJSONArray("video").length(); j++) {
                        report.getVideosList().add(commentGeneral.getJSONArray("video").getString(j));
                    }

                    for (int j = 0; j < commentGeneral.getJSONArray("text").length(); j++) {
                        report.getCommentsList().add(commentGeneral.getJSONArray("text").getString(j));
                    }

                    JSONArray commentTask = jsonObject.getJSONArray("commentaire_tache");

                    for (int j = 0; j < commentTask.length(); j++) {
                        JSONObject objectTask = commentTask.getJSONObject(j);
                        Task task = new Task();

                        for (int k = 0; k < objectTask.getJSONArray("audio").length(); k++) {
                            task.getAudiosString().add(objectTask.getJSONArray("audio").getString(k));
                        }

                        for (int k = 0; k < objectTask.getJSONArray("image").length(); k++) {
                            task.getImagesString().add(objectTask.getJSONArray("image").getString(k));
                        }

                        for (int k = 0; k < objectTask.getJSONArray("video").length(); k++) {
                            task.getVideosString().add(objectTask.getJSONArray("video").getString(k));
                        }

                        for (int k = 0; k < objectTask.getJSONArray("text").length(); k++) {
                            task.getCommentsString().add(objectTask.getJSONArray("text").getString(k));
                        }

                        report.getListTasks().add(task);

                    }

                    Log.e("ReportInfo", report.getIdReport() + " - " + report.getNomResponsable());

                    activity.runOnUiThread(new Runnable() {
                        @RequiresApi(api = Build.VERSION_CODES.O)
                        @Override
                        public void run() {
                            activity.setupViews(report);
                        }
                    });

                } catch (Exception ex) {
                    if (activity != null)
                        new ResultBottomDialog(ex.getMessage(), 3).show(activity.getSupportFragmentManager(), null);
                }
                loadingReports.dismiss();
            }
        });
    }

    public static class GetReportFiles extends AsyncTask<Void, Void, Void> {
        LoadingButton loadingReports;
        TaskPreviewMedias fragment;
        int numTask;
        Report report;
        boolean server_error = false;

        List<File> videos;
        List<File> images;
        List<String> comments;
        List<File> audios;


        public GetReportFiles(Report report, TaskPreviewMedias fragment, int numTask, LoadingButton downloadBtn) {
            this.fragment = fragment;
            this.report = report;
            this.numTask = numTask;
            loadingReports = downloadBtn;
            videos = new ArrayList<>();
            images = new ArrayList<>();
            comments = new ArrayList<>();
            audios = new ArrayList<>();

        }

        @Override
        protected void onPreExecute() {
            loadingReports.startLoading();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                //Download Images :
                if (numTask == report.getListTasks().size()) {
                    //Download Videos :
                    for (int i = 0; i < report.getVideosList().size(); i++) {
                        videos.add(getFilefromInputStream(MEDIA_TYPE_VIDEO, getOutputTempFile(MEDIA_TYPE_VIDEO, report.getVideosList().get(i), fragment.getContext())));
                    }

                    for (int i = 0; i < report.getImagesList().size(); i++) {
                        images.add(getFilefromInputStream(MEDIA_TYPE_IMAGE, getOutputTempFile(MEDIA_TYPE_IMAGE, report.getImagesList().get(i), fragment.getContext())));
                    }

                    //Download Audios :
                    for (int i = 0; i < report.getAudiosList().size(); i++) {
                        audios.add(getFilefromInputStream(MEDIA_TYPE_AUDIO, getOutputTempFile(MEDIA_TYPE_AUDIO, report.getAudiosList().get(i), fragment.getContext())));
                    }

                    //Download Comment :
                    comments.addAll(report.getCommentsList());
                    fragment.getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            fragment.previewImage(images);
                            fragment.previewAudio(audios);
                            fragment.previewVideo(videos);
                            fragment.previewComment(comments);
                        }
                    });
                    return null;
                }

                for (int i = 0; i < report.getListTasks().get(numTask).getImagesString().size(); i++) {
                    images.add(getFilefromInputStream(MEDIA_TYPE_IMAGE, getOutputTempFile(MEDIA_TYPE_IMAGE, report.getListTasks().get(numTask).getImagesString().get(i), fragment.getContext())));
                }

                //Download Videos :
                for (int i = 0; i < report.getListTasks().get(numTask).getVideosString().size(); i++) {
                    videos.add(getFilefromInputStream(MEDIA_TYPE_VIDEO, getOutputTempFile(MEDIA_TYPE_VIDEO, report.getListTasks().get(numTask).getVideosString().get(i), fragment.getContext())));
                }

                //Download Audios :
                for (int i = 0; i < report.getListTasks().get(numTask).getAudiosString().size(); i++) {
                    audios.add(getFilefromInputStream(MEDIA_TYPE_AUDIO, getOutputTempFile(MEDIA_TYPE_AUDIO, report.getListTasks().get(numTask).getAudiosString().get(i), fragment.getContext())));
                }

                //Download Audios :
                comments.addAll(report.getListTasks().get(numTask).getCommentsString());

                Log.e("ReportInfo", "Nb Images : " + images.size() + " - Nb Videos : " + videos.size() + " - Nb Audios : " + audios.size() + " - Nb Comments : " + comments.size());
                fragment.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        fragment.previewImage(images);
                        fragment.previewAudio(audios);
                        fragment.previewVideo(videos);
                        fragment.previewComment(comments);
                    }
                });
            } catch (Exception ex) {
                new ResultBottomDialog(fragment.getResources().getString(R.string.errorOccured),3).show(fragment.getActivity().getSupportFragmentManager(),null);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (!server_error) {
                try {
                    loadingReports.loadingSuccessful();
                    fragment.downloadLayout.setVisibility(View.GONE);
                    fragment.previewScroll.setVisibility(View.VISIBLE);

                } catch (Exception ex) {
                    if (fragment != null) {
                        ex.printStackTrace();
                        loadingReports.loadingFailed();
                        new ResultBottomDialog("Error while getting report infos", 3).show(fragment.getActivity().getSupportFragmentManager(), null);
                    }
                }

            } else {
                loadingReports.loadingFailed();
                new ResultBottomDialog("Error while getting report infos", 3).show(fragment.getActivity().getSupportFragmentManager(), null);
            }

        }
    }


}

