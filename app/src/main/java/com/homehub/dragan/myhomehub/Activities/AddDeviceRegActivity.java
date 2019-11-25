package com.homehub.dragan.myhomehub.Activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.homehub.dragan.myhomehub.Classes.firebase.FirebaseDatabaseHelper;
import com.homehub.dragan.myhomehub.Classes.model.Device_Registration;
import com.homehub.dragan.myhomehub.R;

import java.util.List;

public class AddDeviceRegActivity extends AppCompatActivity {

    private Spinner spinnerDevice;
    private EditText editTxtDeviceName;
    private Button btnContinueReg;
    private String newDeviceRegKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_device_reg);


        //catch new key from prev activity
        newDeviceRegKey = getIntent().getStringExtra("newDeviceRegKey");

        //show action bar
        assert getSupportActionBar() != null;   //null check

        //set up the back and forward buttons
        btnContinueReg = findViewById(R.id.btnContinueReg);

        //set up variables for spinner and edittext
        btnContinueReg = findViewById(R.id.btnContinueReg);
        spinnerDevice = findViewById(R.id.spinnerDevice);
        editTxtDeviceName = findViewById(R.id.editTxtDeviceName);

        //set up event listener for button
        btnContinueReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get string array of urls
                String[] urlStringArray = getResources().getStringArray(R.array.NewDeviceRegistrationsURLS);
                if(spinnerDevice.getSelectedItemPosition() != 3) {
                    if (editTxtDeviceName.getText().length() > 0) {
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
                        intent.putExtra("device", spinnerDevice.getSelectedItem().toString());
                        intent.putExtra("deviceName", editTxtDeviceName.getText().toString());
                        intent.putExtra("deviceURL", urlStringArray[spinnerDevice.getSelectedItemPosition()]);
                        intent.putExtra("newDeviceRegKey", newDeviceRegKey);

                        startActivity(intent);

                    } else {
                        Toast.makeText(AddDeviceRegActivity.this, "Please input a device name.", Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    //get all new incoming data for device registration and sent to firebase
                    Device_Registration deviceReg = new Device_Registration(spinnerDevice.getSelectedItem().toString(), editTxtDeviceName.getText().toString());

                    new FirebaseDatabaseHelper().addDeviceReg(newDeviceRegKey, deviceReg, new FirebaseDatabaseHelper.DeviceDataStatus() {
                        @Override
                        public void DeviceDataIsLoaded(List<Device_Registration> deviceRegistrations, List<String> keys) {

                        }

                        @Override
                        public void DeviceDataIsInserted() {
                            Toast.makeText(AddDeviceRegActivity.this, "Device registration added successfully.", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void DeviceDataIsUpdated() {

                        }

                        @Override
                        public void DeviceDataIsDeleted() {

                        }
                    });

                    finish();
                }


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
