package com.aptitekk.binghamapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class BellSchedulesFragment extends Fragment implements BellSchedulesListFragment.BellSchedulesListListener {

    public BellSchedulesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Add BellSchedulesListFragment to fragmentSpace
        BellSchedulesListFragment bellSchedulesListFragment = new BellSchedulesListFragment();
        bellSchedulesListFragment.addBellSchedulesListListener(this);

        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragmentSpace, bellSchedulesListFragment);
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragmentTransaction.addToBackStack("bellScheduleList");
        fragmentTransaction.commit();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_replaceable, container, false);
    }

    @Override
    public void openSchedule(Fragment scheduleToOpen) {

        // Replace the lists with the bell schedule, and add to backstack so that the back button will remove it.
        MainActivity.listFragments(getFragmentManager());
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragmentSpace, scheduleToOpen);
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragmentTransaction.addToBackStack("openSchedule");
        fragmentTransaction.commit();
    }
}
