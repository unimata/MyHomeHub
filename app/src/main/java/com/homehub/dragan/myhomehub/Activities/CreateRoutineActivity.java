package com.homehub.dragan.myhomehub.Activities;

import android.content.Entity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.homehub.dragan.myhomehub.R;

import com.homehub.dragan.myhomehub.Classes.RoutineList;
import com.homehub.dragan.myhomehub.UI.Routine;

import java.util.ArrayList;

public class CreateRoutineActivity extends AppCompatActivity {

    Spinner sprDevices;
    Spinner sprActions;
    Spinner sprActivators;
    TextView tvAction;
    TextView tvActivator;
    Button btnCreateRoutine;

    String deviceName;
    String actionName;
    String activatorName;

    Boolean deviceChosen;
    Boolean actionChosen;
    Boolean activatorChosen;

    Entity device;

    Routine createdRoutine;

    ArrayList<String> actions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_routine);

        deviceChosen = false;
        actionChosen = false;
        activatorChosen = false;

        //createdRoutine = new Routine ("[error]","[error]","[error]");

        sprDevices = (Spinner) findViewById(R.id.sprDevices);
        sprActions = (Spinner) findViewById(R.id.sprActions);
        sprActivators = (Spinner) findViewById(R.id.sprActivator);

        tvAction = (TextView) findViewById(R.id.tvAction);
        tvActivator = (TextView) findViewById(R.id.tvActivator);

        btnCreateRoutine = (Button) findViewById(R.id.btnCreate);

        //populating Devices spinner
        ArrayAdapter<CharSequence> devicesAdapter = ArrayAdapter.createFromResource(this, R.array.fake_devices, android.R.layout.simple_spinner_item);
        devicesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sprDevices.setAdapter(devicesAdapter);

        //populating Actions spinner
        ArrayAdapter<CharSequence> actionsAdapter = ArrayAdapter.createFromResource(this, R.array.fake_actions, android.R.layout.simple_spinner_item);
        actionsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sprActions.setAdapter(actionsAdapter);

        //populating Activators spinner
        ArrayAdapter<CharSequence> activatorAdapter = ArrayAdapter.createFromResource(this, R.array.fake_activators, android.R.layout.simple_spinner_item);
        activatorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sprActivators.setAdapter(activatorAdapter);

        btnCreateRoutine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (deviceChosen == true && actionChosen == true && activatorChosen == true) { // right now it can only recognize if a device is chosen, not action or activator, idk why

                    createdRoutine = new Routine(deviceName, actionName, activatorName);
                    RoutineList.getInstance().routines.add(createdRoutine);

                    Intent intent;
                    intent = new Intent(getApplicationContext(), AutomationActivity.class);
                    startActivity(intent);
                } else {
                    // Toast message works
                    Toast.makeText(getBaseContext(), "Select all routine parameters before creating device", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //what to do when a device is selected from the list
        sprDevices.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) { // the user has not chosen a device
                    deviceChosen = false;
                    device = null;
                    //createdRoutine.setDeviceName(null);

                    tvAction.setVisibility(View.GONE);
                    tvActivator.setVisibility(View.GONE);
                    sprActions.setVisibility(View.GONE);
                    sprActivators.setVisibility(View.GONE);

                    //btnCreateRoutine.setVisibility(View.GONE); // this is making the app crash for some reason
                } else {
                    deviceChosen = true;
                    deviceName = parent.getItemAtPosition(position).toString(); // this sets the name of the device

                    tvAction.setVisibility(View.VISIBLE);
                    tvActivator.setVisibility(View.VISIBLE);
                    sprActions.setVisibility(View.VISIBLE);
                    sprActivators.setVisibility(View.VISIBLE);
                    btnCreateRoutine.setVisibility(View.VISIBLE);

                    Toast.makeText(getBaseContext(), parent.getItemAtPosition(position)+ " is selected", Toast.LENGTH_SHORT).show();
                    refreshActionView();
                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        sprActions.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position == 0) { // the user has not chosen an action
                    actionChosen = false;
                    //createdRoutine.setAction(null);
                } else {
                    actionChosen = true;
                    actionName = parent.getItemAtPosition(position).toString();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //Toast.makeText(getBaseContext(), "Action no", Toast.LENGTH_SHORT).show();
            }
        });

        sprActivators.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) { // the user has not chosen a device
                    //createdRoutine.setActivator(null);
                    activatorChosen = false;
                } else {
                    activatorName = parent.getItemAtPosition(position).toString();
                    activatorChosen = true;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }

    private void refreshActionView() {

    }
}
