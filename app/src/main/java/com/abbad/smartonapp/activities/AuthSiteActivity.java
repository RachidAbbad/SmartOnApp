package com.abbad.smartonapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.abbad.smartonapp.R;
import com.abbad.smartonapp.adapters.LoadingBottomDialog;
import com.abbad.smartonapp.adapters.ResultBottomDialog;
import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.budiyev.android.codescanner.ScanMode;
import com.google.zxing.Result;

import java.util.Timer;
import java.util.TimerTask;

public class AuthSiteActivity extends AppCompatActivity {
    private CodeScanner codeScanner;
    private CodeScannerView codeScannerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth_site);

        codeScannerView = (CodeScannerView) findViewById(R.id.scanner_view);
        codeScanner = new CodeScanner(this,codeScannerView);
        qrScan();

        codeScannerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                codeScanner.startPreview();
            }
        });

    }


    private void qrScan(){
// Parameters (default values)
        codeScanner.setCamera(CodeScanner.CAMERA_BACK);
        codeScanner.setFormats(CodeScanner.ALL_FORMATS);
        codeScanner.setAutoFocusEnabled(true);
        codeScanner.setScanMode(ScanMode.SINGLE);
        codeScanner.setTouchFocusEnabled(true);

        // Callbacks
        codeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull Result result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        LoadingBottomDialog loadingBottomDialog = new LoadingBottomDialog("Loading ...");

                        loadingBottomDialog.show(getSupportFragmentManager(),null);
                        Timer timer = new Timer();
                        timer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                loadingBottomDialog.dismiss();
                                if(result.getText().equals("S7363Q87376")){
                                    new ResultBottomDialog(getResources().getString(R.string.authSiteSuccess),true).show(getSupportFragmentManager(),null);
                                }
                                else{
                                    new ResultBottomDialog(getResources().getString(R.string.authSiteFailed),false).show(getSupportFragmentManager(),null);
                                }
                            }
                        },1600);



                    }
                });
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        codeScanner.startPreview();
    }

    @Override
    protected void onPause() {
        codeScanner.releaseResources();
        super.onPause();
    }



}