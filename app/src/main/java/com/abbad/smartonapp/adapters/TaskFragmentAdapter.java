package com.abbad.smartonapp.adapters;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.abbad.smartonapp.Fragments.TaskFragment;
import com.abbad.smartonapp.Fragments.TaskPreviewMedias;
import com.abbad.smartonapp.classes.Intervention;
import com.abbad.smartonapp.classes.Report;

public class TaskFragmentAdapter extends FragmentStatePagerAdapter {
    private int totalTasks;
    private Intervention intervention;
    private Report report;
    private boolean isPreview;
    public TaskFragmentAdapter(@NonNull FragmentManager fm, int totalTasks, Intervention intervention,boolean isPreview) {
        super(fm);
        this.totalTasks = totalTasks;
        this.intervention = intervention;
        this.isPreview = isPreview;
    }

    public TaskFragmentAdapter(@NonNull FragmentManager fm, int totalTasks, Report report, boolean isPreview) {
        super(fm);
        this.totalTasks = totalTasks;
        this.isPreview = isPreview;
        this.report = report;
    }



    @NonNull
    @Override
    public Fragment getItem(int position) {
        Bundle b = new Bundle();
        Fragment frag;
        if (isPreview){
            frag = new TaskPreviewMedias(report,position);
        }
        else{
            b.putParcelable("intervention", intervention);
            b.putParcelable("task", intervention.getListTaches().get(position));
            b.putInt("numTask", position);
            frag = new TaskFragment();
        }
        frag.setArguments(b);
        return frag;
    }

    @Override
    public int getCount() {
        return totalTasks;
    }
}
