package com.abbad.smartonapp.ui.profile;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;

import com.abbad.smartonapp.R;
import com.abbad.smartonapp.activities.LoginActivity;
import com.abbad.smartonapp.dialogs.AuthSiteDialog;
import com.abbad.smartonapp.dialogs.LanguagePickerDialog;
import com.abbad.smartonapp.dialogs.LoadingBottomDialog;
import com.abbad.smartonapp.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

import me.weyye.hipermission.HiPermission;
import me.weyye.hipermission.PermissionCallback;
import me.weyye.hipermission.PermissionItem;


public class ProfileViewModel extends ViewModel {
    SessionManager sessionManager;
    Fragment f;
    public ProfileViewModel(Context context1,Fragment f){
        sessionManager = new SessionManager(context1);
        this.f = f;
    }

    public void logoutClickHandler(AppCompatButton btn) {
        btn.setOnClickListener(new LogoutClickEvent());
    }

    public void changeLanguageHandler(AppCompatButton btn) {
        btn.setOnClickListener(new LanguageChangeHandler());
    }

    public class LogoutClickEvent implements View.OnClickListener {
        LoadingBottomDialog loadingBottomDialog;
        @Override
        public void onClick(View v) {
            loadingBottomDialog = new LoadingBottomDialog(f.getResources().getString(R.string.logout_btn_text)+" ...");
            loadingBottomDialog.setCancelable(false);
            loadingBottomDialog.show(f.getFragmentManager(),null);

            sessionManager.logout();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    loadingBottomDialog.dismiss();
                }
            }, 3000);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(f.getActivity(), LoginActivity.class);
                    f.startActivity(intent);
                    f.getActivity().finish();
                }
            }, 4000);






        }
    }

    public void fillUserInfos(TextView name, TextView type_account, TextView gsm, TextView email, LinearLayout gsmLayout,Context context){
        SessionManager sessionManager = new SessionManager(context);

        name.setText(sessionManager.getCurrentUser().getName());
        if(sessionManager.getCurrentUser().getGsm().isEmpty())
            gsm.setText(f.getResources().getString(R.string.noDisponible));
        else
            gsm.setText(sessionManager.getCurrentUser().getGsm());
        email.setText(sessionManager.getCurrentUser().getEmail());
        type_account.setText(sessionManager.getCurrentUser().getType());

    }

    public void authClickHandeler(AppCompatButton btn){

        btn.setOnClickListener(new AuthSiteEvent());
    }

    public class AuthSiteEvent implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            List<PermissionItem> permissionItems = new ArrayList<PermissionItem>();
            if(checkNFC()==1){
                permissionItems.add(new PermissionItem(Manifest.permission.NFC, "CAMERA", R.drawable.permission_ic_sensors));
            }
            else if (checkNFC()==2){

            }
            else if (checkNFC()==3){
                permissionItems.add(new PermissionItem(Manifest.permission.CAMERA, "CAMERA", R.drawable.permission_ic_camera));
            }
            HiPermission.create(f.getActivity())
                    .title("Ask for permission")
                    .permissions(permissionItems)
                    .style(R.style.PermissionBlueStyle)
                    .checkMutiPermission(new PermissionCallback() {
                        @Override
                        public void onClose() {
                            Log.i("permission_granted", "onClose");
                        }

                        @Override
                        public void onFinish() {
                            new AuthSiteDialog().show(f.getActivity().getSupportFragmentManager(),null);
                        }

                        @Override
                        public void onDeny(String permission, int position) {
                            Log.i("permission_granted", "onDeny");
                        }

                        @Override
                        public void onGuarantee(String permission, int position) {
                            Log.i("permission_granted", "onGuarantee");
                            new AuthSiteDialog().show(f.getActivity().getSupportFragmentManager(),null);

                        }
                    });



        }
    }

    public class LanguageChangeHandler implements View.OnClickListener{

        @Override
        public void onClick(View v) {

            LanguagePickerDialog languagePickerDialog = new LanguagePickerDialog();
            languagePickerDialog.show(f.getActivity().getSupportFragmentManager(),"BottomSheet Fragment");
        }
    }

    public int checkNFC(){
        NfcManager manager = (NfcManager) f.getContext().getSystemService(Context.NFC_SERVICE);
        NfcAdapter adapter = manager.getDefaultAdapter();
        if (adapter != null && adapter.isEnabled()) {
            return 1;
        }else if(adapter != null && !adapter.isEnabled()){
            f.getActivity().startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));
            return 2;
        }else{
            return 3;
        }
    }

}