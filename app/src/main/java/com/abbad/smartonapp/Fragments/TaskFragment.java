package com.abbad.smartonapp.Fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
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
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import com.abbad.smartonapp.R;
import com.abbad.smartonapp.activities.OnInterventionActivity;
import com.abbad.smartonapp.classes.Intervention;
import com.abbad.smartonapp.classes.Task;
import com.abbad.smartonapp.datas.ReportData;
import com.abbad.smartonapp.datas.TaskData;
import com.abbad.smartonapp.dialogs.ResultBottomDialog;
import com.abbad.smartonapp.dialogs.SubmitGeneralDialog;
import com.abbad.smartonapp.utils.InterventionManager;
import com.dx.dxloadingbutton.lib.LoadingButton;
import com.rm.rmswitch.RMSwitch;

import org.json.JSONException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import me.weyye.hipermission.HiPermission;
import me.weyye.hipermission.PermissionCallback;
import me.weyye.hipermission.PermissionItem;

import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_AUDIO;
import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;
import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;

public class TaskFragment extends Fragment {
    //View Components:
    private TextView taskTitle;
    //Task body :
    private TextView task_zone;
    private LinearLayout detaillsLayout;
    //Medias Layout :
    private LinearLayout imageLayout, videoLayout, audioLayout, commentLayout, recordResultLayout;
    //Toggles
    private ImageButton imageToggle, videoToggle, audioToggle, commentToggle;
    //Audio Components :
    private ImageButton audioMainButton, audioRemoveBtn, audioSaveBtn;
    private TextView recordCounterMsg, audioSaveStatus;
    private Chronometer audioCounterTimer;
    //Video Components :
    private LinearLayout videoLayoutInput;
    private AppCompatButton videoAddStatus;
    //Image Components :
    private LinearLayout imageLayoutInput;
    private AppCompatButton imageNumber;
    //Comment Components
    private EditText commentEditText;
    //Task status
    private RMSwitch taskStatus;
    private TextView taskStatusText;
    //Submit comment
    private LoadingButton submitComment;
    //MediaRecorder & MediaPlayer
    private MediaRecorder mediaRecorder;
    private int AudioRecordingStatus = 0;
    //Task info:
    private Intervention currentIntervention;
    private Task currentTask;
    private int currentTaskIndex;
    //Captured image index :
    private int imageIndex = 0, videoIndex = 0, audioIndex = 0, commentIndex = 0;
    //Preview Medias :
    private LinearLayout generalVideoPreview, generalImagePreview, generalCommentPreview, generalAudioPreview;
    private GridLayout previewVideo, previewImage;
    private LinearLayout previewComment, previewAudio;
    private TextView noImages, noVideos, noComments, noAudios;

