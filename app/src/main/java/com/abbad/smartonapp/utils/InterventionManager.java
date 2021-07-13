package com.abbad.smartonapp.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class InterventionManager {
    private static SharedPreferences sharedPreferences;
    private static Context context;

    public InterventionManager(Context context) {
        InterventionManager.context = context;
        sharedPreferences = context.getSharedPreferences("InterventionDetails", Context.MODE_PRIVATE);
    }

    public static void saveCurrentIntervention(String idIntervention) throws JSONException {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("IdIntervention", idIntervention);
        editor.apply();
    }

    public static void saveTaskStatus(String idIntervention,int numTask,boolean status){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(idIntervention+"_"+numTask, status);
        editor.apply();
    }

    public static String getCurrentIntervention() throws JSONException {
        return context.getSharedPreferences("InterventionDetails", Context.MODE_PRIVATE).getString("IdIntervention",null);
    }

    public static boolean getTaskStatus(String idIntervention,int numTask){
        return context.getSharedPreferences("InterventionDetails", Context.MODE_PRIVATE).getBoolean(idIntervention+"_"+numTask,false);

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


}
