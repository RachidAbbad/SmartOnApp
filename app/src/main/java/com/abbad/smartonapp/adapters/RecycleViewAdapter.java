package com.abbad.smartonapp.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;


import com.abbad.smartonapp.R;
import com.abbad.smartonapp.classes.Intervention;
import com.abbad.smartonapp.dialogs.InterventionDetailsDialog;

import java.util.ArrayList;
import java.util.List;

public class RecycleViewAdapter extends RecyclerView.Adapter<RecycleViewAdapter.Viewholder> {

    private List<Intervention> list_intervention;
    private TextView title;
    private TextView date;
    private CardView garvity;
    private FragmentManager fragmentManager;

    public RecycleViewAdapter(FragmentManager fragmentManager,List<Intervention> list_intervention){
        this.list_intervention = list_intervention;
        this.fragmentManager = fragmentManager;
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
        Intervention intervention = list_intervention.get(position);
        title.setText(intervention.getTitle());
        date.setText(intervention.getDate());

        switch (intervention.getGravity()){
            case 1:
                garvity.setBackgroundResource(R.color.danger1);
                break;
            case 2:
                garvity.setBackgroundResource(R.color.danger2);
                break;
            case 3:
                garvity.setBackgroundResource(R.color.danger3);
                break;
            case 4:
                garvity.setBackgroundResource(R.color.danger4);
                break;
            default:
                garvity.setBackgroundResource(R.color.danger5);
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
        }

        @Override
        public void onClick(View v) {
            InterventionDetailsDialog interventionDetailsDialog = new InterventionDetailsDialog(list_intervention.get(getLayoutPosition()));
            interventionDetailsDialog.setCancelable(true);
            interventionDetailsDialog.show(fragmentManager,"");
        }
    }

}
