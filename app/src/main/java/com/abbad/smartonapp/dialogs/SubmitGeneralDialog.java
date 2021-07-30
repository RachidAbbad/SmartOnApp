package com.abbad.smartonapp.dialogs;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.StrictMode;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.AppCompatButton;

import com.abbad.smartonapp.R;
import com.abbad.smartonapp.activities.OnInterventionActivity;
import com.abbad.smartonapp.classes.Intervention;
import com.abbad.smartonapp.datas.InterventionData;
import com.abbad.smartonapp.datas.TaskData;
import com.abbad.smartonapp.utils.InterventionManager;
import com.dx.dxloadingbutton.lib.LoadingButton;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.ncorti.slidetoact.SlideToActView;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import me.weyye.hipermission.HiPermission;
import me.weyye.hipermission.PermissionCallback;
import me.weyye.hipermission.PermissionItem;

import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_AUDIO;
import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;
import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;
import static android.view.View.GONE;

public class SubmitGeneralDialog  extends BottomSheetDialogFragment {

    //Task body :
    private TextView intervTitle;
    //Medias Layout :
    private LinearLayout imageLayout,videoLayout,audioLayout,commentLayout,recordResultLayout;
    //Toggles
    private ImageButton imageToggle,videoToggle,audioToggle,commentToggle;
    //Audio Components :
    private ImageButton audioMainButton,audioRemoveBtn,audioSaveBtn;
    private TextView recordCounterMsg,audioSaveStatus;
    private Chronometer audioCounterTimer;
    //Video Components :
    private LinearLayout videoLayoutInput;
    private AppCompatButton videoAddStatus;
    //Image Components :
    private LinearLayout imageLayoutInput;
    private AppCompatButton imageNumber;
    //Comment Components
    private EditText commentEditText;

    //Submit the report
    private AppCompatButton saveCommentBtn;

    //Submit Report
    private SlideToActView submitBtn;

    //MediaRecorder & MediaPlayer
    private MediaRecorder mediaRecorder;
    private MediaPlayer mediaPlayer;
    private int AudioRecordingStatus=0;
    //Task info:
    private Intervention currentIntervention;

    //Captured image index :
    private int imageIndex = 1;

    OnInterventionActivity activity = (OnInterventionActivity) getActivity();
    //Constructors:

    public SubmitGeneralDialog(Intervention intervention){
        currentIntervention = intervention;
    }


    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(@NonNull Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View contentView = View.inflate(getContext(), R.layout.submit_final_report, null);
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
        initViews(contentView);
        setupComponentsEvents();
        this.setCancelable(false);
    }

    @SuppressLint("SetTextI18n")
    public void initViews(View view){
        //Init Layouts :
        imageLayout = view.findViewById(R.id.imageLayout);
        videoLayout = view.findViewById(R.id.videoLayout);
        audioLayout = view.findViewById(R.id.audioLayout);
        commentLayout = view.findViewById(R.id.commentLayout);
        intervTitle = view.findViewById(R.id.task_body);
        intervTitle.setSelected(true);
        recordResultLayout = view.findViewById(R.id.recordResultLayout);
        //Init Audio Components :
        audioMainButton = view.findViewById(R.id.audioButton);
        audioSaveStatus = view.findViewById(R.id.audioSaveStatus);
        recordCounterMsg = view.findViewById(R.id.recordCounterMsg);
        audioCounterTimer = view.findViewById(R.id.recordCounterChrono);
        audioRemoveBtn = view.findViewById(R.id.audioRemoveBtn);
        audioSaveBtn = view.findViewById(R.id.audioSaveBtn);
        //Init Video Components :
        videoLayoutInput = view.findViewById(R.id.videoLayoutInput);
        videoAddStatus = view.findViewById(R.id.videoAddStatus);
        //Init Image componenets :
        imageLayoutInput = view.findViewById(R.id.imageLayoutInput);
        imageNumber = view.findViewById(R.id.numberAdded);
        //Init comment Compos
        commentEditText =view.findViewById(R.id.commentEditText);

        //Submit button :
        saveCommentBtn = view.findViewById(R.id.saveComment);

        //Submit subReport
        submitBtn = view.findViewById(R.id.submitBtn);

        //Set intervention Body :
        intervTitle.setText(currentIntervention.getTitle());

        //Init Toggles
        imageToggle = view.findViewById(R.id.imageToggle);
        videoToggle = view.findViewById(R.id.videoToggle);
        audioToggle = view.findViewById(R.id.audioToggle);
        commentToggle = view.findViewById(R.id.commentToggle);
        //Setup toggles
        imageToggle.getBackground().setAlpha(255);
        videoToggle.getBackground().setAlpha(170);
        commentToggle.getBackground().setAlpha(170);
        audioToggle.getBackground().setAlpha(170);
        setupTogglesEvents();
    }

