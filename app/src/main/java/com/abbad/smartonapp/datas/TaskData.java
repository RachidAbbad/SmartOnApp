package com.abbad.smartonapp.datas;

import android.app.Activity;
import android.util.Log;

import com.abbad.smartonapp.Fragments.TaskFragment;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TaskData {
    private String idIntervention;
    private boolean status;
    private List<File> images;
    private List<File> videos;
    private List<File> audios;
    private List<File> comments;
    private Activity activity;

    public TaskData(Activity activity){
        this.activity = activity;
    }

    public String getIdIntervention() {
        return idIntervention;
    }

    public boolean isStatus() {
        return status;
    }

    public static List<File> getImages(String idIntervention,int numTask,Activity activity) {
        try {

            File directory = new File(activity.getExternalCacheDir()+"/Images");
            List<File> files = Arrays.asList(directory.listFiles());
            List<File> results = new ArrayList<>();
            for (int i = 0; i < files.size(); i++)
            {
                String name = files.get(i).getName();
                String requiredString = name.substring(name.indexOf("(") + 1, name.indexOf(")"));
                if(name.contains(idIntervention) && numTask == Integer.parseInt(requiredString))
                    results.add(files.get(i));
            }
            Log.e("List Images","Images number : "+results.size()+" | Task num : "+numTask);
            return results;
        }catch (Exception ex)
        {
            return new ArrayList<>();
        }
    }

    public static List<File> getVideos(String idIntervention,int numTask,Activity activity) {
        File directory = null;
        try {
            directory = new File(activity.getExternalCacheDir()+"/Videos");
            List<File> files = Arrays.asList(directory.listFiles());
            List<File> results = new ArrayList<>();
            for (int i = 0; i < files.size(); i++)
            {
                String name = files.get(i).getName();
                String requiredString = name.substring(name.indexOf("(") + 1, name.indexOf(")"));
                if(name.contains(idIntervention) && numTask == Integer.parseInt(requiredString))
                    results.add(files.get(i));
            }
            Log.e("List Videos","Videos number : "+results.size()+" | Task num : "+numTask);
            return results;

        }catch (Exception ex){
            return new ArrayList<>();
        }
    }

    public static List<File> getAudios(String idIntervention,int numTask,Activity activity) {
        File directory = null;
        try {
            directory = new File(activity.getExternalCacheDir()+"/Audios");
            List<File> files = Arrays.asList(directory.listFiles());
            List<File> results = new ArrayList<>();
            for (int i = 0; i < files.size(); i++)
            {
                String name = files.get(i).getName();
                String requiredString = name.substring(name.indexOf("(") + 1, name.indexOf(")"));
                if(name.contains(idIntervention) && numTask == Integer.parseInt(requiredString))
                    results.add(files.get(i));
            }
            Log.e("List Audios","Audio number : "+results.size()+" | Task num : "+numTask);
            return results;

        }catch (Exception ex){
            return new ArrayList<>();
        }
    }

    public static List<File> getComments(String idIntervention,int numTask,Activity activity) {
        File directory = null;
        try {
            directory = new File(activity.getExternalCacheDir()+"/Comments");
            List<File> files = Arrays.asList(directory.listFiles());
            List<File> results = new ArrayList<>();
            for (int i = 0; i < files.size(); i++)
            {
                String name = files.get(i).getName();
                String requiredString = name.substring(name.indexOf("(") + 1, name.indexOf(")"));
                if(name.contains(idIntervention) && numTask == Integer.parseInt(requiredString))
                    results.add(files.get(i));
            }
            Log.e("List Comments","Comments number : "+results.size()+" | Task num : "+numTask);
            return results;

        }catch (Exception ex){
            return new ArrayList<>();
        }
    }

    public static boolean deleteAllTasksData(String idIntervention,int numTask,Activity activity){
        boolean deleteResult = true;
        List<File> listFiles = new ArrayList<>();
        listFiles.addAll(getVideos(idIntervention,numTask,activity));
        listFiles.addAll(getAudios(idIntervention,numTask,activity));
        listFiles.addAll(getImages(idIntervention,numTask,activity));
        listFiles.addAll(getComments(idIntervention,numTask,activity));
        try{
            for (File file:listFiles) {
                if(!file.delete())
                    deleteResult = false;
            }
            return deleteResult;
        }
        catch (Exception ex){
            System.out.printf(ex.getMessage());
            return false;
        }
    }
}
