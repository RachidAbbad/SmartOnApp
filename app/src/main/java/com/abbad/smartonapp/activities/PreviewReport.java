package com.abbad.smartonapp.activities;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
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
import android.widget.TextView;

import com.abbad.smartonapp.R;
import com.abbad.smartonapp.adapters.TaskFragmentAdapter;
import com.abbad.smartonapp.classes.Intervention;
import com.abbad.smartonapp.classes.Report;
import com.abbad.smartonapp.classes.Task;
import com.abbad.smartonapp.datas.InterventionData;
import com.abbad.smartonapp.datas.ReportData;
import com.abbad.smartonapp.datas.TaskData;
import com.abbad.smartonapp.dialogs.ResultBottomDialog;
import com.abbad.smartonapp.dialogs.SubmitGeneralDialog;
import com.abbad.smartonapp.utils.InterventionManager;
import com.dx.dxloadingbutton.lib.LoadingButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.io.File;
import java.util.List;

public class PreviewReport extends AppCompatActivity {

    //Report Infos
    private String idReport;
    private Report report;

    //Views
    private TextView nomReport, nomSite, nomResponsable, dateValidation, dateIntervention;

    //View Components :
    private ViewPager viewPager;
    private TabLayout mTabLayout;

    //Navigation
    private FloatingActionButton nextBtn, previousBtn;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preview_report_info);
        initViews();
        idReport = getIntent().getStringExtra("idReport");
        if (idReport != null)
            new ReportData.GetReportById(getIntent().getStringExtra("idReport"), this).execute();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void setupViews(Report report) {
        this.report = report;
        Log.e("ReportInfo", "Respnosable Report : " + report.getNomResponsable());
        nomResponsable.setText(report.getNomResponsable());
        nomReport.setText(report.getNomIntervention());
        nomSite.setText(report.getNomSite());
        dateValidation.setText(report.getDateValidation());
        dateIntervention.setText(report.getDateIntervention());
        setupElements();
        setDynamicFragmentToTabLayout();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void initViews() {
        //Init views:
        nomReport = findViewById(R.id.report_title);
        nomSite = findViewById(R.id.nom_site);
        nomResponsable = findViewById(R.id.responsable);
        dateValidation = findViewById(R.id.date_report);
        dateIntervention = findViewById(R.id.date_interv);

        //Initialise the layout
        viewPager = findViewById(R.id.viewpager);
        mTabLayout = findViewById(R.id.tabs);

        //Navigation View :

        nextBtn = findViewById(R.id.nextBtn);
        previousBtn = findViewById(R.id.previousBtn);
    }

    private void setupElements() {
        int max = report.getListTasks().size();


        // setOffscreenPageLimit means number
        // of tabs to be shown in one page

        viewPager.setOffscreenPageLimit(5);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
        mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // setCurrentItem as the tab position
                viewPager.setCurrentItem(tab.getPosition());
                if (viewPager.getCurrentItem() == 0) {
                    previousBtn.setVisibility(View.INVISIBLE);
                    nextBtn.setVisibility(View.VISIBLE);
                } else if (viewPager.getCurrentItem() == max) {
                    nextBtn.setVisibility(View.INVISIBLE);
                    previousBtn.setVisibility(View.VISIBLE);
                } else {
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


        if (viewPager.getCurrentItem() == 0) {
            previousBtn.setVisibility(View.INVISIBLE);
        }
        //Set Navigation OnClickListener
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewPager.getCurrentItem() > 0)
                    previousBtn.setVisibility(View.VISIBLE);
                else if (viewPager.getCurrentItem() == max)
                    nextBtn.setVisibility(View.INVISIBLE);
                viewPager.setCurrentItem(getNextItem(1), true);
            }
        });

        previousBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewPager.getCurrentItem() == 0)
                    previousBtn.setVisibility(View.INVISIBLE);
                else if (viewPager.getCurrentItem() < max)
                    nextBtn.setVisibility(View.VISIBLE);
                viewPager.setCurrentItem(getPreviousItem(1), true);
            }
        });
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
        for (int i = 0; i < report.getListTasks().size() + 1; i++) {
            if (i == report.getListTasks().size()) {
                TextView tabItem = (TextView) LayoutInflater.from(getApplicationContext()).inflate(R.layout.tab_layout_item, null);
                tabItem.setText("Comment Général");
                //tabItem.setTypeface(ResourcesCompat.getFont(this, R.font.catamaran_medium));
                tabItem.setTextColor(getResources().getColor(R.color.uiTextColor));
                mTabLayout.addTab(mTabLayout.newTab().setCustomView(tabItem));
                continue;
            }
            // set the tab name as "Page: " + i
            TextView tabItem = (TextView) LayoutInflater.from(getApplicationContext()).inflate(R.layout.tab_layout_item, null);
            int currentNumTask = i + 1;
            tabItem.setText(getResources().getString(R.string.task) + " " + currentNumTask);
            //tabItem.setTypeface(ResourcesCompat.getFont(this, R.font.catamaran_medium));
            tabItem.setTextColor(getResources().getColor(R.color.uiTextColor));
            mTabLayout.addTab(mTabLayout.newTab().setCustomView(tabItem));
        }

        TaskFragmentAdapter mDynamicFragmentAdapter = new TaskFragmentAdapter(getSupportFragmentManager(), mTabLayout.getTabCount(), report, true);

        // set the adapter
        viewPager.setAdapter(mDynamicFragmentAdapter);

        // set the current item as 0 (when app opens for first time)
        viewPager.setCurrentItem(0);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        deleteReportMedia();
    }

    public void errorServer(String error) {
    }

    private void deleteReportMedia(){
        File dir = new File(getExternalCacheDir(),"Temp");
        if (dir.isDirectory())
        {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++)
            {
                new File(dir, children[i]).delete();
            }
        }
    }
}