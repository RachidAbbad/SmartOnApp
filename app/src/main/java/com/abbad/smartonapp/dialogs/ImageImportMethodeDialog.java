package com.abbad.smartonapp.dialogs;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Build;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;

import com.abbad.smartonapp.Fragments.TaskFragment;
import com.abbad.smartonapp.R;
import com.abbad.smartonapp.activities.OnInterventionActivity;
import com.abbad.smartonapp.utils.Comun;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class ImageImportMethodeDialog extends BottomSheetDialogFragment {
    private CardView chooseGallery,chooseCapture;

    private TaskFragment fragment;
    private SubmitGeneralDialog submitGeneralDialog;

    public ImageImportMethodeDialog(TaskFragment fragment){
        this.fragment = fragment;
    }

    public ImageImportMethodeDialog(SubmitGeneralDialog submitGeneralDialog){
        this.submitGeneralDialog = submitGeneralDialog;
    }


    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(@NonNull Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View contentView = View.inflate(getContext(), R.layout.image_input_layout, null);
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
        dialog.setContentView(contentView);
        chooseGallery = contentView.findViewById(R.id.chooseGallery);
        chooseCapture = contentView.findViewById(R.id.chooseCapture);

        chooseGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (submitGeneralDialog==null)
                    fragment.permissionPickHanler();
                else
                    submitGeneralDialog.permissionPickHanler();
                ImageImportMethodeDialog.this.dismiss();
            }
        });

        chooseCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (submitGeneralDialog==null)
                    fragment.permissionCameraInputHanler();
                else
                    submitGeneralDialog.permissionCameraInputHanler();
                ImageImportMethodeDialog.this.dismiss();
            }
        });

    }




}
