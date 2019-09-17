package com.homehub.dragan.myhomehub.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import com.homehub.dragan.myhomehub.Classes.RoutineList;

import com.homehub.dragan.myhomehub.R;
import com.homehub.dragan.myhomehub.UI.ComplexRecyclerViewAdapter;
import com.homehub.dragan.myhomehub.UI.Routine;
import com.homehub.dragan.myhomehub.UI.RoutineRecyclerViewAdapter;

import java.util.ArrayList;

public class ManageRoutinesActivity extends AppCompatActivity {

    public ArrayList<Object> items = RoutineList.getInstance().getRoutines();
    RecyclerView rvRoutines;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_routines);

        refreshRoutines();
    }

    private ArrayList<Object> getRoutineList() {
        ArrayList<Object> list = items;
        //Log.d("debuggin", list.toString());
        return list;
    }

    public void refreshRoutines(){
        rvRoutines = (RecyclerView) findViewById(R.id.rvRoutines);
        rvRoutines.setAdapter(new RoutineRecyclerViewAdapter(getRoutineList()));
        rvRoutines.setLayoutManager(new LinearLayoutManager(this));
    }
}
