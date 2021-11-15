package com.abbad.smartonapp.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.ArrayAdapter;

import androidx.annotation.Nullable;

import com.abbad.smartonapp.classes.Notification;
import com.abbad.smartonapp.datas.NotificationData;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class PushNotificationsService extends Service {

    public List<Notification> globalListNotification;
    public List<Notification> globalListAlert;
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        globalListNotification = new ArrayList<>();
        globalListAlert = new ArrayList<>();
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                new NotificationData.GetAllNotification(PushNotificationsService.this).execute();
                //new NotificationData.GetAllAlert(PushNotificationsService.this).execute();
            }
        },5000);
        //TODO execute "getAllNotificationsTask" --> This class should have a boolean isNotification (isNotification -> Notification / !isNotification -> Alert)

        //TODO check new Notifications

        //TODO Display a notification when a new Notification/Alert added
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private List<Notification> newNotification(){
        List<Notification> notificationList = new ArrayList<>();

        return notificationList;
    }

    private List<Notification> newAlert(){
        List<Notification> notificationList = new ArrayList<>();

        return notificationList;
    }

    public void errorServer(String s) {
    }

    private void displayNotification(boolean isNotification, Notification notification){
        //TODO This a general function to show notifications
        if (isNotification) {
            //TODO This is a normal notification
            return;
        }
        //TODO This is an alert notification
    }
}
