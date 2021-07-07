package com.abbad.smartonapp.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.abbad.smartonapp.R;
import com.abbad.smartonapp.classes.Notification;

import java.util.ArrayList;
import java.util.List;


public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.Viewholder>{
    private TextView mainTitle,detailText,notifTime;
    private CardView leftColor;
    //private LinearLayout detailLayout;

    List<Notification> notificationList;

    public NotificationAdapter(){
        notificationList = new ArrayList<>();
        notificationList.add(new Notification("Notification example 1","Lorem Ipsum est simplement du faux texte employé dans la composition et la mise en page avant impression. Le Lorem Ipsum est le faux texte standard de l'imprimerie depuis les années 1500.","07-07-2021 17:12"));
        notificationList.add(new Notification("Notification example 2","Lorem Ipsum est simplement du faux texte employé dans la composition et la mise en page avant impression. Le Lorem Ipsum est le faux texte standard de l'imprimerie depuis les années 1500.","05-07-2021 09:32"));
        notificationList.add(new Notification("Notification example 3","Lorem Ipsum est simplement du faux texte employé dans la composition et la mise en page avant impression. Le Lorem Ipsum est le faux texte standard de l'imprimerie depuis les années 1500.","05-07-2021 10:19"));
        notificationList.add(new Notification("Notification example 4","Lorem Ipsum est simplement du faux texte employé dans la composition et la mise en page avant impression. Le Lorem Ipsum est le faux texte standard de l'imprimerie depuis les années 1500.","04-07-2021 08:12"));
        notificationList.add(new Notification("Notification example 5","Lorem Ipsum est simplement du faux texte employé dans la composition et la mise en page avant impression. Le Lorem Ipsum est le faux texte standard de l'imprimerie depuis les années 1500.","01-07-2021 11:52"));
        notificationList.add(new Notification("Notification example 6","Lorem Ipsum est simplement du faux texte employé dans la composition et la mise en page avant impression. Le Lorem Ipsum est le faux texte standard de l'imprimerie depuis les années 1500.","29-06-2021 10:17"));
        notificationList.add(new Notification("Notification example 7","Lorem Ipsum est simplement du faux texte employé dans la composition et la mise en page avant impression. Le Lorem Ipsum est le faux texte standard de l'imprimerie depuis les années 1500.","25-06-2021 17:12"));
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_item_layout, parent, false);
        return new Viewholder(view);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {
        Notification notification = notificationList.get(position);
        mainTitle.setText(notification.getTitle());
        detailText.setText(notification.getDetails());
        notifTime.setText(notification.getTime());
        if (position % 2 == 0) {
            leftColor.setBackgroundResource(R.color.secondColor);
        } else {
            leftColor.setBackgroundResource(R.color.firstColor);
        }
    }



    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder{

        public Viewholder(@NonNull View itemView) {
            super(itemView);
            mainTitle = itemView.findViewById(R.id.mainTitle);
            detailText = itemView.findViewById(R.id.detailText);
            notifTime = itemView.findViewById(R.id.notifTime);
            leftColor = itemView.findViewById(R.id.leftColor);
            //detailLayout = itemView.findViewById(R.id.detailLayout);
        }

    }
}
