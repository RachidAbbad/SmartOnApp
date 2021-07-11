package com.abbad.smartonapp.Fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.LauncherApps;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import com.abbad.smartonapp.R;
import com.abbad.smartonapp.activities.OnInterventionActivity;
import com.abbad.smartonapp.classes.Task;
import com.abbad.smartonapp.dialogs.ImageImportMethodeDialog;
import com.abbad.smartonapp.dialogs.ResultBottomDialog;
import com.abbad.smartonapp.ui.dashboard.DashboardFragment;
import com.google.android.material.tabs.TabLayout;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import me.weyye.hipermission.HiPermission;
import me.weyye.hipermission.PermissionCallback;
import me.weyye.hipermission.PermissionItem;

import static android.icu.text.DateTimePatternGenerator.PatternInfo.OK;
import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_AUDIO;
import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;
import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;

public class TaskFragment extends Fragment {
    //View Components:
    private TextView taskTitle;
        //Task body :
        private TextView taskBody;
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
        //Comment Components
        private EditText commentEditText;

    //Submit the report
    private AppCompatButton submitReport;

    //MediaRecorder & MediaPlayer
    private MediaRecorder mediaRecorder;
    private MediaPlayer mediaPlayer;
    private int AudioRecordingStatus=0;
    //Task info:
    private Task currentTask;

    //Captured image index :
    private int imageIndex = 1;

    OnInterventionActivity activity = (OnInterventionActivity) getActivity();
    //Constructors:
    public TaskFragment(Task task){
        currentTask = task;
    }

    public TaskFragment(){ }



