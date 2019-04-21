package com.homehub.dragan.myhomehub.Activities;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.homehub.dragan.myhomehub.UI.ComplexRecyclerViewAdapter;
import com.homehub.dragan.myhomehub.R;
import com.homehub.dragan.myhomehub.Classes.DeviceList;
import com.homehub.dragan.myhomehub.UI.SliderBasedControl;
import com.homehub.dragan.myhomehub.UI.SwitchBasedControl;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public ArrayList<Object> items = DeviceList.getInstance().getDevices();
    //public DeviceList devicelist =

    private MediaPlayer mPlayer;
    //private FirebaseAuth mAuth;
    private Context mContext;
    private RecyclerView recyclerView;
    private TextView mTextMessage;
    private FloatingActionButton mAddButton;



    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(MenuItem item) {
            Intent intent;
            switch (item.getItemId()) {

                case R.id.navigation_dashboard:
                    return true;
                case R.id.navigation_account:

                    intent = new Intent(MainActivity.this, AccountActivity.class);
                    //intent.putExtra("channel", "4lmrrOD8Ll2SkO2A");
                    startActivity(intent);
                    MainActivity.super.finish();
                    return true;
                case R.id.navigation_automation:

                    intent = new Intent(MainActivity.this, AutomationActivity.class);
                    startActivity(intent);
                    MainActivity.super.finish();
                    return true;
            }
            return false;
        }
    };

    private void initDevices(){

    }

    private ArrayList<Object> getDeviceList() {
        ArrayList<Object> list = items;
        //Log.d("debuggin", list.toString());
        return list;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_ACTION_BAR);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //FirebaseApp.initializeApp(this);
        mContext = getApplicationContext();

        //initDevices();

        refreshDeviceList();

        //mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        navigation.setSelectedItemId(R.id.navigation_dashboard);
    }

    //used to initially load in device list, and to reload after adding new device to list
    public void refreshDeviceList(){
        RecyclerView rvContacts = (RecyclerView) findViewById(R.id.recyclerView);
        rvContacts.setAdapter(new ComplexRecyclerViewAdapter(getDeviceList()));
        rvContacts.setLayoutManager(new LinearLayoutManager(this));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.logout, menu);


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.lblEdit:
                Toast.makeText(mContext, "Edit", Toast.LENGTH_SHORT).show();
                //Intent i = new Intent(MainActivity.this, LoginActivity.class);
                //startActivity(i);
                refreshDeviceList();
                return true;
            case R.id.lblLogout:
                Toast.makeText(mContext, "Logout", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }



}