    public void setupTogglesEvents(){
        imageToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Gone Animation :
                Animation animation = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.slide_in);
                animation.setDuration(500);
                imageLayout.startAnimation(animation);
                //Set Visibility Gone to other layouts
                imageLayout.setVisibility(View.VISIBLE);
                videoLayout.setVisibility(GONE);
                audioLayout.setVisibility(GONE);
                commentLayout.setVisibility(GONE);
                //Set opacity to 0.7 to other toggles

                imageToggle.getBackground().setAlpha(255);
                videoToggle.getBackground().setAlpha(100);
                commentToggle.getBackground().setAlpha(100);
                audioToggle.getBackground().setAlpha(100);

                permissionCameraHanler();
            }
        });

        videoToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation animation = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.slide_in);
                animation.setDuration(500);
                videoLayout.startAnimation(animation);
                //Set Visibility Gone to other layouts
                videoLayout.setVisibility(View.VISIBLE);
                imageLayout.setVisibility(GONE);
                audioLayout.setVisibility(GONE);
                commentLayout.setVisibility(GONE);
                //Set opacity to 0.7 to other toggles

                videoToggle.getBackground().setAlpha(255);
                imageToggle.getBackground().setAlpha(100);
                commentToggle.getBackground().setAlpha(100);
                audioToggle.getBackground().setAlpha(100);

                permissionVideoHanler();


            }
        });

        audioToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation animation = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.slide_in);
                animation.setDuration(500);
                audioLayout.startAnimation(animation);
                //Set Visibility Gone to other layouts
                audioLayout.setVisibility(View.VISIBLE);
                imageLayout.setVisibility(GONE);
                videoLayout.setVisibility(GONE);
                commentLayout.setVisibility(GONE);
                //Set opacity to 0.7 to other toggles

                audioToggle.getBackground().setAlpha(255);
                imageToggle.getBackground().setAlpha(100);
                commentToggle.getBackground().setAlpha(100);
                videoToggle.getBackground().setAlpha(100);

                permissionAudioHanler();

            }
        });

        commentToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation animation = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.slide_in);
                animation.setDuration(500);
                commentLayout.startAnimation(animation);
                //Set Visibility Gone to other layouts
                commentLayout.setVisibility(View.VISIBLE);
                imageLayout.setVisibility(GONE);
                videoLayout.setVisibility(GONE);
                audioLayout.setVisibility(GONE);
                //Set opacity to 0.7 to other toggles

                commentToggle.getBackground().setAlpha(255);
                imageToggle.getBackground().setAlpha(100);
                audioToggle.getBackground().setAlpha(100);
                videoToggle.getBackground().setAlpha(100);

            }
        });
    }

    public void setupComponentsEvents(){
        //Audio section :

        audioMainButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                permissionAudioInputHanler();
            }
        });
        audioRemoveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetAudio();
            }
        });
        audioSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveAudio();
            }
        });

        //Video section :


        videoLayoutInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                permissionVideoInputHanler();
            }
        });

        //Image Section :
        //imageToggle not setting click listenr because we have 2 cases : from Gallery / from Camera => Deffirent permissions
        imageLayoutInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ImageImportMethodeDialog(SubmitGeneralDialog.this).
                        show(getActivity().getSupportFragmentManager(),null);
            }
        });

        saveCommentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    saveComment();
                } catch (IOException exception) {
                    exception.printStackTrace();
                }

            }
        });

        submitBtn.setOnSlideCompleteListener(new SlideToActView.OnSlideCompleteListener() {
            @Override
            public void onSlideComplete(@NotNull SlideToActView slideToActView) {
                //TODO Send Data to The Server (The BACKEND Team are laaate !!)
                InterventionManager.resetAllData(currentIntervention,getActivity());
            }
        });


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1002){
            if (resultCode == Activity.RESULT_OK){
                showResultDialog(getResources().getString(R.string.saveSuccessVideoMsg),1);
                videoLayoutInput.setEnabled(false);
                videoLayoutInput.setAlpha(0.7f);
            }
            else {
                showResultDialog(getResources().getString(R.string.saveCancelVideoMsg),2);
            }
        }
        if (requestCode == 200){
            if(resultCode == Activity.RESULT_OK){
                int imageNb;
                ClipData clipData = data.getClipData();
                imageNb = clipData.getItemCount();
                if (clipData != null) {
                    for (int i = 0; i < imageNb; i++) {
                        ClipData.Item item = clipData.getItemAt(i);
                        Uri uri = item.getUri();
                        try {
                            imageIndex++;
                            File file = getOutputMediaFile(1,currentIntervention.getId()+"_"+imageIndex);
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), item.getUri());

                            ByteArrayOutputStream bos = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100 /*ignored for PNG*/, bos);
                            byte[] bitmapdata = bos.toByteArray();
                            FileOutputStream fos = new FileOutputStream(file);
                            fos.write(bitmapdata);
                            fos.flush();
                            fos.close();
                        }
                        catch (IOException exception){
                            exception.printStackTrace();
                        }
                    }
                }
                showResultDialog(imageNb+" "+getResources().getString(R.string.saveSuccessImagesMsg),1);
            }
            else {
                showResultDialog(getResources().getString(R.string.saveCancelImageMsg),2);
            }

        }
        if (requestCode == 100){
            if (resultCode == Activity.RESULT_OK){
                showResultDialog(getResources().getString(R.string.saveSuccessImageMsg),1);
            }
            else {
                showResultDialog(getResources().getString(R.string.saveCancelImageMsg),2);
            }
        }
    }

    //Camera Methodes :
    public void permissionCameraHanler(){
        List<PermissionItem> permissionItems = new ArrayList<PermissionItem>();
        permissionItems.add(new PermissionItem(Manifest.permission.CAMERA, "Camera", R.drawable.permission_ic_camera));
        HiPermission.create(getActivity())
                .title("Ask for permission")
                .permissions(permissionItems)
                .style(R.style.PermissionBlueStyle)
                .checkMutiPermission(new PermissionCallback() {
                    @Override
                    public void onClose() {
                        Log.i("permission_granted", "onClose");
                    }

                    @Override
                    public void onFinish() {
                    }

                    @Override
                    public void onDeny(String permission, int position) {
                        imageLayoutInput.setEnabled(false);
                    }

                    @Override
                    public void onGuarantee(String permission, int position) {

                    }
                });
    }

    public void permissionCameraInputHanler(){
        List<PermissionItem> permissionItems = new ArrayList<PermissionItem>();
        permissionItems.add(new PermissionItem(Manifest.permission.CAMERA, "Camera", R.drawable.permission_ic_camera));
        HiPermission.create(getActivity())
                .title("Ask for permission")
                .permissions(permissionItems)
                .style(R.style.PermissionBlueStyle)
                .checkMutiPermission(new PermissionCallback() {
                    @Override
                    public void onClose() {
                        Log.i("permission_granted", "onClose");
                    }

                    @Override
                    public void onFinish() {
                        captureImage();
                    }

                    @Override
                    public void onDeny(String permission, int position) {
                        imageLayoutInput.setEnabled(false);
                    }

                    @Override
                    public void onGuarantee(String permission, int position) {
                        captureImage();
                    }
                });
    }

    public void permissionPickHanler(){
        List<PermissionItem> permissionItems = new ArrayList<PermissionItem>();
        permissionItems.add(new PermissionItem(Manifest.permission.CAMERA, "Camera", R.drawable.permission_ic_camera));
        HiPermission.create(getActivity())
                .title("Ask for permission")
                .permissions(permissionItems)
                .style(R.style.PermissionBlueStyle)
                .checkMutiPermission(new PermissionCallback() {
                    @Override
                    public void onClose() {
                        Log.i("permission_granted", "onClose");
                    }

                    @Override
                    public void onFinish() {
                        pickImage();
                    }

                    @Override
                    public void onDeny(String permission, int position) {

                    }

                    @Override
                    public void onGuarantee(String permission, int position) {
                        pickImage();
                    }
                });
    }

    public void resumeTask() throws JSONException, FileNotFoundException {
        if(InterventionManager.getCurrentIntervention().equals(currentIntervention.getId())){
            //Image Section
            imageNumber.setVisibility(View.VISIBLE);
            if (InterventionData.getImages(currentIntervention.getId(),getActivity()).size()!=0){
                imageIndex = InterventionData.getImages(currentIntervention.getId(),getActivity()).size()+1;
                imageNumber.setText(InterventionData.getImages(currentIntervention.getId(),getActivity()).size()+" Images has added");
            }
            else
                imageNumber.setText(getResources().getString(R.string.imagesNotCaptured));
            //Video Section
            videoAddStatus.setVisibility(View.VISIBLE);
            if (InterventionData.getVideos(currentIntervention.getId(),getActivity()).size()!=0){
                videoLayoutInput.setEnabled(false);
                videoAddStatus.setText(getResources().getString(R.string.videoAlreadyRecorded));
            }
            else
                videoAddStatus.setText(getResources().getString(R.string.videoNotRecorded));
            //Audio Section
            audioSaveStatus.setVisibility(View.VISIBLE);
            if (InterventionData.getAudios(currentIntervention.getId(),getActivity()).size()!=0){
                AudioRecordingStatus = 2;
                audioSaveStatus.setText(getResources().getString(R.string.audioAlreadyRecorded));
            }
            else
                audioSaveStatus.setText(getResources().getString(R.string.audioNotRecorded));
            //Comments :
            if (InterventionData.getComments(currentIntervention.getId(),getActivity()).size()!=0){
                Scanner in = new Scanner(new FileReader(InterventionData.getComments(currentIntervention.getId(),getActivity()).get(0)));
                StringBuilder sb = new StringBuilder();
                while(in.hasNext()) {
                    sb.append(in.next());
                }
                in.close();

                commentEditText.setText(sb.toString());
            }
        }
    }

    public void captureImage(){
        imageNumber.setVisibility(GONE);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri fileUri;
        // create a file to save the video
        fileUri = Uri.fromFile(getOutputMediaFile(1,currentIntervention.getId()+"_"+(imageIndex++)));
        // set the image file name
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        startActivityForResult(intent,100);
    }

    public void pickImage(){
        imageNumber.setVisibility(GONE);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 200);
    }

    //Video Methodes :
    public void permissionVideoHanler(){
        List<PermissionItem> permissionItems = new ArrayList<PermissionItem>();
        permissionItems.add(new PermissionItem(Manifest.permission.CAMERA, "Camera", R.drawable.permission_ic_camera));
        HiPermission.create(getActivity())
                .title("Ask for permission")
                .permissions(permissionItems)
                .style(R.style.PermissionBlueStyle)
                .checkMutiPermission(new PermissionCallback() {
                    @Override
                    public void onClose() {
                        Log.i("permission_granted", "onClose");
                    }

                    @Override
                    public void onFinish() {

                    }

                    @Override
                    public void onDeny(String permission, int position) {
                        videoLayoutInput.setEnabled(false);
                    }

                    @Override
                    public void onGuarantee(String permission, int position) {

                    }
                });
    }

    public void permissionVideoInputHanler(){
        List<PermissionItem> permissionItems = new ArrayList<PermissionItem>();
        permissionItems.add(new PermissionItem(Manifest.permission.CAMERA, "Camera", R.drawable.permission_ic_camera));
        HiPermission.create(getActivity())
                .title("Ask for permission")
                .permissions(permissionItems)
                .style(R.style.PermissionBlueStyle)
                .checkMutiPermission(new PermissionCallback() {
                    @Override
                    public void onClose() {
                        Log.i("permission_granted", "onClose");
                    }

                    @Override
                    public void onFinish() {
                        recordVideo();
                    }

                    @Override
                    public void onDeny(String permission, int position) {
                        videoLayoutInput.setEnabled(false);
                    }

                    @Override
                    public void onGuarantee(String permission, int position) {
                        recordVideo();
                    }
                });
    }

    public void recordVideo(){
        videoAddStatus.setVisibility(GONE);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        Uri fileUri;
        // create a file to save the video
        fileUri = Uri.fromFile(getOutputMediaFile(3,currentIntervention.getId()+"_vid"));

        // set the image file name
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

        // set the video image quality to high
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        startActivityForResult(intent, 1002);
    }

    private File getOutputMediaFile(int type,String FileName){

        File mediaStorageDir = null;
        File mediaFile = null;

        if(type == MEDIA_TYPE_VIDEO) {
            mediaStorageDir = new File(getActivity().getExternalCacheDir(), "FinalVideo");
            // Create the storage directory(MyCameraVideo) if it does not exist
            if (! mediaStorageDir.exists()){
                if (! mediaStorageDir.mkdirs()){
                    new ResultBottomDialog(getResources().getString(R.string.saveFailedVideoMsg),3).show(getActivity().getSupportFragmentManager(),null);
                }
            }
            // For unique video file name appending current timeStamp with file name
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    FileName + ".mp4");
        }
        else if (type == MEDIA_TYPE_IMAGE){
            mediaStorageDir = new File(getActivity().getExternalCacheDir(), "FinalImages");
            // Create the storage directory(MyCameraVideo) if it does not exist
            if (! mediaStorageDir.exists()){
                if (! mediaStorageDir.mkdirs()){
                    new ResultBottomDialog(getResources().getString(R.string.saveFailedImageMsg),3).show(getActivity().getSupportFragmentManager(),null);
                }
            }
            // For unique video file name appending current timeStamp with file name
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    FileName + ".jpg");
        }
        else if (type == MEDIA_TYPE_AUDIO){
            mediaStorageDir = new File(getActivity().getExternalCacheDir(), "FinalAudios");
            // Create the storage directory(MyCameraVideo) if it does not exist
            if (! mediaStorageDir.exists()){
                if (! mediaStorageDir.mkdirs()){
                    new ResultBottomDialog(getResources().getString(R.string.saveFailedAudioMsg),3).show(getActivity().getSupportFragmentManager(),null);

                }
            }
            // For unique video file name appending current timeStamp with file name
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    FileName + ".mp3");
        }

        else if (type == 10){
            mediaStorageDir = new File(getActivity().getExternalCacheDir(), "FinalComments");
            // Create the storage directory(MyCameraVideo) if it does not exist
            if (! mediaStorageDir.exists()){
                if (! mediaStorageDir.mkdirs()){
                    new ResultBottomDialog(getResources().getString(R.string.saveFailedCommentMsg),3).show(getActivity().getSupportFragmentManager(),null);
                }
            }
            // For unique video file name appending current timeStamp with file name
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    FileName + ".txt");
        }
        return mediaFile;
    }
    //Audio Methodes :
    public void permissionAudioHanler(){
        List<PermissionItem> permissionItems = new ArrayList<PermissionItem>();
        permissionItems.add(new PermissionItem(Manifest.permission.RECORD_AUDIO, "Record Audio", R.drawable.permission_ic_micro_phone));
        permissionItems.add(new PermissionItem(Manifest.permission.WRITE_EXTERNAL_STORAGE, "External Storage", R.drawable.permission_ic_storage));
        HiPermission.create(getActivity())
                .title("Ask for permission")
                .permissions(permissionItems)
                .style(R.style.PermissionBlueStyle)
                .checkMutiPermission(new PermissionCallback() {
                    @Override
                    public void onClose() {

                    }

                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onFinish() {

                    }

                    @Override
                    public void onDeny(String permission, int position) {

                    }

                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onGuarantee(String permission, int position) {

                    }
                });
    }

    public void permissionAudioInputHanler(){
        List<PermissionItem> permissionItems = new ArrayList<PermissionItem>();
        permissionItems.add(new PermissionItem(Manifest.permission.RECORD_AUDIO, "Record Audio", R.drawable.permission_ic_micro_phone));
        permissionItems.add(new PermissionItem(Manifest.permission.WRITE_EXTERNAL_STORAGE, "External Storage", R.drawable.permission_ic_storage));
        HiPermission.create(getActivity())
                .title("Ask for permission")
                .permissions(permissionItems)
                .style(R.style.PermissionBlueStyle)
                .checkMutiPermission(new PermissionCallback() {
                    @Override
                    public void onClose() {

                    }

                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onFinish() {
                        manageAudio();
                    }

                    @Override
                    public void onDeny(String permission, int position) {

                    }

                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onGuarantee(String permission, int position) {
                        manageAudio();
                    }
                });
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void manageAudio(){
        if (AudioRecordingStatus==0){
            try {
                AudioRecordingStatus = 1;
                startRecording();
                audioMainButton.animate().alpha(0.2f).setDuration(400);
                audioMainButton.animate().alpha(1).setDuration(400);
                audioMainButton.setImageResource(R.drawable.ic_stop_recording);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }
        else if (AudioRecordingStatus==1){
            stopRecording();
            AudioRecordingStatus = 2;
            audioMainButton.animate().alpha(0.2f).setDuration(400);
            audioMainButton.animate().alpha(1).setDuration(400);
            audioMainButton.setImageResource(R.drawable.ic_rounded_mic);
            audioSaveStatus.setVisibility(View.VISIBLE);
            return;
        }
        else if(AudioRecordingStatus==2){
            new ResultBottomDialog(getResources().getString(R.string.audioAlreadyRecorded),3).show(getActivity().getSupportFragmentManager(),null);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void startRecording() throws IOException {

        recordCounterMsg.setVisibility(GONE);
        audioSaveStatus.setVisibility(GONE);
        audioCounterTimer.setVisibility(View.VISIBLE);
        audioCounterTimer.setBase(SystemClock.elapsedRealtime());
        audioCounterTimer.start();
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setOutputFile(getOutputMediaFile(2,currentIntervention.getId()+"_aud"));
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        try {
            mediaRecorder.prepare();
        } catch (IOException e) {
            Log.e(OnInterventionActivity.class.getSimpleName() + ":startRecording()", "prepare() failed");
        }

        mediaRecorder.start();
    }

    public void stopRecording() {
        if (mediaRecorder != null) {
            audioCounterTimer.stop();
            mediaRecorder.stop();
            Log.e(OnInterventionActivity.class.getSimpleName() + ":stopRecording()", "Recording stoped");
            recordResultLayout.animate().alpha(1).setDuration(700);

            recordResultLayout.setVisibility(View.VISIBLE);
        }
    }

    public void resetAudio(){
        AudioRecordingStatus = 0;
        audioSaveStatus.setText(getResources().getString(R.string.saveCancelAudioMsg));
        recordResultLayout.animate().alpha(0).setDuration(700);
        audioCounterTimer.setVisibility(GONE);
        recordResultLayout.setVisibility(GONE);
        recordCounterMsg.setVisibility(View.VISIBLE);
        recordCounterMsg.setText(getResources().getString(R.string.pressToRecordAudio));
        recordResultLayout.setVisibility(GONE);


    }

    public void saveAudio(){
        if (mediaRecorder != null) {
            recordResultLayout.animate().alpha(0).setDuration(700);
            recordResultLayout.setVisibility(GONE);
            mediaRecorder.release();
            mediaRecorder = null;
            showResultDialog(getResources().getString(R.string.saveSuccessAudioMsg),1);
            audioSaveStatus.setVisibility(GONE);
        }
    }

    //Comments Methodes :
    public void saveComment() throws IOException {
        if (commentEditText.getText().toString().length() != 0 || !commentEditText.getText().toString().equals("")){
            File commentFile = getOutputMediaFile(10,currentIntervention.getId()+"_comment");
            FileOutputStream out = new FileOutputStream(commentFile);
            out.write(commentEditText.getText().toString().getBytes());
            out.close();
        }
        else
            showResultDialog(getResources().getString(R.string.fillCommentToSubmit),2);

    }

    //
    public void showResultDialog(String content,int type){
        new ResultBottomDialog(content,type).show(getActivity().getSupportFragmentManager(),null);
    }

    //TODO Handel read external storage exception in saving comments
}
