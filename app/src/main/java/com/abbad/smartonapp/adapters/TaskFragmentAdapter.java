package com.abbad.smartonapp.adapters;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.abbad.smartonapp.Fragments.TaskFragment;
import com.abbad.smartonapp.classes.Intervention;

public class TaskFragmentAdapter extends FragmentStatePagerAdapter {
    private int totalTasks;
    private Intervention intervention;
    public TaskFragmentAdapter(@NonNull FragmentManager fm, int totalTasks, Intervention intervention) {
        super(fm);
        this.totalTasks = totalTasks;
        this.intervention = intervention;
    }



    @NonNull
    @Override
    public Fragment getItem(int position) {
        Bundle b = new Bundle();
        b.putInt("position", position);
        b.putParcelable("intervention", intervention);
        b.putInt("numTask", position);
        Fragment frag = new TaskFragment();
        frag.setArguments(b);
        return frag;
    }

    @Override
    public int getCount() {
        return totalTasks;
    }
}
