package com.abbad.smartonapp.ui.alerts;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.abbad.smartonapp.R;
import com.abbad.smartonapp.activities.MainActivity;
import com.abbad.smartonapp.adapters.NotificationAdapter;
import com.abbad.smartonapp.classes.Notification;
import com.abbad.smartonapp.datas.AlertesData;
import com.abbad.smartonapp.ui.notifications.NotificationsViewModel;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class AlertsFragment extends Fragment {
    private List<Notification> listAlerts;
    private NotificationsViewModel notificationsViewModel;
    private RecyclerView alerteRecyclerView;
    public NotificationAdapter alertsAdapter;
    private AlertsViewModel mViewModel;

    public static AlertsFragment newInstance() {
        return new AlertsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewModel = new AlertsViewModel(this);
        View view = inflater.inflate(R.layout.alerts_fragment, container, false);
        alerteRecyclerView = view.findViewById(R.id.alerteRecyclerView);

        listAlerts = AlertesData.getListAlertes((MainActivity) getActivity());

        alertsAdapter = new NotificationAdapter(listAlerts);
        alerteRecyclerView.setAdapter(alertsAdapter);
        refreshNotificationsList();

        return  view;
    }

    public void refreshNotificationsList() {
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Log.i("sys_output", "Notifications had been updated");

                new AlertsViewModel.GetAlertsOnActivty((MainActivity) getActivity()).execute();
            }
        }, 0, 6000);
    }

}