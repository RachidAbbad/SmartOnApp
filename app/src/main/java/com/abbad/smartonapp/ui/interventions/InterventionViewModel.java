package com.abbad.smartonapp.ui.interventions;

import android.os.Build;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.RecyclerView;

import com.abbad.smartonapp.R;
import com.abbad.smartonapp.activities.MainActivity;
import com.abbad.smartonapp.adapters.RecycleViewAdapter;
import com.abbad.smartonapp.classes.Intervention;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static android.view.View.GONE;

public class InterventionViewModel extends ViewModel {
    private InterventionFragment f;


    //Constructors :
    public InterventionViewModel(InterventionFragment f) {
        this.f = f;
    }


    //Calendar section :
    public class ViewSwitcherHandler implements View.OnClickListener {
        private ImageButton viewSwitcher;
        private LinearLayout listView;
        private LinearLayout calendarView;

        public ViewSwitcherHandler(ImageButton viewSwitcher, LinearLayout listView, LinearLayout calendarView) {
            this.calendarView = calendarView;
            this.listView = listView;
            this.viewSwitcher = viewSwitcher;
        }

        @Override
        public void onClick(View v) {
            viewSwitcher.animate().alpha(0.0F).setDuration(500).start();
            viewSwitcher.animate().alpha(1.0F).setDuration(500).start();
            if (calendarView.getVisibility() == GONE) {
                viewSwitcher.setImageResource(R.drawable.ic_list_view);
                listView.animate().alpha(0.0F).setDuration(500).start();
                calendarView.animate().alpha(1.0F).setDuration(500).start();
                calendarView.setVisibility(View.VISIBLE);
                listView.setVisibility(GONE);
            } else {
                viewSwitcher.setImageResource(R.drawable.ic_calendar_view);

                calendarView.animate().alpha(0.0F).setDuration(500).start();
                listView.animate().alpha(1.0F).setDuration(500).start();

                calendarView.setVisibility(GONE);
                listView.setVisibility(View.VISIBLE);
            }

        }
    }

    public void switchViewSetter(ImageButton viewSwitcher, LinearLayout listView, LinearLayout calendarView) {
        viewSwitcher.setOnClickListener(new ViewSwitcherHandler(viewSwitcher, listView, calendarView));
    }

    public class CalendarClickHandler implements CompactCalendarView.CompactCalendarViewListener {
        private LinearLayout interLayout;
        private CompactCalendarView calendarView;
        private TextView currentDate, noIntervText;
        private RecyclerView recyclerView;
        private List<Intervention> interventionList;

        public CalendarClickHandler(LinearLayout interLayout, CompactCalendarView calendarView, TextView currentDate, RecyclerView recyclerView, TextView noIntervText, List<Intervention> listIntervention) {
            this.interLayout = interLayout;
            this.calendarView = calendarView;
            this.currentDate = currentDate;
            this.recyclerView = recyclerView;
            this.noIntervText = noIntervText;
            this.interventionList = listIntervention;

        }

        @Override
        public void onDayClick(Date dateClicked) {
            List<Intervention> interventions = new ArrayList<>();
            f.clickedDate = dateClicked;
            Log.i("dateClicked", dateClicked.toString());
            if (interventionList == null)
                interventionList = new ArrayList<>();
            for (Intervention intervention : interventionList) {
                try {
                    if (new SimpleDateFormat("yyyy-MM-dd").parse(intervention.getDate()).compareTo(dateClicked) == 0)
                        interventions.add(intervention);
                } catch (ParseException parseException) {
                    parseException.printStackTrace();
                }
            }
            Animation animation = AnimationUtils.loadAnimation(f.getActivity().getApplicationContext(), R.anim.slide_in);
            animation.setDuration(500);
            if (interventions.size() == 0) {
                recyclerView.setVisibility(GONE);
                noIntervText.setVisibility(View.VISIBLE);
                noIntervText.setAnimation(animation);
            } else {
                recyclerView.setVisibility(View.VISIBLE);
                noIntervText.setVisibility(GONE);
                RecycleViewAdapter adapter = new RecycleViewAdapter((MainActivity) f.getActivity(), interventions);
                recyclerView.setAdapter(adapter);
                recyclerView.startAnimation(animation);
            }

        }

        @Override
        public void onMonthScroll(Date firstDayOfNewMonth) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(calendarView.getFirstDayOfCurrentMonth());
            String month = getMonthName(cal.get(Calendar.MONTH));
            currentDate.setText(month + " " + cal.get(Calendar.YEAR));
        }
    }


    public void displayDayIntervention(List<Intervention> interventionList, RecyclerView recyclerView, TextView noIntervText, Date dateClicked) {
        List<Intervention> interventions = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        String todayDate = calendar.get(Calendar.YEAR) + "-" + calendar.get(Calendar.MONTH) + "-" + calendar.get(Calendar.DAY_OF_MONTH);
        Log.e("TodayDateString", todayDate);
        SimpleDateFormat dateParser = new SimpleDateFormat("yyyy-MM-dd");
        if (interventionList == null)
            interventionList = new ArrayList<>();
        for (Intervention intervention : interventionList) {
            try {
                if (dateParser.parse(intervention.getDate()).compareTo(dateParser.parse(todayDate)) == 0)
                    interventions.add(intervention);
            } catch (ParseException parseException) {
                parseException.printStackTrace();
            }
        }

        if (interventions.size() == 0) {
            recyclerView.setVisibility(GONE);
            noIntervText.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            noIntervText.setVisibility(GONE);
            RecycleViewAdapter adapter = new RecycleViewAdapter((MainActivity) f.getActivity(), interventions);
            recyclerView.setAdapter(adapter);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void calendarClickHandler(LinearLayout interLayout, CompactCalendarView calendarView, TextView currentDate, RecyclerView recyclerView, TextView noIntervText, List<Intervention> listIntervention) {
        CalendarClickHandler calendarClickHandler = new CalendarClickHandler(interLayout, calendarView, currentDate, recyclerView, noIntervText, listIntervention);
        calendarView.setListener(calendarClickHandler);
    }

    public static String getMonthName(int month) {

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

    //List Section :
}