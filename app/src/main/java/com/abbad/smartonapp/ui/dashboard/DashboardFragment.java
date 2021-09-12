package com.abbad.smartonapp.ui.dashboard;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.abbad.smartonapp.R;
import com.abbad.smartonapp.utils.SessionManager;
import com.github.anastr.speedviewlib.SpeedView;

import org.json.JSONException;

import java.io.IOException;
import java.net.URISyntaxException;

public class DashboardFragment extends Fragment {

    public DashboardViewModel dashboardViewModel;
    public ScrollView mainLayout;

    private LinearLayout containerLayout;
    private LinearLayout serverError;
    private TextView exceptionText, chadiereId;
    private SpeedView s1, s2, s3, s4, s5, s6;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);

        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);
        s1 = root.findViewById(R.id.t_depart_indicator);
        s2 = root.findViewById(R.id.t_retoure_indicator);
        s3 = root.findViewById(R.id.t_inertie_indicator);
        s4 = root.findViewById(R.id.t_fumees_indicator);
        s5 = root.findViewById(R.id.depression_indicator);
        s6 = root.findViewById(R.id.luminosité_indicator);
        serverError = root.findViewById(R.id.serverError);
        mainLayout = root.findViewById(R.id.mainLayout);
        containerLayout = root.findViewById(R.id.containerLayout);

        exceptionText = root.findViewById(R.id.exceptionText);
        chadiereId = root.findViewById(R.id.chadiereId);



        chadiereId.append(SessionManager.getIdCapteur(getContext()));
        try {
            dashboardViewModel.refreshData(this);
        } catch (IOException e) {
            e.printStackTrace();
        }


        return root;
    }

    @Override
    public void onPause() {
        super.onPause();
        //dashboardViewModel.cancelTimer();
    }


    public SpeedView getS1() {
        return s1;
    }

    public SpeedView getS2() {
        return s2;
    }

    public SpeedView getS3() {
        return s3;
    }

    public SpeedView getS4() {
        return s4;
    }

    public SpeedView getS5() {
        return s5;
    }

    public SpeedView getS6() {
        return s6;
    }

    public ScrollView getMainLayout() {
        return mainLayout;
    }

    public LinearLayout getServerError() {
        return serverError;
    }

    public LinearLayout getContainerLayout() {
        return containerLayout;
    }

    public TextView getExceptionText() {
        return exceptionText;
    }



    public void reportError(String content){
        getServerError().setVisibility(View.VISIBLE);
        getMainLayout().setVisibility(View.GONE);
        getExceptionText().setText(content);
    }

    public void errorSolved(){
        getServerError().setVisibility(View.GONE);
        getMainLayout().setVisibility(View.VISIBLE);
    }
}