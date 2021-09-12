package com.abbad.smartonapp.dialogs;

import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;

import com.abbad.smartonapp.R;
import com.abbad.smartonapp.activities.InterventionDetails;
import com.abbad.smartonapp.activities.OnInterventionActivity;
import com.abbad.smartonapp.classes.Intervention;
import com.abbad.smartonapp.datas.InterventionData;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class InterventionResumeDialog extends BottomSheetDialogFragment {

    private Intervention intervention;
    //View Components :
    private AppCompatButton resumeBtn,cancelBtn;

    public InterventionResumeDialog(Intervention intervention) {
        this.intervention = intervention;
    }

    @Override
    public void setupDialog(@NonNull Dialog dialog, int style) {
        View contentView = View.inflate(getContext(), R.layout.intervention_resume_layout, null);
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
        resumeBtn = contentView.findViewById(R.id.resumeBtn);
        cancelBtn = contentView.findViewById(R.id.cancelBtn);
        dialog.setContentView(contentView);
        ((View) contentView.getParent()).setBackgroundColor(getResources().getColor(android.R.color.transparent));
        dialog.setContentView(contentView);

        resumeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(getActivity(), InterventionDetails.class);
                InterventionData.currentIntervention = intervention;
                getActivity().startActivity(intent);
                InterventionResumeDialog.this.dismiss();
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InterventionResumeDialog.this.dismiss();
            }
        });
    }
}
