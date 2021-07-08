package com.abbad.smartonapp.ui.interventions;

import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.RecyclerView;

import com.abbad.smartonapp.R;
import com.abbad.smartonapp.adapters.RecycleViewAdapter;
import com.abbad.smartonapp.classes.Intervention;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class InterventionViewModel extends ViewModel {
    private InterventionFragment f;
    public InterventionViewModel(InterventionFragment f){
        this.f = f;
    }

    public class ViewSwitcherHandler implements View.OnClickListener{
        private ImageButton viewSwitcher;
        private LinearLayout listView;
        private LinearLayout calendarView;
        public ViewSwitcherHandler(ImageButton viewSwitcher, LinearLayout listView,LinearLayout calendarView){
            this.calendarView = calendarView;
            this.listView = listView;
            this.viewSwitcher = viewSwitcher;
        }
        @Override
        public void onClick(View v) {
            if (calendarView.getVisibility() == View.GONE){
                viewSwitcher.animate().alpha(0.0F).setDuration(500).start();
                viewSwitcher.animate().alpha(1.0F).setDuration(500).start();
                viewSwitcher.setImageResource(R.drawable.ic_calendar_view);
                listView.animate().alpha(0.0F).setDuration(500).start();
                calendarView.animate().alpha(1.0F).setDuration(500).start();
                calendarView.setVisibility(View.VISIBLE);
                listView.setVisibility(View.GONE);
            }
            else{
                calendarView.animate().alpha(0.0F).setDuration(500).start();
                listView.animate().alpha(1.0F).setDuration(500).start();
                viewSwitcher.setImageResource(R.drawable.ic_list_view);
                calendarView.setVisibility(View.GONE);
                listView.setVisibility(View.VISIBLE);
            }

        }
    }

    public void switchViewSetter(ImageButton viewSwitcher, LinearLayout listView,LinearLayout calendarView ){
        viewSwitcher.setOnClickListener(new ViewSwitcherHandler(viewSwitcher,listView,calendarView ));
    }

    public class CalendarClickHandler implements CompactCalendarView.CompactCalendarViewListener{
        private LinearLayout interLayout;
        private CompactCalendarView calendarView;
        private TextView currentDate;
        private RecyclerView recyclerView;
        public CalendarClickHandler(LinearLayout interLayout,CompactCalendarView calendarView, TextView currentDate,RecyclerView recyclerView){
            this.interLayout = interLayout;
            this.calendarView = calendarView;
            this.currentDate = currentDate;
            this.recyclerView = recyclerView;
        }
        @Override
        public void onDayClick(Date dateClicked) {
            List<Intervention> interventions = new ArrayList<>();

            for (Intervention intervention: f.getList_intervention()){
                try {
                    if (new SimpleDateFormat("dd-MM-yyyy").parse(intervention.getDate()).compareTo(dateClicked)==0)
                        interventions.add(intervention);
                } catch (ParseException parseException) {
                    parseException.printStackTrace();
                }
            }
            RecycleViewAdapter adapter = new RecycleViewAdapter(f.getActivity().getSupportFragmentManager(),interventions);
            recyclerView.setAdapter(adapter);
            Animation animation = AnimationUtils.loadAnimation(f.getActivity().getApplicationContext(), R.anim.slide_in);
            animation.setDuration(500);
            recyclerView.startAnimation(animation);
        }

        @Override
        public void onMonthScroll(Date firstDayOfNewMonth) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(calendarView.getFirstDayOfCurrentMonth());
            String month = getMonthName(cal.get(Calendar.MONTH));
            currentDate.setText(month+" "+cal.get(Calendar.YEAR));
        }
    }

    public void calendarClickHandler(LinearLayout interLayout,CompactCalendarView calendarView, TextView currentDate,RecyclerView recyclerView){
        calendarView.setListener(new CalendarClickHandler(interLayout, calendarView,  currentDate,recyclerView));
    }

    private String getMonthName(int month) {

        String monthName = null;
        switch (month) {
            case 0:
                monthName = "January";
                break;
            case 1:
                monthName = "February";
                break;
            case 2:
                monthName = "March";
                break;
            case 3:
                monthName = "April";
                break;
            case 4:
                monthName = "May";
                break;
            case 5:
                monthName = "June";
                break;
            case 6:
                monthName = "July";
                break;
            case 7:
                monthName = "August";
                break;
            case 8:
                monthName = "September";
                break;
            case 9:
                monthName = "October";
                break;
            case 10:
                monthName = "November";
                break;
            case 11:
                monthName = "December";
                break;
        }

        return monthName;
    }
}