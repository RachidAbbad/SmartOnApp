package com.abbad.smartonapp.ui.notifications;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.abbad.smartonapp.R;
import com.abbad.smartonapp.activities.MainActivity;
import com.abbad.smartonapp.adapters.NotificationAdapter;
import com.abbad.smartonapp.classes.Notification;
import com.abbad.smartonapp.datas.NotificationData;
import com.abbad.smartonapp.ui.dashboard.DashboardViewModel;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class NotificationsFragment extends Fragment {
    private List<Notification> listNotifications;
    private NotificationsViewModel notificationsViewModel;
    private LinearLayout serverError,noNotificationLayout,workLayout;
    private RecyclerView recyclerView;
    private TextView exceptionText;
    public NotificationAdapter notificationAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        notificationsViewModel = new NotificationsViewModel(this);
        View root = inflater.inflate(R.layout.fragment_notifications, container, false);
        listNotifications = NotificationData.getListNotifications(this);
        recyclerView = root.findViewById(R.id.recyclerView);
        noNotificationLayout = root.findViewById(R.id.noNotificationLayout);
        serverError = root.findViewById(R.id.serverError);
        workLayout = root.findViewById(R.id.workLayout);
        exceptionText = root.findViewById(R.id.exceptionText);
        notificationAdapter = new NotificationAdapter(listNotifications);
        recyclerView.setAdapter(notificationAdapter);

        return root;
    }


    public void refreshNotificationsList() {
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                NotificationsViewModel.getNotifications(NotificationsFragment.this);
            }
        }, 0, 6000);
    }

    public void noNotifications(){
        noNotificationLayout.setVisibility(View.VISIBLE);
        workLayout.setVisibility(View.GONE);
        serverError.setVisibility(View.GONE);
    }

    public void errorServer(String textEx){
        noNotificationLayout.setVisibility(View.GONE);
        workLayout.setVisibility(View.GONE);
        serverError.setVisibility(View.VISIBLE);
        exceptionText.setText(textEx);
    }

    public void backToService(){
        noNotificationLayout.setVisibility(View.GONE);
        workLayout.setVisibility(View.VISIBLE);
        serverError.setVisibility(View.GONE);
    }
}