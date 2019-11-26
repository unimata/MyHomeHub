package com.homehub.dragan.myhomehub.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.homehub.dragan.myhomehub.Classes.RoutineList;
import com.homehub.dragan.myhomehub.Classes.model.Trigger;

import com.homehub.dragan.myhomehub.R;
import com.homehub.dragan.myhomehub.Classes.model.Routine;
import com.homehub.dragan.myhomehub.UI.RoutineRecyclerViewAdapter;

import java.util.ArrayList;

public class AutomationActivity extends AppCompatActivity {

    // old navigation bar

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(MenuItem item) {
            Intent intent;
            switch (item.getItemId()) {
                case R.id.navigation_dashboard:
                    intent = new Intent(AutomationActivity.this, MainActivity.class);
                    //intent.putExtra("channel", "4lmrrOD8Ll2SkO2A");
                    startActivity(intent);
                    AutomationActivity.super.finish();
                    return true;
                case R.id.navigation_account:
                    intent = new Intent(AutomationActivity.this, AccountActivity.class);
                    //intent.putExtra("channel", "4lmrrOD8Ll2SkO2A");
                    startActivity(intent);
                    AutomationActivity.super.finish();
                    return true;
                case R.id.navigation_automation:
                    return true;
            }
            return false;
        }
    };

    public ArrayList<Routine> items = RoutineList.getInstance().getRoutines();
    RecyclerView rvRoutines;

    private Trigger dummyTrigger = new Trigger("me trigger");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_automation);

        //RecyclerView rvRoutines = (RecyclerView) findViewById(R.id.rvAutomationRoutines);
        rvRoutines = (RecyclerView) findViewById(R.id.rvAutomationRoutines);
        //rvRoutines.getAdapter().bindViewHolder()

        refreshRoutineList();

        Button btnNewRoutine = (Button) findViewById(R.id.btnCreateRoutine);
        Button btnTestRoutines = (Button) findViewById(R.id.btnTestRoutines);

        btnTestRoutines.setVisibility(View.GONE);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.setSelectedItemId(R.id.navigation_automation);

        dummyTrigger.setIsTriggeredOnDeviceAction(false);



        btnNewRoutine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                intent = new Intent(getApplicationContext(), CreateRoutineActivity.class);
                startActivity(intent);
            }
        });

        btnTestRoutines.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RoutineList.getInstance().routines.add(new Routine("WeMo","Do something","Something", dummyTrigger));
                RoutineList.getInstance().routines.add(new Routine("Hue","Do something","Something", dummyTrigger));
                RoutineList.getInstance().routines.add(new Routine("LiFX","Do something","Something", dummyTrigger));
                refreshRoutineList();
            }
        });

        rvRoutines.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("message","clicked");
            }
        });

    }

    private ArrayList<Routine> getRoutineList() {
        ArrayList<Routine> list = items;
        //list.add(new Routine("Bolbu","go o", "noon"));
        //Log.d("debuggin", list.toString());
        return list;
    }

    public void refreshRoutineList(){

        if (RoutineList.getInstance().routines.isEmpty()) {
            rvRoutines.setVisibility(View.INVISIBLE);
        } else {
            rvRoutines.setVisibility(View.VISIBLE);
            rvRoutines.setAdapter(new RoutineRecyclerViewAdapter(getRoutineList(),this));
            rvRoutines.setLayoutManager(new LinearLayoutManager(this));
        }
    }

    public void editButtonPressed(){
        Intent intent;
        intent = new Intent(getApplicationContext(), EditRoutineActivity.class);
        startActivity(intent);
    }
}
