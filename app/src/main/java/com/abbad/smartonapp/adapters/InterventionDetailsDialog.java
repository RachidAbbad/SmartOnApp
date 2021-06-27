package com.abbad.smartonapp.adapters;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.abbad.smartonapp.R;
import com.abbad.smartonapp.classes.Intervention;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textview.MaterialTextView;

public class InterventionDetailsDialog extends BottomSheetDialogFragment {

    private Intervention intervention;
    private LinearLayout tasksLayout,toolsLayout;
    private TextView inter_title,inter_desc;
    public InterventionDetailsDialog(Intervention intervention) {
        this.intervention = intervention;
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(@NonNull Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View contentView = View.inflate(getContext(), R.layout.intervention_item_dialog, null);
        dialog.setContentView(contentView);
        ((View) contentView.getParent()).setBackgroundColor(getResources().getColor(android.R.color.transparent));
        tasksLayout = contentView.findViewById(R.id.tasks);
        toolsLayout = contentView.findViewById(R.id.tools);
        inter_desc = contentView.findViewById(R.id.desc);
        inter_title = contentView.findViewById(R.id.interv_name);

        inter_title.setText(intervention.getTitle());
                inter_desc.setText("Lorem fefh diz fuziuf fzorf bviru");

        dialog.setContentView(contentView);
        displayTasks(intervention.getTodos(),tasksLayout);
        displayTools(intervention.getTools(),toolsLayout);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void displayTasks(String[] tasks, LinearLayout tasksLayout){
        for (int i=1;i<=tasks.length;i++) {
            TextView taskNumView = new TextView(getContext().getApplicationContext());
            taskNumView.setTextAppearance(R.style.TaskTitlesInterventionStyle);
            taskNumView.setText(getResources().getString(R.string.task)+ i+" :");

            TextView taskView = new TextView(getContext().getApplicationContext());
            taskView.setText(tasks[i-1]);
            taskView.setTextAppearance(R.style.TaskBodyInterventionStyle);
            tasksLayout.addView(taskNumView);
            tasksLayout.addView(taskView);
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void displayTools(String[] tools, LinearLayout tasksLayout){
        for (String tool : tools) {
            TextView toolView = new TextView(getContext().getApplicationContext());
            toolView.setText(tool);
            toolView.setTextAppearance(R.style.TaskBodyInterventionStyle);
            tasksLayout.addView(toolView);
        }
    }
    private void displayDesc(String desc){

    }
}
