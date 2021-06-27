package com.abbad.smartonapp.adapters;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Configuration;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.abbad.smartonapp.R;
import com.abbad.smartonapp.activities.MainActivity;
import com.abbad.smartonapp.utils.Comun;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.Locale;

public class LanguagePickerDialog extends BottomSheetDialogFragment {
    CardView english_btn,french_btn;
    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(@NonNull Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View contentView = View.inflate(getContext(), R.layout.fragment_language_picker, null);
        dialog.setContentView(contentView);
        ((View) contentView.getParent()).setBackgroundColor(getResources().getColor(android.R.color.transparent));
        dialog.setContentView(contentView);
        english_btn = contentView.findViewById(R.id.english_btn);
        french_btn = contentView.findViewById(R.id.french_btn);

        english_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Comun.setLocale(LanguagePickerDialog.this.getActivity(), "en");
            }
        });

        french_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Comun.setLocale(LanguagePickerDialog.this.getActivity(), "fr");
            }
        });

    }




}
