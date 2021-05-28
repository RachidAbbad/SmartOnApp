package com.abbad.smartonapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.abbad.smartonapp.R;
import com.abbad.smartonapp.utils.WebServiceConnection;

import java.sql.Time;
import java.util.Timer;
import java.util.TimerTask;

public class NoConnectionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_connection);
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                Log.i("sys_output","run time");
                if(WebServiceConnection.isNetworkAvailable(getApplicationContext())){
                    Intent i = new Intent(NoConnectionActivity.this, LoginActivity.class);
                    startActivity(i);
                    timer.cancel();
                }
            }
        },0, 500);

    }
}