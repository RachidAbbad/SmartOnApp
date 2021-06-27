package com.abbad.smartonapp.ui.interventions;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.abbad.smartonapp.R;
import com.abbad.smartonapp.adapters.RecycleViewAdapter;
import com.abbad.smartonapp.ui.dashboard.DashboardViewModel;

public class Intervention extends Fragment {

    private InterventionViewModel mViewModel;

    InterventionViewModel interventionViewModel;
    RecyclerView recyclerView;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        interventionViewModel =
                new ViewModelProvider(this).get(InterventionViewModel.class);

        View root = inflater.inflate(R.layout.intervention_fragment, container, false);
        recyclerView = root.findViewById(R.id.list_intervention) ;
        recyclerView.setLayoutManager( new LinearLayoutManager(getContext()));
        RecycleViewAdapter adapter = new RecycleViewAdapter(getActivity().getSupportFragmentManager());
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        return root;
    }


}