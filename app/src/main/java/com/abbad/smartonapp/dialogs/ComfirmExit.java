package com.abbad.smartonapp.dialogs;

import android.app.Dialog;
import android.content.Intent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;

import com.abbad.smartonapp.R;
import com.abbad.smartonapp.activities.InterventionDetails;
import com.abbad.smartonapp.datas.InterventionData;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class ComfirmExit extends BottomSheetDialogFragment {

    private AppCompatButton okBtn,cancelBtn;

    @Override
    public void setupDialog(@NonNull Dialog dialog, int style) {
        View contentView = View.inflate(getContext(), R.layout.comfirm_exit_layout, null);
        okBtn = contentView.findViewById(R.id.resumeBtn);
        cancelBtn = contentView.findViewById(R.id.cancelBtn);
        dialog.setContentView(contentView);
        ((View) contentView.getParent()).setBackgroundColor(getResources().getColor(android.R.color.transparent));
        dialog.setContentView(contentView);

        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finishAndRemoveTask();
                System.exit(0);
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ComfirmExit.this.dismiss();
            }
        });
    }
}
