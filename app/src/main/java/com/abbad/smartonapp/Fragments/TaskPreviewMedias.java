package com.abbad.smartonapp.Fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.StrictMode;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.abbad.smartonapp.R;
import com.abbad.smartonapp.activities.PreviewReport;
import com.abbad.smartonapp.classes.Intervention;
import com.abbad.smartonapp.classes.Report;
import com.abbad.smartonapp.classes.Task;
import com.abbad.smartonapp.datas.ReportData;
import com.abbad.smartonapp.datas.TaskData;
import com.dx.dxloadingbutton.lib.LoadingButton;

import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.util.List;


public class TaskPreviewMedias extends Fragment {

    //Views:
    public LinearLayout downloadLayout;
    public ScrollView previewScroll;
    public LoadingButton downloadBtn;
    private Report report;
    private Intervention intervention;
    private int numTask;
    private LinearLayout generalVideoPreview, generalImagePreview, generalCommentPreview, generalAudioPreview;
    private GridLayout previewVideo, previewImage;
    private LinearLayout previewComment, previewAudio;
    private TextView noImages, noVideos, noComments, noAudios;

    public TaskPreviewMedias(Report report, int numTask) {
        this.report = report;
        this.numTask = numTask;
    }

    public TaskPreviewMedias() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_task_preview_medias, container, false);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        initViews(view);

        return view;
    }

    private void initViews(View view) {
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
        downloadBtn = view.findViewById(R.id.download_btn);
        previewScroll = view.findViewById(R.id.preview_scroll_preview);
        downloadLayout = view.findViewById(R.id.download_layout);

        //Set OnClickListener
        downloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ReportData.GetReportFiles(report, TaskPreviewMedias.this, numTask, downloadBtn).execute();
            }
        });

    }

    //Preview Media :
    public void previewVideo(List<File> listVideos) {
        if (listVideos == null || listVideos.size() == 0) {
            previewVideo.setVisibility(View.GONE);
            noVideos.setVisibility(View.VISIBLE);
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
    }

    public void previewImage(List<File> listImages) {
        if (listImages == null || listImages.size() == 0) {

            previewImage.setVisibility(View.GONE);
            noImages.setVisibility(View.VISIBLE);
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
    }

    public void previewAudio(List<File> listAudios) {
        if (listAudios == null || listAudios.size() == 0) {

            previewAudio.setVisibility(View.GONE);
            noAudios.setVisibility(View.VISIBLE);
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
    }

    public void previewComment(List<String> listComments) {
        if (listComments == null || listComments.size() == 0) {
            previewComment.setVisibility(View.GONE);
            noComments.setVisibility(View.VISIBLE);
            return;
        }
        previewComment.removeAllViews();
        for (int i = 0; i < listComments.size(); i++) {
            try {
                View view = getLayoutInflater().inflate(R.layout.comment_preview_layout, null);

                TextView numComment = view.findViewById(R.id.numComment);
                TextView bodyComment = view.findViewById(R.id.bodyComment);

                numComment.append((i + 1) + " :");
                bodyComment.setText(listComments.get(i));

                previewComment.addView(view);

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }


}