package com.abbad.smartonapp.activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.abbad.smartonapp.R;
import com.abbad.smartonapp.ui.dashboard.DashboardFragment;
import com.abbad.smartonapp.utils.SessionManager;
import com.abbad.smartonapp.utils.WebServiceConnection;
import com.dx.dxloadingbutton.lib.LoadingButton;
import com.github.florent37.materialtextfield.MaterialTextField;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Timer;
import java.util.TimerTask;



public class LoginActivity extends AppCompatActivity {
    EditText emailInput;
    EditText passInput;
    MaterialTextField emailLayout;
    MaterialTextField passLayout;
    AppCompatTextView errorLoginText;
    LoadingButton btnLogin;
    SessionManager sessionManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        sessionManager = new SessionManager(getApplicationContext());
        if(!sessionManager.isUserLoggedOut()){
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            this.finish();
        }
        emailInput = findViewById(R.id.emailInput);
        passInput = findViewById(R.id.passwordInput);
        emailLayout = findViewById(R.id.email_layout);
        passLayout = findViewById(R.id.pass_layout);
        btnLogin = findViewById(R.id.btn_login);
        errorLoginText = findViewById(R.id.error_login_text);
        btnLogin.setOnClickListener(new BtnClickEvent(this));

        passInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    if(emailInput.getText().toString().length() != 0 && !emailInput.getText().toString().equals("")
                            && passInput.getText().toString().length() != 0 && !passInput.getText().toString().equals(""))
                        new WebServiceConnection.loginSyncTask(LoginActivity.this).execute();
                }
                return false;
            }
        });

    }

    public class BtnClickEvent implements View.OnClickListener {
        private AppCompatActivity activity;
        public BtnClickEvent(AppCompatActivity a){
            activity = a;
        }

        @Override
        public void onClick(View v) {
            if(emailInput.getText().toString().length() != 0 && !emailInput.getText().toString().equals("")
            && passInput.getText().toString().length() != 0 && !passInput.getText().toString().equals(""))
                new WebServiceConnection.loginSyncTask((LoginActivity) activity).execute();
        }

    }

    public EditText getEmailInput() {
        return emailInput;
    }

    public EditText getPassInput() {
        return passInput;
    }

    public LoadingButton getBtnLogin() {
        return btnLogin;
    }
}