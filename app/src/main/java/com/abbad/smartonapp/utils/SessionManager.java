package com.abbad.smartonapp.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.abbad.smartonapp.classes.User;
import com.dx.dxloadingbutton.lib.LoadingButton;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

public class SessionManager {
        private static SharedPreferences sharedPreferences;
        Context context;


        public SessionManager(Context context) {
            this.context = context;
            sharedPreferences = context.getSharedPreferences("LoginDetails", Context.MODE_PRIVATE);
        }

        public static boolean isTheFirstTime(){
            Log.i("FirstTime",Boolean.toString(sharedPreferences.getBoolean("isFirstTime", true)));
            return sharedPreferences.getBoolean("isFirstTime", true);
        }

        public static void saveUserInfos(JSONObject userJson,String pass,String AuthToken) throws JSONException {
            Log.i("userJson",userJson.toString());
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("Email", userJson.getString("email"));
            editor.putString("Id", userJson.getString("id"));
            editor.putString("NomComlplet" , userJson.getString("name")+" "+ userJson.getString("prenom"));
            editor.putString("Gsm",userJson.getString("gsm"));
            editor.putString("Token",AuthToken);
            editor.putString("CurrentInterv",null);
            switch (userJson.getInt("type_user")){
                case 3 :
                    editor.putString("Type_account","Téchnicien");
            }
            editor.putString("Password", pass);
            editor.apply();
            Log.i("userInfos",sharedPreferences.getString("Token",null)+"   -   "+AuthToken);
        }

        public static String getCurrentInterv(Context context){
            return context.getSharedPreferences("LoginDetails", Context.MODE_PRIVATE).getString("CurrentInterv",null);
        }

        public static void saveCurrentInterv(String id){
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("CurrentInterv", id);
            editor.apply();
        }

        public static String getLocale(Context context){
            return context.getSharedPreferences("LoginDetails", Context.MODE_PRIVATE).getString("selected_language","fr");
        }

        public static void setFirsTimeUse(boolean b){
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("isFirstTime",false);
            editor.apply();
        }

        public boolean isUserLoggedOut() {
            boolean isEmailEmpty = sharedPreferences.getString("Email", "").isEmpty();
            boolean isPasswordEmpty = sharedPreferences.getString("Password", "").isEmpty();
            return isEmailEmpty || isPasswordEmpty;
        }

        public void logout(){
            SharedPreferences.Editor editor = sharedPreferences.edit();

            editor.putString("Email", "");
            editor.putString("Id", "");
            editor.putString("NomComlplet" , "");
            editor.putString("Gsm","");
            editor.putString("Token","");
            editor.putString("Type","");
            editor.putString("Password", "");
            editor.apply();
        }

        public User getCurrentUser(){
            User user = new User();
            SharedPreferences.Editor editor = sharedPreferences.edit();
            user.setName(sharedPreferences.getString("NomComlplet",null));
            user.setCharType(sharedPreferences.getString("Type",null));
            user.setGsm(sharedPreferences.getString("Gsm",null));
            user.setEmail(sharedPreferences.getString("Email",null));
            return user;
        }

        public static String getAuthToken(){
            return sharedPreferences.getString("Token",null);
        }

        public static String getLanguage(){
            return sharedPreferences.getString("selected_language","fr");
        }
        public void putLanguage(String language){
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("selected_language", language);
            editor.apply();
        }
}