    public TaskFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.task_rapport, container, false);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        initViews(view);
        setupComponentsEvents();
        try {
            resumeTask();
        } catch (JSONException | IOException ex) {
            ex.printStackTrace();
        }

        Log.e("ExternalCache", getActivity().getExternalCacheDir().getAbsolutePath());
        return view;
    }

    @SuppressLint("SetTextI18n")
    public void initViews(View view) {
        //Init Layouts :
        imageLayout = view.findViewById(R.id.imageLayout);
        videoLayout = view.findViewById(R.id.videoLayout);
        audioLayout = view.findViewById(R.id.audioLayout);
        commentLayout = view.findViewById(R.id.commentLayout);
        detaillsLayout = view.findViewById(R.id.detaillsLayout);
        recordResultLayout = view.findViewById(R.id.recordResultLayout);
        //Init Audio Components :
        audioMainButton = view.findViewById(R.id.audioButton);
        audioSaveStatus = view.findViewById(R.id.audioSaveStatus);
        recordCounterMsg = view.findViewById(R.id.recordCounterMsg);
        audioCounterTimer = view.findViewById(R.id.recordCounterChrono);
        audioRemoveBtn = view.findViewById(R.id.audioRemoveBtn);
        audioSaveBtn = view.findViewById(R.id.audioSaveBtn);
        //Preview Layouts
        previewImage = view.findViewById(R.id.preview_image_layout);
        previewVideo = view.findViewById(R.id.preview_video_layout);
        previewComment = view.findViewById(R.id.preview_comment_layout);
        previewAudio = view.findViewById(R.id.preview_audio_layout);
        generalVideoPreview = view.findViewById(R.id.general_preview_video);
        generalImagePreview = view.findViewById(R.id.general_preview_image);
        generalAudioPreview = view.findViewById(R.id.general_preview_audio);
        generalCommentPreview = view.findViewById(R.id.general_preview_comment);
        noImages = view.findViewById(R.id.no_image_file);
        noVideos = view.findViewById(R.id.no_video_file);
        noAudios = view.findViewById(R.id.no_audio_file);
        noComments = view.findViewById(R.id.no_comment_file);

        //Init Video Components :
        videoLayoutInput = view.findViewById(R.id.videoLayoutInput);
        videoAddStatus = view.findViewById(R.id.videoAddStatus);
        //Init Image componenets :
        imageLayoutInput = view.findViewById(R.id.imageLayoutInput);
        imageNumber = view.findViewById(R.id.numberAdded);
        //Init comment Compos
        commentEditText = view.findViewById(R.id.commentEditText);
        //Task Status compos :
        taskStatus = view.findViewById(R.id.taskStatus);
        taskStatusText = view.findViewById(R.id.taskStatusText);
        task_zone = view.findViewById(R.id.task_zone);

        //Submit button :

        //Submit subReport
        submitComment = view.findViewById(R.id.saveComment);

        //Set Task Body :
        currentIntervention = getArguments().getParcelable("intervention");
        currentTask = getArguments().getParcelable("task");
        currentTaskIndex = getArguments().getInt("numTask");
        task_zone.setText(currentTask.getTitleTache());
        for (int k = 0; k < currentTask.getListEquipements().size(); k++) {
            TextView equip = new TextView(getActivity());
            TextView actions = new TextView(getActivity());

            equip.setTypeface(ResourcesCompat.getFont(getActivity(), R.font.catamaran_medium), Typeface.BOLD);
            actions.setTypeface(ResourcesCompat.getFont(getActivity(), R.font.catamaran_medium));

            equip.setTextSize(19);
            actions.setTextSize(19);

            equip.setTextColor(getResources().getColor(R.color.uiTextColor));
            actions.setTextColor(getResources().getColor(R.color.uiTextColor));

            equip.setText(currentTask.getListEquipements().get(k) + " : ");
            actions.setText("- " + currentTask.getListActions().get(k));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(0, 0, 4, 0);
            equip.setLayoutParams(params);

            detaillsLayout.addView(equip);
            detaillsLayout.addView(actions);
        }

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

    public void setupTogglesEvents() {
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

                generalImagePreview.startAnimation(animation);
                generalImagePreview.setVisibility(View.VISIBLE);
                generalVideoPreview.setVisibility(View.GONE);
                generalAudioPreview.setVisibility(View.GONE);
                generalCommentPreview.setVisibility(View.GONE);
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
                imageLayout.setVisibility(View.GONE);
                audioLayout.setVisibility(View.GONE);
                commentLayout.setVisibility(View.GONE);

                generalVideoPreview.startAnimation(animation);
                generalVideoPreview.setVisibility(View.VISIBLE);
                generalImagePreview.setVisibility(View.GONE);
                generalAudioPreview.setVisibility(View.GONE);
                generalCommentPreview.setVisibility(View.GONE);
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

                generalAudioPreview.setAnimation(animation);
                generalAudioPreview.setVisibility(View.VISIBLE);
                generalCommentPreview.setVisibility(View.GONE);
                generalVideoPreview.setVisibility(View.GONE);
                generalImagePreview.setVisibility(View.GONE);
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

                generalCommentPreview.setAnimation(animation);
                generalAudioPreview.setVisibility(View.GONE);
                generalCommentPreview.setVisibility(View.VISIBLE);
                generalVideoPreview.setVisibility(View.GONE);
                generalImagePreview.setVisibility(View.GONE);

                //Set opacity to 0.7 to other toggles

                commentToggle.getBackground().setAlpha(255);
                imageToggle.getBackground().setAlpha(100);
                audioToggle.getBackground().setAlpha(100);
                videoToggle.getBackground().setAlpha(100);

            }
        });
    }

    public void setupComponentsEvents() {
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
                try {
                    saveAudio();
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            }
        });

        //Video section :


        videoLayoutInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recordVideo();
            }
        });

        //Task Status Evnets :
        taskStatus.addSwitchObserver(new RMSwitch.RMSwitchObserver() {
            @Override
            public void onCheckStateChange(RMSwitch switchView, boolean isChecked) {
                if (isChecked) {
                    taskStatusText.setText(getResources().getString(R.string.taskDone));
                    taskStatusText.setTextColor(getResources().getColor(R.color.uiGreen));
                    InterventionManager.saveTaskStatus(currentIntervention.getId(), currentTaskIndex, true);
                    return;
                }
                taskStatusText.setText(getResources().getString(R.string.taskNotDone));
                InterventionManager.saveTaskStatus(currentIntervention.getId(), currentTaskIndex, false);
                taskStatusText.setTextColor(getResources().getColor(R.color.uiRed));
            }
        });
        taskStatus.setChecked(false);

        //Image Section :
        //imageToggle not setting click listenr because we have 2 cases : from Gallery / from Camera => Deffirent permissions
        imageLayoutInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                permissionCameraInputHanler();
            }
        });


        submitComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    saveComment();
                } catch (IOException exception) {
                    submitComment.startLoading();
                    submitComment.loadingFailed();
                    showResultDialog(exception.getMessage(), 3);
                }
            }
        });
    }

    public void resumeTask() throws JSONException, IOException {
        if (InterventionManager.getCurrentIntervention().equals(currentIntervention.getId())) {
            //Task Status
            taskStatus.setChecked(InterventionManager.getTaskStatus(currentIntervention.getId(), currentTaskIndex));
            //Image Section
            imageNumber.setVisibility(View.VISIBLE);
            if (TaskData.getImages(currentIntervention.getId(), currentTaskIndex, getActivity()).size() != 0) {
                imageIndex = TaskData.getImages(currentIntervention.getId(), currentTaskIndex, getActivity()).size() + 1;
                imageNumber.setText(TaskData.getImages(currentIntervention.getId(), currentTaskIndex, getActivity()).size() + " Images has added");
                previewImage();
            } else {
                imageNumber.setText(getResources().getString(R.string.imagesNotCaptured));
                previewImage.setVisibility(View.GONE);
                noImages.setVisibility(View.VISIBLE);
            }

            //Video Section
            videoAddStatus.setVisibility(View.VISIBLE);
            if (TaskData.getVideos(currentIntervention.getId(), currentTaskIndex, getActivity()).size() != 0) {
                videoIndex = TaskData.getVideos(currentIntervention.getId(), currentTaskIndex, getActivity()).size() + 1;
                videoAddStatus.setText(getResources().getString(R.string.videoAlreadyRecorded));
                previewVideo();
            } else {
                videoAddStatus.setText(getResources().getString(R.string.videoNotRecorded));
                previewVideo.setVisibility(View.GONE);
                noVideos.setVisibility(View.VISIBLE);
            }
            //Audio Section
            audioSaveStatus.setVisibility(View.VISIBLE);
            if (TaskData.getAudios(currentIntervention.getId(), currentTaskIndex, getActivity()).size() != 0) {
                audioIndex = TaskData.getAudios(currentIntervention.getId(), currentTaskIndex, getActivity()).size() + 1;
                audioSaveStatus.setText(getResources().getString(R.string.audioAlreadyRecorded));
                previewAudio();
            } else {
                audioSaveStatus.setText(getResources().getString(R.string.audioNotRecorded));

            }
            //Comments :
            if (TaskData.getComments(currentIntervention.getId(), currentTaskIndex, getActivity()).size() != 0) {
                commentIndex = TaskData.getComments(currentIntervention.getId(), currentTaskIndex, getActivity()).size() + 1;
                previewComment();
            }
            //Resume Task Status :
            if (InterventionManager.getTaskReportStatus(currentIntervention.getId(), currentTaskIndex)) {
                submitComment.startLoading();
                submitComment.loadingSuccessful();
                imageLayoutInput.setEnabled(false);
                videoLayoutInput.setEnabled(false);
                audioMainButton.setEnabled(false);
                commentEditText.setEnabled(false);
            }
            //Check if all tasks completed
            if (InterventionManager.getInterventionReport(currentIntervention.getId())) {
                new SubmitGeneralDialog(currentIntervention).show(getActivity().getSupportFragmentManager(), null);
            }

        }
    }

    //Preview Media :
    public void previewVideo() {
        List<File> listVideos = TaskData.getVideos(currentIntervention.getId(), currentTaskIndex, getContext());
        if (listVideos == null) {
            if (listVideos.size() == 0) {
                generalImagePreview.setVisibility(View.GONE);
                generalVideoPreview.setVisibility(View.VISIBLE);
                previewVideo.setVisibility(View.GONE);
                noVideos.setVisibility(View.VISIBLE);
            }
            return;
        }
        Bitmap bMap;
        previewVideo.removeAllViews();
        for (int i = 0; i < listVideos.size(); i++) {

            File file = listVideos.get(i);
            bMap = ThumbnailUtils.createVideoThumbnail(file.getAbsolutePath(), MediaStore.Video.Thumbnails.MICRO_KIND);
            ImageView pVideo = new ImageView(getContext());

            pVideo.setLayoutParams(new ViewGroup.LayoutParams(400, 400));

            pVideo.setImageBitmap(bMap);
            previewVideo.addView(pVideo);

            pVideo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.fromFile(file), "video/*");
                    getActivity().startActivity(intent);
                }
            });
        }

        generalImagePreview.setVisibility(View.GONE);
        generalVideoPreview.setVisibility(View.VISIBLE);
        previewVideo.setVisibility(View.VISIBLE);
        noVideos.setVisibility(View.GONE);
    }

    public void previewImage() {
        List<File> listImages = TaskData.getImages(currentIntervention.getId(), currentTaskIndex, getContext());
        if (listImages == null) {
            if (listImages.size() == 0) {
                generalImagePreview.setVisibility(View.VISIBLE);
                generalVideoPreview.setVisibility(View.GONE);
                previewImage.setVisibility(View.GONE);
                noImages.setVisibility(View.VISIBLE);
            }
            return;
        }
        Bitmap bMap;
        previewImage.removeAllViews();
        for (int i = 0; i < listImages.size(); i++) {
            File file = listImages.get(i);
            bMap = BitmapFactory.decodeFile(file.getAbsolutePath());
            ImageView pImage = new ImageView(getContext());

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(400, 400);
            params.setMargins(2, 4, 2, 0);
            pImage.setLayoutParams(params);

            pImage.setImageBitmap(bMap);

            previewImage.addView(pImage);

            pImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.fromFile(file), "image/*");
                    getActivity().startActivity(intent);
                }
            });
        }

        generalImagePreview.setVisibility(View.VISIBLE);
        generalVideoPreview.setVisibility(View.GONE);
        previewImage.setVisibility(View.VISIBLE);
        noImages.setVisibility(View.GONE);
    }

    public void previewAudio() {
        List<File> listAudios = TaskData.getAudios(currentIntervention.getId(), currentTaskIndex, getContext());
        if (listAudios == null) {
            if (listAudios.size() == 0) {
                generalImagePreview.setVisibility(View.GONE);
                generalVideoPreview.setVisibility(View.GONE);
                generalCommentPreview.setVisibility(View.GONE);
                generalAudioPreview.setVisibility(View.VISIBLE);
                previewAudio.setVisibility(View.GONE);
                noAudios.setVisibility(View.VISIBLE);
            }
            return;
        }
        previewAudio.removeAllViews();
        for (int i = 0; i < listAudios.size(); i++) {
            try {
                File file = listAudios.get(i);

                View view = getLayoutInflater().inflate(R.layout.audio_player_layout, null);

                ImageButton audioMainButton = view.findViewById(R.id.audioButton);
                TextView recordCounterMsg = view.findViewById(R.id.recordCounterMsg);
                Chronometer audioCounterTimer = view.findViewById(R.id.recordCounterChrono);

                boolean firstPlay = true;
                MediaPlayer mediaPlayer = new MediaPlayer();
                recordCounterMsg.setVisibility(View.GONE);
                audioCounterTimer.setVisibility(View.VISIBLE);
                mediaPlayer.setDataSource(file.getPath());
                mediaPlayer.prepare();
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        audioMainButton.animate().alpha(0.2f).setDuration(400);
                        audioMainButton.animate().alpha(1).setDuration(400);
                        audioMainButton.setImageResource(R.drawable.ic_play);
                        audioCounterTimer.stop();
                        audioCounterTimer.setBase(SystemClock.elapsedRealtime() - mediaPlayer.getDuration());
                    }
                });
                audioCounterTimer.setBase(SystemClock.elapsedRealtime() - mediaPlayer.getDuration());
                audioMainButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            if (!mediaPlayer.isPlaying()) {
                                if (mediaPlayer.getCurrentPosition() == 0)
                                    audioCounterTimer.setBase(SystemClock.elapsedRealtime());
                                audioMainButton.animate().alpha(0.2f).setDuration(400);
                                audioMainButton.animate().alpha(1).setDuration(400);
                                audioMainButton.setImageResource(R.drawable.ic_pause);
                                mediaPlayer.start();
                                audioCounterTimer.start();
                            } else {

                                audioMainButton.animate().alpha(0.2f).setDuration(400);
                                audioMainButton.animate().alpha(1).setDuration(400);
                                audioMainButton.setImageResource(R.drawable.ic_play);
                                mediaPlayer.pause();
                                audioCounterTimer.stop();
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                });


                previewAudio.addView(view);

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        generalImagePreview.setVisibility(View.GONE);
        generalVideoPreview.setVisibility(View.GONE);
        generalCommentPreview.setVisibility(View.GONE);
        generalAudioPreview.setVisibility(View.VISIBLE);
        previewAudio.setVisibility(View.VISIBLE);
        noAudios.setVisibility(View.GONE);
    }

    public void previewComment() {
        List<File> listComments = TaskData.getComments(currentIntervention.getId(), currentTaskIndex, getContext());
        if (listComments == null) {
            if (listComments.size() == 0) {
                generalImagePreview.setVisibility(View.GONE);
                generalVideoPreview.setVisibility(View.GONE);
                generalCommentPreview.setVisibility(View.VISIBLE);
                generalAudioPreview.setVisibility(View.GONE);
                previewComment.setVisibility(View.GONE);
                noComments.setVisibility(View.VISIBLE);
            }
            return;
        }
        previewComment.removeAllViews();
        for (int i = 0; i < listComments.size(); i++) {
            try {
                File file = listComments.get(i);

                View view = getLayoutInflater().inflate(R.layout.comment_preview_layout, null);

                TextView numComment = view.findViewById(R.id.numComment);
                TextView bodyComment = view.findViewById(R.id.bodyComment);

                numComment.append((i + 1) + " :");
                bodyComment.setText(ReportData.readTextFile(Uri.fromFile(file), getContext()));

                previewComment.addView(view);

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        generalImagePreview.setVisibility(View.GONE);
        generalVideoPreview.setVisibility(View.GONE);
        generalCommentPreview.setVisibility(View.VISIBLE);
        generalAudioPreview.setVisibility(View.GONE);
        previewComment.setVisibility(View.VISIBLE);
        noComments.setVisibility(View.GONE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1002) {
            if (resultCode == Activity.RESULT_OK) {
                if(data == null)
                    Log.e("IntentResultStatus", "Result Ok With null data");
                showResultDialog(getResources().getString(R.string.saveSuccessVideoMsg), 1);
                previewVideo();
                videoIndex++;
                Log.e("IntentResultStatus", "Result Ok");
            } else {
                showResultDialog(getResources().getString(R.string.saveCancelVideoMsg), 2);
                Log.e("IntentResultStatus", "Result Failed");
            }
        }
        if (requestCode == 100) {
            if (resultCode == Activity.RESULT_OK) {
                showResultDialog(getResources().getString(R.string.saveSuccessImageMsg), 1);
                previewImage();
                imageIndex++;
            } else {
                showResultDialog(getResources().getString(R.string.saveCancelImageMsg), 2);
            }
        }
    }

    //Camera Methodes :
    public void permissionCameraHanler() {
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

    public void permissionCameraInputHanler() {
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
                    }
                });
    }

    public void captureImage() {
        imageNumber.setVisibility(View.GONE);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File file = getOutputMediaFile(MEDIA_TYPE_IMAGE, currentIntervention.getId() + "_taskNum(" + currentTaskIndex + ")_num" + (imageIndex));
        Log.e("FileIntent", file.getPath());
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));

        startActivityForResult(intent, 100);

    }

    //Video Methodes :
    public void permissionVideoHanler() {
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

    public void recordVideo() {
        videoAddStatus.setVisibility(View.GONE);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        Uri fileUri;
        // create a file to save the video
        fileUri = Uri.fromFile(getOutputMediaFile(3, currentIntervention.getId() + "_taskNum(" + currentTaskIndex + ")_num" + (videoIndex)));

        // set the image file name
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

        // set the video image quality to high
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        startActivityForResult(intent, 1002);
    }

    //Audio Methodes :
    public void permissionAudioHanler() {
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

    public void permissionAudioInputHanler() {
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
                        try {
                            manageAudio();
                        } catch (IOException exception) {
                            exception.printStackTrace();
                        }
                    }

                    @Override
                    public void onDeny(String permission, int position) {

                    }

                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onGuarantee(String permission, int position) {
                        try {
                            manageAudio();
                        } catch (IOException exception) {
                            exception.printStackTrace();
                        }
                    }
                });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void manageAudio() throws IOException {
        if (AudioRecordingStatus == 0) {
            try {
                AudioRecordingStatus = 1;
                startRecording();
                audioMainButton.animate().alpha(0.2f).setDuration(400);
                audioMainButton.animate().alpha(1).setDuration(400);
                audioMainButton.setImageResource(R.drawable.ic_stop_recording);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (AudioRecordingStatus == 1) {
            stopRecording();
            AudioRecordingStatus = 2;
            audioMainButton.animate().alpha(0.2f).setDuration(400);
            audioMainButton.animate().alpha(1).setDuration(400);
            audioMainButton.setImageResource(R.drawable.ic_rounded_mic);
            audioSaveStatus.setVisibility(View.VISIBLE);
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void startRecording() throws IOException {

        recordCounterMsg.setVisibility(View.GONE);
        audioSaveStatus.setVisibility(View.GONE);
        audioCounterTimer.setVisibility(View.VISIBLE);
        audioCounterTimer.setBase(SystemClock.elapsedRealtime());
        audioCounterTimer.start();
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mediaRecorder.setOutputFile(getOutputMediaFile(2, currentIntervention.getId() + "_taskNum(" + currentTaskIndex + ")_num" + audioIndex));
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
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

    public void resetAudio() {
        AudioRecordingStatus = 0;
        audioSaveStatus.setText(getResources().getString(R.string.saveCancelAudioMsg));
        recordResultLayout.animate().alpha(0).setDuration(700);
        audioCounterTimer.setVisibility(View.GONE);
        recordResultLayout.setVisibility(View.GONE);
        recordCounterMsg.setVisibility(View.VISIBLE);
        recordCounterMsg.setText(getResources().getString(R.string.pressToRecordAudio));
        recordResultLayout.setVisibility(View.GONE);


    }

    public void saveAudio() throws IOException {
        if (mediaRecorder != null) {
            recordResultLayout.animate().alpha(0).setDuration(700);
            recordResultLayout.setVisibility(View.GONE);
            mediaRecorder.release();
            mediaRecorder = null;
            showResultDialog(getResources().getString(R.string.saveSuccessAudioMsg), 1);
            audioSaveStatus.setVisibility(View.GONE);

            previewAudio();

            resetAudio();

            audioIndex++;
        }
    }

    //Comments Methodes :

    public void saveComment() throws IOException {
        if (commentEditText.getText().toString().length() != 0 || !commentEditText.getText().toString().equals("")) {
            submitComment.startLoading();

            File commentFile = getOutputMediaFile(10, currentIntervention.getId() + "_taskNum(" + currentTaskIndex + ")_num" + commentIndex);
            FileOutputStream out = new FileOutputStream(commentFile);
            out.write(commentEditText.getText().toString().getBytes());
            out.close();
            previewComment();
            commentIndex++;
            submitComment.loadingSuccessful();
            submitComment.reset();
        } else {
            showResultDialog(getResources().getString(R.string.fillCommentToSubmit), 2);
            submitComment.startLoading();
            submitComment.loadingFailed();
        }

    }


    //Utils
    public void showResultDialog(String content, int type) {
        new ResultBottomDialog(content, type).show(getActivity().getSupportFragmentManager(), null);
    }

    private File getOutputMediaFile(int type, String FileName) {

        File mediaStorageDir = null;
        File mediaFile = null;

        if (type == MEDIA_TYPE_VIDEO) {
            mediaStorageDir = new File(getActivity().getExternalCacheDir(), "Videos");
            // Create the storage directory(MyCameraVideo) if it does not exist
            if (!mediaStorageDir.exists()) {
                if (!mediaStorageDir.mkdirs()) {
                    new ResultBottomDialog(getResources().getString(R.string.saveFailedVideoMsg), 3).show(getActivity().getSupportFragmentManager(), null);
                }
            }
            // For unique video file name appending current timeStamp with file name
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    FileName + ".mp4");

            currentTask.getVideos().add(mediaFile);

        } else if (type == MEDIA_TYPE_IMAGE) {
            mediaStorageDir = new File(getActivity().getExternalCacheDir(), "Images");
            if (!mediaStorageDir.exists()) {
                if (!mediaStorageDir.mkdirs()) {
                    new ResultBottomDialog(getResources().getString(R.string.saveFailedImageMsg), 3).show(getActivity().getSupportFragmentManager(), null);
                }
            }
            // For unique video file name appending current timeStamp with file name
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    FileName + ".jpg");

            currentTask.getImages().add(mediaFile);

        } else if (type == MEDIA_TYPE_AUDIO) {
            mediaStorageDir = new File(getActivity().getExternalCacheDir(), "Audios");
            if (!mediaStorageDir.exists()) {
                if (!mediaStorageDir.mkdirs()) {
                    new ResultBottomDialog(getResources().getString(R.string.saveFailedAudioMsg), 3).show(getActivity().getSupportFragmentManager(), null);

                }
            }
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    FileName + ".mp3");
            currentTask.getAudios().add(mediaFile);

        } else if (type == 10) {
            mediaStorageDir = new File(getActivity().getExternalCacheDir(), "Comments");
            // Create the storage directory(MyCameraVideo) if it does not exist
            if (!mediaStorageDir.exists()) {
                if (!mediaStorageDir.mkdirs()) {
                    new ResultBottomDialog(getResources().getString(R.string.saveFailedCommentMsg), 3).show(getActivity().getSupportFragmentManager(), null);
                }
            }
            // For unique video file name appending current timeStamp with file name
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    FileName + ".txt");
            currentTask.getAudios().add(mediaFile);
        }


        return mediaFile;
    }
}
