package com.abbad.smartonapp.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.MediaController;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.viewpager.widget.ViewPager;

import com.abbad.smartonapp.R;
import com.abbad.smartonapp.adapters.TaskFragmentAdapter;
import com.abbad.smartonapp.classes.Intervention;
import com.abbad.smartonapp.classes.Task;
import com.abbad.smartonapp.datas.InterventionData;
import com.abbad.smartonapp.dialogs.ResultBottomDialog;
import com.abbad.smartonapp.dialogs.SubmitGeneralDialog;
import com.abbad.smartonapp.utils.InterventionManager;
import com.dx.dxloadingbutton.lib.LoadingButton;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONException;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;


public class OnInterventionActivity extends AppCompatActivity{
    //Current Intervention Repport:
    private Intervention intervention;

    //View Components :
    private ViewPager viewPager;
    private TabLayout mTabLayout;
    private TextView intervBody,intervId;


    //Navigation

    private FloatingActionButton nextBtn,previousBtn;

    //Constructors
    public OnInterventionActivity(Intervention intervention){
        this.intervention = intervention;
    }
    public OnInterventionActivity(){ }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.intervention_rapport);
        initViews();
        setDynamicFragmentToTabLayout();
        //Resume the general report when the user has completed all the tasks and exit the app :
        resumeIntervention();
        //Save intervention on shared preference :
        InterventionManager.saveCurrentIntervention(intervention.getId());
        Task currentTask = intervention.getListTaches().get(0);
        for (int k=0;k<currentTask.getListEquipements().size();k++) {

            for (int j=0;j<currentTask.getListActions().size();j++) {
                Log.e("TacheInfos",currentTask.getListActions().get(j));
            }}

        this.onPause();
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void initViews() {
        intervention = InterventionData.currentIntervention;
        //Initialise the layout
        viewPager = findViewById(R.id.viewpager);
        mTabLayout = findViewById(R.id.tabs);
        intervBody = findViewById(R.id.interv_title);
        intervId = findViewById(R.id.interv_id);

        //Set intervention body to text view :
        intervBody.setText(intervention.getTitle());
        intervId.append(intervention.getId());

        //Set Focus on text view to start wrapping animation:
        intervBody.setSelected(true);

        //Navigation View :

        nextBtn = findViewById(R.id.nextBtn);
        previousBtn = findViewById(R.id.previousBtn);


        int max = intervention.getListTaches().size() -1 ;



        // setOffscreenPageLimit means number
        // of tabs to be shown in one page

        viewPager.setOffscreenPageLimit(5);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
        mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // setCurrentItem as the tab position
                viewPager.setCurrentItem(tab.getPosition());
                if (viewPager.getCurrentItem()==0){
                    previousBtn.setVisibility(View.INVISIBLE);
                    nextBtn.setVisibility(View.VISIBLE);
                }
                else if (viewPager.getCurrentItem()== max){
                    nextBtn.setVisibility(View.INVISIBLE);
                    previousBtn.setVisibility(View.VISIBLE);
                }
                else{
                    nextBtn.setVisibility(View.VISIBLE);
                    previousBtn.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });





        if (viewPager.getCurrentItem()==0) {
            previousBtn.setVisibility(View.INVISIBLE);
        }
        //Set Navigation OnClickListener
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewPager.getCurrentItem()>0)
                    previousBtn.setVisibility(View.VISIBLE);
                else if (viewPager.getCurrentItem() == max)
                    nextBtn.setVisibility(View.INVISIBLE);
                viewPager.setCurrentItem(getNextItem(1), true);
            }
        });

        previousBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewPager.getCurrentItem()==0)
                    previousBtn.setVisibility(View.INVISIBLE);
                else if (viewPager.getCurrentItem() < max)
                    nextBtn.setVisibility(View.VISIBLE);
                viewPager.setCurrentItem(getPreviousItem(1), true);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


    }

    private int getNextItem(int i) {
        return viewPager.getCurrentItem() + i;
    }

    private int getPreviousItem(int i) {
        return viewPager.getCurrentItem() - i;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    private void setDynamicFragmentToTabLayout() {
        // here we have given 10 as the tab number
        // you can give any number here
        for (int i = 0; i < intervention.getListTaches().size(); i++) {
            // set the tab name as "Page: " + i
            TextView tabItem =(TextView) LayoutInflater.from(getApplicationContext()).inflate(R.layout.tab_layout_item,null);
            int currentNumTask = i+1;
            tabItem.setText(getResources().getString(R.string.task)+" "+currentNumTask);
            //tabItem.setTypeface(ResourcesCompat.getFont(this, R.font.catamaran_medium));
            tabItem.setTextColor(getResources().getColor(R.color.uiTextColor));
            mTabLayout.addTab(mTabLayout.newTab().setCustomView(tabItem));
        }

        TaskFragmentAdapter mDynamicFragmentAdapter = new TaskFragmentAdapter(getSupportFragmentManager(), mTabLayout.getTabCount(),intervention);

        // set the adapter
        viewPager.setAdapter(mDynamicFragmentAdapter);

        // set the current item as 0 (when app opens for first time)
        viewPager.setCurrentItem(0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void resumeIntervention(){
        if (InterventionManager.getInterventionReport(intervention.getId())){
            new SubmitGeneralDialog(intervention).show(getSupportFragmentManager(),null);
        }
    }
}