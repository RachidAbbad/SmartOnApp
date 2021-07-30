package com.abbad.smartonapp.dialogs;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Build;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.abbad.smartonapp.R;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class ResultBottomDialog extends BottomSheetDialogFragment {
    private String content;
    private int eventType;

    public ResultBottomDialog(String content,int eventType){
        this.content = content;
        this.eventType = eventType;
    }



    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(@NonNull Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View contentView = View.inflate(getContext(), R.layout.result_dialog_layout, null);
        ImageView resultIcon = contentView.findViewById(R.id.resultIcon);


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

        ((TextView) contentView.findViewById(R.id.text_content)).setText(content);
        dialog.setContentView(contentView);
        ((View) contentView.getParent()).setBackgroundColor(getResources().getColor(android.R.color.transparent));
        dialog.setContentView(contentView);

        if (eventType==1)
            resultIcon.setImageResource(R.drawable.ic_done);
        else if (eventType == 2)
            resultIcon.setImageResource(R.drawable.ic_warning);
        else
            resultIcon.setImageResource(R.drawable.ic_errorr);
    }


}