    //Methodes
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.task_rapport, container, false);
        initViews(view);
        setupComponentsEvents();
        return view;
    }

    @SuppressLint("SetTextI18n")
    public void initViews(View view){
        //Init Layouts :
        imageLayout = view.findViewById(R.id.imageLayout);
        videoLayout = view.findViewById(R.id.videoLayout);
        audioLayout = view.findViewById(R.id.audioLayout);
        commentLayout = view.findViewById(R.id.commentLayout);
        taskBody = view.findViewById(R.id.task_body);
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

        //Submit button :
        submitReport = view.findViewById(R.id.submitReport);
        //Set Task Body :
        currentTask = getArguments().getParcelable("task");
        taskBody.setText(currentTask.getBody());
        //Init Toggles :
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
                videoLayout.setVisibility(View.GONE);
                audioLayout.setVisibility(View.GONE);
                commentLayout.setVisibility(View.GONE);
                //Set opacity to 0.7 to other toggles

                imageToggle.getBackground().setAlpha(255);
                videoToggle.getBackground().setAlpha(100);
                commentToggle.getBackground().setAlpha(100);
                audioToggle.getBackground().setAlpha(100);
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
                imageLayout.setVisibility(View.GONE);
                audioLayout.setVisibility(View.GONE);
                commentLayout.setVisibility(View.GONE);
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
                imageLayout.setVisibility(View.GONE);
                videoLayout.setVisibility(View.GONE);
                commentLayout.setVisibility(View.GONE);
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
                imageLayout.setVisibility(View.GONE);
                videoLayout.setVisibility(View.GONE);
                audioLayout.setVisibility(View.GONE);
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
                manageAudio();
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
                recordVideo();
            }
        });

        //Image Section :
            //imageToggle not setting click listenr because we have 2 cases : from Gallery / from Camera => Deffirent permissions
        imageLayoutInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ImageImportMethodeDialog(TaskFragment.this).
                        show(getActivity().getSupportFragmentManager(),null);
            }
        });

        submitReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //new DashboardFragment().show();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1002){
            videoAddStatus.setVisibility(View.VISIBLE);
            if (resultCode == Activity.RESULT_OK){
                videoAddStatus.setText("Video added successfully");
                videoLayoutInput.setEnabled(false);
                videoLayoutInput.setAlpha(0.7f);
            }
            else {
                videoAddStatus.setText("You cancel the video");
            }
        }
        if (requestCode == 200){
            ClipData clipData = data.getClipData();
            if (clipData != null) {
                for (int i = 0; i < clipData.getItemCount(); i++) {
                    ClipData.Item item = clipData.getItemAt(i);
                    Uri uri = item.getUri();
                    try {
                        File file = getOutputMediaFile(1,currentTask.getBody()+"_"+i);
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
        }
        if (requestCode == 100){

        }
    }

    //Serch for file by name


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
                        captureImage();
                    }

                    @Override
                    public void onDeny(String permission, int position) {

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

    public void captureImage(){
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri fileUri;
        // create a file to save the video
        fileUri = Uri.fromFile(getOutputMediaFile(1,currentTask.getBody()+(imageIndex++)));
        // set the image file name
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        startActivityForResult(intent,100);
    }

    public void pickImage(){
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

                    }

                    @Override
                    public void onGuarantee(String permission, int position) {

                    }
                });
    }

    public void recordVideo(){
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        Uri fileUri;
        // create a file to save the video
        fileUri = Uri.fromFile(getOutputMediaFile(3,currentTask.getBody()));

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
            mediaStorageDir = new File(getActivity().getExternalCacheDir(), "Videos");
            // Create the storage directory(MyCameraVideo) if it does not exist
            if (! mediaStorageDir.exists()){
                if (! mediaStorageDir.mkdirs()){
                    Toast.makeText(getActivity(), "Failed to create directory MyCameraVideo.",
                            Toast.LENGTH_LONG).show();
                }
            }
            // For unique video file name appending current timeStamp with file name
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    FileName + ".mp4");

        } else if (type == MEDIA_TYPE_IMAGE){
            mediaStorageDir = new File(getActivity().getExternalCacheDir(), "Images");
            // Create the storage directory(MyCameraVideo) if it does not exist
            if (! mediaStorageDir.exists()){
                if (! mediaStorageDir.mkdirs()){
                    Toast.makeText(getActivity(), "Failed to create directory MyCameraVideo.",
                            Toast.LENGTH_LONG).show();
                }
            }
            // For unique video file name appending current timeStamp with file name
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    FileName + ".jpg");
        }
        else if (type == MEDIA_TYPE_AUDIO){
            mediaStorageDir = new File(getActivity().getExternalCacheDir(), "Audios");
            // Create the storage directory(MyCameraVideo) if it does not exist
            if (! mediaStorageDir.exists()){
                if (! mediaStorageDir.mkdirs()){
                    Toast.makeText(getActivity(), "Failed to create directory MyCameraVideo.",
                            Toast.LENGTH_LONG).show();
                }
            }
            // For unique video file name appending current timeStamp with file name
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    FileName + ".mp3");
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
            new ResultBottomDialog("You already recorded an audio",false).show(getActivity().getSupportFragmentManager(),null);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void startRecording() throws IOException {
        String audioName = getActivity().getExternalCacheDir().getAbsolutePath() + "/" + currentTask.getBody() + ".mp3";
        Log.i(OnInterventionActivity.class.getSimpleName(), audioName);
        recordCounterMsg.setVisibility(View.GONE);
        audioSaveStatus.setVisibility(View.GONE);
        audioCounterTimer.setVisibility(View.VISIBLE);
        audioCounterTimer.setBase(SystemClock.elapsedRealtime());
        audioCounterTimer.start();
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setOutputFile(getOutputMediaFile(2,currentTask.getBody()));
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        try {
            mediaRecorder.prepare();
        } catch (IOException e) {
            Log.e(OnInterventionActivity.class.getSimpleName() + ":startRecording()", "prepare() failed");
        }

        mediaRecorder.start();
    }

    private void stopRecording() {
        if (mediaRecorder != null) {
            audioCounterTimer.stop();
            mediaRecorder.stop();
            Log.e(OnInterventionActivity.class.getSimpleName() + ":stopRecording()", "Recording stoped");
            recordResultLayout.animate().alpha(1).setDuration(700);

            recordResultLayout.setVisibility(View.VISIBLE);
        }
    }

    private void resetAudio(){
        AudioRecordingStatus = 0;
        audioSaveStatus.setText("Audio removed successfully");
        recordResultLayout.animate().alpha(0).setDuration(700);
        audioCounterTimer.setVisibility(View.GONE);

        recordResultLayout.setVisibility(View.GONE);
        recordCounterMsg.setVisibility(View.VISIBLE);
        recordCounterMsg.setText("Press the button to start recording");
        recordResultLayout.setVisibility(View.GONE);


    }

    private void saveAudio(){
        if (mediaRecorder != null) {
            recordResultLayout.animate().alpha(0).setDuration(700);
            recordResultLayout.setVisibility(View.GONE);
            mediaRecorder.release();
            mediaRecorder = null;
            audioSaveStatus.setVisibility(View.VISIBLE);
            audioSaveStatus.setText("Audio saved successfully");
        }
    }

    //Comments Methodes :

}
