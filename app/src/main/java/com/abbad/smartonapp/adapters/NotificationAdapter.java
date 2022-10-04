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

    public List<Notification> notificationList;

    public NotificationAdapter(List<Notification> notificationList){
        this.notificationList = notificationList;
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
        holder.setIsRecyclable(false);
        Notification notification = notificationList.get(position);
        mainTitle.setText(notification.getTitle());
        detailText.setText(notification.getBody());
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

    public void setNewData(List<Notification> notificationsList){
        this.notificationList.clear();
        this.notificationList.addAll(notificationsList);
        this.notifyDataSetChanged();
    }

}
