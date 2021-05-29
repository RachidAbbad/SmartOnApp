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
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;

import com.abbad.smartonapp.R;
import com.abbad.smartonapp.ui.dashboard.DashboardFragment;
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

import br.com.simplepass.loadingbutton.customViews.CircularProgressButton;

public class LoginActivity extends AppCompatActivity {
    EditText emailInput;
    EditText passInput;
    MaterialTextField emailLayout;
    MaterialTextField passLayout;
    AppCompatTextView errorLoginText;
    LoadingButton btnLogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        emailInput = findViewById(R.id.emailInput);
        passInput = findViewById(R.id.passwordInput);
        emailLayout = findViewById(R.id.email_layout);
        passLayout = findViewById(R.id.pass_layout);
        btnLogin = findViewById(R.id.btn_login);
        errorLoginText = findViewById(R.id.error_login_text);
        btnLogin.setOnClickListener(new BtnClickEvent(getApplicationContext()));
    }

    public class BtnClickEvent implements View.OnClickListener{
        private Context context;
        public BtnClickEvent(Context _context){
            this.context = _context;
        }
        @SuppressLint("ResourceAsColor")
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onClick(View v) {

            btnLogin.startLoading();
            AsyncTask.execute(new Runnable() {
                @SuppressLint("ResourceAsColor")
                @Override
                public void run() {
                    try{
                        URL url = new URL("http://smartonviatoile.com/api/Auth/login");
                        HttpURLConnection http = (HttpURLConnection)url.openConnection();
                        http.setRequestMethod("POST");
                        http.setDoOutput(true);
                        http.setRequestProperty("Content-Type", "application/json");

                        String data = "{\"email\": \""+emailInput.getText().toString()
                                +"\",\n \"password\": \""+passInput.getText().toString()+"\"}";

                        byte[] out = data.getBytes(StandardCharsets.UTF_8);

                        OutputStream stream = http.getOutputStream();
                        stream.write(out);

                        BufferedReader reader = new BufferedReader(new InputStreamReader(http.getInputStream()));
                        StringBuilder sb = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            sb.append(line);
                        }
                        JSONObject obj = new JSONObject(sb.toString());
                        getValide((boolean) obj.getBoolean("status"));

                        http.disconnect();
                    }catch(IOException | JSONException e){
                        e.printStackTrace();
                    }

                }
            });
            if(bool){
                btnLogin.loadingSuccessful();
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
            }
            else{
                        emailLayout.setOutlineAmbientShadowColor(R.color.uiRed);
                        passLayout.setOutlineAmbientShadowColor(R.color.uiRed);
                        passLayout.setBackgroundColor(R.color.uiRed);
                        emailLayout.setBackgroundColor(R.color.uiRed);
                        errorLoginText.setText("Email ou Mot de passe incorrect");
                        btnLogin.loadingFailed();

            }


        }
    }

    boolean bool;
    public void getValide(boolean b){
        bool = b;
    }
}