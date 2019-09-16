package com.homehub.dragan.myhomehub.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.homehub.dragan.myhomehub.R;

public class AutomationActivity extends AppCompatActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_automation);

        Button btnNewRoutine = (Button) findViewById(R.id.btnCreateRoutine);
        Button btnManageRoutines = (Button) findViewById(R.id.btnManageRoutines);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.setSelectedItemId(R.id.navigation_automation);

        btnNewRoutine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                intent = new Intent(getApplicationContext(), CreateRoutineActivity.class);
                startActivity(intent);
            }
        });

        btnManageRoutines.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                intent = new Intent(getApplicationContext(), ManageRoutinesActivity.class);
                startActivity(intent);
            }
        });
    }
}
