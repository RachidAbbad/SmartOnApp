package com.abbad.smartonapp.activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.abbad.smartonapp.R;
import com.abbad.smartonapp.classes.Intervention;
import com.abbad.smartonapp.classes.Task;
import com.abbad.smartonapp.classes.User;
import com.abbad.smartonapp.datas.InterventionData;
import com.abbad.smartonapp.dialogs.ResultBottomDialog;
import com.abbad.smartonapp.dialogs.SubmitGeneralDialog;
import com.abbad.smartonapp.utils.InterventionManager;
import com.ncorti.slidetoact.SlideToActView;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;

import java.util.List;

public class InterventionDetails extends AppCompatActivity {

    private Intervention intervention;
    private LinearLayout tasksLayout,toolsLayout,materialsLayout;
    private TextView inter_title,interv_date,collaborateurs;
    private SlideToActView confirmInterv;

    private LinearLayout resumeLayout;
    private AppCompatButton resumeBtn;

    private boolean isPreview = false;

    public InterventionDetails(Intervention intervention){
        this.intervention = intervention;
    }

    public InterventionDetails( ){

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.intervention_item_dialog);

        tasksLayout = findViewById(R.id.tasks);
        toolsLayout = findViewById(R.id.tools);
        materialsLayout = findViewById(R.id.materialsLayout);
        inter_title = findViewById(R.id.interv_name);
        interv_date = findViewById(R.id.date_interv);
        collaborateurs = findViewById(R.id.collaborateurs);

        confirmInterv = findViewById(R.id.confirmInterv);
        resumeLayout = findViewById(R.id.resumeLayout);
        resumeBtn = findViewById(R.id.resumeBtn);
        //inter_id = findViewById(R.id.interv_id);
        if (InterventionData.currentIntervention != null)
            intervention = InterventionData.currentIntervention;
        else
            intervention = InterventionData.getInterventionById(InterventionManager.getCurrentIntervention());

        inter_title.setText(intervention.getTitle());
        interv_date.setText(intervention.getDate());
        confirmInterv.setOnSlideCompleteListener(new InterventionDetails.IntervConfirmedEvent());
        //inter_id.append(intervention.getId());
        displayTasks(intervention.getListTaches(),tasksLayout);
        displayMaterials(intervention.getListMaterials(),toolsLayout);
        displayTools(intervention.getListOutils(),toolsLayout);
        displayCollaborateurs(intervention.getCollaboraters(),toolsLayout);


        isPreview = getIntent().getBooleanExtra("isPreview",false);
        if (isPreview){
            resumeLayout.setVisibility(View.GONE);
            confirmInterv.setVisibility(View.GONE);
        }else {
            try {
                checkInCompletedIntervention();
            } catch (JSONException e) {
                new ResultBottomDialog(getResources().getString(R.string.errorOccured),3).show(getSupportFragmentManager(),null);
            }
        }
    }

    private void displayCollaborateurs(List<User> collaboraters, LinearLayout toolsLayout) {
            collaborateurs.append(intervention.getNomResponsableExecutif());
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void displayMaterials(List<String> listMaterials, LinearLayout toolsLayout) {
        TextView toolView = new TextView(getApplicationContext());
        materialsLayout.addView(toolView);
        for (int i=0;i<listMaterials.size();i++) {
            toolView.append(listMaterials.get(i));
            toolView.setTextAppearance(R.style.TaskBodyInterventionStyle);

            if (i+1 != listMaterials.size())
                toolView.append(" - ");
            else
                toolView.append(".");

        }
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    private void displayTasks(List<Task> tasks, LinearLayout tasksLayout){
        for (int i=0;i<tasks.size();i++) {
            TextView taskNumView = new TextView(getApplicationContext());
            taskNumView.setTextAppearance(R.style.TaskTitlesInterventionStyle);
            taskNumView.setText(getResources().getString(R.string.task)+ (i+1) +" : "+tasks.get(i).getTitleTache());
            tasksLayout.addView(taskNumView);
            for (int k=0;k<tasks.get(i).getListEquipements().size();k++) {
                TextView taskView = new TextView(getApplicationContext());
                if (k != 0)
                    taskView.append("\n");
                taskView.setText(tasks.get(i).getListEquipements().get(k)+" : ");
                taskView.append(tasks.get(i).getListActions().get(k));
                taskView.setTextAppearance(R.style.TaskBodyInterventionStyle);
                tasksLayout.addView(taskView);
            }
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void displayTools(List<String> tools, LinearLayout tasksLayout){
        TextView toolView = new TextView(getApplicationContext());
        tasksLayout.addView(toolView);
        for (int i=0;i<tools.size();i++) {
            toolView.append(tools.get(i));
            toolView.setTextAppearance(R.style.TaskBodyInterventionStyle);

            if (i+1 != tools.size())
                toolView.append(" - ");
            else
                toolView.append(".");

        }
    }

    public class IntervConfirmedEvent implements SlideToActView.OnSlideCompleteListener {
        @SuppressLint("ShowToast")
        @Override
        public void onSlideComplete(@NotNull SlideToActView slideToActView) {
            if (InterventionManager.getInterventionReport(intervention.getId())){
                new SubmitGeneralDialog(intervention).show(getSupportFragmentManager(),null);
            }
            else{
                Intent intent= new Intent(InterventionDetails.this, OnInterventionActivity.class);
                InterventionData.currentIntervention = intervention;
                startActivity(intent);
            }
        }
    }

    public void checkInCompletedIntervention() throws JSONException {
        String idInterv = InterventionManager.getCurrentIntervention();
        if (idInterv !=null){
            if (idInterv.equals(intervention.getId())){
                confirmInterv.setText("Glisser pour continuer");
            }
            else{
                confirmInterv.setVisibility(View.GONE);
                resumeLayout.setVisibility(View.VISIBLE);
                resumeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent= new Intent(InterventionDetails.this, InterventionDetails.class);
                        InterventionData.currentIntervention = InterventionData.getInterventionById(idInterv);
                        startActivity(intent);
                        InterventionDetails.this.finish();
                    }
                });
            }

        }
    }
}