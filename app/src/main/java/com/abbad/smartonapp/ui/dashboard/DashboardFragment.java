package com.abbad.smartonapp.ui.dashboard;

import android.os.Bundle;
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

public class DashboardFragment extends Fragment {

    private DashboardViewModel dashboardViewModel;
    private SpeedView s1,s2,s3,s4,s5,s6,s7,s8;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);
        s1 = root.findViewById(R.id.temp_Indicator_1);
        s2 = root.findViewById(R.id.temp_Indicator_2);
        s3 = root.findViewById(R.id.temp_Indicator_3);
        s4 = root.findViewById(R.id.temp_Indicator_4);
        s5 = root.findViewById(R.id.temp_Indicator_5);
        s6 = root.findViewById(R.id.temp_Indicator_6);
        s7 = root.findViewById(R.id.temp_Indicator_7);
        s8 = root.findViewById(R.id.temp_Indicator_8);
        dashboardViewModel.onLoadSpeedMeters(s1,s2,s3,s4,s5,s6,s7,s8);
        return root;
    }
}