package com.abbad.smartonapp.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.abbad.smartonapp.R;
import com.abbad.smartonapp.activities.InterventionDetails;
import com.abbad.smartonapp.activities.ListReports;
import com.abbad.smartonapp.activities.PreviewReport;
import com.abbad.smartonapp.classes.Notification;
import com.abbad.smartonapp.classes.Report;
import com.abbad.smartonapp.datas.InterventionData;

import java.util.List;

public class ReportsAdapter extends RecyclerView.Adapter<ReportsAdapter.Viewholder> {
    public List<Report> reportsList;
    private TextView mainTitle, detailText, notifTime;
    private CardView leftColor;
    //private LinearLayout detailLayout;
    private Activity activity;

    public ReportsAdapter(List<Report> reportsList, ListReports activity) {
        this.reportsList = reportsList;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ReportsAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_item_layout, parent, false);
        ReportsAdapter.Viewholder viewholder = new ReportsAdapter.Viewholder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewholder.onClick(view);
            }
        });
        return viewholder;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull ReportsAdapter.Viewholder holder, int position) {
        holder.setIsRecyclable(false);
        Report report = reportsList.get(position);
        mainTitle.setText(report.getNomIntervention());
        if (report.getEtat()) {
            detailText.setText("En Cours");
            detailText.setTextColor(activity.getResources().getColor(R.color.danger2, null));
        } else {
            detailText.setText("Validé");
            detailText.setTextColor(activity.getResources().getColor(R.color.danger5, null));
        }
        notifTime.setText(report.getDateValidation());
        if (position % 2 == 0) {
            leftColor.setBackgroundResource(R.color.secondColor);
        } else {
            leftColor.setBackgroundResource(R.color.firstColor);
        }
    }


    @Override
    public int getItemCount() {
        return reportsList.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public Viewholder(@NonNull View itemView) {
            super(itemView);
            mainTitle = itemView.findViewById(R.id.mainTitle);
            detailText = itemView.findViewById(R.id.detailText);
            notifTime = itemView.findViewById(R.id.notifTime);
            leftColor = itemView.findViewById(R.id.leftColor);
            //detailLayout = itemView.findViewById(R.id.detailLayout);
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(activity, PreviewReport.class);
            Log.e("ReportInfo",reportsList.get(getLayoutPosition()).getIdReport()+" "+getLayoutPosition());
            intent.putExtra("idReport", reportsList.get(getLayoutPosition()).getIdReport());
            activity.startActivity(intent);
        }

    }

}

