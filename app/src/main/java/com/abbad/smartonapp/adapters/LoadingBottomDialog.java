package com.abbad.smartonapp.adapters;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.abbad.smartonapp.R;
import com.dx.dxloadingbutton.lib.LoadingButton;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.w3c.dom.Text;

public class LoadingBottomDialog extends BottomSheetDialogFragment {
    private String content;

    public LoadingBottomDialog(String content){
        this.content = content;
    }


    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(@NonNull Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View contentView = View.inflate(getContext(), R.layout.custom_loading_dialog, null);
        ((TextView) contentView.findViewById(R.id.text_content)).setText(content);
        dialog.setContentView(contentView);
        ((View) contentView.getParent()).setBackgroundColor(getResources().getColor(android.R.color.transparent));
        dialog.setContentView(contentView);
    }
}
