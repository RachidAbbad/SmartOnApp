package com.abbad.smartonapp.utils;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class Comun {


    public static void askCameraPermission(Activity activity){
        int grant = ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA);
        if (grant != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.CAMERA},
                    200);
        }
    }


}
