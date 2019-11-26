package com.homehub.dragan.myhomehub.Activities;

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

import com.homehub.dragan.myhomehub.Classes.RoutineList;
import com.homehub.dragan.myhomehub.Classes.model.Entity;
import com.homehub.dragan.myhomehub.Classes.provider.DatabaseManager;
import com.homehub.dragan.myhomehub.R;
import com.homehub.dragan.myhomehub.Classes.model.Routine;

import java.util.ArrayList;

public class EditRoutineActivity extends HomeHubActivity {

    String triggerDeviceName;
    String triggerAction;

    TextView tvDevice;
    TextView tvAction;
    TextView tvTriggerType;
    TextView tvActivator;
    TextView tvTriggerAction;

    Spinner sprActions;
    Spinner sprActivators;
    Spinner sprTriggerType;
    Spinner sprTriggerAction;

    ArrayList<Entity> mEntities;
    DatabaseManager mDatabase;

    Entity triggerDevice;

    Button btnFinish;

    ArrayList<String> devices = new ArrayList<String>();
    ArrayList<String> actions = new ArrayList<String>();

    int position;
    int entityPosition;

    private Routine selectedRoutine;
    private Routine updatedRoutine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_routine);

        position = RoutineList.getInstance().selectedRoutinePosition;

        selectedRoutine = RoutineList.getInstance().routines.get(position);

        mDatabase = DatabaseManager.getInstance(this);
        mEntities = mDatabase.getEntities();

        devices.add("-- choose a device --");

        for (Entity e : mEntities) {
            //Log.d("yo", e.getFriendlyName());
            devices.add(e.getFriendlyName());

        }

        tvDevice = (TextView) findViewById(R.id.tvDevice);
        tvAction = (TextView) findViewById(R.id.tvAction);
        tvActivator = (TextView) findViewById(R.id.tvActivator);
        tvTriggerType = (TextView) findViewById(R.id.tvTriggerType);
        tvTriggerAction = (TextView) findViewById(R.id.tvTriggerAction);

        sprActions = (Spinner) findViewById(R.id.sprActions);
        sprActivators = (Spinner) findViewById(R.id.sprActivator);
        sprTriggerType = (Spinner) findViewById(R.id.sprTriggerType);
        sprTriggerAction = (Spinner) findViewById(R.id.sprTriggerAction);

        btnFinish = (Button) findViewById(R.id.btnFinish);

        actions.add("-- choose a state --");
        actions.add("Turn on");
        actions.add("Turn off");

        tvDevice.setText(selectedRoutine.getDeviceName());

        //populating Actions spinner
        ArrayAdapter<String> actionsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, actions);
        actionsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sprActions.setAdapter(actionsAdapter);

        //populating the TriggerTypes spinner
        ArrayAdapter<CharSequence> triggerTypeAdapter = ArrayAdapter.createFromResource(this, R.array.trigger_types, android.R.layout.simple_spinner_item);
        triggerTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sprTriggerType.setAdapter(triggerTypeAdapter);

        //populating Activators spinner
        ArrayAdapter<String> activatorAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, devices);
        activatorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sprActivators.setAdapter(activatorAdapter);

        //populating the TriggerActions spinner
        ArrayAdapter<String> triggerActionsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, actions);
        triggerActionsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sprTriggerAction.setAdapter(triggerActionsAdapter);

        //checking routine's Action
        if (selectedRoutine.getAction() == "Turn on") {
            sprActions.setSelection(1);
        } else {
            sprActions.setSelection(2);
        }

        //checking routine's TriggerType
        if (selectedRoutine.getTrigger().isTriggeredOnDeviceAction) {

            sprTriggerType.setSelection(1);

            //setting Activator
            sprActivators.setSelection(selectedRoutine.getTriggerDeviceNum());
            //entityPosition = selectedRoutine.getTriggerDeviceNum();


            //setting TriggerAction
            if (selectedRoutine.getTriggerAction() == "Turn on") {
                sprTriggerAction.setSelection(1);
            } else {
                sprTriggerAction.setSelection(2);
            }


        } else {
            tvTriggerAction.setVisibility(View.GONE);
            sprTriggerAction.setVisibility(View.GONE);
            sprTriggerType.setSelection(2);

            //setting Activator
            if (selectedRoutine.getActivatorName() == "Sunrise") {
                sprActivators.setSelection(1);
            } else {
                sprActivators.setSelection(2);
            }
        }


        btnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //RoutineList.getInstance().routines.remove(selectedRoutine);
                // above line was to prove that the Finish button can do stuff to the selected routine

                if (selectedRoutine.getTrigger().isTriggeredOnDeviceAction == true && selectedRoutine.getAction() != null && selectedRoutine.getActivatorName() != null && selectedRoutine.getTriggerAction() != null) {

                    updatedRoutine = new Routine(selectedRoutine.getDeviceName(), selectedRoutine.getAction(), selectedRoutine.getActivatorName(), selectedRoutine.getTrigger());

                    updatedRoutine.setTriggerDevice(triggerDevice);
                    updatedRoutine.setTriggerAction(triggerAction);
                    updatedRoutine.setTriggerDeviceNum(entityPosition);

                    RoutineList.getInstance().routines.set(position, updatedRoutine); // works!

                    Intent intent;
                    intent = new Intent(getApplicationContext(), AutomationActivity.class);
                    startActivity(intent);

                } else if (selectedRoutine.getTrigger().isTriggeredOnDeviceAction == false && selectedRoutine.getAction() != null && selectedRoutine.getActivatorName() != null) {

                    updatedRoutine = new Routine(selectedRoutine.getDeviceName(), selectedRoutine.getAction(), selectedRoutine.getActivatorName(), selectedRoutine.getTrigger());
                    RoutineList.getInstance().routines.set(position, updatedRoutine);

                    Intent intent;
                    intent = new Intent(getApplicationContext(), AutomationActivity.class);
                    startActivity(intent);

                } else {
                    Toast.makeText(getBaseContext(), "Select all routine parameters before creating device", Toast.LENGTH_SHORT).show();
                }
            }
        });

        sprActions.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) { // the user has not chosen an action
                    selectedRoutine.setAction(null);
                    //createdRoutine.setAction(null);
                } else {
                    selectedRoutine.setAction(parent.getItemAtPosition(position).toString());
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
                    selectedRoutine.setActivatorName(null);
                } else {
                    selectedRoutine.setActivatorName(parent.getItemAtPosition(position).toString());
                    entityPosition = position;
                    triggerDevice = mEntities.get(position-1);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        sprTriggerType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 1) {
                    selectedRoutine.getTrigger().setIsTriggeredOnDeviceAction(true);
                } else if (position == 2) {
                    selectedRoutine.getTrigger().setIsTriggeredOnDeviceAction(false);
                } else {
                    //Toast.makeText(getBaseContext(), "Hmm", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        sprTriggerAction.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {

                } else {
                    triggerAction = parent.getItemAtPosition(position).toString();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void setSelectedRoutine(Routine selectedRoutine) {
        this.selectedRoutine = selectedRoutine;
    }
}
