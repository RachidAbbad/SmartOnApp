package com.abbad.smartonapp.dialogs;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.abbad.smartonapp.R;
import com.abbad.smartonapp.classes.Intervention;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.ncorti.slidetoact.SlideToActView;

import org.jetbrains.annotations.NotNull;

public class InterventionDetailsDialog extends BottomSheetDialogFragment {

    private Intervention intervention;
    private LinearLayout tasksLayout,toolsLayout;
    private TextView inter_title,inter_desc;
    private SlideToActView confirmInterv;

    public InterventionDetailsDialog(Intervention intervention) {
        this.intervention = intervention;
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(@NonNull Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View contentView = View.inflate(getContext(), R.layout.intervention_item_dialog, null);
        contentView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT < 16) {
                    contentView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                } else {
                    contentView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
                BottomSheetDialog dialog = (BottomSheetDialog) getDialog();
                FrameLayout bottomSheet = (FrameLayout)
                        dialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
                BottomSheetBehavior behavior = BottomSheetBehavior.from(bottomSheet);
                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                behavior.setPeekHeight(0); // Remove this line to hide a dark background if you manually hide the dialog.
            }
        });
        dialog.setContentView(contentView);
        ((View) contentView.getParent()).setBackgroundColor(getResources().getColor(android.R.color.transparent));
        tasksLayout = contentView.findViewById(R.id.tasks);
        toolsLayout = contentView.findViewById(R.id.tools);
        inter_desc = contentView.findViewById(R.id.desc);
        inter_title = contentView.findViewById(R.id.interv_name);
        confirmInterv = contentView.findViewById(R.id.confirmInterv);
        inter_title.setText(intervention.getTitle());
        inter_desc.setText("Lorem fefh diz fuziuf fzorf bviru");
        confirmInterv.setOnSlideCompleteListener(new IntervConfirmedEvent());
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
        for (int i=0;i<tools.length;i++) {
            TextView toolView = new TextView(getContext().getApplicationContext());
            TextView space = new TextView(getContext().getApplicationContext());
            toolView.setText(tools[i]);
            space.setText(" - ");
            toolView.setTextAppearance(R.style.TaskBodyInterventionStyle);
            tasksLayout.addView(toolView);
            if (i+1 != tools.length)
                tasksLayout.addView(space);

        }
    }
    private void displayDesc(String desc){

    }

    public class IntervConfirmedEvent implements SlideToActView.OnSlideCompleteListener {



        @SuppressLint("ShowToast")
        @Override
        public void onSlideComplete(@NotNull SlideToActView slideToActView) {
            Toast.makeText(getContext(),"Intervention confirmed",Toast.LENGTH_LONG);
            Log.i("slideCompleted","Intervention confirmed");
            InterventionDetailsDialog.this.dismiss();
        }
    }
}
