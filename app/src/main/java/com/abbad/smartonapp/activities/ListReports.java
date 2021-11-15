package com.abbad.smartonapp.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.abbad.smartonapp.R;
import com.abbad.smartonapp.adapters.ReportsAdapter;
import com.abbad.smartonapp.classes.Report;
import com.abbad.smartonapp.datas.ReportData;

import java.util.List;

public class ListReports extends AppCompatActivity {

    LinearLayout serverError,noReportLayout,workLayout;
    TextView exceptionText;
    RecyclerView recyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_reports);

        recyclerView = findViewById(R.id.historique_recycler);
        noReportLayout = findViewById(R.id.noInterventionLayout);
        serverError = findViewById(R.id.serverError);
        workLayout = findViewById(R.id.workLayout);
        exceptionText = findViewById(R.id.exceptionText);

        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        new ReportData.GetAllReport(this).execute();
    }

    public void displayData(List<Report> listReports) {
        recyclerView.setAdapter(new ReportsAdapter(listReports,this));
    }

    public void noIntervention(){
        noReportLayout.setVisibility(View.VISIBLE);
        workLayout.setVisibility(View.GONE);
        serverError.setVisibility(View.GONE);
    }

    public void errorServer(String textEx){
        noReportLayout.setVisibility(View.GONE);
        workLayout.setVisibility(View.GONE);
        serverError.setVisibility(View.VISIBLE);
        exceptionText.setText(textEx);
    }

    public void backToService(){
        noReportLayout.setVisibility(View.GONE);
        workLayout.setVisibility(View.VISIBLE);
        serverError.setVisibility(View.GONE);
    }
}