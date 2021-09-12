package com.abbad.smartonapp.adapters;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;


import com.abbad.smartonapp.R;
import com.abbad.smartonapp.activities.HistoriqueInteventions;
import com.abbad.smartonapp.activities.InterventionDetails;
import com.abbad.smartonapp.activities.MainActivity;
import com.abbad.smartonapp.classes.Intervention;
import com.abbad.smartonapp.datas.InterventionData;

import java.util.ArrayList;
import java.util.List;

public class RecycleViewAdapter extends RecyclerView.Adapter<RecycleViewAdapter.Viewholder> {

    public List<Intervention> list_intervention;
    private TextView title;
    private TextView date;
    private CardView garvity;
    private FragmentManager fragmentManager;
    private MainActivity mainActivity;
    private HistoriqueInteventions historiqueInteventions;

    public RecycleViewAdapter(MainActivity activity, List<Intervention> list_intervention){
        this.list_intervention = list_intervention;
        this.fragmentManager = activity.getSupportFragmentManager();
        mainActivity = activity;
    }

    public RecycleViewAdapter(HistoriqueInteventions activity, List<Intervention> list_intervention){
        this.list_intervention = list_intervention;
        this.fragmentManager = activity.getSupportFragmentManager();
        historiqueInteventions = activity;
    }

    public RecycleViewAdapter(MainActivity activity){
        mainActivity = activity;
        list_intervention = new ArrayList<>();
    }


    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.intervention_item, parent, false);
        Viewholder viewholder = new Viewholder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewholder.onClick(view);
            }
        });
        return viewholder;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {
        // to set data to textview and imageview of each card layout
        holder.setIsRecyclable(false);
        Intervention intervention = list_intervention.get(position);
        title.setText(intervention.getTitle());
        date.setText(intervention.getDate());


        switch (intervention.getType()){
            case "corrective":
            case "preventive":
                garvity.setBackgroundResource(R.color.danger2);
                break;
            case "préventive":
                garvity.setBackgroundResource(R.color.danger5);
                break;
            default:
                garvity.setBackgroundResource(R.color.danger1);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return list_intervention.size();
    }


    public class Viewholder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public Viewholder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.interv_title);
            date = itemView.findViewById(R.id.interv_date);
            garvity = itemView.findViewById(R.id.interv_gravity);
            title.setSelected(true);
        }

        @Override
        public void onClick(View v) {
            Intent intent = null;
            InterventionData.currentIntervention = list_intervention.get(getLayoutPosition());
            if (mainActivity != null) {
                intent = new Intent(mainActivity, InterventionDetails.class);
                mainActivity.startActivity(intent);
            }else if (historiqueInteventions != null) {
                intent = new Intent(historiqueInteventions, InterventionDetails.class);
                intent.putExtra("isPreview",true);
                historiqueInteventions.startActivity(intent);
            }

        }
    }

    public void setList_intervention(List<Intervention> list_intervention){
        Log.e("ListInterv",list_intervention.size()+"");
        this.list_intervention.clear();
        this.list_intervention.addAll(list_intervention);
        this.notifyDataSetChanged();
    }

}
