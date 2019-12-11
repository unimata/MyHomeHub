package com.homehub.dragan.myhomehub.Activities;

import com.homehub.dragan.myhomehub.Classes.model.Entity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.homehub.dragan.myhomehub.Classes.model.Trigger;
import com.homehub.dragan.myhomehub.Classes.provider.DatabaseManager;
import com.homehub.dragan.myhomehub.R;

import com.homehub.dragan.myhomehub.Classes.RoutineList;
import com.homehub.dragan.myhomehub.Classes.model.Routine;

import java.util.ArrayList;

public class CreateRoutineActivity extends HomeHubActivity {

    Spinner sprDevices;
    Spinner sprActions;
    Spinner sprActivators;
    Spinner sprTriggerTypes;
    Spinner sprTriggerActions;
    TextView tvAction;
    TextView tvActivator;
    TextView tvTriggerType;
    TextView tvTriggerAction;
    Button btnCreateRoutine;

    int triggerDeviceNum;

    String deviceName;
    String actionName;
    String triggerTypeName;
    String activatorName;
    String triggerActionName;

    Boolean deviceChosen;
    Boolean actionChosen;
    Boolean activatorChosen;
    Boolean triggerTypeChosen;
    Boolean triggerActionChosen;
    Boolean isActionBasedTrigger;

    Entity chosenDevice;
    Entity triggerDevice;
    ArrayList<Entity> mEntities;
    DatabaseManager mDatabase;

    Routine createdRoutine;
    Trigger trigger = new Trigger("fake trigger");

