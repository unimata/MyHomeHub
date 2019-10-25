package com.homehub.dragan.myhomehub.Fragments;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.homehub.dragan.myhomehub.Activities.AccountSubActivity;
import com.homehub.dragan.myhomehub.Activities.AddDeviceRegActivity;
import com.homehub.dragan.myhomehub.R;

public class DeviceRegistrationsFragment extends Fragment {

    private ListView deviceRegListView;
    private Button btnNewDeviceReg;

    public static DeviceRegistrationsFragment newInstance() { return new DeviceRegistrationsFragment(); }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_device_registrations, container, false);

        //set up the add device reg button
        btnNewDeviceReg = view.findViewById(R.id.btnNewDeviceReg);

        //set up event listener for button
        btnNewDeviceReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //disable button to prevent double click
                btnNewDeviceReg.setEnabled(false);

                Intent intent;
                intent = new Intent(getActivity(), AddDeviceRegActivity.class);
                startActivity(intent);

            }
        });

        //get view id of deviceRegListView
        deviceRegListView = view.findViewById(R.id.deviceRegListView);

        //set up array adapter for the list view
        ArrayAdapter<CharSequence> deviceRegArray = ArrayAdapter.createFromResource(getActivity().getApplicationContext(), R.array.DummyDeviceRegistrations, android.R.layout.simple_list_item_1);
        deviceRegListView.setAdapter(deviceRegArray);


        return view;
    }

    @Override
    public void onResume(){
        super.onResume();

        //re-enable button
        btnNewDeviceReg.setEnabled(true);

        //refresh deviceRegListView with new registrations if there is any
        //TODO: set up list with actual device registrations, and actually refresh here
        //......./
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
}
