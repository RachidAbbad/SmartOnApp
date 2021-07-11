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
import com.abbad.smartonapp.dialogs.ResultBottomDialog;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;

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
        fillInterv();

        //List Part :
        RecycleViewAdapter adapter = new RecycleViewAdapter(getActivity().getSupportFragmentManager(),list_intervention);
        recyclerView.setAdapter(adapter);
        interventionViewModel.displayTodayInters(intervLayout, calendarView,  currentMonth,recyclerViewCalendar,noIntervText);

        //Switch between listView & Calendar view
        interventionViewModel.switchViewSetter(viewSwitcher,intervLayout,calendarLayout);
        adapter.notifyDataSetChanged();

        //Calendar Part :
        try {setupEvents(); } catch (ParseException parseException) { new ResultBottomDialog("Error during uploading interventions",false).show(getActivity().getSupportFragmentManager(),null);}
        interventionViewModel.calendarClickHandler(intervLayout, calendarView,  currentMonth,recyclerViewCalendar,noIntervText);


        return root;
    }

    private void fillInterv(){
        list_intervention = new ArrayList<>();
        list_intervention.add(new Intervention("la dépose de la buse de fumée du raccordement au conduit"
                ,"02-06-2021"
                ,3
                ,new String[]{"le nettoyage intérieur avec repose après travaux","le nettoyage des éléments et du local après intervention"}
                ,new String[]{"Tournevis","lunettes de protection"}
                ,"Interv735651"
        ));
        list_intervention.add(new Intervention("Effectuer une calibration des contrôles"
                ,"15-06-2021"
                ,4
                ,new String[]{"Vérifiez les pompes","la lubrification","le fonctionnement du moteur","l’alignement"}
                ,new String[]{"Tournevis","lunettes de protection"}
                ,"Interv735652"
        ));
        list_intervention.add(new Intervention("Vérification le système de contrôle"
                ,"02-06-2021"
                ,1
                ,new String[]{"les sondes de température intérieure et extérieure","les dispositifs de sécurité","la valve de relâche haute pression et haute température"}
                ,new String[]{"Tournevis","lunettes de protection"}
                ,"Interv735653"
        ));
        list_intervention.add(new Intervention("la dépose de la buse de fumée du raccordement au conduit"
                ,"11-07-2021"
                ,3
                ,new String[]{"le nettoyage intérieur avec repose après travaux","le nettoyage des éléments et du local après intervention"}
                ,new String[]{"Tournevis","lunettes de protection"}
                ,"Interv735651"
        ));
        list_intervention.add(new Intervention("Vérification du cheminée (évacuation des fumées de combustion)"
                ,"29-05-2021"
                ,3
                ,new String[]{"les échangeurs de chaleur","l’alimentation de gaz vers la chaudière","les fuites de gaz"}
                ,new String[]{"Tournevis","lunettes de protection"}
                ,"Interv735654"
        ));
        list_intervention.add(new Intervention("la dépose de la buse de fumée du raccordement au conduit"
                ,"19-06-2021"
                ,3
                ,new String[]{"le nettoyage intérieur avec repose après travaux","le nettoyage des éléments et du local après intervention"}
                ,new String[]{"Tournevis","lunettes de protection"}
                ,"Interv735655"
        ));
        list_intervention.add(new Intervention("Effectuer une calibration des contrôles"
                ,"26-05-2021"
                ,4
                ,new String[]{"Vérifiez les pompes","la lubrification","le fonctionnement du moteur","l’alignement"}
                ,new String[]{"Tournevis","lunettes de protection"}
                ,"Interv735656"
        ));
        list_intervention.add(new Intervention("Vérification le système de contrôle"
                ,"18-06-2021"
                ,1
                ,new String[]{"les sondes de température intérieure et extérieure","les dispositifs de sécurité","la valve de relâche haute pression et haute température"}
                ,new String[]{"Tournevis","lunettes de protection"}
                ,"Interv735657"
        ));
        list_intervention.add(new Intervention("Vérification du cheminée (évacuation des fumées de combustion)"
                ,"14-06-2021"
                ,5
                ,new String[]{"les échangeurs de chaleur","l’alimentation de gaz vers la chaudière","les fuites de gaz"}
                ,new String[]{"Tournevis","lunettes de protection"}
                ,"Interv735658"
        ));
        list_intervention.add(new Intervention("la dépose de la buse de fumée du raccordement au conduit"
                ,"02-06-2021"
                ,4
                ,new String[]{"le nettoyage intérieur avec repose après travaux","le nettoyage des éléments et du local après intervention"}
                ,new String[]{"Tournevis","lunettes de protection"}
                ,"Interv735659"
        ));
        list_intervention.add(new Intervention("Effectuer une calibration des contrôles"
                ,"09-06-2021"
                ,5
                ,new String[]{"Vérifiez les pompes","la lubrification","le fonctionnement du moteur","l’alignement"}
                ,new String[]{"Tournevis","lunettes de protection"}
                ,"Interv735660"
        ));
        list_intervention.add(new Intervention("Vérification le système de contrôle"
                ,"14-06-2021"
                ,1
                ,new String[]{"les sondes de température intérieure et extérieure","les dispositifs de sécurité","la valve de relâche haute pression et haute température"}
                ,new String[]{"Tournevis","lunettes de protection"}
                ,"Interv735661"
        ));
        list_intervention.add(new Intervention("Vérification du cheminée (évacuation des fumées de combustion)"
                ,"30-05-2021"
                ,3
                ,new String[]{"les échangeurs de chaleur","l’alimentation de gaz vers la chaudière","les fuites de gaz"}
                ,new String[]{"Tournevis","lunettes de protection"}
                ,"Interv735662"
        ));
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

    public List<Intervention> getList_intervention() {
        return list_intervention;
    }


}