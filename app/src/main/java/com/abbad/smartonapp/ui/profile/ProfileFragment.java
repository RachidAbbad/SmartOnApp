package com.abbad.smartonapp.ui.profile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.abbad.smartonapp.R;

public class ProfileFragment extends Fragment {

    private ProfileViewModel mViewModel;
    private AppCompatButton logouBtn;
    private TextView fullname;
    private TextView email;
    private TextView gsm;
    private TextView typeUser;
    private LinearLayout gsmLayout;
    private AppCompatButton authSite;

    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewModel = new ProfileViewModel(getContext(),this);
        View root = inflater.inflate(R.layout.profile_fragment, container, false);
        logouBtn = root.findViewById(R.id.logout_btn);
        typeUser = root.findViewById(R.id.userType);
        logouBtn = root.findViewById(R.id.logout_btn);
        fullname = root.findViewById(R.id.userName);
        gsm = root.findViewById(R.id.userGsm);
        gsmLayout = root.findViewById(R.id.gsmLayout);
        email = root.findViewById(R.id.userEmail);
        authSite = root.findViewById(R.id.auth_site_btn);
        mViewModel.authClickHandeler(authSite);
        mViewModel.logoutClickHandler(logouBtn);
        mViewModel.fillUserInfos(fullname,typeUser,gsm,email,gsmLayout,getContext());



        return root;
    }


}