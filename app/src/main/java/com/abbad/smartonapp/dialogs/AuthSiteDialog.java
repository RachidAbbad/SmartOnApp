package com.abbad.smartonapp.dialogs;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;
import android.os.Build;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;

import com.abbad.smartonapp.R;
import com.abbad.smartonapp.activities.MainActivity;
import com.abbad.smartonapp.utils.AuthAuSite;
import com.abbad.smartonapp.utils.SessionManager;
import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.budiyev.android.codescanner.ScanMode;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.zxing.Result;
import com.rm.rmswitch.RMSwitch;
import com.rm.rmswitch.RMTristateSwitch;

import org.apache.http.auth.AUTH;

import java.util.Timer;
import java.util.TimerTask;

public class AuthSiteDialog extends BottomSheetDialogFragment {
    public static int AUTH_QR = 1;
    public static int AUTH_NFC = 2;
    //Qr code components :
    CodeScannerView codeScannerView;
    CodeScanner codeScanner;
    private CardView qrScannerLayout, nfcScannerLayout, qrBtn, nfcBtn;
    private RMSwitch switch_qr_nfc;
    private ImageView qrIcon, nfcIcon;

    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(@NonNull Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View contentView = View.inflate(getContext(), R.layout.auth_site_dialog, null);
        contentView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT < 16) {
                    contentView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                } else {
                    contentView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
                BottomSheetDialog dialog = (BottomSheetDialog) getDialog();
                FrameLayout bottomSheet = (FrameLayout)
                        dialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
                BottomSheetBehavior behavior = BottomSheetBehavior.from(bottomSheet);
                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                behavior.setPeekHeight(0); // Remove this line to hide a dark background if you manually hide the dialog.
            }
        });
        dialog.setContentView(contentView);
        ((View) contentView.getParent()).setBackgroundColor(getResources().getColor(android.R.color.transparent));
        dialog.setContentView(contentView);
        qrScannerLayout = contentView.findViewById(R.id.qrScanner);
        nfcScannerLayout = contentView.findViewById(R.id.nfcScanner);
        codeScannerView = contentView.findViewById(R.id.scanner_view);
        switch_qr_nfc = contentView.findViewById(R.id.switch_qr_nfc);
        codeScanner = new CodeScanner(getActivity().getApplicationContext(), codeScannerView);
        codeScanner.startPreview();

        qrScan();
        switch_qr_nfc.addSwitchObserver(new RMSwitch.RMSwitchObserver() {
            @Override
            public void onCheckStateChange(RMSwitch switchView, boolean isChecked) {
                if (isChecked) {
                    codeScanner.stopPreview();
                    showNfcAuth();
                } else {
                    codeScanner.startPreview();
                    showQrCodeAuth();
                }
            }
        });


    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        codeScanner.releaseResources();
        super.onDismiss(dialog);
    }

    private void qrScan() {
        // Parameters (default values)
        codeScanner.setCamera(CodeScanner.CAMERA_BACK);
        codeScanner.setFormats(CodeScanner.ALL_FORMATS);
        codeScanner.setAutoFocusEnabled(true);
        codeScanner.setScanMode(ScanMode.SINGLE);
        codeScanner.setTouchFocusEnabled(true);

        // Callbacks
        codeScanner.setDecodeCallback(new DecodeCallback() {
            @SuppressLint("WrongThread")
            @Override
            public void onDecoded(@NonNull Result result) {
                new AuthAuSite((MainActivity) getActivity(), SessionManager.getUserId(getContext()), result.getText(), AuthSiteDialog.this, AUTH_QR).execute();
            }
        });


    }

    @SuppressLint("ResourceAsColor")
    private void showNfcAuth() {
        try {
            NfcManager nfcManager = (NfcManager) getContext().getSystemService(Context.NFC_SERVICE);
            NfcAdapter nfcAdapter = nfcManager.getDefaultAdapter();
            if (nfcAdapter == null) {
                Toast.makeText(getActivity(), "NFC not supported in this device", Toast.LENGTH_LONG);
                switch_qr_nfc.setChecked(false);
                new ResultBottomDialog(getResources().getString(R.string.nfcNotSupported), 3).show(getActivity().getSupportFragmentManager(), null);
                return;
            }
            qrScannerLayout.setVisibility(View.GONE);
            nfcScannerLayout.setVisibility(View.VISIBLE);

            MainActivity mainActivity = (MainActivity) getActivity();
            mainActivity.enableNfcScan();
        } catch (Exception ex) {
            new ResultBottomDialog(ex.getMessage(), 3).show(getActivity().getSupportFragmentManager(), null);
        }


    }

    @SuppressLint("ResourceAsColor")
    private void showQrCodeAuth() {
        qrScannerLayout.setVisibility(View.VISIBLE);
        nfcScannerLayout.setVisibility(View.GONE);
    }

    public void retryNfcScaning() {
        try {
            MainActivity mainActivity = (MainActivity) getActivity();
            mainActivity.enableNfcScan();
        } catch (Exception ex) {
            new ResultBottomDialog(ex.getMessage(), 3).show(getActivity().getSupportFragmentManager(), null);
        }
    }

    public AuthSiteDialog getAuthDialog() {
        return this;
    }
}
