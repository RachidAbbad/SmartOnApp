package com.abbad.smartonapp.ui.dashboard;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.abbad.smartonapp.R;
import com.github.anastr.speedviewlib.SpeedView;

import org.json.JSONException;

import java.io.IOException;
import java.net.URISyntaxException;

public class DashboardFragment extends Fragment {

    private DashboardViewModel dashboardViewModel;
    private SpeedView s1,s2,s3,s4,s5,s6,s7,s8;
    private TextView textView;

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

        textView = root.findViewById(R.id.test);
        try {
            dashboardViewModel.getChaudiereValues(s1,s2,s3,s4,s5,s6, getContext());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return root;
    }

    @Override
    public void onPause() {
        super.onPause();
        dashboardViewModel.cancelTimer();
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            dashboardViewModel.getChaudiereValues(s1, s2, s3, s4, s5, s6, getContext());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}