    ArrayList<String> devices = new ArrayList<String>();
    ArrayList<String> actions = new ArrayList<String>();
    ArrayList<String> activators = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_routine);

        mDatabase = DatabaseManager.getInstance(this);
        mEntities = mDatabase.getEntities();

        devices.add("-- choose a device --");
        actions.add("-- choose an action --");
        activators.add("-- choose a trigger --");

        for (Entity e : mEntities) {
            //Log.d("yo", e.getFriendlyName());
            devices.add(e.getFriendlyName());
        }

        deviceChosen = false;
        actionChosen = false;
        activatorChosen = false;
        triggerTypeChosen = false;
        triggerActionChosen = false;
        isActionBasedTrigger = false;

        //createdRoutine = new Routine ("[error]","[error]","[error]");

        sprDevices = (Spinner) findViewById(R.id.sprDevices);
        sprActions = (Spinner) findViewById(R.id.sprActions);
        sprActivators = (Spinner) findViewById(R.id.sprActivator);
        sprTriggerTypes = (Spinner) findViewById(R.id.sprTriggerType);
        sprTriggerActions = (Spinner) findViewById(R.id.sprTriggerAction);

        tvAction = (TextView) findViewById(R.id.tvAction);
        tvTriggerType = (TextView) findViewById(R.id.tvTriggerType);
        tvActivator = (TextView) findViewById(R.id.tvActivator);
        tvTriggerAction = (TextView) findViewById(R.id.tvTriggerAction);

        btnCreateRoutine = (Button) findViewById(R.id.btnCreate);

        //populating Devices spinner
        ArrayAdapter<String> devicesAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, devices);
        devicesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sprDevices.setAdapter(devicesAdapter);

        btnCreateRoutine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isActionBasedTrigger == false && deviceChosen == true && actionChosen == true && triggerTypeChosen == true && activatorChosen == true) { // time-based

                    trigger.setIsTriggeredOnDeviceAction(isActionBasedTrigger);
                    createdRoutine = new Routine(deviceName, actionName, activatorName, trigger);
                    RoutineList.getInstance().routines.add(createdRoutine);

                    Intent intent;
                    intent = new Intent(getApplicationContext(), AutomationActivity.class);
                    startActivity(intent);
                } else if (isActionBasedTrigger == true && deviceChosen == true && actionChosen == true && triggerTypeChosen == true && activatorChosen == true && triggerActionChosen == true) { // action-based
                    trigger.setIsTriggeredOnDeviceAction(isActionBasedTrigger);
                    createdRoutine = new Routine(deviceName, actionName, activatorName, trigger);

                    createdRoutine.setTriggerDevice(triggerDevice);
                    createdRoutine.setTriggerAction(triggerActionName);
                    createdRoutine.setTriggerDeviceNum(triggerDeviceNum);

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
                if (position == 0 || position == 1) { // the user has not chosen a device
                    deviceChosen = false;
                    chosenDevice = null;
                    triggerDevice = null;
                    //createdRoutine.setDeviceName(null);

                    tvAction.setVisibility(View.GONE);
                    tvActivator.setVisibility(View.GONE);
                    tvTriggerType.setVisibility(View.GONE);
                    tvTriggerAction.setVisibility(View.GONE);
                    sprActions.setVisibility(View.GONE);
                    sprActivators.setVisibility(View.GONE);
                    sprTriggerTypes.setVisibility(View.GONE);
                    sprTriggerActions.setVisibility(View.GONE);

                    //btnCreateRoutine.setVisibility(View.GONE); // this is making the app crash for some reason
                } else {

                    chosenDevice = mEntities.get(position-1);
                    //Log.d("device name", chosenDevice.getFriendlyName()); // this proves whether the above line works

                    deviceChosen = true;
                    deviceName = parent.getItemAtPosition(position).toString(); // this sets the name of the device

                    // figure out what type of device is selected and what to show based on that.
                    if (chosenDevice.isToggleable()) {

                        actions.clear();
                        actions.add("-- choose a state --");
                        actions.add("Turn on");
                        actions.add("Turn off");

                    } else { // not doing this for now

                        actions.clear();
                        switch (chosenDevice.getFriendlyDomainName()){
                            case "Input Select":
                                break;
                            case "Input Number":
                                break;
                            case "Input DateTime":
                                break;
                            case "Input Text":
                                break;
                            case "Input Boolean":
                                break;
                        }
                    }

                    populateActions(actions);
                    populateTriggerTypes();

                    // make other elements visible
                    tvAction.setVisibility(View.VISIBLE);
                    //tvActivator.setVisibility(View.VISIBLE);
                    tvTriggerType.setVisibility(View.VISIBLE);
                    sprActions.setVisibility(View.VISIBLE);
                    //sprActivators.setVisibility(View.VISIBLE);
                    sprTriggerTypes.setVisibility(View.VISIBLE);
                    btnCreateRoutine.setVisibility(View.VISIBLE);

                    //Toast.makeText(getBaseContext(), parent.getItemAtPosition(position)+ " is selected", Toast.LENGTH_SHORT).show();
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

        sprTriggerTypes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) { // the user has not chosen a device
                    //createdRoutine.setActivator(null);
                    triggerTypeChosen = false;
                    tvActivator.setVisibility(View.GONE);
                    sprActivators.setVisibility(View.GONE);
                    tvTriggerAction.setVisibility(View.GONE);
                    sprTriggerActions.setVisibility(View.GONE);
                } else {

                    if (position == 1) { // triggered on action from other device

                        isActionBasedTrigger = true;

                        tvTriggerAction.setVisibility(View.VISIBLE);
                        sprTriggerActions.setVisibility(View.VISIBLE);

                        activators.clear();

                        populateActivators(devices);
                        populateTriggerActions(actions);

                        tvActivator.setText("Triggered By:");

                    } else { // time-based trigger

                        isActionBasedTrigger = false;

                        activators.clear();
                        activators.add("-- choose a time to trigger --");
                        activators.add("Sunrise");
                        activators.add("Sunset");
                        populateActivators(activators);

                        tvTriggerAction.setVisibility(View.GONE);
                        sprTriggerActions.setVisibility(View.GONE);
                        tvActivator.setText("Triggered At:");

                    }

                    triggerTypeChosen = true;
                    tvActivator.setVisibility(View.VISIBLE);
                    sprActivators.setVisibility(View.VISIBLE);
                    triggerTypeName = parent.getItemAtPosition(position).toString();

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        sprActivators.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) { // the user has not chosen a device
                    //createdRoutine.setActivator(null);
                    activatorChosen = false;
                } else {
                    if (isActionBasedTrigger) {
                        triggerDevice = mEntities.get(position-1);
                        triggerDeviceNum = position;
                    }
                    activatorName = parent.getItemAtPosition(position).toString();
                    activatorChosen = true;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        sprTriggerActions.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    triggerActionChosen = false;
                } else {
                    triggerActionChosen = true;
                    triggerActionName = parent.getItemAtPosition(position).toString();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void populateActions(ArrayList<String> actions) {
        ArrayAdapter<String> actionsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, actions);
        actionsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sprActions.setAdapter(actionsAdapter);
    }

    private void populateTriggerTypes() {
        ArrayAdapter<CharSequence> triggerTypeAdapter = ArrayAdapter.createFromResource(this, R.array.trigger_types, android.R.layout.simple_spinner_item);
        triggerTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sprTriggerTypes.setAdapter(triggerTypeAdapter);
    }

    private void populateActivators(ArrayList<String> activators) {
        ArrayAdapter<String> activatorAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, activators);
        activatorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sprActivators.setAdapter(activatorAdapter);
    }

    private void populateTriggerActions(ArrayList<String> actions) {
        ArrayAdapter<String> triggerActionsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, actions);
        triggerActionsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sprTriggerActions.setAdapter(triggerActionsAdapter);
    }

}
