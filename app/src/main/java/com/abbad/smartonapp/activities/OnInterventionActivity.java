package com.abbad.smartonapp.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
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
import com.abbad.smartonapp.dialogs.ResultBottomDialog;
import com.abbad.smartonapp.utils.InterventionManager;
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
    private TextView intervBody;

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
        //Save intervention on shared preference :
        try {
            InterventionManager.saveCurrentIntervention(intervention.getId());
        } catch (JSONException e) {
            Log.e("Error",e.getMessage());
            new ResultBottomDialog("Error has occured",2).show(getSupportFragmentManager(),null);
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void initViews() {
        //Initialise the layout
        viewPager = findViewById(R.id.viewpager);
        mTabLayout = findViewById(R.id.tabs);
        intervBody = findViewById(R.id.interv_title);

        //Get Current Intervention
        intervention = getIntent().getParcelableExtra("intervention");

        //Set intervention body to text view :
        intervBody.setText(intervention.getTitle());

        //Set Focus on text view to start wrapping animation:
        intervBody.setSelected(true);

        // setOffscreenPageLimit means number
        // of tabs to be shown in one page

        viewPager.setOffscreenPageLimit(5);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
        mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // setCurrentItem as the tab position
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        setDynamicFragmentToTabLayout();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    private void setDynamicFragmentToTabLayout() {
        // here we have given 10 as the tab number
        // you can give any number here
        for (int i = 0; i < intervention.getTodos().length; i++) {
            // set the tab name as "Page: " + i
            TextView tabItem =(TextView) LayoutInflater.from(getApplicationContext()).inflate(R.layout.tab_layout_item,null);
            int currentNumTask = i+1;
            tabItem.setText(getResources().getString(R.string.task)+" "+currentNumTask);
            tabItem.setTypeface(ResourcesCompat.getFont(this, R.font.catamaran_medium));
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
}