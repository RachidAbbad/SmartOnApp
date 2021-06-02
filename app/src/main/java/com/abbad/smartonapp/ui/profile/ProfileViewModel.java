package com.abbad.smartonapp.ui.profile;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaRouter;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;

import com.abbad.smartonapp.R;
import com.abbad.smartonapp.activities.AuthSiteActivity;
import com.abbad.smartonapp.activities.LoginActivity;
import com.abbad.smartonapp.activities.MainActivity;
import com.abbad.smartonapp.utils.Comun;
import com.abbad.smartonapp.utils.SessionManager;
import com.google.android.material.snackbar.Snackbar;

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

    public class LogoutClickEvent implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            sessionManager.logout();
            Intent intent = new Intent(f.getActivity(), LoginActivity.class);
            f.startActivity(intent);
            f.getActivity().finish();
        }
    }

    public void fillUserInfos(TextView name, TextView type_account, TextView gsm, TextView email, LinearLayout gsmLayout,Context context){
        SessionManager sessionManager = new SessionManager(context);

        name.setText(sessionManager.getCurrentUser().getName());
        if(sessionManager.getCurrentUser().getGsm().isEmpty())
            gsmLayout.setVisibility(View.GONE);

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
            permissionItems.add(new PermissionItem(Manifest.permission.CAMERA, "CAMERA", R.drawable.permission_ic_camera));
            HiPermission.create(f.getActivity())
                    .title("Ask for permission")
                    .permissions(permissionItems)
                    .msg("Asking for camera permission to scan Qr codes")
                    .style(R.style.PermissionBlueStyle)
                    .checkMutiPermission(new PermissionCallback() {
                        @Override
                        public void onClose() {
                            Log.i("permission_granted", "onClose");
                        }

                        @Override
                        public void onFinish() {
                            Intent intent = new Intent(f.getContext(), AuthSiteActivity.class);
                            f.startActivity(intent);
                        }

                        @Override
                        public void onDeny(String permission, int position) {
                            Log.i("permission_granted", "onDeny");
                        }

                        @Override
                        public void onGuarantee(String permission, int position) {
                            Log.i("permission_granted", "onGuarantee");
                            Intent intent = new Intent(f.getContext(), AuthSiteActivity.class);
                            f.startActivity(intent);

                        }
                    });



        }
    }

}