package com.abbad.smartonapp.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.abbad.smartonapp.classes.Intervention;
import com.abbad.smartonapp.datas.InterventionData;
import com.abbad.smartonapp.datas.TaskData;
import com.abbad.smartonapp.services.UploadReportService;

import org.json.JSONException;
import org.json.JSONObject;

public class InterventionManager {
    private static SharedPreferences sharedPreferences;
    private static Context context;

    public InterventionManager(Context context) {
        InterventionManager.context = context;
        sharedPreferences = context.getSharedPreferences("InterventionDetails", Context.MODE_PRIVATE);
    }

    public static void saveCurrentIntervention(String idIntervention){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("IdIntervention", idIntervention);
        editor.apply();
    }

    public static void saveTaskStatus(String idIntervention,int numTask,boolean status){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(idIntervention+"_"+numTask, status);
        editor.apply();
    }

    public static void saveTaskReportStatus(String idIntervention,int numTask,boolean status){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("Report_"+idIntervention+"_"+numTask, status);
        editor.apply();
    }

    public static void saveInterventionReport(String idIntervention,boolean resumeKey){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("IntervReport_"+idIntervention, resumeKey);
        editor.apply();
    }

    public static String getCurrentIntervention() {
        return context.getSharedPreferences("InterventionDetails", Context.MODE_PRIVATE).getString("IdIntervention",null);
    }

    public static boolean getTaskStatus(String idIntervention,int numTask){
        return context.getSharedPreferences("InterventionDetails", Context.MODE_PRIVATE).getBoolean(idIntervention+"_"+numTask,false);

    }

    public static boolean getInterventionReport(String idIntervention){
        return context.getSharedPreferences("InterventionDetails",Context.MODE_PRIVATE).getBoolean("IntervReport_"+idIntervention,false);
    }

    public static void removeCurrentIntervention(String idInterv) throws JSONException {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("IdIntervention");
        for (String key : sharedPreferences.getAll().keySet()) {
            if (key.contains(idInterv)) {
                editor.remove(key);
            }
        }
        editor.apply();
    }

    public static boolean getTaskReportStatus(String idIntervention,int numTask){
        return context.getSharedPreferences("InterventionDetails",Context.MODE_PRIVATE).getBoolean("Report_"+idIntervention+"_"+numTask,false);
    }

    public static boolean resetAllData(String interventionId,int nbTache, Context activity){
        boolean deleteResult=true;
        try{
            SharedPreferences sharedPreferences = activity.getSharedPreferences("InterventionDetails", Context.MODE_PRIVATE);
            //Delete Intervention Data in Shared Memory
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();
            //Delete Intervention Data in Internal Storage
            deleteResult = InterventionData.deleteAllData(interventionId,activity);
            //Delete Intervention Tasks in Shared Preference
            for (int i = 0; i<nbTache;i++)
                deleteResult = TaskData.deleteAllTasksData(interventionId,i,activity);
            return deleteResult;
        }catch (Exception exception){
            System.out.print(exception.getMessage());
            return false;
        }
    }


}
