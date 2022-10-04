package com.abbad.smartonapp.ui.interventions;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.abbad.smartonapp.R;
import com.abbad.smartonapp.activities.HistoriqueInteventions;
import com.abbad.smartonapp.activities.InterventionDetails;
import com.abbad.smartonapp.activities.ListReports;
import com.abbad.smartonapp.activities.MainActivity;
import com.abbad.smartonapp.adapters.RecycleViewAdapter;
import com.abbad.smartonapp.classes.Intervention;
import com.abbad.smartonapp.datas.InterventionData;
import com.abbad.smartonapp.datas.ReportData;
import com.abbad.smartonapp.dialogs.InterventionResumeDialog;
import com.abbad.smartonapp.dialogs.ResultBottomDialog;
import com.abbad.smartonapp.utils.InterventionManager;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;

import org.json.JSONException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class InterventionFragment extends Fragment {

    //ViewModel :
    public InterventionViewModel interventionViewModel;

    //Calendar Components :
    private RecyclerView recyclerViewCalendar;
    private LinearLayout calendarLayout,serverError,noInterventionLayout,workLayout;
    private LinearLayout intervLayout;
    private TextView currentMonth,noIntervText,exceptionText;
    private ImageButton viewSwitcher,historiqueInterv,rapport_btn;
    private CompactCalendarView calendarView;
    public Date clickedDate;

    //List Components :
    private RecyclerView recyclerView;
     public RecycleViewAdapter adapter;
    //Static List of interventions :
    public List<Intervention> list_intervention;

    //

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        interventionViewModel = new InterventionViewModel(this);

        View root = inflater.inflate(R.layout.intervention_fragment, container, false);

        //Initialisation of views :
        recyclerView = root.findViewById(R.id.list_intervention) ;
        recyclerViewCalendar = root.findViewById(R.id.list_intervention_calendar);
        intervLayout = root.findViewById(R.id.listView) ;
        currentMonth = root.findViewById(R.id.currentMonth) ;
        calendarView = root.findViewById(R.id.compactcalendar_view);
        noIntervText = root.findViewById(R.id.noIntervText) ;
        calendarLayout = root.findViewById(R.id.calendarView) ;
        viewSwitcher = root.findViewById(R.id.viewSwitcher) ;
        rapport_btn = root.findViewById(R.id.list_reports_btn);
        historiqueInterv = root.findViewById(R.id.historiqueInterv);
        noInterventionLayout = root.findViewById(R.id.noInterventionLayout);
        serverError = root.findViewById(R.id.serverError);
        workLayout = root.findViewById(R.id.workLayout);
        exceptionText = root.findViewById(R.id.exceptionText);

        InterventionData.listInterventions = new ArrayList<>();

        historiqueInterv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), HistoriqueInteventions.class);
                startActivity(intent);
            }
        });


        //set Current Month to TextView :
        Calendar cal = Calendar.getInstance();
        cal.setTime(calendarView.getFirstDayOfCurrentMonth());
        String month = InterventionViewModel.getMonthName(cal.get(Calendar.MONTH));
        currentMonth.setText(month+" "+cal.get(Calendar.YEAR));

        //Set layout manager to recyleViews
        recyclerView.setLayoutManager( new LinearLayoutManager(getContext()));
        recyclerViewCalendar.setLayoutManager( new LinearLayoutManager(getContext()));

        //List Part :
        adapter = new RecycleViewAdapter((MainActivity) getActivity());
        recyclerView.setAdapter(adapter);

        clickedDate = new Date();

        //Switch between listView & Calendar view
        interventionViewModel.switchViewSetter(viewSwitcher,intervLayout,calendarLayout);
        adapter.notifyDataSetChanged();

        rapport_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ListReports.class);
                startActivity(intent);
            }
        });

        return root;
    }

    public void refreshIntervention(){
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void run() {
                list_intervention = InterventionData.getListInterventions(InterventionFragment.this);
            }
        },0,5000);
    }

    public void setupEvents(List<Intervention> listIntervention) throws ParseException {
        Calendar cal = Calendar.getInstance();
        calendarView.removeAllEvents();
        for (Intervention in: listIntervention){
            cal.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(in.getDate()));
            switch (in.getType()){
                case "correctif":
                    calendarView.addEvent(new Event(getResources().getColor(R.color.danger1), cal.getTimeInMillis()));
                    break;
                case "preventif":
                    calendarView.addEvent(new Event(getResources().getColor(R.color.danger5), cal.getTimeInMillis()));
                    break;
                default:
                    calendarView.addEvent(new Event(getResources().getColor(R.color.danger2), cal.getTimeInMillis()));
                    break;
            }
        }
    }

    public static void checkInCompletedIntervention(MainActivity activity){
        String idInterv = InterventionManager.getCurrentIntervention();
        if (idInterv !=null){
            new InterventionResumeDialog(InterventionData.getInterventionById(idInterv)).show(activity.getSupportFragmentManager(),null);
            InterventionData.currentIntervention = InterventionData.getInterventionById(idInterv);
        }
    }
    public List<Intervention> getList_intervention() {
        return list_intervention;
    }
    public Intervention getInterventionById(String id){
        for (Intervention inter: list_intervention) {
            if (inter.getId().equals(id))
                return inter;
        }
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void refreshAll(List<Intervention> interventionList) throws ParseException {
        Log.e("ListInterv",interventionList.size()+"");
        setupEvents(interventionList);
        list_intervention = interventionList;
        adapter.setList_intervention(interventionList);
        adapter.notifyDataSetChanged();
        interventionViewModel.calendarClickHandler(intervLayout, calendarView, currentMonth,recyclerViewCalendar,noIntervText, interventionList);
    }

    public void noIntervention(){
        noInterventionLayout.setVisibility(View.VISIBLE);
        workLayout.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        serverError.setVisibility(View.GONE);
        viewSwitcher.setEnabled(true);
        historiqueInterv.setEnabled(true);
    }

    public void errorServer(String textEx){
        noInterventionLayout.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
        workLayout.setVisibility(View.GONE);
        serverError.setVisibility(View.VISIBLE);
        exceptionText.setText(textEx);
        viewSwitcher.setEnabled(false);
        historiqueInterv.setEnabled(false);
    }

    public void backToService(){
        noInterventionLayout.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        workLayout.setVisibility(View.VISIBLE);
        serverError.setVisibility(View.GONE);
        viewSwitcher.setEnabled(true);
        historiqueInterv.setEnabled(true);
    }
}