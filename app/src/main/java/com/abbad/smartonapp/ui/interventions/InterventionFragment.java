package com.abbad.smartonapp.ui.interventions;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.abbad.smartonapp.R;
import com.abbad.smartonapp.adapters.RecycleViewAdapter;
import com.abbad.smartonapp.classes.Intervention;
import com.abbad.smartonapp.datas.InterventionData;
import com.abbad.smartonapp.dialogs.InterventionResumeDialog;
import com.abbad.smartonapp.dialogs.ResultBottomDialog;
import com.abbad.smartonapp.utils.InterventionManager;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;

import org.json.JSONException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class InterventionFragment extends Fragment {

    //ViewModel :
    private InterventionViewModel interventionViewModel;

    //Calendar Components :
    private RecyclerView recyclerViewCalendar;
    private LinearLayout calendarLayout;
    private LinearLayout intervLayout;
    private TextView currentMonth,noIntervText;
    private ImageButton viewSwitcher;
    private CompactCalendarView calendarView;


    //List Components :
    private RecyclerView recyclerView;
    //Static List of interventions :
    private List<Intervention> list_intervention;

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


        //set Current Month to TextView :
        Calendar cal = Calendar.getInstance();
        cal.setTime(calendarView.getFirstDayOfCurrentMonth());
        String month = InterventionViewModel.getMonthName(cal.get(Calendar.MONTH));
        currentMonth.setText(month+" "+cal.get(Calendar.YEAR));

        //Set layout manager to recyleViews
        recyclerView.setLayoutManager( new LinearLayoutManager(getContext()));
        recyclerViewCalendar.setLayoutManager( new LinearLayoutManager(getContext()));

        //Fill Interventions Table with static data
        list_intervention = InterventionData.getListInterventions();

        //List Part :
        RecycleViewAdapter adapter = new RecycleViewAdapter(getActivity().getSupportFragmentManager(),list_intervention);
        recyclerView.setAdapter(adapter);
        interventionViewModel.displayTodayInters(intervLayout, calendarView,  currentMonth,recyclerViewCalendar,noIntervText);

        //Switch between listView & Calendar view
        interventionViewModel.switchViewSetter(viewSwitcher,intervLayout,calendarLayout);
        adapter.notifyDataSetChanged();

        //Calendar Part :
        try {setupEvents(); } catch (ParseException parseException) { new ResultBottomDialog("Error during uploading interventions",2).show(getActivity().getSupportFragmentManager(),null);}
        interventionViewModel.calendarClickHandler(intervLayout, calendarView,  currentMonth,recyclerViewCalendar,noIntervText);

        //Check for incompleted intervention :
        try {
            checkInCompletedIntervention();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return root;
    }



    private void setupEvents() throws ParseException {
        Calendar cal = Calendar.getInstance();

        for (Intervention in: list_intervention){
            cal.setTime(new SimpleDateFormat("dd-MM-yyyy").parse(in.getDate()));
            switch (in.getGravity()){
                case 1:
                    calendarView.addEvent(new Event(getResources().getColor(R.color.danger1), cal.getTimeInMillis()));
                    break;
                case 2:
                    calendarView.addEvent(new Event(getResources().getColor(R.color.danger2), cal.getTimeInMillis()));
                    break;
                case 3:
                    calendarView.addEvent(new Event(getResources().getColor(R.color.danger3), cal.getTimeInMillis()));
                    break;
                case 4:
                    calendarView.addEvent(new Event(getResources().getColor(R.color.danger4), cal.getTimeInMillis()));
                    break;
                default:
                    calendarView.addEvent(new Event(getResources().getColor(R.color.danger5), cal.getTimeInMillis()));
                    break;
            }
        }
    }

    public void checkInCompletedIntervention() throws JSONException {
        String idInterv = InterventionManager.getCurrentIntervention();
        if (idInterv !=null)
            new InterventionResumeDialog(getInterventionById(idInterv)).show(getActivity().getSupportFragmentManager(),null);
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


}