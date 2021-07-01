package com.abbad.smartonapp.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;


import com.abbad.smartonapp.R;
import com.abbad.smartonapp.activities.MainActivity;
import com.abbad.smartonapp.classes.Intervention;

import java.util.ArrayList;
import java.util.List;

public class RecycleViewAdapter extends RecyclerView.Adapter<RecycleViewAdapter.Viewholder> {

    private List<Intervention> list_intervention;
    private TextView title;
    private TextView date;
    private CardView garvity;
    private FragmentManager fragmentManager;

    public RecycleViewAdapter(FragmentManager fragmentManager){
        list_intervention = new ArrayList<>();
        list_intervention.add(new Intervention("la dépose de la buse de fumée du raccordement au conduit"
                ,"02-06-2021"
                ,3
                ,new String[]{"le nettoyage intérieur avec repose après travaux","le nettoyage des éléments et du local après intervention"}
                ,new String[]{"Tournevis","lunettes de protection"}
                ));
        list_intervention.add(new Intervention("Effectuer une calibration des contrôles"
                ,"15-06-2021"
                ,4
                ,new String[]{"Vérifiez les pompes","la lubrification","le fonctionnement du moteur","l’alignement"}
                ,new String[]{"Tournevis","lunettes de protection"}
        ));
        list_intervention.add(new Intervention("Vérification le système de contrôle"
                ,"02-06-2021"
                ,1
                ,new String[]{"les sondes de température intérieure et extérieure","les dispositifs de sécurité","la valve de relâche haute pression et haute température"}
                ,new String[]{"Tournevis","lunettes de protection"}
        ));
        list_intervention.add(new Intervention("Vérification du cheminée (évacuation des fumées de combustion)"
                ,"29-05-2021"
                ,3
                ,new String[]{"les échangeurs de chaleur","l’alimentation de gaz vers la chaudière","les fuites de gaz"}
                ,new String[]{"Tournevis","lunettes de protection"}
        ));
        list_intervention.add(new Intervention("la dépose de la buse de fumée du raccordement au conduit"
                ,"19-06-2021"
                ,3
                ,new String[]{"le nettoyage intérieur avec repose après travaux","le nettoyage des éléments et du local après intervention"}
                ,new String[]{"Tournevis","lunettes de protection"}
        ));
        list_intervention.add(new Intervention("Effectuer une calibration des contrôles"
                ,"26-05-2021"
                ,4
                ,new String[]{"Vérifiez les pompes","la lubrification","le fonctionnement du moteur","l’alignement"}
                ,new String[]{"Tournevis","lunettes de protection"}
        ));
        list_intervention.add(new Intervention("Vérification le système de contrôle"
                ,"18-06-2021"
                ,1
                ,new String[]{"les sondes de température intérieure et extérieure","les dispositifs de sécurité","la valve de relâche haute pression et haute température"}
                ,new String[]{"Tournevis","lunettes de protection"}
        ));
        list_intervention.add(new Intervention("Vérification du cheminée (évacuation des fumées de combustion)"
                ,"14-06-2021"
                ,5
                ,new String[]{"les échangeurs de chaleur","l’alimentation de gaz vers la chaudière","les fuites de gaz"}
                ,new String[]{"Tournevis","lunettes de protection"}
        ));
        list_intervention.add(new Intervention("la dépose de la buse de fumée du raccordement au conduit"
                ,"02-06-2021"
                ,4
                ,new String[]{"le nettoyage intérieur avec repose après travaux","le nettoyage des éléments et du local après intervention"}
                ,new String[]{"Tournevis","lunettes de protection"}
        ));
        list_intervention.add(new Intervention("Effectuer une calibration des contrôles"
                ,"09-06-2021"
                ,5
                ,new String[]{"Vérifiez les pompes","la lubrification","le fonctionnement du moteur","l’alignement"}
                ,new String[]{"Tournevis","lunettes de protection"}
        ));
        list_intervention.add(new Intervention("Vérification le système de contrôle"
                ,"14-06-2021"
                ,1
                ,new String[]{"les sondes de température intérieure et extérieure","les dispositifs de sécurité","la valve de relâche haute pression et haute température"}
                ,new String[]{"Tournevis","lunettes de protection"}
        ));
        list_intervention.add(new Intervention("Vérification du cheminée (évacuation des fumées de combustion)"
                ,"30-05-2021"
                ,3
                ,new String[]{"les échangeurs de chaleur","l’alimentation de gaz vers la chaudière","les fuites de gaz"}
                ,new String[]{"Tournevis","lunettes de protection"}
        ));
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
