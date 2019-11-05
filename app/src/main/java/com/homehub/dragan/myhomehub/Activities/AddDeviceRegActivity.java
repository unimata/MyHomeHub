package com.homehub.dragan.myhomehub.Activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;

import com.homehub.dragan.myhomehub.R;

public class AddDeviceRegActivity extends AppCompatActivity {

    private Button btnContinueReg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_device_reg);

        //TODO: Improve and complete layout UI with actual data/required fields

        //show action bar
        assert getSupportActionBar() != null;   //null check
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);   //show back button

        //set up the back and forward buttons
        btnContinueReg = findViewById(R.id.btnContinueReg);

        //set up event listener for button
        btnContinueReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //disable button to prevent double click
                btnContinueReg.setEnabled(false);

                //broadcast receiver to finish completely when user is done
                BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

                    @Override
                    public void onReceive(Context arg0, Intent intent) {
                        String action = intent.getAction();
                        if (action.equals("finish_activity")) {
                            finish();
                        }
                    }
                };
                registerReceiver(broadcastReceiver, new IntentFilter("finish_activity"));

                Intent intent = new Intent(getApplicationContext(), DeviceRegWebActivity.class);
                startActivity(intent);

            }
        });
    }

    @Override
    public void onResume(){
        super.onResume();

        //re-enable button to prevent double click
        btnContinueReg.setEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}
