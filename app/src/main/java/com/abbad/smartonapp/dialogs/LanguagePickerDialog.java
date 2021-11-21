package com.abbad.smartonapp.dialogs;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Build;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;

import com.abbad.smartonapp.R;
import com.abbad.smartonapp.utils.Comun;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class LanguagePickerDialog extends BottomSheetDialogFragment {
    CardView english_btn,french_btn;
    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(@NonNull Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View contentView = View.inflate(getContext(), R.layout.fragment_language_picker, null);
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
        english_btn = contentView.findViewById(R.id.english_btn);
        french_btn = contentView.findViewById(R.id.french_btn);

        english_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Comun.setLocale(LanguagePickerDialog.this.getActivity(), "en");
                LanguagePickerDialog.this.getActivity().finish();
                LanguagePickerDialog.this.getActivity().startActivity(getActivity().getIntent());

            }
        });

        french_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Comun.setLocale(LanguagePickerDialog.this.getActivity(), "fr");
                LanguagePickerDialog.this.getActivity().finish();
                LanguagePickerDialog.this.getActivity().startActivity(getActivity().getIntent());


            }
        });

    }




}
