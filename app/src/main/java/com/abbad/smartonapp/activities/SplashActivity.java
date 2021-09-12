package com.abbad.smartonapp.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.abbad.smartonapp.R;
import com.abbad.smartonapp.utils.Comun;
import com.abbad.smartonapp.utils.SessionManager;
import com.abbad.smartonapp.utils.WebServiceConnection;

import java.util.Timer;
import java.util.TimerTask;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.firstColor));

        new Timer().schedule(new TimerTask() {

            public void run() {
                Comun.setLocale(SplashActivity.this,SessionManager.getLocale(getApplicationContext()));
                if(WebServiceConnection.isNetworkAvailable(getApplicationContext())){
                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
                else{
                    Intent intent = new Intent(SplashActivity.this, NoConnectionActivity.class);
                    startActivity(intent);
                }
                finish();

            }
        }, 2000);


    }
}